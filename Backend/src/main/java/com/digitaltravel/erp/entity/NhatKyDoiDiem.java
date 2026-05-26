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
@Table(name = "NHATKYDOIDIEM")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NhatKyDoiDiem {
    @Id
    @Column(name = "MaNhatKyDoiDiem", nullable = false, length = 50)
    String MaNhatKyDoiDiem;

    // FK -> HOCHIEUSO(MaKhachHang)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaKhachHang", nullable = false)
    HoChieuSo khachHang;

    // FK -> VOUCHER(MaVoucher)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaVoucher", nullable = false)
    Voucher voucher;

    // NUMBER(15): so diem da dung de doi voucher nay
    @Column(name = "DiemQuyDoi", nullable = false)
    Long DiemQuyDoi;

    @Column(name = "NgayQuyDoi", nullable = false)
    LocalDateTime NgayQuyDoi;

}
