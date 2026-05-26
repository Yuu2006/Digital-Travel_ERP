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
@Table(name = "DONDATTOUR")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DonDatTour {
    @Id
    @Column(name = "MaDatTour", nullable = false, length = 50)
    String maDatTour;

    // FK -> TOURTHUCTE(MaTourThucTe)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaTourThucTe", nullable = false)
    TourThucTe tourThucTe;

    // FK -> HOCHIEUSO(MaKhachHang)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaKhachHang", nullable = false)
    HoChieuSo khachHang;

    @Column(name = "NgayDat", nullable = false)
    LocalDateTime ngayDat;

    // NUMBER(18,2): tong tien da tinh bao gom dich vu, phong, uu dai
    @Column(name = "TongTien", nullable = false, precision = 18, scale = 2)
    BigDecimal tongTien;

    // Gia tri hop le: CHO_XAC_NHAN | DA_XAC_NHAN | CHO_HUY | DA_HUY | TU_CHOI_HOAN_TIEN | HET_HAN_GIU_CHO | THANH_TOAN_THAT_BAI
    @Column(name = "TrangThai", nullable = false, length = 30)
    String trangThai;

    // Thoi gian het han giu cho (null = khong gioi han)
    @Column(name = "ThoiGianHetHan")
    LocalDateTime thoiGianHetHan;

    @Column(name = "GhiChu", length = 2000)
    String GhiChu;

    @Column(name = "HanhDongXanh", length = 1000)
    String HanhDongXanh;
}
