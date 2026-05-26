package com.digitaltravel.erp.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BoSungQuyetToanRequest {
    @NotBlank(message = "Ghi chú bổ sung không được để trống")
    @Size(max = 4000)
    private String ghiChu;

    @Size(max = 2000)
    private String hoaDonAnh;
}
