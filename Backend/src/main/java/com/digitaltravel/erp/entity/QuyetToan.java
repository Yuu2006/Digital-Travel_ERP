package com.digitaltravel.erp.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "QUYETTOAN")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuyetToan {
    @Id
    @Column(name = "MaQuyetToan", nullable = false, length = 50)
    String MaQuyetToan;

    // FK -> TOURTHUCTE(MaTourThucTe) — 1-1, moi tour chi co 1 quyet toan
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaTourThucTe", nullable = false, unique = true)
    TourThucTe tourThucTe;

    // NUMBER(18,2): tong doanh thu tich luy tu tat ca don da xac nhan
    @Column(name = "TongDoanhThu", nullable = false, precision = 18, scale = 2)
    BigDecimal TongDoanhThu;

    // NUMBER(18,2): tong chi phi thuc te da duoc duyet
    @Column(name = "TongChiPhi", nullable = false, precision = 18, scale = 2)
    BigDecimal TongChiPhi;

    // NUMBER(18,2): chi phi/gia tri cam ket noi bo de doi chieu khi quyet toan
    @Column(name = "GiaCamKet", precision = 18, scale = 2)
    BigDecimal GiaCamKet;

    // NUMBER(18,2): = TongDoanhThu - TongChiPhi
    @Column(name = "LoiNhuan", nullable = false, precision = 18, scale = 2)
    BigDecimal LoiNhuan;

    // FK -> NHANVIEN(MaNhanVien) — ke toan lap quyet toan
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaNhanVien", nullable = false)
    NhanVien nhanVien;

    @Column(name = "NgayQuyetToan", nullable = false)
    LocalDateTime NgayQuyetToan;

    // Gia tri hop le: CHUA_QUYET_TOAN | DA_QUYET_TOAN
    @Column(name = "TrangThai", nullable = false, length = 20)
    String TrangThai;

    // CLOB: ghi chu ke toan
    @Lob
    @Column(name = "GhiChu")
    String GhiChu;

    @Column(name = "HoaDonAnh", length = 1000)
    String HoaDonAnh;
}
