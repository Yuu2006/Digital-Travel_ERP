package com.digitaltravel.erp.dto.responses;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChiTietDatTourResponse {
    String maChiTietDat;
    String loaiKhach;
    String maKhachHang;
    String maNguoiDongHanh;
    String hoTen;
    String cccd;
    String soDienThoai;
    LocalDate ngaySinh;
    Integer tuoi;
    String nhomTuoi;
    String gioiTinh;
    String ghiChu;
    String ghiChuYTe;
    BigDecimal giaTaiThoiDiemDat;
}
