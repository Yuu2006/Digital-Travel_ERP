package com.digitaltravel.erp.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.digitaltravel.erp.entity.YeuCauHoTro;

@Repository
public interface YeuCauHoTroRepository extends JpaRepository<YeuCauHoTro, String> {

    // Lấy yêu cầu hoàn tiền của 1 đơn đặt tour
    @Query("SELECT y FROM YeuCauHoTro y WHERE y.donDatTour.maDatTour = :maDatTour AND y.LoaiYeuCau = :loaiYeuCau")
    List<YeuCauHoTro> findByMaDatTourAndLoaiYeuCau(
            @Param("maDatTour") String maDatTour,
            @Param("loaiYeuCau") String loaiYeuCau
    );

    @Query("""
            SELECT CASE WHEN COUNT(y) > 0 THEN true ELSE false END
            FROM YeuCauHoTro y
            WHERE y.khachHang.MaKhachHang = :maKhachHang
              AND y.LoaiYeuCau = 'KHIEU_NAI'
              AND y.TrangThai IN ('CHUA_XU_LY', 'CHO_BO_SUNG', 'CHO_GIAI_TRINH', 'CHO_DUYET')
              AND y.donDatTour.tourThucTe.MaTourThucTe = :maTourThucTe
            """)
    boolean existsActiveKhieuNaiByKhachHangAndTour(
            @Param("maKhachHang") String maKhachHang,
            @Param("maTourThucTe") String maTourThucTe
    );

    // Nhân viên SALES xem tất cả yêu cầu theo loại + trạng thái
    @Query("""
            SELECT y FROM YeuCauHoTro y
            JOIN FETCH y.khachHang kh
            LEFT JOIN FETCH y.donDatTour
            WHERE (:loaiYeuCau IS NULL OR y.LoaiYeuCau = :loaiYeuCau)
              AND (:trangThai IS NULL OR y.TrangThai = :trangThai)
            ORDER BY y.MaYeuCauHoTro DESC
            """)
    Page<YeuCauHoTro> timKiem(
            @Param("loaiYeuCau") String loaiYeuCau,
            @Param("trangThai") String trangThai,
            Pageable pageable
    );

    // KH xem yêu cầu của chính mình
    @Query("""
            SELECT y FROM YeuCauHoTro y
            JOIN FETCH y.khachHang kh
            LEFT JOIN FETCH y.donDatTour
            WHERE kh.MaKhachHang = :maKhachHang
              AND (:loaiYeuCau IS NULL OR y.LoaiYeuCau = :loaiYeuCau)
            ORDER BY y.MaYeuCauHoTro DESC
            """)
    Page<YeuCauHoTro> timKiemTheoKhachHang(
            @Param("maKhachHang") String maKhachHang,
            @Param("loaiYeuCau") String loaiYeuCau,
            Pageable pageable
    );

    @Query("""
            SELECT y FROM YeuCauHoTro y
            JOIN FETCH y.khachHang kh
            LEFT JOIN FETCH y.donDatTour ddt
            LEFT JOIN FETCH ddt.tourThucTe tt
            WHERE y.nhanVienXuLy.MaNhanVien = :maNhanVien
              AND y.TrangThai = 'CHO_BO_SUNG'
            ORDER BY y.MaYeuCauHoTro DESC
            """)
    List<YeuCauHoTro> findYeuCauGiaiTrinhCuaHdv(@Param("maNhanVien") String maNhanVien);

    @Query("""
            SELECT y FROM YeuCauHoTro y
            JOIN FETCH y.khachHang kh
            LEFT JOIN FETCH y.donDatTour ddt
            WHERE kh.MaKhachHang = :maKhachHang
              AND y.TrangThai = 'CHO_BO_SUNG'
            ORDER BY y.MaYeuCauHoTro DESC
            """)
    List<YeuCauHoTro> findYeuCauCanKhachHangBoSung(@Param("maKhachHang") String maKhachHang);
}
