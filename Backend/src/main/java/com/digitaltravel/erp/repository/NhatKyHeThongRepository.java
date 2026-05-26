package com.digitaltravel.erp.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.digitaltravel.erp.entity.NhatKyHeThong;

@Repository
public interface NhatKyHeThongRepository extends JpaRepository<NhatKyHeThong, String> {
    @Query("""
            SELECT nk FROM NhatKyHeThong nk
            LEFT JOIN nk.taiKhoan tk
            WHERE (:maTaiKhoan IS NULL OR tk.MaTaiKhoan = :maTaiKhoan)
              AND (:hanhDong IS NULL OR nk.hanhDong = :hanhDong)
              AND (:doiTuong IS NULL OR nk.doiTuong = :doiTuong)
              AND (:maDoiTuong IS NULL OR nk.maDoiTuong = :maDoiTuong)
              AND (:tuThoiGian IS NULL OR nk.thoiGian >= :tuThoiGian)
              AND (:denThoiGian IS NULL OR nk.thoiGian <= :denThoiGian)
            """)
    Page<NhatKyHeThong> timKiem(
            @Param("maTaiKhoan") String maTaiKhoan,
            @Param("hanhDong") String hanhDong,
            @Param("doiTuong") String doiTuong,
            @Param("maDoiTuong") String maDoiTuong,
            @Param("tuThoiGian") LocalDateTime tuThoiGian,
            @Param("denThoiGian") LocalDateTime denThoiGian,
            Pageable pageable);
}
