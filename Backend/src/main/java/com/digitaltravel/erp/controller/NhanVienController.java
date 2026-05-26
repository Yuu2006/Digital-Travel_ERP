package com.digitaltravel.erp.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.digitaltravel.erp.config.TaiKhoanDetails;
import com.digitaltravel.erp.dto.requests.BoSungQuyetToanRequest;
import com.digitaltravel.erp.dto.requests.BoSungRequest;
import com.digitaltravel.erp.dto.requests.BoSungQuyetToanRequest;
import com.digitaltravel.erp.dto.requests.GanVaiTroRequest;
import com.digitaltravel.erp.dto.requests.NangLucRequest;
import com.digitaltravel.erp.dto.requests.XuLyHoTroRequest;
import com.digitaltravel.erp.dto.requests.XuLyYeuCauRequest;
import com.digitaltravel.erp.dto.responses.ApiResponse;
import com.digitaltravel.erp.dto.responses.DonDatTourResponse;
import com.digitaltravel.erp.dto.responses.HoChieuSoResponse;
import com.digitaltravel.erp.dto.responses.NangLucResponse;
import com.digitaltravel.erp.dto.responses.NhanVienResponse;
import com.digitaltravel.erp.dto.responses.PhanCongResponse;
import com.digitaltravel.erp.dto.responses.QuyetToanResponse;
import com.digitaltravel.erp.dto.responses.YeuCauHoTroResponse;
import com.digitaltravel.erp.service.DatTourService;
import com.digitaltravel.erp.service.HanhDongXanhService;
import com.digitaltravel.erp.service.HuyTourService;
import com.digitaltravel.erp.service.NangLucService;
import com.digitaltravel.erp.service.NhanVienService;
import com.digitaltravel.erp.service.PhanCongTourService;
import com.digitaltravel.erp.service.QuyetToanService;
import com.digitaltravel.erp.service.YeuCauHoTroService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class NhanVienController {

    private final NhanVienService nhanVienService;
    private final HuyTourService huyTourService;
    private final PhanCongTourService phanCongTourService;
    private final DatTourService datTourService;
    private final YeuCauHoTroService yeuCauHoTroService;
    private final NangLucService nangLucService;
    private final HanhDongXanhService hanhDongXanhService;
    private final QuyetToanService quyetToanService;

    // ──────────────── QUẢN LÝ NHÂN VIÊN (ADMIN/MANAGER) ──────────────────

    /**
     * UC68 — Danh sách nhân viên (filter + phân trang)
     */
    @GetMapping("/api/quan-tri/nhan-vien")
    @PreAuthorize("hasAnyRole('DIEUHANH', 'ADMIN')")
    public ResponseEntity<ApiResponse<Page<NhanVienResponse>>> danhSachNhanVien(
            @RequestParam(required = false) String hoTen,
            @RequestParam(required = false) String maVaiTro,
            @RequestParam(required = false) String trangThai,
            @PageableDefault(size = 10, sort = "MaNhanVien", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(
                nhanVienService.timKiem(hoTen, maVaiTro, trangThai, pageable)));
    }

    /**
     * UC68 — Chi tiết nhân viên
     */
    @GetMapping("/api/quan-tri/nhan-vien/{maNhanVien}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SANPHAM', 'KINHDOANH', 'DIEUHANH', 'KETOAN', 'HDV')")
    public ResponseEntity<ApiResponse<NhanVienResponse>> chiTietNhanVien(
            @PathVariable String maNhanVien) {
        return ResponseEntity.ok(ApiResponse.ok(nhanVienService.chiTiet(maNhanVien)));
    }

    /**
     * UC66 — Khóa tài khoản nhân viên
     */
    @PutMapping("/api/quan-tri/nhan-vien/{maNhanVien}/khoa")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> khoaTaiKhoan(
            @PathVariable String maNhanVien,
            @AuthenticationPrincipal TaiKhoanDetails user) {
        nhanVienService.khoaTaiKhoan(maNhanVien, user.getTaiKhoan().getMaTaiKhoan());
        return ResponseEntity.ok(ApiResponse.noContent("Khoa tai khoan nhan vien thanh cong"));
    }

    /**
     * UC67 — Mở khóa tài khoản nhân viên
     */
    @PutMapping("/api/quan-tri/nhan-vien/{maNhanVien}/mo-khoa")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> moKhoaTaiKhoan(@PathVariable String maNhanVien) {
        nhanVienService.moKhoaTaiKhoan(maNhanVien);
        return ResponseEntity.ok(ApiResponse.noContent("Mo khoa tai khoan nhan vien thanh cong"));
    }

    // ──────────────── XỬ LÝ YÊU CẦU HỦY TOUR (SALES/MANAGER) ───────────

    /**
     * UC33 — Danh sách yêu cầu hủy/hoàn tiền
     */
    @GetMapping("/api/kinh-doanh/yeu-cau-huy")
    @PreAuthorize("hasAnyRole('KINHDOANH', 'ADMIN')")
    public ResponseEntity<ApiResponse<Page<YeuCauHoTroResponse>>> danhSachYeuCau(
            @RequestParam(required = false) String loaiYeuCau,
            @RequestParam(required = false) String trangThai,
            @PageableDefault(size = 10, sort = "MaYeuCauHoTro", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(
                huyTourService.danhSachYeuCau(loaiYeuCau, trangThai, pageable)));
    }

    /**
     * UC33 — SALES duyệt yêu cầu hủy tour
     */
    @PostMapping("/api/kinh-doanh/yeu-cau-huy/{maYeuCau}/duyet")
    @PreAuthorize("hasAnyRole('KINHDOANH', 'ADMIN')")
    public ResponseEntity<ApiResponse<YeuCauHoTroResponse>> duyetHuyTour(
            @PathVariable String maYeuCau,
            @AuthenticationPrincipal TaiKhoanDetails user,
            @Valid @RequestBody(required = false) XuLyYeuCauRequest request) {
        String maNhanVien = user.getTaiKhoan().getMaTaiKhoan();
        YeuCauHoTroResponse result = huyTourService.duyetHuyTour(
                maYeuCau, maNhanVien, request != null ? request : new XuLyYeuCauRequest());
        return ResponseEntity.ok(ApiResponse.ok("Duyet huy tour thanh cong", result));
    }

    /**
     * UC33 — SALES từ chối yêu cầu hủy tour
     */
    @PostMapping("/api/kinh-doanh/yeu-cau-huy/{maYeuCau}/tu-choi")
    @PreAuthorize("hasAnyRole('KINHDOANH', 'ADMIN')")
    public ResponseEntity<ApiResponse<YeuCauHoTroResponse>> tuChoiHuyTour(
            @PathVariable String maYeuCau,
            @AuthenticationPrincipal TaiKhoanDetails user,
            @Valid @RequestBody(required = false) XuLyYeuCauRequest request) {
        String maNhanVien = user.getTaiKhoan().getMaTaiKhoan();
        YeuCauHoTroResponse result = huyTourService.tuChoiHuyTour(
                maYeuCau, maNhanVien, request != null ? request : new XuLyYeuCauRequest());
        return ResponseEntity.ok(ApiResponse.ok("Tu choi yeu cau huy tour thanh cong", result));
    }

    // ──────────────── HDV: XEM TOUR ĐƯỢC GIAO ────────────────────────────

    /**
     * HDV xem danh sách tour được phân công
     */
    @GetMapping("/api/huong-dan-vien/tour-cua-toi")
    @PreAuthorize("hasAnyRole('HDV', 'DIEUHANH', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<PhanCongResponse>>> tourCuaToi(
            @AuthenticationPrincipal TaiKhoanDetails user) {
        String maTaiKhoan = user.getTaiKhoan().getMaTaiKhoan();
        return ResponseEntity.ok(ApiResponse.ok(phanCongTourService.tourCuaToi(maTaiKhoan)));
    }

    @PostMapping("/api/huong-dan-vien/phan-cong/{maPhanCong}/dong-y")
    @PreAuthorize("hasAnyRole('HDV', 'ADMIN')")
    public ResponseEntity<ApiResponse<PhanCongResponse>> dongYPhanCong(
            @PathVariable String maPhanCong,
            @AuthenticationPrincipal TaiKhoanDetails user) {
        String maTaiKhoan = user.getTaiKhoan().getMaTaiKhoan();
        return ResponseEntity.ok(ApiResponse.ok("Da dong y phan cong",
                phanCongTourService.dongYPhanCong(maPhanCong, maTaiKhoan)));
    }

    @PostMapping("/api/huong-dan-vien/phan-cong/{maPhanCong}/tu-choi")
    @PreAuthorize("hasAnyRole('HDV', 'ADMIN')")
    public ResponseEntity<ApiResponse<PhanCongResponse>> tuChoiPhanCong(
            @PathVariable String maPhanCong,
            @AuthenticationPrincipal TaiKhoanDetails user) {
        String maTaiKhoan = user.getTaiKhoan().getMaTaiKhoan();
        return ResponseEntity.ok(ApiResponse.ok("Da tu choi phan cong",
                phanCongTourService.tuChoiPhanCong(maPhanCong, maTaiKhoan)));
    }

    /**
     * HDV xem danh sách hành động xanh (danh mục)
     */
    @GetMapping("/api/huong-dan-vien/hanh-dong-xanh")
    @PreAuthorize("hasAnyRole('HDV', 'DIEUHANH', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<com.digitaltravel.erp.dto.responses.HanhDongXanhResponse>>> danhSachHanhDongXanh(
            @RequestParam(required = false) String maTourThucTe) {
        return ResponseEntity.ok(ApiResponse.ok(hanhDongXanhService.danhSach(maTourThucTe)));
    }

    // ──────────────── UC39: HDV xem lịch công tác ─────────────────────────

    /**
     * Xem lịch công tác của hướng dẫn viên.
     */
    @GetMapping({"/api/huong-dan-vien/lich-cong-tac", "/api/dieu-hanh/lich-cong-tac"})
    @PreAuthorize("hasAnyRole('HDV', 'DIEUHANH', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<PhanCongResponse>>> lichCongTac(
            @AuthenticationPrincipal TaiKhoanDetails user) {
        String maTaiKhoan = user.getTaiKhoan().getMaTaiKhoan();
        return ResponseEntity.ok(ApiResponse.ok(phanCongTourService.tourCuaToi(maTaiKhoan)));
    }

    /**
     * Điều hành xem lịch công tác của 1 HDV cụ thể
     */
    @GetMapping("/api/dieu-hanh/nhan-vien/{maNhanVien}/lich-cong-tac")
    @PreAuthorize("hasAnyRole('DIEUHANH', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<PhanCongResponse>>> lichCongTacNhanVien(
            @PathVariable String maNhanVien) {
        return ResponseEntity.ok(ApiResponse.ok(phanCongTourService.lichCongTacNhanVien(maNhanVien)));
    }

    // ──────────────── UC24: Tìm kiếm khách hàng ──────────────────────────

    /**
     * Tìm kiếm khách hàng cho kinh doanh.
     */
    @GetMapping("/api/kinh-doanh/khach-hang")
    @PreAuthorize("hasAnyRole('ADMIN', 'SANPHAM', 'KINHDOANH', 'DIEUHANH', 'KETOAN', 'HDV')")
    public ResponseEntity<ApiResponse<Page<HoChieuSoResponse>>> timKiemKhachHang(
            @RequestParam(required = false) String hoTen,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String soDienThoai,
            @RequestParam(required = false) String maVoucherChuaNhan,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(
                nhanVienService.timKiemKhachHang(hoTen, email, soDienThoai, maVoucherChuaNhan, pageable)));
    }

    /**
     * Xem chi tiết khách hàng.
     */
    @GetMapping("/api/kinh-doanh/khach-hang/{maKhachHang}")
    @PreAuthorize("hasAnyRole('KINHDOANH', 'ADMIN')")
    public ResponseEntity<ApiResponse<HoChieuSoResponse>> chiTietKhachHang(
            @PathVariable String maKhachHang) {
        return ResponseEntity.ok(ApiResponse.ok(nhanVienService.chiTietKhachHang(maKhachHang)));
    }

    // ──────────────── UC34: SALES xem đơn đặt tour ───────────────────────

    /**
     * Xem danh sách đơn đặt tour cho kinh doanh.
     */
    @GetMapping("/api/kinh-doanh/don-dat-tour")
    @PreAuthorize("hasAnyRole('KINHDOANH', 'ADMIN')")
    public ResponseEntity<ApiResponse<Page<DonDatTourResponse>>> danhSachDonDatTour(
            @RequestParam(required = false) String trangThai,
            @RequestParam(required = false) String maTourThucTe,
            @PageableDefault(size = 10, sort = "ngayDat", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(datTourService.danhSachTatCa(trangThai, maTourThucTe, pageable)));
    }

    // ──────────────── UC41: SALES xử lý yêu cầu hỗ trợ / khiếu nại ─────────

    /**
     * Xem danh sách yêu cầu hỗ trợ.
     */
    @GetMapping("/api/kinh-doanh/yeu-cau-ho-tro")
    @PreAuthorize("hasAnyRole('KINHDOANH', 'ADMIN')")
    public ResponseEntity<ApiResponse<Page<YeuCauHoTroResponse>>> danhSachYeuCauHoTro(
            @RequestParam(required = false) String loaiYeuCau,
            @RequestParam(required = false) String trangThai,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(
                yeuCauHoTroService.danhSachTatCa(loaiYeuCau, trangThai, pageable)));
    }

    /**
     * Xử lý yêu cầu hỗ trợ của khách hàng.
     */
    @PutMapping("/api/kinh-doanh/yeu-cau-ho-tro/{maYeuCau}")
    @PreAuthorize("hasAnyRole('KINHDOANH', 'ADMIN')")
    public ResponseEntity<ApiResponse<YeuCauHoTroResponse>> xuLyYeuCauHoTro(
            @PathVariable String maYeuCau,
            @Valid @RequestBody XuLyHoTroRequest request,
            @AuthenticationPrincipal TaiKhoanDetails user) {
        return ResponseEntity.ok(ApiResponse.ok("Cap nhat yeu cau thanh cong",
                yeuCauHoTroService.xuLy(maYeuCau, request, user.getTaiKhoan().getMaTaiKhoan())));
    }

    @PostMapping("/api/kinh-doanh/yeu-cau-ho-tro/{maYeuCau}/yeu-cau-hdv-giai-trinh")
    @PreAuthorize("hasAnyRole('KINHDOANH', 'ADMIN')")
    public ResponseEntity<ApiResponse<YeuCauHoTroResponse>> yeuCauHdvGiaiTrinh(
            @PathVariable String maYeuCau,
            @Valid @RequestBody BoSungRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Da gui yeu cau HDV giai trinh",
                yeuCauHoTroService.yeuCauHdvGiaiTrinh(maYeuCau, request.getNoiDung())));
    }

    @PostMapping("/api/kinh-doanh/yeu-cau-ho-tro/{maYeuCau}/yeu-cau-khach-hang-bo-sung")
    @PreAuthorize("hasAnyRole('KINHDOANH', 'ADMIN')")
    public ResponseEntity<ApiResponse<YeuCauHoTroResponse>> yeuCauKhachHangBoSung(
            @PathVariable String maYeuCau,
            @Valid @RequestBody BoSungRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Da gui yeu cau khach hang bo sung",
                yeuCauHoTroService.yeuCauKhachHangBoSung(maYeuCau, request.getNoiDung())));
    }

    @GetMapping("/api/huong-dan-vien/yeu-cau-giai-trinh")
    @PreAuthorize("hasAnyRole('HDV', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<YeuCauHoTroResponse>>> danhSachYeuCauGiaiTrinh(
            @AuthenticationPrincipal TaiKhoanDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(
                yeuCauHoTroService.danhSachGiaiTrinhCuaHdv(user.getTaiKhoan().getMaTaiKhoan())));
    }

    @PutMapping("/api/huong-dan-vien/yeu-cau-giai-trinh/{maYeuCau}")
    @PreAuthorize("hasAnyRole('HDV', 'ADMIN')")
    public ResponseEntity<ApiResponse<YeuCauHoTroResponse>> hdvCapNhatGiaiTrinh(
            @PathVariable String maYeuCau,
            @Valid @RequestBody BoSungRequest request,
            @AuthenticationPrincipal TaiKhoanDetails user) {
        return ResponseEntity.ok(ApiResponse.ok("Da gui giai trinh den admin",
                yeuCauHoTroService.hdvCapNhatGiaiTrinh(
                        user.getTaiKhoan().getMaTaiKhoan(), maYeuCau, request.getNoiDung())));
    }

    @GetMapping("/api/huong-dan-vien/quyet-toan/can-bo-sung")
    @PreAuthorize("hasAnyRole('HDV', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<QuyetToanResponse>>> danhSachQuyetToanCanBoSung(
            @AuthenticationPrincipal TaiKhoanDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(
                quyetToanService.danhSachCanBoSungCuaHdv(user.getTaiKhoan().getMaTaiKhoan())));
    }

    @PutMapping("/api/huong-dan-vien/quyet-toan/{maQuyetToan}/bo-sung")
    @PreAuthorize("hasAnyRole('HDV', 'ADMIN')")
    public ResponseEntity<ApiResponse<QuyetToanResponse>> hdvBoSungQuyetToan(
            @PathVariable String maQuyetToan,
            @Valid @RequestBody BoSungQuyetToanRequest request,
            @AuthenticationPrincipal TaiKhoanDetails user) {
        return ResponseEntity.ok(ApiResponse.ok("Da gui bo sung quyet toan den admin",
                quyetToanService.hdvBoSung(
                        user.getTaiKhoan().getMaTaiKhoan(),
                        maQuyetToan,
                        request.getGhiChu(),
                        request.getHoaDonAnh())));
    }

    // ──────────────── UC63: Năng lực & Hồ sơ HDV ───────────────────────────

    /**
     * Xem hồ sơ cá nhân của hướng dẫn viên.
     */
    @GetMapping("/api/huong-dan-vien/ho-so")
    @PreAuthorize("hasAnyRole('HDV', 'DIEUHANH', 'ADMIN')")
    public ResponseEntity<ApiResponse<NhanVienResponse>> hoSoCaNhan(
            @AuthenticationPrincipal TaiKhoanDetails user) {
        String maTaiKhoan = user.getTaiKhoan().getMaTaiKhoan();
        return ResponseEntity.ok(ApiResponse.ok(nhanVienService.layHoSoCaNhan(maTaiKhoan)));
    }

    /**
     * Xem năng lực của hướng dẫn viên hiện tại.
     */
    @GetMapping("/api/huong-dan-vien/nang-luc")
    @PreAuthorize("hasAnyRole('HDV', 'DIEUHANH', 'ADMIN')")
    public ResponseEntity<ApiResponse<NangLucResponse>> nangLucCuaToi(
            @AuthenticationPrincipal TaiKhoanDetails user) {
        String maTaiKhoan = user.getTaiKhoan().getMaTaiKhoan();
        // Cần lấy maNhanVien từ taiKhoan
        com.digitaltravel.erp.entity.NhanVien nv = nhanVienService.findNhanVienByTaiKhoan(maTaiKhoan);
        return ResponseEntity.ok(ApiResponse.ok(nangLucService.layNangLuc(nv.getMaNhanVien())));
    }

    /**
     * Xem năng lực của nhân viên.
     */
    @GetMapping("/api/dieu-hanh/nhan-vien/{maNhanVien}/nang-luc")
    @PreAuthorize("hasAnyRole('DIEUHANH', 'ADMIN')")
    public ResponseEntity<ApiResponse<NangLucResponse>> nangLucNhanVien(
            @PathVariable String maNhanVien) {
        return ResponseEntity.ok(ApiResponse.ok(nangLucService.layNangLuc(maNhanVien)));
    }

    /**
     * Cập nhật năng lực của nhân viên.
     */
    @PutMapping("/api/dieu-hanh/nhan-vien/{maNhanVien}/nang-luc")
    @PreAuthorize("hasAnyRole('DIEUHANH', 'ADMIN')")
    public ResponseEntity<ApiResponse<NangLucResponse>> capNhatNangLuc(
            @PathVariable String maNhanVien,
            @Valid @RequestBody NangLucRequest request,
            @AuthenticationPrincipal TaiKhoanDetails user) {
        return ResponseEntity.ok(ApiResponse.ok("Cap nhat nang luc thanh cong",
                nangLucService.capNhatNangLuc(maNhanVien, request, user.getUsername())));
    }

    // ──────────────── UC69: Gán vai trò ──────────────────────────────────

    /**
     * Gán vai trò cho nhân viên.
     */
    @PutMapping("/api/quan-tri/nhan-vien/{maNhanVien}/vai-tro")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<NhanVienResponse>> ganVaiTro(
            @PathVariable String maNhanVien,
            @Valid @RequestBody GanVaiTroRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Gan vai tro thanh cong",
                nhanVienService.ganVaiTro(maNhanVien, request)));
    }
}
