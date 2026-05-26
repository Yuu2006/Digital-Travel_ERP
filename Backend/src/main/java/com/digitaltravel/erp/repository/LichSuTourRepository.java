package com.digitaltravel.erp.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.digitaltravel.erp.entity.LichSuTour;

@Repository
public interface LichSuTourRepository extends JpaRepository<LichSuTour, String> {

    // UC22: Lịch sử tour của khách hàng
    @Query("""
            SELECT lst FROM LichSuTour lst
            JOIN FETCH lst.tourThucTe ttt
            JOIN FETCH ttt.tourMau
            LEFT JOIN FETCH lst.chiTietDatTour ctd
            LEFT JOIN FETCH ctd.donDatTour
            WHERE lst.khachHang.MaKhachHang = :maKhachHang
            ORDER BY lst.NgayThamGia DESC
            """)
    Page<LichSuTour> findByMaKhachHang(@Param("maKhachHang") String maKhachHang, Pageable pageable);

    // Kiểm tra KH đã tham gia tour chưa (dùng cho đánh giá)
    @Query("""
            SELECT lst FROM LichSuTour lst
            WHERE lst.khachHang.MaKhachHang = :maKhachHang
              AND lst.tourThucTe.MaTourThucTe = :maTourThucTe
            """)
    Optional<LichSuTour> findByMaKhachHangAndMaTourThucTe(
            @Param("maKhachHang") String maKhachHang,
            @Param("maTourThucTe") String maTourThucTe
    );
}
