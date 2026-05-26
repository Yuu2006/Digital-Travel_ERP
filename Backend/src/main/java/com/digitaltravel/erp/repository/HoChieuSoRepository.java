package com.digitaltravel.erp.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.digitaltravel.erp.entity.HoChieuSo;

@Repository
public interface HoChieuSoRepository extends JpaRepository<HoChieuSo, String> {

    @Query("""
            SELECT hcs FROM HoChieuSo hcs JOIN FETCH hcs.taiKhoan tk
            WHERE tk.MaTaiKhoan = :maTaiKhoan
            """)
    Optional<HoChieuSo> findByMaTaiKhoan(@Param("maTaiKhoan") String maTaiKhoan);

    @Query("""
            SELECT CASE WHEN COUNT(hcs) > 0 THEN true ELSE false END
            FROM HoChieuSo hcs
            WHERE hcs.taiKhoan.MaTaiKhoan = :maTaiKhoan
            """)
    boolean existsByMaTaiKhoan(@Param("maTaiKhoan") String maTaiKhoan);

    @Query("""
            SELECT hcs FROM HoChieuSo hcs JOIN FETCH hcs.taiKhoan tk
            WHERE (tk.Cccd = :cccd AND :cccd IS NOT NULL AND :cccd <> '')
               OR (tk.SoDienThoai = :soDienThoai AND :soDienThoai IS NOT NULL AND :soDienThoai <> '')
            """)
    java.util.List<HoChieuSo> findByCccdOrSoDienThoai(@Param("cccd") String cccd, @Param("soDienThoai") String soDienThoai);

    // UC24: Nhân viên tìm hồ sơ khách hàng
    @Query("""
            SELECT hcs FROM HoChieuSo hcs JOIN FETCH hcs.taiKhoan tk
            WHERE (:hoTen IS NULL OR LOWER(tk.HoTen) LIKE LOWER(CONCAT('%', :hoTen, '%')))
              AND (:email IS NULL OR LOWER(tk.Email) LIKE LOWER(CONCAT('%', :email, '%')))
              AND (:soDienThoai IS NULL OR tk.SoDienThoai LIKE CONCAT('%', :soDienThoai, '%'))
            ORDER BY tk.HoTen
            """)
    Page<HoChieuSo> timKiemKhachHang(
            @Param("hoTen") String hoTen,
            @Param("email") String email,
            @Param("soDienThoai") String soDienThoai,
            Pageable pageable
    );

    @Query(value = """
            SELECT hcs FROM HoChieuSo hcs JOIN FETCH hcs.taiKhoan tk
            WHERE (:hoTen IS NULL OR LOWER(tk.HoTen) LIKE LOWER(CONCAT('%', :hoTen, '%')))
              AND (:email IS NULL OR LOWER(tk.Email) LIKE LOWER(CONCAT('%', :email, '%')))
              AND (:soDienThoai IS NULL OR tk.SoDienThoai LIKE CONCAT('%', :soDienThoai, '%'))
              AND NOT EXISTS (
                  SELECT k FROM KhuyenMaiKh k
                  WHERE k.khachHang = hcs
                    AND k.voucher.MaVoucher = :maVoucher
              )
            ORDER BY tk.HoTen
            """,
            countQuery = """
            SELECT COUNT(hcs) FROM HoChieuSo hcs JOIN hcs.taiKhoan tk
            WHERE (:hoTen IS NULL OR LOWER(tk.HoTen) LIKE LOWER(CONCAT('%', :hoTen, '%')))
              AND (:email IS NULL OR LOWER(tk.Email) LIKE LOWER(CONCAT('%', :email, '%')))
              AND (:soDienThoai IS NULL OR tk.SoDienThoai LIKE CONCAT('%', :soDienThoai, '%'))
              AND NOT EXISTS (
                  SELECT k FROM KhuyenMaiKh k
                  WHERE k.khachHang = hcs
                    AND k.voucher.MaVoucher = :maVoucher
              )
            """)
    Page<HoChieuSo> timKiemKhachHangChuaNhanVoucher(
            @Param("hoTen") String hoTen,
            @Param("email") String email,
            @Param("soDienThoai") String soDienThoai,
            @Param("maVoucher") String maVoucher,
            Pageable pageable
    );
}
