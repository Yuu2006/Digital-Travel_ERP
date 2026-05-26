package com.digitaltravel.erp.dto.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class QuenMatKhauRequest {

    @NotBlank(message = "Email không được để trống")
    String email;
}
