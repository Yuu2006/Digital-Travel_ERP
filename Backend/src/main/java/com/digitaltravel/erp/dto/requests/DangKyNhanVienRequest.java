package com.digitaltravel.erp.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class DangKyNhanVienRequest {

    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Size(min = 4, max = 100, message = "Tên đăng nhập phải từ 4 đến 100 ký tự")
    String tenDangNhap;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    String matKhau;

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 200)
    String hoTen;

    @Email(message = "Email không hợp lệ")
    String email;

    @Size(max = 20)
    String soDienThoai;

    /**
     * Ma vai tro can gan: SANPHAM, KINHDOANH, DIEUHANH, KETOAN, HDV
     * (ADMIN va KHACHHANG khong dung endpoint nay)
     */
    @NotBlank(message = "Mã vai trò không được để trống")
    String maVaiTro;
}
