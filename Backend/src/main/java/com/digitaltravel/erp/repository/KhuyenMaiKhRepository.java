package com.digitaltravel.erp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.digitaltravel.erp.entity.KhuyenMaiKh;
import com.digitaltravel.erp.entity.KhuyenMaiKhId;

@Repository
public interface KhuyenMaiKhRepository extends JpaRepository<KhuyenMaiKh, KhuyenMaiKhId> {

    // Xem ví voucher của một khách hàng
    @Query("""
            SELECT k FROM KhuyenMaiKh k
            JOIN FETCH k.voucher v
            WHERE k.khachHang.MaKhachHang = :maKhachHang
            ORDER BY k.NgayNhan DESC
            """)
    Page<KhuyenMaiKh> findByMaKhachHang(@Param("maKhachHang") String maKhachHang, Pageable pageable);

    // Kiểm tra KH đã có voucher chưa
    @Query("""
            SELECT k FROM KhuyenMaiKh k
            JOIN FETCH k.voucher
            WHERE k.khachHang.MaKhachHang = :maKhachHang
              AND k.voucher.MaVoucher = :maVoucher
            """)
    Optional<KhuyenMaiKh> findByKhachHangAndVoucher(
            @Param("maKhachHang") String maKhachHang,
            @Param("maVoucher") String maVoucher
    );

    @Query("""
            SELECT k FROM KhuyenMaiKh k
            JOIN FETCH k.voucher
            WHERE k.khachHang.MaKhachHang = :maKhachHang
              AND k.voucher.MaVoucher = :maVoucher
              AND k.TrangThai = 'CO_HIEU_LUC'
            """)
    Optional<KhuyenMaiKh> findHieuLucByKhachHangAndVoucher(
            @Param("maKhachHang") String maKhachHang,
            @Param("maVoucher") String maVoucher
    );

    @Query("""
            SELECT k FROM KhuyenMaiKh k
            JOIN FETCH k.voucher
            JOIN FETCH k.khachHang h
            JOIN FETCH h.taiKhoan
            WHERE k.voucher.MaVoucher = :maVoucher
              AND k.TrangThai IN ('CO_HIEU_LUC', 'DA_SU_DUNG')
            ORDER BY k.NgayNhan DESC
            """)
    List<KhuyenMaiKh> findDangPhanBoByMaVoucher(@Param("maVoucher") String maVoucher);

    @Query("""
            SELECT COUNT(k) FROM KhuyenMaiKh k
            WHERE k.voucher.MaVoucher = :maVoucher
              AND k.TrangThai IN ('CO_HIEU_LUC', 'DA_SU_DUNG')
            """)
    long countDaPhanBoByMaVoucher(@Param("maVoucher") String maVoucher);
}
