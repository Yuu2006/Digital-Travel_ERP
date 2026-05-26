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
@Table(name = "YEUCAUHOTRO")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class YeuCauHoTro {
    @Id
    @Column(name = "MaYeuCauHoTro", nullable = false, length = 50)
    String MaYeuCauHoTro;

    // FK -> DONDATTOUR(MaDatTour) — nullable (yeu cau co the khong gan voi don nao)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaDatTour")
    DonDatTour donDatTour;

    // FK -> HOCHIEUSO(MaKhachHang)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaKhachHang", nullable = false)
    HoChieuSo khachHang;

    @Column(name = "LoaiYeuCau", nullable = false, length = 100)
    String LoaiYeuCau;

    // CLOB: noi dung chi tiet yeu cau / khieu nai
    @Lob
    @Column(name = "NoiDung", nullable = false)
    String NoiDung;

    // Gia tri hop le: CHUA_XU_LY | CHO_BO_SUNG | CHO_GIAI_TRINH | CHO_DUYET | DA_XU_LY | TU_CHOI
    @Column(name = "TrangThai", nullable = false, length = 20)
    String TrangThai;

    // FK -> NHANVIEN(MaNhanVien) — nullable (chua phan cong xu ly)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaNhanVienXuLy")
    NhanVien nhanVienXuLy;

}
