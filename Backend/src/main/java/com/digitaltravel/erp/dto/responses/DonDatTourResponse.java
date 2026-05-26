package com.digitaltravel.erp.dto.responses;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DonDatTourResponse {
    String maDatTour;
    String maTourThucTe;
    String tieuDeTour;
    LocalDate ngayKhoiHanh;
    BigDecimal giaHienHanh;
    Integer thoiLuong;
    String maKhachHang;
    String tenKhachHang;
    LocalDateTime ngayDat;
    BigDecimal tongTien;
    BigDecimal tongTienGoc;
    BigDecimal soTienUuDai;
    String maVoucher;
    String maCodeVoucher;
    Long diemXanhDuKien;
    String trangThai;
    Boolean daBaoChuyenKhoan;
    String trangThaiTour;
    LocalDateTime thoiGianHetHan;
    String ghiChu;
    String maGiaoDich;
    String phuongThuc;
    List<String> danhSachHanhDongXanh;
    Integer soNguoiLon;
    Integer soTreEm;
    String tenHuongDanVien;
    String soDienThoaiHuongDanVien;
    BigDecimal danhGiaHuongDanVien;
    Integer soDanhGiaHuongDanVien;
    Boolean daKhieuNai;
    Boolean daDanhGia;
    String trangThaiKhieuNai;
    List<ChiTietDatTourResponse> chiTietKhach;
    List<ChiTietDichVuResponse> chiTietDichVu;
}
