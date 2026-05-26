package com.digitaltravel.erp.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import com.digitaltravel.erp.dto.responses.ApiResponse;
import com.digitaltravel.erp.dto.responses.DanhGiaKhResponse;
import com.digitaltravel.erp.dto.responses.TourCongKhaiResponse;
import com.digitaltravel.erp.dto.responses.TourThucTeResponse;
import com.digitaltravel.erp.dto.responses.DichVuThemResponse;
import com.digitaltravel.erp.dto.responses.HanhDongXanhResponse;
import com.digitaltravel.erp.service.DanhGiaService;
import com.digitaltravel.erp.service.TourThucTeService;
import com.digitaltravel.erp.service.DichVuThemService;
import com.digitaltravel.erp.service.HanhDongXanhService;

import lombok.RequiredArgsConstructor;

/**
 * UC25 — Xem danh sách tour công khai (không cần đăng nhập)
 * UC26 — Xem chi tiết tour kèm lịch trình + đánh giá (không cần đăng nhập)
 */
@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class TourCongKhaiController {

    private final TourThucTeService tourThucTeService;
    private final DanhGiaService danhGiaService;
    private final DichVuThemService dichVuThemService;
    private final HanhDongXanhService hanhDongXanhService;

    // ── UC25: Danh sách tour đang mở bán ─────────────────────────────────────
    @GetMapping("/tour")
    public ResponseEntity<ApiResponse<Page<TourThucTeResponse>>> danhSachTour(
            @RequestParam(required = false) java.math.BigDecimal giaTu,
            @RequestParam(required = false) java.math.BigDecimal giaDen,
            @RequestParam(required = false) Integer thoiLuongMin,
            @RequestParam(required = false) Integer thoiLuongMax,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(
                tourThucTeService.danhSachCongKhai(giaTu, giaDen, thoiLuongMin, thoiLuongMax, pageable)));
    }

    // ── UC26: Chi tiết tour + lịch trình ─────────────────────────────────────
    @GetMapping("/tour/{maTourThucTe}")
    public ResponseEntity<ApiResponse<TourCongKhaiResponse>> chiTietTour(
            @PathVariable String maTourThucTe) {
        return ResponseEntity.ok(ApiResponse.ok(tourThucTeService.chiTietCongKhai(maTourThucTe)));
    }

    // ── UC26: Đánh giá của tour ───────────────────────────────────────────────
    @GetMapping("/tour/{maTourThucTe}/danh-gia")
    public ResponseEntity<ApiResponse<Page<DanhGiaKhResponse>>> danhGiaTour(
            @PathVariable String maTourThucTe,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(danhGiaService.danhSachDanhGia(maTourThucTe, pageable)));
    }

    // ── UC26: Hành động xanh của tour ─────────────────────────────────────────
    @GetMapping("/tour/{maTourThucTe}/hanh-dong-xanh")
    public ResponseEntity<ApiResponse<List<HanhDongXanhResponse>>> hanhDongXanhTour(
            @PathVariable String maTourThucTe) {
        return ResponseEntity.ok(ApiResponse.ok(hanhDongXanhService.danhSach(maTourThucTe)));
    }

    // ── UC26: Dịch vụ bổ sung của tour ─────────────────────────────────────
    @GetMapping("/tour/{maTourThucTe}/dich-vu-them")
    public ResponseEntity<ApiResponse<List<DichVuThemResponse>>> dichVuThemTour(
            @PathVariable String maTourThucTe) {
        return ResponseEntity.ok(ApiResponse.ok(dichVuThemService.danhSach(maTourThucTe)));
    }
}
