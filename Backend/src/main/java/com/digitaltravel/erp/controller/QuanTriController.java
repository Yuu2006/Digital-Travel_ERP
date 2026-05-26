package com.digitaltravel.erp.controller;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.digitaltravel.erp.dto.responses.ApiResponse;
import com.digitaltravel.erp.dto.responses.NhatKyHeThongResponse;
import com.digitaltravel.erp.service.NhatKyHeThongService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/quan-tri")
@RequiredArgsConstructor
public class QuanTriController {

    private final NhatKyHeThongService nhatKyHeThongService;

    @GetMapping("/nhat-ky-he-thong")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<NhatKyHeThongResponse>>> nhatKyHeThong(
            @RequestParam(required = false) String maTaiKhoan,
            @RequestParam(required = false) String hanhDong,
            @RequestParam(required = false) String doiTuong,
            @RequestParam(required = false) String maDoiTuong,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime tuThoiGian,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime denThoiGian,
            @PageableDefault(size = 20, sort = "thoiGian", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(
                nhatKyHeThongService.timKiem(maTaiKhoan, hanhDong, doiTuong, maDoiTuong, tuThoiGian,
                        denThoiGian, pageable)));
    }
}
