package com.digitaltravel.erp.entity;

import java.math.BigDecimal;
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
@Table(name = "TOURTHUCTE")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TourThucTe {
    @Id
    @Column(name = "MaTourThucTe", nullable = false, length = 50)
    String MaTourThucTe;

    // FK -> TOURMAU(MaTourMau)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaTourMau", nullable = false)
    TourMau tourMau;

    // Oracle DATE -> LocalDate
    @Column(name = "NgayKhoiHanh", nullable = false)
    LocalDate NgayKhoiHanh;

    // NUMBER(18,2): gia hien tai co the thay doi theo dynamic pricing
    @Column(name = "GiaHienHanh", nullable = false, precision = 18, scale = 2)
    BigDecimal GiaHienHanh;

    // NUMBER(5): toi da bao nhieu khach
    @Column(name = "SoKhachToiDa", nullable = false)
    Integer SoKhachToiDa;

    // NUMBER(5): toi thieu de tour dien ra
    @Column(name = "SoKhachToiThieu", nullable = false)
    Integer SoKhachToiThieu;

    // NUMBER(5): so cho con trong hien tai
    @Column(name = "ChoConLai", nullable = false)
    Integer ChoConLai;

    // Gia tri hop le: CHO_KICH_HOAT | MO_BAN | DANG_DIEN_RA | KET_THUC | HUY |
    // DA_QUYET_TOAN
    @Column(name = "TrangThai", nullable = false, length = 20)
    String TrangThai;
}
