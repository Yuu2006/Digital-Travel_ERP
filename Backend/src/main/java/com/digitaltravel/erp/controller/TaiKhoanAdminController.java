package com.digitaltravel.erp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digitaltravel.erp.config.VaiTroConst;
import com.digitaltravel.erp.dto.requests.DangKyNhanVienRequest;
import com.digitaltravel.erp.dto.responses.ApiResponse;
import com.digitaltravel.erp.entity.NhanVien;
import com.digitaltravel.erp.entity.TaiKhoan;
import com.digitaltravel.erp.entity.VaiTro;
import com.digitaltravel.erp.exception.AppException;
import com.digitaltravel.erp.repository.NhanVienRepository;
import com.digitaltravel.erp.repository.TaiKhoanRepository;
import com.digitaltravel.erp.repository.VaiTroRepository;
import com.digitaltravel.erp.service.MaTuDongService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/quan-tri")
@RequiredArgsConstructor
public class TaiKhoanAdminController {

    private final PasswordEncoder passwordEncoder;
    private final TaiKhoanRepository taiKhoanRepository;
    private final VaiTroRepository vaiTroRepository;
    private final NhanVienRepository nhanVienRepository;
    private final MaTuDongService maTuDongService;



    @PostMapping("/dang-ky-nhan-vien")
    @Transactional
    public ResponseEntity<ApiResponse<Void>> dangKyNhanVien(@Valid @RequestBody DangKyNhanVienRequest request) {
        String maVaiTro = request.getMaVaiTro().toUpperCase();

        if (!VaiTroConst.VAI_TRO_NHAN_VIEN.contains(maVaiTro)) {
            throw AppException.badRequest(VaiTroConst.VAI_TRO_NHAN_VIEN_MSG);
        }
        if (taiKhoanRepository.existsByTenDangNhap(request.getTenDangNhap())) {
            throw AppException.badRequest("Tên đăng nhập đã tồn tại");
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()
                && taiKhoanRepository.existsByEmail(request.getEmail())) {
            throw AppException.badRequest("Email đã được sử dụng");
        }

        VaiTro vaiTro = vaiTroRepository.findById(maVaiTro)
                .orElseThrow(() -> AppException.notFound("Khong tim thay vai tro: " + maVaiTro));

        TaiKhoan taiKhoan = new TaiKhoan();
        taiKhoan.setMaTaiKhoan(maTuDongService.taoMaTaiKhoanTheoVaiTro(maVaiTro));
        taiKhoan.setTenDangNhap(request.getTenDangNhap());
        taiKhoan.setMatKhau(passwordEncoder.encode(request.getMatKhau()));
        taiKhoan.setHoTen(request.getHoTen());
        taiKhoan.setEmail(request.getEmail());
        taiKhoan.setSoDienThoai(request.getSoDienThoai());
        taiKhoan.setVaiTro(vaiTro);
        taiKhoan.setTrangThai("HOAT_DONG");
        taiKhoanRepository.save(taiKhoan);

        NhanVien nhanVien = new NhanVien();
        nhanVien.setMaNhanVien(maTuDongService.taoMaNhanVien());
        nhanVien.setTaiKhoan(taiKhoan);
        nhanVien.setLoaiNhanVien(maVaiTro);
        nhanVien.setTrangThaiLamViec("HOAT_DONG");
        nhanVienRepository.save(nhanVien);

        return ResponseEntity.status(201).body(ApiResponse.noContent("Tao tai khoan nhan vien thanh cong"));
    }
}
