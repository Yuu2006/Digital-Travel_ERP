package com.digitaltravel.erp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "HOCHIEUSO")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HoChieuSo {
    @Id
    @Column(name = "MaKhachHang", nullable = false, length = 50)
    String MaKhachHang;

    // FK -> TAIKHOAN(MaTaiKhoan), unique (1-1 voi TaiKhoan)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaTaiKhoan", nullable = false, unique = true)
    TaiKhoan taiKhoan;

    // CLOB — thong tin y te nhay cam
    @Lob
    @Column(name = "GhiChuYTe")
    String GhiChuYTe;

    @Column(name = "DiUng", length = 1000)
    String DiUng;

    // Gia tri hop le: THANH_VIEN | DONG | BAC | VANG | KIM_CUONG
    @Column(name = "HangThanhVien", nullable = false, length = 20)
    String HangThanhVien;

    // NUMBER(15) — dung Long, khong phai BigDecimal
    @Column(name = "DiemXanh", nullable = false)
    Long DiemXanh;

}
