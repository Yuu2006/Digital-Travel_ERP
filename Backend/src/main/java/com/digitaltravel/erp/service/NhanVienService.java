package com.digitaltravel.erp.service;

import java.util.Locale;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.digitaltravel.erp.config.VaiTroConst;
import com.digitaltravel.erp.dto.requests.GanVaiTroRequest;
import com.digitaltravel.erp.dto.responses.HoChieuSoResponse;
import com.digitaltravel.erp.dto.responses.NhanVienResponse;
import com.digitaltravel.erp.entity.NhanVien;
import com.digitaltravel.erp.entity.TaiKhoan;
import com.digitaltravel.erp.entity.VaiTro;
import com.digitaltravel.erp.exception.AppException;
import com.digitaltravel.erp.repository.HoChieuSoRepository;
import com.digitaltravel.erp.repository.NhanVienRepository;
import com.digitaltravel.erp.repository.TaiKhoanRepository;
import com.digitaltravel.erp.repository.VaiTroRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NhanVienService {



    private final NhanVienRepository nhanVienRepository;
    private final TaiKhoanRepository taiKhoanRepository;
    private final HoChieuSoRepository hoChieuSoRepository;
    private final VaiTroRepository vaiTroRepository;

    @Transactional(readOnly = true)
    // UC66 - Tìm kiếm tài khoản: Tra cứu nhân viên
    public Page<NhanVienResponse> timKiem(String hoTen, String maVaiTro, String trangThai, Pageable pageable) {
        return nhanVienRepository.timKiem(hoTen, maVaiTro, trangThai, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    // UC66 - Tìm kiếm tài khoản: Xem chi tiết nhân viên
    public NhanVienResponse chiTiet(String maNhanVien) {
        NhanVien nv = nhanVienRepository.findById(maNhanVien)
                .orElseThrow(() -> AppException.notFound("Khong tim thay nhan vien: " + maNhanVien));
        return toResponse(nv);
    }

    @Transactional
    // UC64 - Xóa/Khóa tài khoản: Khóa tài khoản nhân viên
    public void khoaTaiKhoan(String maNhanVien, String nguoiThucHien) {
        NhanVien nv = nhanVienRepository.findById(maNhanVien)
                .orElseThrow(() -> AppException.notFound("Khong tim thay nhan vien: " + maNhanVien));

        TaiKhoan tkNguoiThucHien = taiKhoanRepository.findById(nguoiThucHien).orElse(null);
        if (tkNguoiThucHien != null && nv.getTaiKhoan().getMaTaiKhoan().equals(nguoiThucHien)) {
            throw AppException.badRequest("Không thể tự khóa chính tài khoản của bạn.");
        }

        TaiKhoan tk = nv.getTaiKhoan();
        if ("KHOA".equals(tk.getTrangThai())) {
            throw AppException.badRequest("Tài khoản đã ở trạng thái KHOA.");
        }
        tk.setTrangThai("KHOA");
        taiKhoanRepository.save(tk);
    }

    @Transactional
    // UC65 - Mở khóa tài khoản: Mở khóa nhân viên
    public void moKhoaTaiKhoan(String maNhanVien) {
        NhanVien nv = nhanVienRepository.findById(maNhanVien)
                .orElseThrow(() -> AppException.notFound("Khong tim thay nhan vien: " + maNhanVien));

        TaiKhoan tk = nv.getTaiKhoan();
        if ("HOAT_DONG".equals(tk.getTrangThai())) {
            throw AppException.badRequest("Tài khoản đã ở trạng thái HOAT_DONG.");
        }
        tk.setTrangThai("HOAT_DONG");
        taiKhoanRepository.save(tk);
    }

    @Transactional
    // UC67 - Phân quyền truy cập: Gán vai trò nhân viên
    public NhanVienResponse ganVaiTro(String maNhanVien, GanVaiTroRequest request) {
        NhanVien nv = nhanVienRepository.findById(maNhanVien)
                .orElseThrow(() -> AppException.notFound("Khong tim thay nhan vien: " + maNhanVien));

        TaiKhoan tk = nv.getTaiKhoan();
        String maVaiTro = request.getMaVaiTro().trim().toUpperCase(Locale.ROOT);
        if (!VaiTroConst.VAI_TRO_NHAN_VIEN.contains(maVaiTro)) {
            throw AppException.badRequest(VaiTroConst.VAI_TRO_NHAN_VIEN_MSG);
        }
        VaiTro vaiTroHopLe = vaiTroRepository.findById(maVaiTro)
                .orElseThrow(() -> AppException.notFound("Khong tim thay vai tro: " + maVaiTro));

        tk.setVaiTro(vaiTroHopLe);
        nv.setLoaiNhanVien(maVaiTro);
        taiKhoanRepository.save(tk);
        nhanVienRepository.save(nv);
        return toResponse(nv);
    }

    // UC24 - Tra cứu khách hàng: Tìm hồ sơ khách hàng
    public Page<HoChieuSoResponse> timKiemKhachHang(String hoTen, String email, String soDienThoai,
                                                     String maVoucherChuaNhan, Pageable pageable) {
        boolean locKhachChuaNhanVoucher = maVoucherChuaNhan != null && !maVoucherChuaNhan.isBlank();

        return (locKhachChuaNhanVoucher
                ? hoChieuSoRepository.timKiemKhachHangChuaNhanVoucher(hoTen, email, soDienThoai, maVoucherChuaNhan, pageable)
                : hoChieuSoRepository.timKiemKhachHang(hoTen, email, soDienThoai, pageable))
                .map(this::toHoChieuSoResponse);
    }

    // UC24 - Tra cứu khách hàng: Xem chi tiết khách hàng
    public HoChieuSoResponse chiTietKhachHang(String maKhachHang) {
        return hoChieuSoRepository.findById(maKhachHang)
                .map(this::toHoChieuSoResponse)
                .orElseThrow(() -> AppException.notFound("Khong tim thay khach hang: " + maKhachHang));
    }

    // UC66 - Tìm kiếm tài khoản: Tìm nhân viên theo tài khoản
    public NhanVien findNhanVienByTaiKhoan(String maTaiKhoan) {
        return nhanVienRepository.findByMaTaiKhoan(maTaiKhoan)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy hồ sơ nhân viên"));
    }

    @Transactional(readOnly = true)
    // UC63 - Cập nhật năng lực nhân viên: Lấy hồ sơ cá nhân
    public NhanVienResponse layHoSoCaNhan(String maTaiKhoan) {
        NhanVien nv = findNhanVienByTaiKhoan(maTaiKhoan);
        return toResponse(nv);
    }

    // ── Mapper ────────────────────────────────────────────────────────────
    private NhanVienResponse toResponse(NhanVien nv) {
        TaiKhoan tk = nv.getTaiKhoan();
        if (tk == null) {
            return NhanVienResponse.builder()
                    .maNhanVien(nv.getMaNhanVien())
                    .trangThaiLamViec(nv.getTrangThaiLamViec())
                    .loaiNhanVien(nv.getLoaiNhanVien())
                    .ngayVaoLam(nv.getNgayVaoLam())
                    .build();
        }
        return NhanVienResponse.builder()
                .maNhanVien(nv.getMaNhanVien())
                .maTaiKhoan(tk.getMaTaiKhoan())
                .tenDangNhap(tk.getTenDangNhap())
                .hoTen(tk.getHoTen())
                .email(tk.getEmail())
                .soDienThoai(tk.getSoDienThoai())
                .maVaiTro(tk.getVaiTro() != null ? tk.getVaiTro().getMaVaiTro() : null)
                .trangThaiTaiKhoan(tk.getTrangThai())
                .trangThaiLamViec(nv.getTrangThaiLamViec())
                .loaiNhanVien(nv.getLoaiNhanVien())
                .ngayVaoLam(nv.getNgayVaoLam())
                .cccd(tk.getCccd())
                .ngaySinh(tk.getNgaySinh())
                .build();
    }

    private HoChieuSoResponse toHoChieuSoResponse(com.digitaltravel.erp.entity.HoChieuSo hcs) {
        TaiKhoan tk = hcs.getTaiKhoan();
        return HoChieuSoResponse.builder()
                .maKhachHang(hcs.getMaKhachHang())
                .maTaiKhoan(tk.getMaTaiKhoan())
                .tenDangNhap(tk.getTenDangNhap())
                .hoTen(tk.getHoTen())
                .email(tk.getEmail())
                .cccd(tk.getCccd())
                .ngaySinh(tk.getNgaySinh())
                .soDienThoai(tk.getSoDienThoai())
                .diUng(hcs.getDiUng())
                .ghiChuYTe(hcs.getGhiChuYTe())
                .hangThanhVien(hcs.getHangThanhVien())
                .diemXanh(hcs.getDiemXanh())
                .build();
    }
}
