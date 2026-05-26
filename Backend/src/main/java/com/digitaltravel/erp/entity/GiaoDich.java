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
@Table(name = "GIAODICH")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GiaoDich {

    @Id
    @Column(name = "MaGiaoDich", nullable = false, length = 50)
    String maGiaoDich;

    // FK -> DONDATTOUR(MaDatTour)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaDatTour", nullable = false)
    DonDatTour donDatTour;

    @Column(name = "LoaiGiaoDich", nullable = false, length = 50)
    String loaiGiaoDich;

    @Column(name = "PhuongThuc", nullable = false, length = 50)
    String phuongThuc;

    // NUMBER(18,2): so tien giao dich
    @Column(name = "SoTien", nullable = false, precision = 18, scale = 2)
    BigDecimal soTien;

    // Ma giao dich ngan hang / cong thanh toan (MoMo transId,...)
    @Column(name = "MaGDNH", length = 200)
    String maGDNH;

    // Gia tri hop le: CHO_THANH_TOAN | THANH_CONG | THAT_BAI | DA_HOAN_TIEN
    @Column(name = "TrangThai", nullable = false, length = 30)
    String trangThai;

    @Column(name = "NgayThanhToan")
    LocalDateTime ngayThanhToan;
}
