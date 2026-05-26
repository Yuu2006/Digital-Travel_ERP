package com.digitaltravel.erp.entity;

import java.math.BigDecimal;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "DICHVUTHEM")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DichVuThem {
    @Id
    @Column(name = "MaDichVuThem", nullable = false, length = 50)
    String MaDichVuThem;

    @Column(name = "Ten", nullable = false, length = 200)
    String Ten;

    @Column(name = "DonViTinh", length = 100)
    String DonViTinh;

    // NUMBER(18,2): don gia dich vu
    @Column(name = "DonGia", nullable = false, precision = 18, scale = 2)
    BigDecimal DonGia;

}
