package com.digitaltravel.erp.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.digitaltravel.erp.config.TaiKhoanDetails;
import com.digitaltravel.erp.dto.requests.CapNhatHoChieuSoRequest;
import com.digitaltravel.erp.dto.requests.DatTourRequest;
import com.digitaltravel.erp.dto.requests.BoSungRequest;
import com.digitaltravel.erp.dto.requests.HuyTourRequest;
import com.digitaltravel.erp.dto.requests.YeuCauHoTroRequest;
import com.digitaltravel.erp.dto.responses.ApiResponse;
import com.digitaltravel.erp.dto.responses.DonDatTourResponse;
import com.digitaltravel.erp.dto.responses.HoChieuSoResponse;
import com.digitaltravel.erp.dto.responses.LichSuTourResponse;
import com.digitaltravel.erp.dto.responses.YeuCauHoTroResponse;
import com.digitaltravel.erp.service.DatTourService;
import com.digitaltravel.erp.service.HuyTourService;
import com.digitaltravel.erp.service.KhachHangService;
import com.digitaltravel.erp.service.YeuCauHoTroService;

import com.digitaltravel.erp.service.DichVuThemService;
import com.digitaltravel.erp.service.HanhDongXanhService;
import com.digitaltravel.erp.dto.responses.DichVuThemResponse;
import com.digitaltravel.erp.dto.responses.HanhDongXanhResponse;
import java.util.List;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/khach-hang")
@RequiredArgsConstructor
public class KhachHangController {

    private final KhachHangService khachHangService;
    private final DatTourService datTourService;
    private final HuyTourService huyTourService;
    private final YeuCauHoTroService yeuCauHoTroService;
    private final DichVuThemService dichVuThemService;
    private final HanhDongXanhService hanhDongXanhService;

    // ──────────────────────── HỒ SƠ KHÁCH HÀNG ───────────────────────────────

    // Xem hồ sơ của bản thân
    @GetMapping("/ho-so")
    @PreAuthorize("hasAnyRole('KHACHHANG', 'ADMIN')")
    public ResponseEntity<ApiResponse<HoChieuSoResponse>> layHoSo(
            @AuthenticationPrincipal TaiKhoanDetails user) {
        String maTaiKhoan = user.getTaiKhoan().getMaTaiKhoan();
        return ResponseEntity.ok(ApiResponse.ok(khachHangService.layHoSo(maTaiKhoan)));
    }

    // Cập nhật hồ sơ (chỉ DiUng và GhiChuYTe)
    @PutMapping("/ho-so")
    @PreAuthorize("hasAnyRole('KHACHHANG', 'ADMIN')")
    public ResponseEntity<ApiResponse<HoChieuSoResponse>> capNhatHoSo(
            @AuthenticationPrincipal TaiKhoanDetails user,
            @Valid @RequestBody CapNhatHoChieuSoRequest request) {
        String maTaiKhoan = user.getTaiKhoan().getMaTaiKhoan();
        return ResponseEntity.ok(ApiResponse.ok("Cập nhật hồ sơ thành công",
                khachHangService.capNhatHoSo(maTaiKhoan, request)));
    }

    // ──────────────────────── ĐẶT TOUR ───────────────────────────────────────

    // Tạo đơn đặt tour
    @PostMapping("/dat-tour")
    @PreAuthorize("hasAnyRole('KHACHHANG', 'ADMIN')")
    public ResponseEntity<ApiResponse<DonDatTourResponse>> datTour(
            @AuthenticationPrincipal TaiKhoanDetails user,
            @Valid @RequestBody DatTourRequest request) {
        String maTaiKhoan = user.getTaiKhoan().getMaTaiKhoan();
        DonDatTourResponse result = datTourService.datTour(maTaiKhoan, request);
        return ResponseEntity.status(201).body(ApiResponse.created(result));
    }

    // Danh sách đơn đặt tour của tôi
    @GetMapping("/dat-tour")
    @PreAuthorize("hasAnyRole('KHACHHANG', 'ADMIN')")
    public ResponseEntity<ApiResponse<Page<DonDatTourResponse>>> danhSachDatTour(
            @AuthenticationPrincipal TaiKhoanDetails user,
            @PageableDefault(size = 10, sort = "ngayDat", direction = Sort.Direction.DESC) Pageable pageable) {
        String maTaiKhoan = user.getTaiKhoan().getMaTaiKhoan();
        return ResponseEntity.ok(ApiResponse.ok(datTourService.danhSachCuaToi(maTaiKhoan, pageable)));
    }

    // Chi tiết đơn đặt tour của tôi
    @GetMapping("/dat-tour/{maDatTour}")
    @PreAuthorize("hasAnyRole('KHACHHANG', 'ADMIN')")
    public ResponseEntity<ApiResponse<DonDatTourResponse>> chiTietDatTour(
            @AuthenticationPrincipal TaiKhoanDetails user,
            @PathVariable String maDatTour) {
        String maTaiKhoan = user.getTaiKhoan().getMaTaiKhoan();
        return ResponseEntity.ok(ApiResponse.ok(datTourService.chiTietCuaToi(maTaiKhoan, maDatTour)));
    }

