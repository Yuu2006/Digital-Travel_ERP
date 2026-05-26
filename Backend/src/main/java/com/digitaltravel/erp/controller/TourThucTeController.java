package com.digitaltravel.erp.controller;

import java.math.BigDecimal;

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
import com.digitaltravel.erp.dto.requests.CapNhatTourThucTeRequest;
import com.digitaltravel.erp.dto.requests.TaoTourThucTeRequest;
import com.digitaltravel.erp.dto.responses.ApiResponse;
import com.digitaltravel.erp.dto.responses.TourThucTeResponse;
import com.digitaltravel.erp.service.TourThucTeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/dieu-hanh/tour-thuc-te")
@RequiredArgsConstructor
public class TourThucTeController {

    private final TourThucTeService tourThucTeService;

    // UC14: Danh sach tour thuc te cho dieu hanh
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SANPHAM', 'KINHDOANH', 'DIEUHANH', 'KETOAN', 'HDV')")
    public ResponseEntity<ApiResponse<Page<TourThucTeResponse>>> danhSach(
            @RequestParam(required = false) String trangThai,
            @RequestParam(required = false) String maTourMau,
            @RequestParam(required = false) BigDecimal giaTu,
            @RequestParam(required = false) BigDecimal giaDen,
            @RequestParam(required = false) Integer thoiLuongMin,
            @RequestParam(required = false) Integer thoiLuongMax,
            @RequestParam(defaultValue = "false") boolean congKhai,
            @PageableDefault(size = 10, sort = "NgayKhoiHanh", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<TourThucTeResponse> result;
        if (congKhai) {
            // Cho khách hàng: chỉ tour MO_BAN, còn chỗ, ngày khởi hành > hôm nay
            result = tourThucTeService.danhSachCongKhai(giaTu, giaDen, thoiLuongMin, thoiLuongMax, pageable);
        } else {
            result = tourThucTeService.danhSach(trangThai, maTourMau, giaTu, giaDen, pageable);
        }
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    // UC14: Chi tiết tour thực tế
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SANPHAM', 'KINHDOANH', 'DIEUHANH', 'KETOAN', 'HDV')")
    public ResponseEntity<ApiResponse<TourThucTeResponse>> chiTiet(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(tourThucTeService.chiTiet(id)));
    }

    // UC11: Khởi tạo tour thực tế từ tour mẫu
    @PostMapping
    @PreAuthorize("hasAnyRole('SANPHAM', 'ADMIN')")
    public ResponseEntity<ApiResponse<TourThucTeResponse>> taoMoi(
            @Valid @RequestBody TaoTourThucTeRequest request,
            @AuthenticationPrincipal TaiKhoanDetails user) {
        TourThucTeResponse result = tourThucTeService.taoMoi(request, user.getUsername());
        return ResponseEntity.status(201).body(ApiResponse.created(result));
    }

    // UC13: Sửa tour thực tế
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SANPHAM', 'ADMIN')")
    public ResponseEntity<ApiResponse<TourThucTeResponse>> capNhat(
            @PathVariable String id,
            @Valid @RequestBody CapNhatTourThucTeRequest request,
            @AuthenticationPrincipal TaiKhoanDetails user) {
        return ResponseEntity
                .ok(ApiResponse.ok("Cap nhat thanh cong", tourThucTeService.capNhat(id, request, user.getUsername())));
    }

    // UC12: Xóa tour thực tế (soft delete → HUY)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SANPHAM', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> xoa(@PathVariable String id) {
        tourThucTeService.xoa(id);
        return ResponseEntity.ok(ApiResponse.noContent("Huy tour thuc te thanh cong"));
    }
}
