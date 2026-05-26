package com.digitaltravel.erp.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "CHITIETDATTOUR")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChiTietDatTour {
    @Id
    @Column(name = "MaChiTietDat", nullable = false, length = 50)
    String MaChiTietDat;

    // FK -> DONDATTOUR(MaDatTour)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaDatTour", nullable = false)
    DonDatTour donDatTour;

    // Nguoi dat chinh co ho so so; nguoi dong hanh dung DsNguoiDongHanh.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaKhachHang")
    HoChieuSo khachHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaNguoiDongHanh")
    DsNguoiDongHanh nguoiDongHanh;

    @Column(name = "LoaiKhach", nullable = false, length = 30)
    String LoaiKhach;

    // NUMBER(18,2): gia goc tai thoi diem dat, giu nguyen de doi soat
    @Column(name = "GiaTaiThoiDiemDat", nullable = false, precision = 18, scale = 2)
    BigDecimal GiaTaiThoiDiemDat;

}
