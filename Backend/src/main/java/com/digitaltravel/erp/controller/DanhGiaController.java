package com.digitaltravel.erp.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.digitaltravel.erp.config.TaiKhoanDetails;
import com.digitaltravel.erp.dto.requests.DanhGiaRequest;
import com.digitaltravel.erp.dto.responses.ApiResponse;
import com.digitaltravel.erp.dto.responses.DanhGiaKhResponse;
import com.digitaltravel.erp.service.DanhGiaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * UC35 — Khách hàng gửi đánh giá tour sau khi kết thúc
 */
@RestController
@RequiredArgsConstructor
public class DanhGiaController {

    private final DanhGiaService danhGiaService;

    // ── UC35: KH gửi đánh giá ────────────────────────────────────────────────
    @PostMapping("/api/khach-hang/danh-gia")
    @PreAuthorize("hasAnyRole('KHACHHANG', 'ADMIN')")
    public ResponseEntity<ApiResponse<DanhGiaKhResponse>> guiDanhGia(
            @AuthenticationPrincipal TaiKhoanDetails user,
            @Valid @RequestBody DanhGiaRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(
                danhGiaService.guiDanhGia(user.getTaiKhoan().getMaTaiKhoan(), request)));
    }

    // ── Admin: Xem tất cả đánh giá (moderation) ───────────────────────────────
    @GetMapping("/api/kinh-doanh/danh-gia")
    @PreAuthorize("hasAnyRole('KINHDOANH', 'ADMIN')")
    public ResponseEntity<ApiResponse<Page<DanhGiaKhResponse>>> tatCaDanhGia(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(danhGiaService.tatCaDanhGia(pageable)));
    }
}

