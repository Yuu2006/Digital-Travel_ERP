package com.digitaltravel.erp.dto.requests;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class NguoiDongHanhRequest {

    @NotBlank(message = "Họ tên khách đi cùng không được để trống")
    @Size(max = 200, message = "Họ tên không được vượt qua 200 ký tự")
    String hoTen;

    @Size(max = 20, message = "CCCD không được vượt qua 20 ký tự")
    String cccd;

    @Size(max = 20, message = "Số điện thoại không được vượt qua 20 ký tự")
    String soDienThoai;

    LocalDate ngaySinh;

    @Size(max = 20, message = "Giới tính không được vượt qua 20 ký tự")
    String gioiTinh;

    @Size(max = 1000, message = "Ghi chú không được vượt qua 1000 ký tự")
    String ghiChu;
}
