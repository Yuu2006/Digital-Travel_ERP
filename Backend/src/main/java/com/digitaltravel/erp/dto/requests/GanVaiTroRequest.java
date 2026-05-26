package com.digitaltravel.erp.dto.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class GanVaiTroRequest {
    @NotBlank(message = "Mã vai trò không được trống")
    String maVaiTro;
}
