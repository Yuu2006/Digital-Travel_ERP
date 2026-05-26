package com.digitaltravel.erp.dto.responses;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class HoChieuSoResponse {
    String maKhachHang;
    String maTaiKhoan;
    String tenDangNhap;
    String hoTen;
    String email;
    String cccd;
    LocalDate ngaySinh;
    String soDienThoai;
    String trangThaiTaiKhoan;
    String diUng;
    String ghiChuYTe;
    String hangThanhVien;
    Long diemXanh;
}
