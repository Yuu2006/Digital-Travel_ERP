package com.digitaltravel.erp.dto.requests;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class QuyetToanRequest {
    @DecimalMin(value = "0", message = "Giá cam kết không được âm")
    private BigDecimal giaCamKet;

    @Size(max = 4000)
    private String ghiChu;

    @Size(max = 1000)
    private String hoaDonAnh;
}
