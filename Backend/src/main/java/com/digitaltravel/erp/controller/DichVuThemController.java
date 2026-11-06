package com.digitaltravel.erp.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digitaltravel.erp.dto.requests.DichVuThemRequest;
import com.digitaltravel.erp.dto.responses.ApiResponse;
import com.digitaltravel.erp.dto.responses.DichVuThemResponse;
import com.digitaltravel.erp.service.DichVuThemService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/san-pham/dich-vu-them")
@RequiredArgsConstructor
public class DichVuThemController {

    private final DichVuThemService dichVuThemService;

    @GetMapping
    // UC19 - Tra cứu dịch vụ: Lấy danh mục dịch vụ bổ sung
    public ResponseEntity<ApiResponse<List<DichVuThemResponse>>> danhSach() {
        return ResponseEntity.ok(ApiResponse.ok(dichVuThemService.danhSach()));
    }

    @GetMapping("/{id}")
    // UC19 - Tra cứu dịch vụ: Xem chi tiết dịch vụ bổ sung
    public ResponseEntity<ApiResponse<DichVuThemResponse>> chiTiet(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(dichVuThemService.chiTiet(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SANPHAM', 'ADMIN')")
    // UC16 - Thêm dịch vụ: Tạo dịch vụ bổ sung
    public ResponseEntity<ApiResponse<DichVuThemResponse>> taoMoi(@Valid @RequestBody DichVuThemRequest request) {
        DichVuThemResponse result = dichVuThemService.taoMoi(request);
        return ResponseEntity.status(201).body(ApiResponse.created(result));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SANPHAM', 'ADMIN')")
    // UC17 - Sửa thông tin dịch vụ: Cập nhật dịch vụ bổ sung
    public ResponseEntity<ApiResponse<DichVuThemResponse>> capNhat(
            @PathVariable String id,
            @Valid @RequestBody DichVuThemRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Cap nhat dich vu thanh cong", dichVuThemService.capNhat(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SANPHAM', 'ADMIN')")
    // UC18 - Xóa dịch vụ: Loại bỏ dịch vụ bổ sung
    public ResponseEntity<ApiResponse<Void>> xoa(@PathVariable String id) {
        dichVuThemService.xoa(id);
        return ResponseEntity.ok(ApiResponse.noContent("Xoa dich vu thanh cong"));
    }
}
