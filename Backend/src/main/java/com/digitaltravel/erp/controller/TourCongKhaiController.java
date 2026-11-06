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

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class TourCongKhaiController {

    private final TourThucTeService tourThucTeService;
    private final DanhGiaService danhGiaService;
    private final DichVuThemService dichVuThemService;
    private final HanhDongXanhService hanhDongXanhService;

    @GetMapping("/tour")
    // UC25 - Tra cứu tour: Lọc danh sách tour công khai
    public ResponseEntity<ApiResponse<Page<TourThucTeResponse>>> danhSachTour(
            @RequestParam(required = false) java.math.BigDecimal giaTu,
            @RequestParam(required = false) java.math.BigDecimal giaDen,
            @RequestParam(required = false) Integer thoiLuongMin,
            @RequestParam(required = false) Integer thoiLuongMax,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(
                tourThucTeService.danhSachCongKhai(giaTu, giaDen, thoiLuongMin, thoiLuongMax, pageable)));
    }

    @GetMapping("/tour/{maTourThucTe}")
    // UC26 - Xem chi tiết tour: Lấy thông tin tour công khai
    public ResponseEntity<ApiResponse<TourCongKhaiResponse>> chiTietTour(
            @PathVariable String maTourThucTe) {
        return ResponseEntity.ok(ApiResponse.ok(tourThucTeService.chiTietCongKhai(maTourThucTe)));
    }

    @GetMapping("/tour/{maTourThucTe}/danh-gia")
    // UC35 - Đánh giá: Lấy phản hồi theo tour
    public ResponseEntity<ApiResponse<Page<DanhGiaKhResponse>>> danhGiaTour(
            @PathVariable String maTourThucTe,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(danhGiaService.danhSachDanhGia(maTourThucTe, pageable)));
    }

    @GetMapping("/tour/{maTourThucTe}/hanh-dong-xanh")
    // UC27 - Đặt tour: Lấy hành động xanh của tour
    public ResponseEntity<ApiResponse<List<HanhDongXanhResponse>>> hanhDongXanhTour(
            @PathVariable String maTourThucTe) {
        return ResponseEntity.ok(ApiResponse.ok(hanhDongXanhService.danhSach(maTourThucTe)));
    }

    @GetMapping("/tour/{maTourThucTe}/dich-vu-them")
    // UC27 - Đặt tour: Lấy dịch vụ thêm của tour
    public ResponseEntity<ApiResponse<List<DichVuThemResponse>>> dichVuThemTour(
            @PathVariable String maTourThucTe) {
        return ResponseEntity.ok(ApiResponse.ok(dichVuThemService.danhSach(maTourThucTe)));
    }
}
