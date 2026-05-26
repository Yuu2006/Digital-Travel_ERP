package com.digitaltravel.erp.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "NHANVIEN")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NhanVien {
    @Id
    @Column(name = "MaNhanVien", nullable = false, length = 50)
    String MaNhanVien;

    // FK -> TAIKHOAN(MaTaiKhoan), unique (1-1 voi TaiKhoan)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaTaiKhoan", nullable = false, unique = true)
    TaiKhoan taiKhoan;

    @Column(name = "LoaiNhanVien", length = 50)
    String LoaiNhanVien;

    // Oracle DATE -> LocalDate (khong co gio phut giay)
    @Column(name = "NgayVaoLam")
    LocalDate NgayVaoLam;

    // Gia tri hop le: HOAT_DONG | BAN | NGHI
    @Column(name = "TrangThaiLamViec", nullable = false, length = 20)
    String TrangThaiLamViec;

}
