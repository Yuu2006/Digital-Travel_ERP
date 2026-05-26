package com.digitaltravel.erp.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class DoiMatKhauRequest {

    @NotBlank
    String matKhauCu;

    @NotBlank
    @Size(min = 6, message = "Mật khẩu mới phải có ít nhất 6 ký tự")
    String matKhauMoi;

    @NotBlank
    String xacNhanMatKhau;
}
