package com.digitaltravel.erp.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KhuyenMaiKhId implements Serializable {
    @Column(name = "MaKhachHang", length = 50)
    String MaKhachHang;

    @Column(name = "MaVoucher", length = 50)
    String MaVoucher;
}
