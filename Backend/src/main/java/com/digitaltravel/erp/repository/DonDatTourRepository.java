package com.digitaltravel.erp.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.digitaltravel.erp.entity.DonDatTour;

@Repository
public interface DonDatTourRepository extends JpaRepository<DonDatTour, String> {

    // Lấy tất cả đơn đặt tour của một khách hàng (theo maKhachHang của HoChieuSo)
    @Query(value = """
            SELECT d FROM DonDatTour d
            JOIN FETCH d.tourThucTe ttt
            JOIN FETCH ttt.tourMau
            WHERE d.khachHang.MaKhachHang = :maKhachHang
            """,
            countQuery = """
            SELECT COUNT(d) FROM DonDatTour d
            WHERE d.khachHang.MaKhachHang = :maKhachHang
            """)
    Page<DonDatTour> findByMaKhachHang(@Param("maKhachHang") String maKhachHang, Pageable pageable);

    // Lấy chi tiết đơn đặt tour đảm bảo thuộc về khách hàng đang đăng nhập (chống IDOR)
    @Query("""
            SELECT d FROM DonDatTour d
            JOIN FETCH d.tourThucTe ttt
            JOIN FETCH ttt.tourMau
            WHERE d.maDatTour = :maDatTour
              AND d.khachHang.MaKhachHang = :maKhachHang
            """)
    Optional<DonDatTour> findByIdAndMaKhachHang(
            @Param("maDatTour") String maDatTour,
            @Param("maKhachHang") String maKhachHang
    );

    @Query("""
            SELECT d FROM DonDatTour d
            WHERE d.khachHang.MaKhachHang = :maKhachHang
              AND d.tourThucTe.MaTourThucTe = :maTourThucTe
              AND d.trangThai NOT IN ('DA_HUY', 'HET_HAN_GIU_CHO', 'THANH_TOAN_THAT_BAI')
            ORDER BY d.ngayDat DESC
            """)
    List<DonDatTour> findRecentByMaKhachHangAndMaTourThucTe(
            @Param("maKhachHang") String maKhachHang,
            @Param("maTourThucTe") String maTourThucTe
    );

    // Dành cho nhân viên: tất cả đơn (filter theo trạng thái)
    @Query(value = """
            SELECT d FROM DonDatTour d
            JOIN FETCH d.tourThucTe ttt
            JOIN FETCH ttt.tourMau
            JOIN FETCH d.khachHang kh
            LEFT JOIN FETCH kh.taiKhoan
            WHERE (:trangThai IS NULL OR d.trangThai = :trangThai)
              AND (:maTourThucTe IS NULL OR d.tourThucTe.MaTourThucTe = :maTourThucTe)
            """,
            countQuery = """
            SELECT COUNT(d) FROM DonDatTour d
            WHERE (:trangThai IS NULL OR d.trangThai = :trangThai)
              AND (:maTourThucTe IS NULL OR d.tourThucTe.MaTourThucTe = :maTourThucTe)
            """)
    Page<DonDatTour> timKiem(
            @Param("trangThai") String trangThai,
            @Param("maTourThucTe") String maTourThucTe,
            Pageable pageable
    );

    // Dùng cho Scheduler: lấy đơn CHO_XAC_NHAN đã hết hạn giữ chỗ
    @Query("SELECT d FROM DonDatTour d WHERE d.trangThai = :trangThai AND d.thoiGianHetHan < :thoiGian")
    List<DonDatTour> findAllByTrangThaiAndThoiGianHetHanBefore(
            @Param("trangThai") String trangThai,
            @Param("thoiGian") LocalDateTime thoiGian
    );

    // Kiểm tra đơn theo maDatTour (không lọc khách hàng) — dùng cho thanh toán
    @Query("""
            SELECT d FROM DonDatTour d
            JOIN FETCH d.tourThucTe ttt
            JOIN FETCH ttt.tourMau
            JOIN FETCH d.khachHang
            WHERE d.maDatTour = :maDatTour
            """)
    Optional<DonDatTour> findByIdWithDetails(@Param("maDatTour") String maDatTour);

    // Dem so don dat tour trong khoang thoi gian theo ngay dat (bao cao)
    @Query("SELECT COUNT(d) FROM DonDatTour d WHERE d.ngayDat BETWEEN :tuNgay AND :denNgay")
    long demDonDatTour(
            @Param("tuNgay") LocalDateTime tuNgay,
            @Param("denNgay") LocalDateTime denNgay
    );

    @Query("""
            SELECT COUNT(d) FROM DonDatTour d
            WHERE d.tourThucTe.MaTourThucTe = :maTourThucTe
              AND d.trangThai NOT IN ('DA_HUY', 'HET_HAN_GIU_CHO', 'THANH_TOAN_THAT_BAI')
            """)
    long countBlockingBookingsByTourThucTe(@Param("maTourThucTe") String maTourThucTe);

    @Query("""
            SELECT COUNT(d) FROM DonDatTour d
            JOIN ChiTietDatTour ct ON ct.donDatTour = d
            WHERE d.tourThucTe.MaTourThucTe = :maTourThucTe
              AND d.trangThai = 'DA_XAC_NHAN'
            """)
    long countConfirmedPassengersByTourThucTe(@Param("maTourThucTe") String maTourThucTe);

    @Query("""
            SELECT COUNT(d) > 0 FROM DonDatTour d
            WHERE d.tourThucTe.MaTourThucTe = :maTourThucTe
              AND d.khachHang.MaKhachHang = :maKhachHang
              AND d.trangThai = 'DA_XAC_NHAN'
            """)
    boolean existsConfirmedKhachHangInTour(
            @Param("maTourThucTe") String maTourThucTe,
            @Param("maKhachHang") String maKhachHang
    );

    // UC51: Xuất dữ liệu đơn đặt tour cho Power BI
    @Query("""
            SELECT d FROM DonDatTour d
            JOIN FETCH d.tourThucTe ttt
            JOIN FETCH ttt.tourMau
            WHERE (:tuNgay IS NULL OR d.ngayDat >= :tuNgay)
              AND (:denNgay IS NULL OR d.ngayDat < :denNgay)
            ORDER BY d.ngayDat DESC
            """)
    java.util.List<DonDatTour> xuatDuLieu(
            @Param("tuNgay") LocalDateTime tuNgay,
            @Param("denNgay") LocalDateTime denNgay
    );
}
