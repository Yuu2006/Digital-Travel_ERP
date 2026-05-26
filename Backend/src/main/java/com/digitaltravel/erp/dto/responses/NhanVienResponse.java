package com.digitaltravel.erp.dto.responses;

import java.time.LocalDate;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NhanVienResponse {
    String maNhanVien;
    String maTaiKhoan;
    String tenDangNhap;
    String hoTen;
    String email;
    String soDienThoai;
    String maVaiTro;
    String trangThaiTaiKhoan;   // HOAT_DONG | KHOA
    String trangThaiLamViec;    // HOAT_DONG | BAN | NGHI
    String loaiNhanVien;
    LocalDate ngayVaoLam;
    String cccd;
    LocalDate ngaySinh;
}
