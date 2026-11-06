package com.digitaltravel.erp.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.digitaltravel.erp.config.TaiKhoanDetails;
import com.digitaltravel.erp.dto.requests.BoSungRequest;
import com.digitaltravel.erp.dto.requests.QuyetToanRequest;
import com.digitaltravel.erp.dto.responses.ApiResponse;
import com.digitaltravel.erp.dto.responses.BaoCaoDoanhThuResponse;
import com.digitaltravel.erp.dto.responses.QuyetToanResponse;
import com.digitaltravel.erp.dto.responses.ThanhToanResponse;
import com.digitaltravel.erp.service.BaoCaoService;
import com.digitaltravel.erp.service.QuyetToanService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ke-toan")
@RequiredArgsConstructor
public class QuyetToanController {

    private final QuyetToanService quyetToanService;
    private final BaoCaoService baoCaoService;

    @GetMapping("/tour-can-quyet-toan")
    @PreAuthorize("hasAnyRole('KETOAN', 'ADMIN')")
    // UC49 - Tra cứu tour cần quyết toán: Lấy tour đủ điều kiện
    public ResponseEntity<ApiResponse<Page<QuyetToanResponse>>> tourCanQuyetToan(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(quyetToanService.tourCanQuyetToan(pageable)));
    }

    @GetMapping("/tinh-toan/{maTour}")
    @PreAuthorize("hasAnyRole('KETOAN', 'ADMIN')")
    // UC45 - Tính lợi nhuận gộp: Tính tổng hợp tài chính
    public ResponseEntity<ApiResponse<QuyetToanResponse>> tinhToan(@PathVariable String maTour) {
        return ResponseEntity.ok(ApiResponse.ok(quyetToanService.tinhToan(maTour)));
    }

    @PostMapping("/quyet-toan/{maTour}")
    @PreAuthorize("hasAnyRole('KETOAN', 'ADMIN')")
    // UC48 - Quyết toán tour: Tạo hồ sơ quyết toán
    public ResponseEntity<ApiResponse<QuyetToanResponse>> taoQuyetToan(
            @PathVariable String maTour,
            @Valid @RequestBody(required = false) QuyetToanRequest request,
            @AuthenticationPrincipal TaiKhoanDetails user) {
        QuyetToanResponse result = quyetToanService.taoQuyetToan(maTour, request, user.getTaiKhoan().getMaTaiKhoan());
        return ResponseEntity.status(201).body(ApiResponse.created(result));
    }

    @PutMapping("/quyet-toan/{maQuyetToan}/chot")
    @PreAuthorize("hasAnyRole('KETOAN', 'ADMIN')")
    // UC48 - Quyết toán tour: Khóa hồ sơ tài chính
    public ResponseEntity<ApiResponse<QuyetToanResponse>> chotQuyetToan(@PathVariable String maQuyetToan) {
        return ResponseEntity.ok(ApiResponse.ok(quyetToanService.chotQuyetToan(maQuyetToan)));
    }

    @PostMapping("/quyet-toan/{maQuyetToan}/yeu-cau-bo-sung")
    @PreAuthorize("hasAnyRole('KETOAN', 'ADMIN')")
    // UC48 - Quyết toán tour: Yêu cầu HDV bổ sung chứng từ
    public ResponseEntity<ApiResponse<QuyetToanResponse>> yeuCauBoSungQuyetToan(
            @PathVariable String maQuyetToan,
            @Valid @RequestBody BoSungRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Da gui yeu cau bo sung quyet toan",
                quyetToanService.yeuCauBoSung(maQuyetToan, request.getNoiDung())));
    }

    @GetMapping("/quyet-toan")
    @PreAuthorize("hasAnyRole('KETOAN', 'ADMIN')")
    // UC48 - Quyết toán tour: Lọc danh sách quyết toán
    public ResponseEntity<ApiResponse<Page<QuyetToanResponse>>> danhSach(
            @RequestParam(required = false) String trangThai,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(quyetToanService.danhSach(trangThai, pageable)));
    }

    /**
     * Chi tiết 1 quyết toán
     */
    @GetMapping("/quyet-toan/{maQuyetToan}")
    @PreAuthorize("hasAnyRole('KETOAN', 'ADMIN')")
    // UC48 - Quyết toán tour: Xem chi tiết quyết toán
    public ResponseEntity<ApiResponse<QuyetToanResponse>> chiTiet(@PathVariable String maQuyetToan) {
        return ResponseEntity.ok(ApiResponse.ok(quyetToanService.chiTiet(maQuyetToan)));
    }

    @GetMapping("/giao-dich-hoan")
    @PreAuthorize("hasAnyRole('KETOAN', 'ADMIN')")
    // UC50 - Xử lý hoàn tiền: Lấy giao dịch chờ hoàn
    public ResponseEntity<ApiResponse<Page<ThanhToanResponse>>> danhSachChoHoanTien(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(quyetToanService.danhSachChoHoanTien(pageable)));
    }

    @PutMapping("/giao-dich-hoan/{maGiaoDich}/xac-nhan")
    @PreAuthorize("hasAnyRole('KETOAN', 'ADMIN')")
    // UC50 - Xử lý hoàn tiền: Xác nhận đã hoàn tiền
    public ResponseEntity<ApiResponse<ThanhToanResponse>> xacNhanHoanTien(
            @PathVariable String maGiaoDich,
            @AuthenticationPrincipal TaiKhoanDetails user) {
        return ResponseEntity.ok(ApiResponse.ok("Xac nhan hoan tien thanh cong",
                quyetToanService.xacNhanHoanTien(maGiaoDich, user.getUsername())));
    }

    @PutMapping("/giao-dich-hoan/{maGiaoDich}/tu-choi")
    @PreAuthorize("hasAnyRole('KETOAN', 'ADMIN')")
    // UC50 - Xử lý hoàn tiền: Từ chối hoàn tiền
    public ResponseEntity<ApiResponse<ThanhToanResponse>> tuChoiHoanTien(
            @PathVariable String maGiaoDich,
            @AuthenticationPrincipal TaiKhoanDetails user) {
        return ResponseEntity.ok(ApiResponse.ok("Tu choi hoan tien thanh cong",
                quyetToanService.tuChoiHoanTien(maGiaoDich, user.getUsername())));
    }

    @GetMapping("/bao-cao/doanh-thu")
    @PreAuthorize("hasAnyRole('KETOAN', 'ADMIN')")
    // UC51 - Trích xuất dữ liệu phân tích: Tổng hợp báo cáo doanh thu
    public ResponseEntity<ApiResponse<BaoCaoDoanhThuResponse>> baoCaoDoanhThu(
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate tuNgay,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate denNgay) {
        return ResponseEntity.ok(ApiResponse.ok(baoCaoService.baoCaoDoanhThu(tuNgay, denNgay)));
    }
}
