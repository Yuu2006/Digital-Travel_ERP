package com.digitaltravel.erp.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "KHUYENMAI_KH")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KhuyenMaiKh {
    // Composite PK: (MaKhachHang, MaVoucher)
    @EmbeddedId
    KhuyenMaiKhId id;

    // FK -> HOCHIEUSO(MaKhachHang) — dung insertable/updatable=false vi da map trong EmbeddedId
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("MaKhachHang")
    @JoinColumn(name = "MaKhachHang")
    HoChieuSo khachHang;

    // FK -> VOUCHER(MaVoucher)
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("MaVoucher")
    @JoinColumn(name = "MaVoucher")
    Voucher voucher;

    // Oracle DATE -> LocalDate
    @Column(name = "NgayHetHan")
    LocalDate NgayHetHan;

    @Column(name = "NgayNhan", nullable = false)
    LocalDateTime NgayNhan;

    // Gia tri hop le: CO_HIEU_LUC | DA_SU_DUNG | DA_THU_HOI | HET_HAN
    @Column(name = "TrangThai", nullable = false, length = 20)
    String TrangThai;

}
