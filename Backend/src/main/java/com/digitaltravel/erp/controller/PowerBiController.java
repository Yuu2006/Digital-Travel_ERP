package com.digitaltravel.erp.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.digitaltravel.erp.config.TaiKhoanDetails;
import com.digitaltravel.erp.dto.requests.XuatDuLieuRequest;
import com.digitaltravel.erp.dto.responses.ApiResponse;
import com.digitaltravel.erp.dto.responses.PowerBiKetNoiResponse;
import com.digitaltravel.erp.dto.responses.PowerBiKhoDuLieuResponse;
import com.digitaltravel.erp.service.PowerBiService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controller trích xuất dữ liệu phân tích (Power BI).
 * <p>
 * Tách riêng khỏi QuyetToanController để rõ ràng, dễ bảo trì.
 * Tất cả endpoint yêu cầu role KETOAN hoặc ADMIN.
 * </p>
 */
@RestController
@RequestMapping("/api/ke-toan/power-bi")
@RequiredArgsConstructor
public class PowerBiController {

    private final PowerBiService powerBiService;

    @GetMapping("/kho-du-lieu")
    @PreAuthorize("hasAnyRole('KETOAN', 'ADMIN')")
    // UC51 - Trích xuất dữ liệu phân tích: Lấy kho dữ liệu
    public ResponseEntity<ApiResponse<List<PowerBiKhoDuLieuResponse>>> danhSachKhoDuLieu() {
        return ResponseEntity.ok(ApiResponse.ok(powerBiService.danhSachKhoDuLieu()));
    }

    @GetMapping("/ket-noi")
    @PreAuthorize("hasAnyRole('KETOAN', 'ADMIN')")
    // UC51 - Trích xuất dữ liệu phân tích: Cấp thông tin kết nối
    public ResponseEntity<ApiResponse<PowerBiKetNoiResponse>> layThongTinKetNoi(
            @RequestParam String maKho,
            @AuthenticationPrincipal TaiKhoanDetails user) {
        PowerBiKetNoiResponse result = powerBiService.layThongTinKetNoi(maKho, user);
        return ResponseEntity.ok(ApiResponse.ok("Thông tin kết nối Power BI", result));
    }

    @PostMapping("/xuat-du-lieu")
    @PreAuthorize("hasAnyRole('KETOAN', 'ADMIN')")
    // UC51 - Trích xuất dữ liệu phân tích: Xuất tập dữ liệu
    public ResponseEntity<byte[]> xuatDuLieu(
            @Valid @RequestBody XuatDuLieuRequest request,
            @AuthenticationPrincipal TaiKhoanDetails user) {

        byte[] data = powerBiService.xuatDuLieu(request, user);

        boolean isExcel = "EXCEL".equalsIgnoreCase(request.getDinhDang());
        String contentType = isExcel
                ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                : "text/csv; charset=UTF-8";

        String timestamp = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String extension = isExcel ? ".xlsx" : ".csv";
        String filename = "PowerBI_" + request.getMaKho() + "_" + timestamp + extension;

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                .body(data);
    }
}
