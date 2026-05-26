package com.digitaltravel.erp.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digitaltravel.erp.config.TaiKhoanDetails;
import com.digitaltravel.erp.dto.requests.ApVoucherRequest;
import com.digitaltravel.erp.dto.requests.DoiDiemVoucherRequest;
import com.digitaltravel.erp.dto.responses.ApiResponse;
import com.digitaltravel.erp.dto.responses.KhuyenMaiKhResponse;
import com.digitaltravel.erp.dto.responses.VoucherResponse;
import com.digitaltravel.erp.service.VoucherService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * UC28 — Áp dụng voucher vào đơn đặt tour
 * UC30 — Đổi điểm xanh lấy voucher
 * UC31 — Xem ví voucher
 */
@RestController
@RequestMapping("/api/khach-hang")
@RequiredArgsConstructor
public class VoucherController {

    private final VoucherService voucherService;

    // ── UC31: Xem ví voucher của tôi ─────────────────────────────────────────
    @GetMapping("/vi-voucher")
    @PreAuthorize("hasAnyRole('KHACHHANG', 'ADMIN')")
    public ResponseEntity<ApiResponse<Page<KhuyenMaiKhResponse>>> viVoucher(
            @AuthenticationPrincipal TaiKhoanDetails user,
            @PageableDefault(size = 10, sort = "NgayNhan", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(
                voucherService.viVoucher(user.getTaiKhoan().getMaTaiKhoan(), pageable)));
    }

    // UC30: Danh sach voucher dang san sang de khach hang doi bang diem xanh
    @GetMapping("/voucher-co-the-doi")
    @PreAuthorize("hasAnyRole('KHACHHANG', 'ADMIN')")
    public ResponseEntity<ApiResponse<Page<VoucherResponse>>> voucherCoTheDoi(
            @PageableDefault(size = 20, sort = "NgayHetHan", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(voucherService.danhSachCoTheDoi(pageable)));
    }

    // ── UC28: Áp dụng voucher vào đơn ────────────────────────────────────────
    @PostMapping("/ap-voucher")
    @PreAuthorize("hasAnyRole('KHACHHANG', 'ADMIN')")
    public ResponseEntity<ApiResponse<VoucherResponse>> apVoucher(
            @AuthenticationPrincipal TaiKhoanDetails user,
            @Valid @RequestBody ApVoucherRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Ap dung voucher thanh cong",
                voucherService.apVoucher(user.getTaiKhoan().getMaTaiKhoan(), request)));
    }

    // ── UC30: Đổi điểm xanh lấy voucher ─────────────────────────────────────
    @PostMapping("/doi-diem")
    @PreAuthorize("hasAnyRole('KHACHHANG', 'ADMIN')")
    public ResponseEntity<ApiResponse<KhuyenMaiKhResponse>> doiDiem(
            @AuthenticationPrincipal TaiKhoanDetails user,
            @Valid @RequestBody DoiDiemVoucherRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Doi diem lay voucher thanh cong",
                voucherService.doiDiem(user.getTaiKhoan().getMaTaiKhoan(), request)));
    }
}
