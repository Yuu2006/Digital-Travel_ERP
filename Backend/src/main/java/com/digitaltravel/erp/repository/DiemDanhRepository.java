package com.digitaltravel.erp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.digitaltravel.erp.entity.DiemDanh;

@Repository
public interface DiemDanhRepository extends JpaRepository<DiemDanh, String> {

    @Query("""
            SELECT d FROM DiemDanh d
            LEFT JOIN FETCH d.khachHang kh
            LEFT JOIN FETCH kh.taiKhoan
            LEFT JOIN FETCH d.nguoiDongHanh
            WHERE d.tourThucTe.MaTourThucTe = :maTour
            ORDER BY d.ThoiGian DESC
            """)
    List<DiemDanh> findByMaTour(@Param("maTour") String maTour);

    @Query("""
            SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END
            FROM DiemDanh d
            WHERE d.tourThucTe.MaTourThucTe = :maTour
              AND d.nhanVien.MaNhanVien = :maNhanVien
            """)
    boolean existsByMaTourAndMaNhanVien(
            @Param("maTour") String maTour,
            @Param("maNhanVien") String maNhanVien
    );
}
