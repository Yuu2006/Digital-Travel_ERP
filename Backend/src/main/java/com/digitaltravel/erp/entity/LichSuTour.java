package com.digitaltravel.erp.entity;

import java.time.LocalDate;
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
@Table(name = "LICHSUTOUR")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LichSuTour {
    @Id
    @Column(name = "MaLichSuTour", nullable = false, length = 50)
    String MaLichSuTour;

    // FK -> HOCHIEUSO(MaKhachHang)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaKhachHang", nullable = false)
    HoChieuSo khachHang;

    // FK -> TOURTHUCTE(MaTourThucTe)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaTourThucTe", nullable = false)
    TourThucTe tourThucTe;

    // FK -> CHITIETDATTOUR(MaChiTietDat) — nullable
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaChiTietDat")
    ChiTietDatTour chiTietDatTour;

    // Oracle DATE -> LocalDate
    @Column(name = "NgayThamGia")
    LocalDate NgayThamGia;

}
