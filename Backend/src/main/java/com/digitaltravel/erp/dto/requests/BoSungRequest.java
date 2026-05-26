package com.digitaltravel.erp.dto.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BoSungRequest {
    @NotBlank(message = "Nội dung bổ sung không được để trống")
    private String noiDung;
}
