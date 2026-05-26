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
@Table(name = "NHATKYSUCO")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NhatKySuCo {
    @Id
    @Column(name = "MaNhatKySuCo", nullable = false, length = 50)
    String MaNhatKySuCo;

    // FK -> TOURTHUCTE(MaTourThucTe)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaTourThucTe", nullable = false)
    TourThucTe tourThucTe;

    // FK -> NHANVIEN(MaNhanVien)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaNhanVienBaoCao", nullable = false)
    NhanVien nhanVienBaoCao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaKhachHang")
    HoChieuSo khachHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaNguoiDongHanh")
    DsNguoiDongHanh nguoiDongHanh;

    // CLOB: mo ta chi tiet su co
    @Lob
    @Column(name = "MoTa", nullable = false)
    String MoTa;

    // CLOB: giai phap da xu ly
    @Lob
    @Column(name = "GiaiPhap")
    String GiaiPhap;

    // Gia tri hop le: THAP | SOS
    @Column(name = "MucDo", nullable = false, length = 20)
    String MucDo;

    // Gia tri hop le: Y_TE | THOI_TIET | PHUONG_TIEN | AN_UONG | KHAC
    @Column(name = "LoaiSuCo", nullable = false, length = 30)
    String LoaiSuCo;

    @Column(name = "ThoiGianBaoCao", nullable = false)
    LocalDateTime ThoiGianBaoCao;

}
