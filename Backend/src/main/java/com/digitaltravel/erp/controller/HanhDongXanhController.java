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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.digitaltravel.erp.dto.requests.HanhDongXanhRequest;
import com.digitaltravel.erp.dto.responses.ApiResponse;
import com.digitaltravel.erp.dto.responses.HanhDongXanhResponse;
import com.digitaltravel.erp.service.HanhDongXanhService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/san-pham/hanh-dong-xanh")
@RequiredArgsConstructor
public class HanhDongXanhController {

    private final HanhDongXanhService hanhDongXanhService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<HanhDongXanhResponse>>> danhSach(
            @RequestParam(required = false) String maTourThucTe
    ) {
        return ResponseEntity.ok(ApiResponse.ok(hanhDongXanhService.danhSach(maTourThucTe)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HanhDongXanhResponse>> chiTiet(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(hanhDongXanhService.chiTiet(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SANPHAM', 'ADMIN')")
    public ResponseEntity<ApiResponse<HanhDongXanhResponse>> taoMoi(@Valid @RequestBody HanhDongXanhRequest request) {
        HanhDongXanhResponse result = hanhDongXanhService.taoMoi(request);
        return ResponseEntity.status(201).body(ApiResponse.created(result));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SANPHAM', 'ADMIN')")
    public ResponseEntity<ApiResponse<HanhDongXanhResponse>> capNhat(
            @PathVariable String id,
            @Valid @RequestBody HanhDongXanhRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Cap nhat hanh dong xanh thanh cong",
                hanhDongXanhService.capNhat(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SANPHAM', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> xoa(@PathVariable String id) {
        hanhDongXanhService.xoa(id);
        return ResponseEntity.ok(ApiResponse.noContent("Xoa hanh dong xanh thanh cong"));
    }
}
