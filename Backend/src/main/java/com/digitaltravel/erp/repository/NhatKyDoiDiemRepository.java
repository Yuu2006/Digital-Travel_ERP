package com.digitaltravel.erp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.digitaltravel.erp.entity.NhatKyDoiDiem;

@Repository
public interface NhatKyDoiDiemRepository extends JpaRepository<NhatKyDoiDiem, String> {

    @Query("""
            SELECT n FROM NhatKyDoiDiem n JOIN FETCH n.voucher
            WHERE n.khachHang.MaKhachHang = :maKhachHang
            ORDER BY n.NgayQuyDoi DESC
            """)
    Page<NhatKyDoiDiem> findByMaKhachHang(@Param("maKhachHang") String maKhachHang, Pageable pageable);
}
