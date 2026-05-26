package com.digitaltravel.erp.entity;

import java.time.LocalDate;

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
@Table(name = "DSNGUOIDONGHANH")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DsNguoiDongHanh {
    @Id
    @Column(name = "MaNguoiDongHanh", nullable = false, length = 50)
    String MaNguoiDongHanh;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaDatTour", nullable = false)
    DonDatTour donDatTour;

    @Column(name = "HoTen", nullable = false, length = 200)
    String HoTen;

    @Column(name = "CCCD", length = 20)
    String Cccd;

    @Column(name = "SoDienThoai", length = 20)
    String SoDienThoai;

    @Column(name = "NgaySinh")
    LocalDate NgaySinh;

    @Column(name = "GioiTinh", length = 20)
    String GioiTinh;

    @Column(name = "GhiChu", length = 1000)
    String GhiChu;
}
