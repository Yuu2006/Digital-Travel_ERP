package com.digitaltravel.erp.dto.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class DangNhapRequest {

    @NotBlank
    String tenDangNhap;

    @NotBlank
    String matKhau;
}
