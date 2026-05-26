package com.digitaltravel.erp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.digitaltravel.erp.entity.DanhGiaKh;

@Repository
public interface DanhGiaKhRepository extends JpaRepository<DanhGiaKh, String> {

    @Query(value = """
            SELECT d FROM DanhGiaKh d
            JOIN FETCH d.khachHang kh
            JOIN FETCH kh.taiKhoan
            JOIN FETCH d.tourThucTe ttt
            JOIN FETCH ttt.tourMau
            WHERE d.tourThucTe.MaTourThucTe = :maTourThucTe
            ORDER BY d.NgayDanhGia DESC
            """,
            countQuery = "SELECT COUNT(d) FROM DanhGiaKh d WHERE d.tourThucTe.MaTourThucTe = :maTourThucTe")
    Page<DanhGiaKh> findByMaTourThucTe(@Param("maTourThucTe") String maTourThucTe, Pageable pageable);

    @Query(value = """
            SELECT d FROM DanhGiaKh d
            JOIN FETCH d.khachHang kh
            JOIN FETCH kh.taiKhoan
            JOIN FETCH d.tourThucTe ttt
            JOIN FETCH ttt.tourMau
            WHERE d.tourThucTe.tourMau.MaTourMau = :maTourMau
            ORDER BY d.NgayDanhGia DESC
            """,
            countQuery = "SELECT COUNT(d) FROM DanhGiaKh d WHERE d.tourThucTe.tourMau.MaTourMau = :maTourMau")
    Page<DanhGiaKh> findByMaTourMau(@Param("maTourMau") String maTourMau, Pageable pageable);

    @Query("""
            SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END
            FROM DanhGiaKh d
            WHERE d.khachHang.MaKhachHang = :maKhachHang
              AND d.tourThucTe.MaTourThucTe = :maTourThucTe
            """)
    boolean existsByKhachHangAndTour(
            @Param("maKhachHang") String maKhachHang,
            @Param("maTourThucTe") String maTourThucTe
    );

    // Lấy tất cả đánh giá (admin quản lý)
    @Query("""
            SELECT d FROM DanhGiaKh d
            JOIN FETCH d.khachHang kh
            JOIN FETCH kh.taiKhoan
            JOIN FETCH d.tourThucTe ttt
            JOIN FETCH ttt.tourMau
            ORDER BY d.NgayDanhGia DESC
            """)
    Page<DanhGiaKh> findAllWithDetails(Pageable pageable);

    @Query("""
            SELECT COALESCE(AVG(d.SoSao), 0)
            FROM DanhGiaKh d
            WHERE d.tourThucTe.tourMau.MaTourMau = :maTourMau
            """)
    java.math.BigDecimal averageSoSaoByMaTourMau(@Param("maTourMau") String maTourMau);

    @Query("""
            SELECT COUNT(d)
            FROM DanhGiaKh d
            WHERE d.tourThucTe.tourMau.MaTourMau = :maTourMau
            """)
    long countByMaTourMau(@Param("maTourMau") String maTourMau);
}
