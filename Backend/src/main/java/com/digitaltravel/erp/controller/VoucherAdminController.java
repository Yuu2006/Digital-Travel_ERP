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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digitaltravel.erp.config.TaiKhoanDetails;
import com.digitaltravel.erp.dto.requests.PhatHanhVoucherRequest;
import com.digitaltravel.erp.dto.requests.VoucherRequest;
import com.digitaltravel.erp.dto.responses.ApiResponse;
import com.digitaltravel.erp.dto.responses.KhuyenMaiKhResponse;
import com.digitaltravel.erp.dto.responses.VoucherResponse;
import com.digitaltravel.erp.service.VoucherService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * UC52 — Quản lý voucher
 * UC53 — Tạo voucher mới
 * UC54 — Phát hành voucher cho khách hàng
 */
@RestController
@RequestMapping("/api/kinh-doanh/voucher")
@RequiredArgsConstructor
public class VoucherAdminController {

    private final VoucherService voucherService;

    // ── Danh sách voucher ─────────────────────────────────────────────────────
    /**
     * Xem danh sách voucher quản trị.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('KINHDOANH', 'ADMIN')")
    public ResponseEntity<ApiResponse<Page<VoucherResponse>>> danhSach(
            @PageableDefault(size = 10, sort = "NgayHieuLuc", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(voucherService.danhSach(pageable)));
    }

    // ── Chi tiết voucher ──────────────────────────────────────────────────────
    /**
     * Xem chi tiết voucher.
     */
    @GetMapping("/{maVoucher}")
    @PreAuthorize("hasAnyRole('KINHDOANH', 'ADMIN')")
    public ResponseEntity<ApiResponse<VoucherResponse>> chiTiet(@PathVariable String maVoucher) {
        return ResponseEntity.ok(ApiResponse.ok(voucherService.chiTiet(maVoucher)));
    }

    // ── UC53: Tạo voucher mới ─────────────────────────────────────────────────
    /**
     * Tạo voucher mới.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('KINHDOANH', 'ADMIN')")
    public ResponseEntity<ApiResponse<VoucherResponse>> taoVoucher(
            @Valid @RequestBody VoucherRequest request,
            @AuthenticationPrincipal TaiKhoanDetails user) {
        return ResponseEntity.status(201).body(ApiResponse.created(
                voucherService.taoVoucher(request, user.getUsername())));
    }

    // ── UC52: Cập nhật / vô hiệu hóa voucher ──────────────────────────────────
    /**
     * Cập nhật thông tin voucher.
     */
    @PutMapping("/{maVoucher}")
    @PreAuthorize("hasAnyRole('KINHDOANH', 'ADMIN')")
    public ResponseEntity<ApiResponse<VoucherResponse>> capNhatVoucher(
            @PathVariable String maVoucher,
            @Valid @RequestBody VoucherRequest request,
            @AuthenticationPrincipal TaiKhoanDetails user) {
        return ResponseEntity.ok(ApiResponse.ok("Cap nhat voucher thanh cong",
                voucherService.capNhatVoucher(maVoucher, request, user.getUsername())));
    }

    @PutMapping({"/{maVoucher}/vo-hieu-hoa", "/{maVoucher}/vo-hieu"})
    @PreAuthorize("hasAnyRole('KINHDOANH', 'ADMIN')")
    public ResponseEntity<ApiResponse<VoucherResponse>> voHieuHoaVoucher(
            @PathVariable String maVoucher,
            @AuthenticationPrincipal TaiKhoanDetails user) {
        return ResponseEntity.ok(ApiResponse.ok("Vo hieu hoa voucher thanh cong",
                voucherService.voHieuHoaVoucher(maVoucher, user.getUsername())));
    }

    // ── UC54: Phát hành / thu hồi voucher cho khách hàng ──────────────────────
    /**
     * Phát hành voucher cho khách hàng.
     */
    @PostMapping("/{maVoucher}/phat-hanh")
    @PreAuthorize("hasAnyRole('KINHDOANH', 'ADMIN')")
    public ResponseEntity<ApiResponse<KhuyenMaiKhResponse>> phatHanh(
            @PathVariable String maVoucher,
            @Valid @RequestBody PhatHanhVoucherRequest request,
            @AuthenticationPrincipal TaiKhoanDetails user) {
        return ResponseEntity.status(201).body(ApiResponse.created(
                voucherService.phatHanhChoKhachHang(maVoucher, request, user.getUsername())));
    }

    @GetMapping("/{maVoucher}/khach-hang-da-phan-bo")
    @PreAuthorize("hasAnyRole('KINHDOANH', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<KhuyenMaiKhResponse>>> danhSachKhachHangDaPhanBo(
            @PathVariable String maVoucher) {
        return ResponseEntity.ok(ApiResponse.ok(voucherService.danhSachKhachHangDaPhanBo(maVoucher)));
    }

    @PutMapping("/{maVoucher}/khach-hang/{maKhachHang}/thu-hoi")
    @PreAuthorize("hasAnyRole('KINHDOANH', 'ADMIN')")
    public ResponseEntity<ApiResponse<KhuyenMaiKhResponse>> thuHoi(
            @PathVariable String maVoucher,
            @PathVariable String maKhachHang,
            @AuthenticationPrincipal TaiKhoanDetails user) {
        return ResponseEntity.ok(ApiResponse.ok("Thu hoi voucher thanh cong",
                voucherService.thuHoiVoucher(maVoucher, maKhachHang, user.getUsername())));
    }
}
