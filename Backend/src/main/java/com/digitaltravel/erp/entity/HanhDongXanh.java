package com.digitaltravel.erp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "HANHDONGXANH")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HanhDongXanh {
    @Id
    @Column(name = "MaHanhDongXanh", nullable = false, length = 50)
    String MaHanhDongXanh;

    @Column(name = "TenHanhDong", nullable = false, length = 200)
    String TenHanhDong;

    // NUMBER(10): so diem cong khi hoan thanh hanh dong nay
    @Column(name = "DiemCong", nullable = false)
    Long DiemCong;

}
