package com.digitaltravel.erp.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.digitaltravel.erp.entity.NhanVien;

@Repository
public interface NhanVienRepository extends JpaRepository<NhanVien, String> {

    // Tìm nhân viên qua maTaiKhoan
    @Query("SELECT nv FROM NhanVien nv JOIN FETCH nv.taiKhoan tk WHERE tk.MaTaiKhoan = :maTaiKhoan")
    Optional<NhanVien> findByMaTaiKhoan(@Param("maTaiKhoan") String maTaiKhoan);

    // Tìm kiếm nhân viên (dùng cho ADMIN/MANAGER)
    @Query("""
            SELECT nv FROM NhanVien nv
            JOIN FETCH nv.taiKhoan tk
            WHERE (:trangThai IS NULL OR nv.TrangThaiLamViec = :trangThai)
              AND (:hoTen IS NULL OR LOWER(tk.HoTen) LIKE LOWER(CONCAT('%', :hoTen, '%')))
              AND (:maVaiTro IS NULL OR tk.vaiTro.MaVaiTro = :maVaiTro)
            """)
    Page<NhanVien> timKiem(
            @Param("hoTen") String hoTen,
            @Param("maVaiTro") String maVaiTro,
            @Param("trangThai") String trangThai,
            Pageable pageable
    );
}
