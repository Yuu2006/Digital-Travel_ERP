package com.digitaltravel.erp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @PostMapping("/dang-ky")
    @Transactional
    // UC56 - Đăng ký: Tạo tài khoản khách hàng và hồ sơ số
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
                new UsernamePasswordAuthenticationToken(request.getTenDangNhap(), request.getMatKhau()));
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
    // UC57 - Đăng nhập: Xác thực người dùng và cấp phiên
    public ResponseEntity<ApiResponse<DangNhapResponse>> dangNhap(@Valid @RequestBody DangNhapRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getTenDangNhap(), request.getMatKhau()));
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
    // UC60 - Đổi mật khẩu: Cập nhật mật khẩu sau xác thực
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
    // UC60 - Đổi mật khẩu: Kiểm tra mật khẩu hiện tại
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
    // UC59 - Quên mật khẩu: Khởi tạo yêu cầu đặt lại mật khẩu
    public ResponseEntity<ApiResponse<Void>> quenMatKhau(@Valid @RequestBody QuenMatKhauRequest request) {
        TaiKhoan taiKhoan = taiKhoanRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> AppException.notFound("Không tìm thấy tài khoản với email này"));
        String resetToken = jwtUtil.generateResetToken(taiKhoan.getTenDangNhap());
        // TODO: Gửi email chứa resetToken cho người dùng
        return ResponseEntity.ok(ApiResponse.ok("Đã gửi hướng dẫn đặt lại mật khẩu vào email của bạn", null));
    }

    @PostMapping("/dat-lai-mat-khau")
    // UC59 - Quên mật khẩu: Lưu mật khẩu mới bằng token
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
    // UC58 - Đăng xuất: Kết thúc phiên người dùng
    public ResponseEntity<ApiResponse<Void>> dangXuat() {
        return ResponseEntity.ok(ApiResponse.noContent("Đăng xuất thành công"));
    }
}
