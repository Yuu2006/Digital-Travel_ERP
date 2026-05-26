package com.digitaltravel.erp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.digitaltravel.erp.entity.TaiKhoan;

@Repository
public interface TaiKhoanRepository extends JpaRepository<TaiKhoan, String>{
    @Query("""
            SELECT CASE WHEN COUNT(tk) > 0 THEN true ELSE false END
            FROM TaiKhoan tk
            WHERE tk.Email = :email
            """)
    boolean existsByEmail(@Param("email") String email);

    @Query("""
            SELECT CASE WHEN COUNT(tk) > 0 THEN true ELSE false END
            FROM TaiKhoan tk
            WHERE tk.TenDangNhap = :tenDangNhap
            """)
    boolean existsByTenDangNhap(@Param("tenDangNhap") String tenDangNhap);

    @Query("""
            SELECT CASE WHEN COUNT(tk) > 0 THEN true ELSE false END
            FROM TaiKhoan tk
            WHERE tk.Cccd = :cccd
            """)
    boolean existsByCccd(@Param("cccd") String cccd);

    @Query("""
            SELECT CASE WHEN COUNT(tk) > 0 THEN true ELSE false END
            FROM TaiKhoan tk
            WHERE tk.Cccd = :cccd AND tk.MaTaiKhoan <> :maTaiKhoan
            """)
    boolean existsByCccdAndMaTaiKhoanNot(@Param("cccd") String cccd, @Param("maTaiKhoan") String maTaiKhoan);

    default boolean TonTaiTaiKhoan(String email) {
        return existsByEmail(email);
    }
    
    @Query("""
            SELECT tk FROM TaiKhoan tk
            WHERE tk.Email = :email
            """)
    Optional<TaiKhoan> findByEmail(@Param("email") String email);

    @Query("""
            SELECT tk FROM TaiKhoan tk
            JOIN FETCH tk.vaiTro
            WHERE tk.TenDangNhap = :tenDangNhap
            """)
    Optional<TaiKhoan> findByTenDangNhapWithVaiTro(@Param("tenDangNhap") String tenDangNhap);

    @Query(
            value = """
            select tk.* from TAIKHOAN tk
            join VAITRO vt on tk.VaiTro = vt.MaVaiTro
            where vt.TenHienThi = :TenVaiTro
            """, nativeQuery = true
    )
    List<TaiKhoan> findUsersByRoleName(@Param("TenVaiTro") String TenVaiTro);
}
