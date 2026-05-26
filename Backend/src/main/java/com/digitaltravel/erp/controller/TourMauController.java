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
import com.digitaltravel.erp.dto.requests.CapNhatTourMauRequest;
import com.digitaltravel.erp.dto.requests.LichTrinhRequest;
import com.digitaltravel.erp.dto.requests.TaoTourMauRequest;
import com.digitaltravel.erp.dto.responses.ApiResponse;
import com.digitaltravel.erp.dto.responses.LichTrinhResponse;
import com.digitaltravel.erp.dto.responses.TourMauChiTietResponse;
import com.digitaltravel.erp.dto.responses.TourMauResponse;
import com.digitaltravel.erp.service.TourMauService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/san-pham/tour-mau")
@RequiredArgsConstructor
public class TourMauController {

    private final TourMauService tourMauService;

    // UC06: Danh sách tour mẫu (filter + phân trang)
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SANPHAM', 'KINHDOANH', 'DIEUHANH', 'KETOAN', 'HDV')")
    public ResponseEntity<ApiResponse<Page<TourMauResponse>>> danhSach(
            @RequestParam(required = false) String tieuDe,
            @RequestParam(required = false) Integer thoiLuongMin,
            @RequestParam(required = false) Integer thoiLuongMax,
            @PageableDefault(size = 10, sort = "MaTourMau", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<TourMauResponse> result = tourMauService.danhSach(tieuDe, thoiLuongMin, thoiLuongMax, pageable);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    // UC06: Chi tiết 1 tour mẫu + lịch trình
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SANPHAM', 'KINHDOANH', 'DIEUHANH', 'KETOAN', 'HDV')")
    public ResponseEntity<ApiResponse<TourMauChiTietResponse>> chiTiet(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(tourMauService.chiTiet(id)));
    }

    // UC02: Thêm tour mẫu
    @PostMapping
    @PreAuthorize("hasAnyRole('SANPHAM', 'ADMIN')")
    public ResponseEntity<ApiResponse<TourMauChiTietResponse>> taoMoi(
            @Valid @RequestBody TaoTourMauRequest request,
            @AuthenticationPrincipal TaiKhoanDetails user) {
        TourMauChiTietResponse result = tourMauService.taoMoi(request, user.getUsername());
        return ResponseEntity.status(201).body(ApiResponse.created(result));
    }

    // UC04: Sửa tour mẫu
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SANPHAM', 'ADMIN')")
    public ResponseEntity<ApiResponse<TourMauResponse>> capNhat(
            @PathVariable String id,
            @Valid @RequestBody CapNhatTourMauRequest request,
            @AuthenticationPrincipal TaiKhoanDetails user) {
        return ResponseEntity
                .ok(ApiResponse.ok("Cap nhat thanh cong", tourMauService.capNhat(id, request, user.getUsername())));
    }

    // UC05: Xóa mềm tour mẫu
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SANPHAM', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> xoa(@PathVariable String id) {
        tourMauService.xoaMem(id);
        return ResponseEntity.ok(ApiResponse.noContent("Xoa tour mau thanh cong (soft delete)"));
    }

    // UC03: Sao chép tour mẫu
    @PostMapping("/{id}/sao-chep")
    @PreAuthorize("hasAnyRole('SANPHAM', 'ADMIN')")
    public ResponseEntity<ApiResponse<TourMauChiTietResponse>> saoChep(
            @PathVariable String id,
            @AuthenticationPrincipal TaiKhoanDetails user) {
        TourMauChiTietResponse result = tourMauService.saoChep(id, user.getUsername());
        return ResponseEntity.status(201).body(ApiResponse.created(result));
    }

    // UC08: Thêm ngày lịch trình
    @PostMapping("/{id}/lich-trinh")
    @PreAuthorize("hasAnyRole('SANPHAM', 'ADMIN')")
    public ResponseEntity<ApiResponse<LichTrinhResponse>> themLichTrinh(
            @PathVariable String id,
            @Valid @RequestBody LichTrinhRequest request) {
        LichTrinhResponse result = tourMauService.themLichTrinh(id, request);
        return ResponseEntity.status(201).body(ApiResponse.created(result));
    }

    // UC09: Sửa lịch trình
    @PutMapping("/{id}/lich-trinh/{maLichTrinh}")
    @PreAuthorize("hasAnyRole('SANPHAM', 'ADMIN')")
    public ResponseEntity<ApiResponse<LichTrinhResponse>> suaLichTrinh(
            @PathVariable String id,
            @PathVariable String maLichTrinh,
            @Valid @RequestBody LichTrinhRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Cap nhat lich trinh thanh cong",
                tourMauService.suaLichTrinh(id, maLichTrinh, request)));
    }

    // UC09: Xóa ngày lịch trình
    @DeleteMapping("/{id}/lich-trinh/{maLichTrinh}")
    @PreAuthorize("hasAnyRole('SANPHAM', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> xoaLichTrinh(
            @PathVariable String id,
            @PathVariable String maLichTrinh) {
        tourMauService.xoaLichTrinh(id, maLichTrinh);
        return ResponseEntity.ok(ApiResponse.noContent("Xoa lich trinh thanh cong"));
    }
}
