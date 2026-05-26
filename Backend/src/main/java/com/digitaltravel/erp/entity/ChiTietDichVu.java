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
@Table(name = "CHITIETDICHVU")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChiTietDichVu {
    @Id
    @Column(name = "MaChiTietDichVu", nullable = false, length = 50)
    String MaChiTietDichVu;

    // FK -> DONDATTOUR(MaDatTour)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaDatTour", nullable = false)
    DonDatTour donDatTour;

    // FK -> DICHVUTHEM(MaDichVuThem)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaDichVuThem", nullable = false)
    DichVuThem dichVuThem;

    // NUMBER(10): so luong dich vu dat them
    @Column(name = "SoLuong", nullable = false)
    Long SoLuong;

    // NUMBER(18,2): don gia tai thoi diem dat
    @Column(name = "DonGia", nullable = false, precision = 18, scale = 2)
    BigDecimal DonGia;

    // NUMBER(18,2): = SoLuong * DonGia
    @Column(name = "ThanhTien", nullable = false, precision = 18, scale = 2)
    BigDecimal ThanhTien;

}
