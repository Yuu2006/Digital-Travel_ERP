package com.digitaltravel.erp.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "TOURMAU")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TourMau {
    @Id
    @Column(name = "MaTourMau", nullable = false, length = 50)
    String MaTourMau;

    @Column(name = "TieuDe", nullable = false, length = 500)
    String TieuDe;

    // CLOB
    @Lob
    @Column(name = "MoTa")
    String MoTa;

    // NUMBER(5): so ngay
    @Column(name = "ThoiLuong", nullable = false)
    Integer ThoiLuong;

    // NUMBER(18,2): gia san (gia toi thieu khong duoc ban thap hon)
    @Column(name = "GiaSan", nullable = false, precision = 18, scale = 2)
    BigDecimal GiaSan;

    // NUMBER(3,2): diem trung binh 0.00 - 5.00
    @Column(name = "DanhGia", precision = 3, scale = 2)
    BigDecimal DanhGia;

    // NUMBER(10): tong so luot danh gia
    @Column(name = "SoDanhGia")
    Integer SoDanhGia;
}
