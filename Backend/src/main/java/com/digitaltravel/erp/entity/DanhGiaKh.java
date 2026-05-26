package com.digitaltravel.erp.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "DANHGIAKH")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DanhGiaKh {
    @Id
    @Column(name = "MaDanhGiaKhachHang", nullable = false, length = 50)
    String MaDanhGiaKhachHang;

    // FK -> TOURTHUCTE(MaTourThucTe)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaTourThucTe", nullable = false)
    TourThucTe tourThucTe;

    // FK -> HOCHIEUSO(MaKhachHang)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaKhachHang", nullable = false)
    HoChieuSo khachHang;

    // NUMBER(1): 1 - 5 sao
    @Column(name = "SoSao", nullable = false)
    Integer SoSao;

    // CLOB: nhan xet van ban
    @Lob
    @Column(name = "NhanXet")
    String NhanXet;

    @Column(name = "NgayDanhGia", nullable = false)
    LocalDateTime NgayDanhGia;

    // FK composite (MaKhachHang, MaTourThucTe) -> LICHSUTOUR
    // insertable/updatable=false vi da map boi khachHang va tourThucTe
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "MaKhachHang",  referencedColumnName = "MaKhachHang",  insertable = false, updatable = false),
        @JoinColumn(name = "MaTourThucTe", referencedColumnName = "MaTourThucTe", insertable = false, updatable = false)
    })
    LichSuTour lichSuTour;

}
