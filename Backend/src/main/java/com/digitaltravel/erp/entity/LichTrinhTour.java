package com.digitaltravel.erp.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "LICHTRINHTOUR")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LichTrinhTour {
    @Id
    @Column(name = "MaLichTrinhTour", nullable = false, length = 50)
    String MaLichTrinhTour;

    // FK -> TOURMAU(MaTourMau)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaTourMau", nullable = false)
    TourMau tourMau;

    // NUMBER(3): ngay thu bao nhieu trong hanh trinh (>0)
    @Column(name = "NgayThu", nullable = false)
    Integer NgayThu;

    @Column(name = "HoatDong", length = 1000)
    String HoatDong;

    // CLOB: mo ta chi tiet hoat dong ngay
    @Lob
    @Column(name = "MoTa")
    String MoTa;

    @Column(name = "ThucDon", length = 1000)
    String ThucDon;

}
