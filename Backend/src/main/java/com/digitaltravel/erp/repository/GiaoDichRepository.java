package com.digitaltravel.erp.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.digitaltravel.erp.entity.GiaoDich;

@Repository
public interface GiaoDichRepository extends JpaRepository<GiaoDich, String> {

    // Tìm giao dịch thanh toán đang chờ/đã hoàn thành của 1 đơn
    @Query("SELECT g FROM GiaoDich g WHERE g.donDatTour.maDatTour = :maDatTour AND g.loaiGiaoDich = 'THANH_TOAN' ORDER BY g.ngayThanhToan DESC")
    List<GiaoDich> findByMaDatTour(@Param("maDatTour") String maDatTour);

    @Query("""
            SELECT g FROM GiaoDich g
            JOIN FETCH g.donDatTour d
            WHERE g.loaiGiaoDich = 'THANH_TOAN'
              AND g.trangThai = 'CHO_THANH_TOAN'
              AND (g.maGDNH IS NULL OR g.maGDNH NOT LIKE 'KHXN:%')
              AND d.trangThai = 'CHO_XAC_NHAN'
              AND g.ngayThanhToan < :thoiDiemHetHan
            """)
    List<GiaoDich> findQrChoThanhToanQuaHan(@Param("thoiDiemHetHan") LocalDateTime thoiDiemHetHan);

    // Kiểm tra idempotency: transId đã xử lý chưa
    Optional<GiaoDich> findByMaGDNH(String maGDNH);

    // Lấy giao dịch thanh toán thành công của 1 đơn
    @Query("SELECT g FROM GiaoDich g WHERE g.donDatTour.maDatTour = :maDatTour AND g.trangThai = 'THANH_CONG'")
    Optional<GiaoDich> findThanhCongByMaDatTour(@Param("maDatTour") String maDatTour);

    @Query("""
            SELECT g FROM GiaoDich g
            WHERE g.donDatTour.maDatTour = :maDatTour
              AND g.loaiGiaoDich = 'HOAN_TIEN'
              AND g.trangThai = 'CHO_THANH_TOAN'
            """)
    Optional<GiaoDich> findPendingRefundByMaDatTour(@Param("maDatTour") String maDatTour);

    // UC50: Lấy giao dịch chờ hoàn tiền
    @Query("""
            SELECT g FROM GiaoDich g
            JOIN FETCH g.donDatTour d
            WHERE g.loaiGiaoDich = 'HOAN_TIEN'
            ORDER BY g.ngayThanhToan DESC
            """)
    org.springframework.data.domain.Page<GiaoDich> findChoHoanTien(
            org.springframework.data.domain.Pageable pageable
    );

    // UC51: Xuất dữ liệu giao dịch cho Power BI
    @Query("""
            SELECT g FROM GiaoDich g
            JOIN FETCH g.donDatTour
            WHERE (:tuNgay IS NULL OR g.ngayThanhToan >= :tuNgay)
              AND (:denNgay IS NULL OR g.ngayThanhToan < :denNgay)
            ORDER BY g.ngayThanhToan DESC
            """)
    java.util.List<GiaoDich> xuatDuLieu(
            @Param("tuNgay") java.time.LocalDateTime tuNgay,
            @Param("denNgay") java.time.LocalDateTime denNgay
    );
}
