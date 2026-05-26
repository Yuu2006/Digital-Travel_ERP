package com.digitaltravel.erp.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "HANHDONG")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HanhDong {
    @Id
    @Column(name = "MaGhiNhanHanhDong", nullable = false, length = 50)
    String MaGhiNhanHanhDong;

    // FK -> TOURTHUCTE(MaTourThucTe)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaTourThucTe", nullable = false)
    TourThucTe tourThucTe;

    // FK -> HOCHIEUSO(MaKhachHang)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaKhachHang", nullable = false)
    HoChieuSo khachHang;

    // FK -> HANHDONGXANH(MaHanhDongXanh)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaHanhDongXanh", nullable = false)
    HanhDongXanh hanhDongXanh;

    // FK -> NHANVIEN(MaNhanVien) — nhan vien da xac minh hanh dong xanh
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaNhanVienXacMinh", nullable = false)
    NhanVien nhanVienXacMinh;

    @Column(name = "ThoiGian", nullable = false)
    LocalDateTime ThoiGian;

    // URL/path anh minh chung
    @Column(name = "MinhChung", length = 1000)
    String MinhChung;

    // FK composite (MaTourThucTe, MaNhanVienXacMinh) -> PHANCONGTOUR
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "MaTourThucTe",     referencedColumnName = "MaTourThucTe", insertable = false, updatable = false),
        @JoinColumn(name = "MaNhanVienXacMinh", referencedColumnName = "MaNhanVien",  insertable = false, updatable = false)
    })
    PhanCongTour phanCongTour;

}
