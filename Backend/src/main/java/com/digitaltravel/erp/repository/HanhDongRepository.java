package com.digitaltravel.erp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.digitaltravel.erp.entity.HanhDong;

@Repository
public interface HanhDongRepository extends JpaRepository<HanhDong, String> {

    // Kiểm tra KH đã được tích hành động này trong tour chưa
    @Query("""
            SELECT h FROM HanhDong h
            WHERE h.tourThucTe.MaTourThucTe = :maTour
              AND h.khachHang.MaKhachHang = :maKhachHang
              AND h.hanhDongXanh.MaHanhDongXanh = :maHanhDong
            """)
    List<HanhDong> findByTourAndKhachAndHanhDong(
            @Param("maTour") String maTour,
            @Param("maKhachHang") String maKhachHang,
            @Param("maHanhDong") String maHanhDong
    );

    @Query("""
            SELECT CASE WHEN COUNT(h) > 0 THEN true ELSE false END
            FROM HanhDong h
            WHERE h.tourThucTe.MaTourThucTe = :maTour
              AND h.nhanVienXacMinh.MaNhanVien = :maNhanVien
            """)
    boolean existsByMaTourAndMaNhanVien(
            @Param("maTour") String maTour,
            @Param("maNhanVien") String maNhanVien
    );
}
