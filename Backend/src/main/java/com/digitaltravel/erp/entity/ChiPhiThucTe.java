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
@Table(name = "CHIPHITHUCTE")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChiPhiThucTe {
    @Id
    @Column(name = "MaChiPhiThucTe", nullable = false, length = 50)
    String MaChiPhiThucTe;

    // FK -> TOURTHUCTE(MaTourThucTe)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaTourThucTe", nullable = false)
    TourThucTe tourThucTe;

    // FK -> NHANVIEN(MaNhanVien) — HDV khai bao chi phi
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaNhanVien", nullable = false)
    NhanVien nhanVien;

    @Column(name = "DanhMuc", nullable = false, length = 200)
    String DanhMuc;

    // NUMBER(18,2): thanh tien chi phi phat sinh
    @Column(name = "ThanhTien", nullable = false, precision = 18, scale = 2)
    BigDecimal ThanhTien;

    // URL anh chup hoa don goc
    @Column(name = "HoaDonAnh", length = 1000)
    String HoaDonAnh;

    // Gia tri hop le: CHO_DUYET | DA_DUYET | TU_CHOI
    @Column(name = "TrangThaiDuyet", nullable = false, length = 20)
    String TrangThaiDuyet;

    @Column(name = "NgayKhai", nullable = false)
    LocalDateTime NgayKhai;

}
