package com.digitaltravel.erp.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.digitaltravel.erp.config.TaiKhoanDetails;
import com.digitaltravel.erp.dto.requests.XacNhanThanhToanOfflineRequest;
import com.digitaltravel.erp.dto.responses.ApiResponse;
import com.digitaltravel.erp.dto.responses.DonDatTourResponse;
import com.digitaltravel.erp.service.DatTourService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/kinh-doanh")
@RequiredArgsConstructor
public class KinhDoanhDatTourController {

    private final DatTourService datTourService;

    @GetMapping("/dat-tour")
    @PreAuthorize("hasAnyRole('ADMIN', 'SANPHAM', 'KINHDOANH', 'DIEUHANH', 'KETOAN', 'HDV')")
    public ResponseEntity<ApiResponse<Page<DonDatTourResponse>>> danhSachTatCa(
            @RequestParam(required = false) String trangThai,
            @RequestParam(required = false) String maTourThucTe,
            @PageableDefault(size = 10, sort = "ngayDat", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(datTourService.danhSachTatCa(trangThai, maTourThucTe, pageable)));
    }

    @PutMapping("/dat-tour/{maDatTour}/xac-nhan")
    @PreAuthorize("hasAnyRole('KINHDOANH', 'ADMIN', 'KETOAN')")
    public ResponseEntity<ApiResponse<DonDatTourResponse>> xacNhanDon(
            @PathVariable String maDatTour,
            @AuthenticationPrincipal TaiKhoanDetails user,
            @Valid @RequestBody(required = false) XacNhanThanhToanOfflineRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Ác nhận thanh toán thành công  cho đơn đặt tour " + maDatTour,
                datTourService.xacNhanDon(maDatTour, user.getTaiKhoan().getMaTaiKhoan(), request)));
    }

    @PutMapping("/dat-tour/{maDatTour}/tu-choi-thanh-toan")
    @PreAuthorize("hasAnyRole('KINHDOANH', 'ADMIN', 'KETOAN')")
    public ResponseEntity<ApiResponse<DonDatTourResponse>> tuChoiThanhToan(@PathVariable String maDatTour) {
        return ResponseEntity.ok(ApiResponse.ok("Đã từ chối thanh toán cho đơn đặt tour " + maDatTour,
                datTourService.tuChoiThanhToan(maDatTour)));
    }
}
