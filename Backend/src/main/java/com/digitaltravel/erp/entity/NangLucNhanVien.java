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
@Table(name = "NANGLUCNHANVIEN")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NangLucNhanVien {
    @Id
    @Column(name = "MaNangLucNhanVien", nullable = false, length = 50)
    String MaNangLucNhanVien;

    // FK -> NHANVIEN(MaNhanVien)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaNhanVien", nullable = false)
    NhanVien nhanVien;

    @Column(name = "NgonNgu", length = 200)
    String NgonNgu;

    @Column(name = "ChungChi", length = 500)
    String ChungChi;

    @Column(name = "ChuyenMon", length = 500)
    String ChuyenMon;

    // NUMBER(3,2): diem trung binh 0.00 - 5.00
    @Column(name = "DanhGia", precision = 3, scale = 2)
    BigDecimal DanhGia;

    // NUMBER(10): so luot danh gia
    @Column(name = "SoDanhGia")
    Integer SoDanhGia;

}
