package com.digitaltravel.erp.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "VAITRO")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VaiTro {
    @Id
    @Column(name = "MaVaiTro", nullable = false, length = 50)
    String MaVaiTro;

    @Column(name = "TenHienThi", nullable = false, length = 100)
    String TenHienThi;

    // Danh sach tai khoan thuoc vai tro nay
    @JsonIgnore
    @OneToMany(mappedBy = "vaiTro", fetch = FetchType.LAZY)
    List<TaiKhoan> dsTaiKhoan = new ArrayList<>();
}
