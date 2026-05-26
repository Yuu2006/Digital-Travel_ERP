package com.digitaltravel.erp.dto.requests;

import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class CapNhatHoChieuSoRequest {

    @Size(max = 20, message = "CCCD không được vượt qua 20 ký tự")
    String cccd;

    LocalDate ngaySinh;

    @Size(max = 100, message = "Tên đăng nhập không được vượt qua 100 ký tự")
    String tenDangNhap;

    @Size(max = 200, message = "Email không được vượt qua 200 ký tự")
    String email;

    @Size(max = 20, message = "Số điện thoại không được vượt qua 20 ký tự")
    String soDienThoai;

    @Size(max = 1000, message = "Dị ứng không được vượt qua 1000 ký tự")
    String diUng;

    // CLOB field - no max length constraint
    String ghiChuYTe;
}
