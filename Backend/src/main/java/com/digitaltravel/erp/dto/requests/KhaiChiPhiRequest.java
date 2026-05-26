package com.digitaltravel.erp.dto.requests;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class KhaiChiPhiRequest {
    @NotBlank
    private String danhMuc;

    @NotNull
    @Positive
    private BigDecimal thanhTien;

    private String hoaDonAnh;
}
