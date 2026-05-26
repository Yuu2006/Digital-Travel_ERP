package com.digitaltravel.erp.entity;

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
@Table(name = "PHANCONGTOUR")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PhanCongTour {
    @Id
    @Column(name = "MaPhanCongTour", nullable = false, length = 50)
    String MaPhanCongTour;

    // FK -> TOURTHUCTE(MaTourThucTe)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaTourThucTe", nullable = false)
    TourThucTe tourThucTe;

    // FK -> NHANVIEN(MaNhanVien)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaNhanVien", nullable = false)
    NhanVien nhanVien;

    @Column(name = "NgayPhanCong", nullable = false)
    LocalDateTime NgayPhanCong;

    @Column(name = "TrangThaiChapNhan", nullable = false, length = 30)
    String TrangThaiChapNhan = "CHO_PHAN_HOI";

    @Column(name = "NgayPhanHoi")
    LocalDateTime NgayPhanHoi;

}
