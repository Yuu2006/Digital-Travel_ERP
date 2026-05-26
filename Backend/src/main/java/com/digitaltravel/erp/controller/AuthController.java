package com.digitaltravel.erp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

import com.digitaltravel.erp.config.JwtUtil;
import com.digitaltravel.erp.config.TaiKhoanDetails;
import com.digitaltravel.erp.config.VaiTroConst;
import com.digitaltravel.erp.dto.requests.DangKyRequest;
import com.digitaltravel.erp.dto.requests.DangNhapRequest;
import com.digitaltravel.erp.dto.requests.DatLaiMatKhauRequest;
import com.digitaltravel.erp.dto.requests.DoiMatKhauRequest;
import com.digitaltravel.erp.dto.requests.QuenMatKhauRequest;
import com.digitaltravel.erp.dto.responses.ApiResponse;
import com.digitaltravel.erp.dto.responses.DangNhapResponse;
import com.digitaltravel.erp.entity.HoChieuSo;
import com.digitaltravel.erp.entity.TaiKhoan;
import com.digitaltravel.erp.entity.VaiTro;
import com.digitaltravel.erp.exception.AppException;
import com.digitaltravel.erp.repository.HoChieuSoRepository;
import com.digitaltravel.erp.repository.TaiKhoanRepository;
import com.digitaltravel.erp.repository.VaiTroRepository;
import com.digitaltravel.erp.service.MaTuDongService;
import com.digitaltravel.erp.service.QuyetToanService;
import org.springframework.data.domain.PageRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final TaiKhoanRepository taiKhoanRepository;
    private final VaiTroRepository vaiTroRepository;
    private final HoChieuSoRepository hoChieuSoRepository;
    private final MaTuDongService maTuDongService;
    private final QuyetToanService quyetToanService;

    @GetMapping("/debug")
    public String debug() {
        try {
            quyetToanService.tourCanQuyetToan(PageRequest.of(0, 10));
            return "SUCCESS";
        } catch (Exception e) {
            java.io.StringWriter sw = new java.io.StringWriter();
            e.printStackTrace(new java.io.PrintWriter(sw));
            return sw.toString();
        }
    }

    @PostMapping("/dang-ky")
    @Transactional
    public ResponseEntity<ApiResponse<DangNhapResponse>> dangKy(@Valid @RequestBody DangKyRequest request) {
        if (!request.getMatKhau().equals(request.getXacNhanMatKhau())) {
            throw AppException.badRequest("Mật khẩu và xác nhận mật khẩu không khớp");
        }
        if (taiKhoanRepository.existsByTenDangNhap(request.getTenDangNhap())) {
            throw AppException.badRequest("Tên đăng nhập đã tồn tại");
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()
                && taiKhoanRepository.existsByEmail(request.getEmail())) {
            throw AppException.badRequest("Email đã được sử dụng");
        }
        if (request.getCccd() != null && !request.getCccd().isBlank()
                && taiKhoanRepository.existsByCccd(request.getCccd())) {
            throw AppException.badRequest("CCCD đã được sử dụng");
        }

        VaiTro vaiTroKhach = vaiTroRepository.findById(VaiTroConst.KHACHHANG)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy vai trò KHÁCH HÀNG"));

        TaiKhoan taiKhoan = new TaiKhoan();
        taiKhoan.setMaTaiKhoan(maTuDongService.taoMaTaiKhoanTheoVaiTro(VaiTroConst.KHACHHANG));
        taiKhoan.setTenDangNhap(request.getTenDangNhap());
        taiKhoan.setMatKhau(passwordEncoder.encode(request.getMatKhau()));
        taiKhoan.setHoTen(request.getHoTen());
        taiKhoan.setCccd(request.getCccd());
        taiKhoan.setNgaySinh(request.getNgaySinh());
        taiKhoan.setEmail(request.getEmail());
        taiKhoan.setSoDienThoai(request.getSoDienThoai());
        taiKhoan.setVaiTro(vaiTroKhach);
        taiKhoan.setTrangThai("HOAT_DONG");
        taiKhoanRepository.save(taiKhoan);

        // Tự động tạo hồ sơ khách hàng (HoChieuSo)
        HoChieuSo hoChieuSo = new HoChieuSo();
        hoChieuSo.setMaKhachHang(maTuDongService.taoMaHoChieuSo());
        hoChieuSo.setTaiKhoan(taiKhoan);
        hoChieuSo.setHangThanhVien("THANH_VIEN");
        hoChieuSo.setDiemXanh(0L);
        hoChieuSoRepository.save(hoChieuSo);

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getTenDangNhap(), request.getMatKhau())
        );
        TaiKhoanDetails details = (TaiKhoanDetails) auth.getPrincipal();
        String token = jwtUtil.generateToken(details);
        DangNhapResponse body = DangNhapResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .maVaiTro(details.getTaiKhoan().getVaiTro().getMaVaiTro())
                .tenHienThi(details.getTaiKhoan().getVaiTro().getTenHienThi())
                .hoTen(details.getTaiKhoan().getHoTen())
                .build();
        return ResponseEntity.status(201).body(ApiResponse.created(body));
    }

    @PostMapping("/dang-nhap")
    public ResponseEntity<ApiResponse<DangNhapResponse>> dangNhap(@Valid @RequestBody DangNhapRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getTenDangNhap(), request.getMatKhau())
        );
        TaiKhoanDetails details = (TaiKhoanDetails) auth.getPrincipal();
        String token = jwtUtil.generateToken(details);
        DangNhapResponse body = DangNhapResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .maVaiTro(details.getTaiKhoan().getVaiTro().getMaVaiTro())
                .tenHienThi(details.getTaiKhoan().getVaiTro().getTenHienThi())
                .hoTen(details.getTaiKhoan().getHoTen())
                .build();
        return ResponseEntity.ok(ApiResponse.ok("Đăng nhập thành công", body));
    }

    @PostMapping("/doi-mat-khau")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> doiMatKhau(
            @AuthenticationPrincipal TaiKhoanDetails details,
            @Valid @RequestBody DoiMatKhauRequest request) {
        if (!request.getMatKhauMoi().equals(request.getXacNhanMatKhau())) {
            throw AppException.badRequest("Mật khẩu mới và xác nhận không khớp");
        }
        if (!passwordEncoder.matches(request.getMatKhauCu(), details.getPassword())) {
            throw AppException.unauthorized("Mật khẩu cũ không đúng");
        }
        TaiKhoan taiKhoan = details.getTaiKhoan();
        taiKhoan.setMatKhau(passwordEncoder.encode(request.getMatKhauMoi()));
        taiKhoanRepository.save(taiKhoan);
        return ResponseEntity.ok(ApiResponse.noContent("Đổi mật khẩu thành công"));
    }

    @PostMapping("/kiem-tra-mat-khau")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> kiemTraMatKhau(
            @AuthenticationPrincipal TaiKhoanDetails details,
            @RequestBody Map<String, String> request) {
        String matKhauCu = request.get("matKhauCu");
        if (!passwordEncoder.matches(matKhauCu, details.getPassword())) {
            throw AppException.unauthorized("Mật khẩu cũ không đúng");
        }
        return ResponseEntity.ok(ApiResponse.noContent("Mật khẩu cũ chính xác"));
    }

    @PostMapping("/quen-mat-khau")
    public ResponseEntity<ApiResponse<String>> quenMatKhau(@Valid @RequestBody QuenMatKhauRequest request) {
        TaiKhoan taiKhoan = taiKhoanRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> AppException.notFound("Không tìm thấy tài khoản với email này"));
        String resetToken = jwtUtil.generateResetToken(taiKhoan.getTenDangNhap());
        // TODO: Thay viec tra ve token bang gui email trong production
        return ResponseEntity.ok(ApiResponse.ok("Đã tạo token đặt lại mật khẩu (hiệu lực 15 phút)", resetToken));
    }

    @PostMapping("/dat-lai-mat-khau")
    public ResponseEntity<ApiResponse<Void>> datLaiMatKhau(@Valid @RequestBody DatLaiMatKhauRequest request) {
        if (!request.getMatKhauMoi().equals(request.getXacNhanMatKhau())) {
            throw AppException.badRequest("Mật khẩu mới và xác nhận không khớp");
        }
        String tenDangNhap = jwtUtil.extractResetSubject(request.getResetToken());
        TaiKhoan taiKhoan = taiKhoanRepository.findByTenDangNhapWithVaiTro(tenDangNhap)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy tài khoản"));
        taiKhoan.setMatKhau(passwordEncoder.encode(request.getMatKhauMoi()));
        taiKhoanRepository.save(taiKhoan);
        return ResponseEntity.ok(ApiResponse.noContent("Đặt lại mật khẩu thành công"));
    }

    @PostMapping("/dang-xuat")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> dangXuat() {
        return ResponseEntity.ok(ApiResponse.noContent("Đăng xuất thành công"));
    }
}
