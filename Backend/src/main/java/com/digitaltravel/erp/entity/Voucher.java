package com.digitaltravel.erp.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "VOUCHER")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Voucher {
    @Id
    @Column(name = "MaVoucher", nullable = false, length = 50)
    String MaVoucher;

    @Column(name = "MaCode", nullable = false, length = 50, unique = true)
    String MaCode;

    // Gia tri hop le: PHAN_TRAM | SO_TIEN
    @Column(name = "LoaiUuDai", nullable = false, length = 20)
    String LoaiUuDai;

    // NUMBER(18,2): gia tri giam (% hoac so tien VND)
    @Column(name = "GiaTriGiam", nullable = false, precision = 18, scale = 2)
    BigDecimal GiaTriGiam;

    // Tran tien giam cho voucher PHAN_TRAM; SO_TIEN khong su dung truong nay.
    @Column(name = "MucGiamToiDa", precision = 18, scale = 2)
    BigDecimal MucGiamToiDa;

    @Column(name = "DieuKienApDung", length = 2000)
    String DieuKienApDung;

    // NUMBER(10): tong so luot duoc phat hanh
    @Column(name = "SoLuotPhatHanh", nullable = false)
    Integer SoLuotPhatHanh;

    // NUMBER(10): so luot da duoc su dung
    @Column(name = "SoLuotDaDung", nullable = false)
    Integer SoLuotDaDung;

    @Column(name = "NgayHieuLuc", nullable = false)
    LocalDate NgayHieuLuc;

    // Oracle DATE -> LocalDate
    @Column(name = "NgayHetHan", nullable = false)
    LocalDate NgayHetHan;

    // Gia tri hop le: SAN_SANG | VO_HIEU_HOA
    @Column(name = "TrangThai", nullable = false, length = 20)
    String TrangThai;

}
