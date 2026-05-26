package com.digitaltravel.erp.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class DatLaiMatKhauRequest {

    @NotBlank(message = "Reset token không được để trống")
    String resetToken;

    @NotBlank(message = "Mật khẩu mới không được để trống")
    @Size(min = 6, message = "Mật khẩu mới phải có ít nhất 6 ký tự")
    String matKhauMoi;

    @NotBlank(message = "Xác nhận mật khẩu không được để trống")
    String xacNhanMatKhau;
}
