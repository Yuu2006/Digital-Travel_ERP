package com.digitaltravel.erp.config;

import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.digitaltravel.erp.exception.AppException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final long expiration;

    public JwtUtil(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration}") long expiration) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.expiration = expiration;
    }

    public String generateToken(UserDetails userDetails) {
        List<String> roles = userDetails.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .toList();
        var builder = Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration));

        if (userDetails instanceof TaiKhoanDetails details) {
            var taiKhoan = details.getTaiKhoan();
            var vaiTro = taiKhoan.getVaiTro();
            builder
                    .claim("maVaiTro", vaiTro.getMaVaiTro())
                    .claim("tenHienThi", vaiTro.getTenHienThi())
                    .claim("hoTen", taiKhoan.getHoTen());
        }

        return builder.signWith(secretKey).compact();
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public String extractRole(String token) {
        Claims claims = parseClaims(token);
        String roleStr = null;

        if (claims.containsKey("maVaiTro")) {
            roleStr = claims.get("maVaiTro", String.class);
        } else if (claims.containsKey("roles")) {
            Object rolesObj = claims.get("roles");
            if (rolesObj instanceof List<?> list && !list.isEmpty()) {
                roleStr = list.get(0).toString();
            }
        } else if (claims.containsKey("role")) {
            roleStr = claims.get("role", String.class);
        }

        if (roleStr == null) {
            roleStr = "";
        }

        String normalizedRole = roleStr.trim().toUpperCase();
        if (normalizedRole.startsWith("ROLE_")) {
            normalizedRole = normalizedRole.substring(5);
        }

        return normalizedRole;
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return parseClaims(token).getExpiration().before(new Date());
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // ----------------------------------------------------------------
    // Reset password token (15 phut)
    // ----------------------------------------------------------------

    private static final long RESET_EXPIRATION = 15 * 60 * 1000L; // 15 phut
    private static final String PURPOSE_RESET   = "RESET_PASSWORD";

    /**
     * Sinh token de dat lai mat khau. Token co hieu luc 15 phut.
     */
    public String generateResetToken(String tenDangNhap) {
        return Jwts.builder()
                .subject(tenDangNhap)
                .claim("purpose", PURPOSE_RESET)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + RESET_EXPIRATION))
                .signWith(secretKey)
                .compact();
    }

    /**
     * Xac thuc reset token va tra ve tenDangNhap neu hop le.
     * Nem AppException.badRequest neu sai/het han/sai purpose.
     */
    public String extractResetSubject(String token) {
        try {
            Claims claims = parseClaims(token);
            if (!PURPOSE_RESET.equals(claims.get("purpose", String.class))) {
                throw AppException.badRequest("Token không hợp lệ");
            }
            return claims.getSubject();
        } catch (JwtException e) {
            throw AppException.badRequest("Token không hợp lệ hoặc đã hết hạn");
        }
    }
}
