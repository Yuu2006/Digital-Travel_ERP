package com.digitaltravel.erp.dto.requests;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

/**
 * Request xuất dữ liệu Power BI thành file Excel/CSV.
 */
@Getter
@Setter
public class XuatDuLieuRequest {

    /** Mã kho dữ liệu (DOANH_THU, DON_DAT_TOUR, CHI_PHI, TOUR, GIAO_DICH). */
    @NotBlank(message = "Mã kho dữ liệu không được trống")
    private String maKho;

    /** Ngày bắt đầu (optional). */
    private LocalDate tuNgay;

    /** Ngày kết thúc (optional). */
    private LocalDate denNgay;

    /** Định dạng file: EXCEL hoặc CSV. */
    @NotBlank(message = "Định dạng không được trống")
    @Pattern(regexp = "^(EXCEL|CSV)$", message = "Định dạng phải là EXCEL hoặc CSV")
    private String dinhDang;
}
