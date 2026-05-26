package com.digitaltravel.erp.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.digitaltravel.erp.entity.Voucher;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, String> {

    @Query("SELECT v FROM Voucher v WHERE v.MaCode = :maCode")
    Optional<Voucher> findByMaCode(@Param("maCode") String maCode);

    @Query(value = """
            SELECT COALESCE(MAX(TO_NUMBER(SUBSTR(MaVoucher, 3))), 0)
            FROM VOUCHER
            WHERE REGEXP_LIKE(MaVoucher, '^VC[0-9]+$')
            """, nativeQuery = true)
    Integer findMaxVoucherNumber();

    // Tất cả voucher (admin quản lý)
    @Query("""
            SELECT v FROM Voucher v
            ORDER BY v.NgayHieuLuc DESC
            """)
    Page<Voucher> timKiem(Pageable pageable);

    // Voucher còn hiệu lực và còn lượt dùng (dùng để validate khi áp vào đơn)
    @Query("""
            SELECT v FROM Voucher v
            WHERE v.NgayHieuLuc <= :today
              AND v.NgayHetHan >= :today
              AND v.TrangThai = 'SAN_SANG'
              AND (
                  SELECT COUNT(k) FROM KhuyenMaiKh k
                  WHERE k.voucher = v
                    AND k.TrangThai IN ('CO_HIEU_LUC', 'DA_SU_DUNG')
              ) < v.SoLuotPhatHanh
            """)
    Page<Voucher> findAllActive(@Param("today") LocalDate today, Pageable pageable);
}