    // Hủy đơn đặt tour (chỉ khi ở trạng thái CHO_XAC_NHAN VA DA_XAC_NHAN)
    @DeleteMapping("/dat-tour/{maDatTour}")
    @PreAuthorize("hasAnyRole('KHACHHANG', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> huyDatTour(
            @AuthenticationPrincipal TaiKhoanDetails user,
            @PathVariable String maDatTour) {
        String maTaiKhoan = user.getTaiKhoan().getMaTaiKhoan();
        datTourService.huyDatTour(maTaiKhoan, maDatTour);
        return ResponseEntity.ok(ApiResponse.noContent("ủy đơn đặt tour thành công  (chờ nhân viên duyệt)"));
    }

    // UC32 — Yêu cầu hủy tour đã thanh toán (DA_XAC_NHAN) → tạo yêu cầu hoàn tiền
    @PostMapping("/dat-tour/{maDatTour}/huy")
    @PreAuthorize("hasAnyRole('KHACHHANG', 'ADMIN')")
    public ResponseEntity<ApiResponse<YeuCauHoTroResponse>> yeuCauHuyTour(
            @AuthenticationPrincipal TaiKhoanDetails user,
            @PathVariable String maDatTour,
            @Valid @RequestBody HuyTourRequest request) {
        String maTaiKhoan = user.getTaiKhoan().getMaTaiKhoan();
        YeuCauHoTroResponse result = huyTourService.yeuCauHuyTour(maTaiKhoan, maDatTour, request);
        return ResponseEntity.ok(ApiResponse.ok("Gửi yêu cầu hủy tour thành công. Đang chờ nhân viên duyệt.", result));
    }

    // ──────────────────────── NHÂN VIÊN SALES ─────────────────────────────────
    // Sales: xem tất cả đơn đặt (có filter)
    @GetMapping("/lich-su-tour")
    @PreAuthorize("hasAnyRole('KHACHHANG', 'ADMIN')")
    public ResponseEntity<ApiResponse<Page<LichSuTourResponse>>> lichSuTour(
            @AuthenticationPrincipal TaiKhoanDetails user,
            @PageableDefault(size = 10, sort = "NgayThamGia", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(
                khachHangService.lichSuTour(user.getTaiKhoan().getMaTaiKhoan(), pageable)));
    }

    // ──────────────────────── YÊU CẦU HỖ TRỢ (UC36) ─────────────────────────

    @PostMapping("/yeu-cau-ho-tro")
    @PreAuthorize("hasAnyRole('KHACHHANG', 'ADMIN')")
    public ResponseEntity<ApiResponse<YeuCauHoTroResponse>> taoYeuCau(
            @AuthenticationPrincipal TaiKhoanDetails user,
            @Valid @RequestBody YeuCauHoTroRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(
                yeuCauHoTroService.taoYeuCau(user.getTaiKhoan().getMaTaiKhoan(), request)));
    }

    @GetMapping("/yeu-cau-ho-tro")
    @PreAuthorize("hasAnyRole('KHACHHANG', 'ADMIN')")
    public ResponseEntity<ApiResponse<Page<YeuCauHoTroResponse>>> danhSachYeuCauCuaToi(
            @AuthenticationPrincipal TaiKhoanDetails user,
            @RequestParam(required = false) String loaiYeuCau,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(
                yeuCauHoTroService.danhSachCuaKhachHang(user.getTaiKhoan().getMaTaiKhoan(), loaiYeuCau, pageable)));
    }

    @GetMapping("/yeu-cau-ho-tro/can-bo-sung")
    @PreAuthorize("hasAnyRole('KHACHHANG', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<YeuCauHoTroResponse>>> danhSachYeuCauCanBoSung(
            @AuthenticationPrincipal TaiKhoanDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(
                yeuCauHoTroService.danhSachCanKhachHangBoSung(user.getTaiKhoan().getMaTaiKhoan())));
    }

    @PutMapping("/yeu-cau-ho-tro/{maYeuCau}/bo-sung")
    @PreAuthorize("hasAnyRole('KHACHHANG', 'ADMIN')")
    public ResponseEntity<ApiResponse<YeuCauHoTroResponse>> boSungYeuCau(
            @AuthenticationPrincipal TaiKhoanDetails user,
            @PathVariable String maYeuCau,
            @Valid @RequestBody BoSungRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(
                yeuCauHoTroService.boSung(user.getTaiKhoan().getMaTaiKhoan(), maYeuCau, request.getNoiDung())));
    }

    // ──────────────────────── DANH MỤC DỊCH VỤ & HÀNH ĐỘNG XANH (Cho Form Đặt
    // Tour) ─────────
    @GetMapping("/dich-vu-them")
    @PreAuthorize("hasAnyRole('KHACHHANG', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<DichVuThemResponse>>> danhSachDichVuThem(
            @RequestParam(required = false) String maTourThucTe) {
        return ResponseEntity.ok(ApiResponse.ok(dichVuThemService.danhSach(maTourThucTe)));
    }

    @GetMapping("/hanh-dong-xanh")
    @PreAuthorize("hasAnyRole('KHACHHANG', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<HanhDongXanhResponse>>> danhSachHanhDongXanh(
            @RequestParam(required = false) String maTourThucTe) {
        return ResponseEntity.ok(ApiResponse.ok(hanhDongXanhService.danhSach(maTourThucTe)));
    }
}
