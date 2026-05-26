package com.digitaltravel.erp.entity;

import java.math.BigDecimal;
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
@Table(name = "DATTOUR_UUDAI")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DatTourUuDai {
    // Composite PK: (MaDatTour, MaVoucher)
    @EmbeddedId
    DatTourUuDaiId id;

    // FK -> DONDATTOUR(MaDatTour)
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("MaDatTour")
    @JoinColumn(name = "MaDatTour")
    DonDatTour donDatTour;

    // FK -> VOUCHER(MaVoucher)
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("MaVoucher")
    @JoinColumn(name = "MaVoucher")
    Voucher voucher;

    // NUMBER(18,2): so tien giam thuc te duoc ap dung
    @Column(name = "SoTienUuDai", nullable = false, precision = 18, scale = 2)
    BigDecimal SoTienUuDai;

    @Column(name = "NgayApDung", nullable = false)
    LocalDateTime NgayApDung;
}
