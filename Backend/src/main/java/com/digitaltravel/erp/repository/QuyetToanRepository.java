package com.digitaltravel.erp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.digitaltravel.erp.entity.QuyetToan;

@Repository
public interface QuyetToanRepository extends JpaRepository<QuyetToan, String> {

        @Query("SELECT qt FROM QuyetToan qt WHERE qt.tourThucTe.MaTourThucTe = :maTour")
        Optional<QuyetToan> findByTourThucTe_MaTourThucTe(@Param("maTour") String maTour);

        @Query("SELECT CASE WHEN COUNT(qt) > 0 THEN true ELSE false END FROM QuyetToan qt WHERE qt.tourThucTe.MaTourThucTe = :maTour")
        boolean existsByTourThucTe_MaTourThucTe(@Param("maTour") String maTour);

        @Query("""
                        SELECT qt FROM QuyetToan qt
                        JOIN FETCH qt.tourThucTe tt
                        JOIN FETCH tt.tourMau
                        JOIN FETCH qt.nhanVien nv
                        JOIN FETCH nv.taiKhoan
                        WHERE (:trangThai IS NULL OR qt.TrangThai = :trangThai)
                        ORDER BY qt.NgayQuyetToan DESC
                        """)
        Page<QuyetToan> findByTrangThai(@Param("trangThai") String trangThai, Pageable pageable);

        /* Lấy các quyết toán chưa chốt thuộc những tour HDV đã đồng ý phụ trách. */
        @Query("""
                        SELECT qt FROM QuyetToan qt
                        JOIN FETCH qt.tourThucTe tt
                        JOIN FETCH tt.tourMau
                        JOIN FETCH qt.nhanVien nv
                        JOIN FETCH nv.taiKhoan
                        JOIN PhanCongTour pc ON pc.tourThucTe.MaTourThucTe = tt.MaTourThucTe
                        WHERE pc.nhanVien.taiKhoan.MaTaiKhoan = :maTaiKhoan
                          AND pc.TrangThaiChapNhan = 'DA_DONG_Y'
                          AND qt.TrangThai = 'CHUA_QUYET_TOAN'
                        ORDER BY qt.NgayQuyetToan DESC
                        """)
        List<QuyetToan> findChuaQuyetToanTheoHdv(@Param("maTaiKhoan") String maTaiKhoan);

        // Thống kê doanh thu theo khoảng thời gian (quyết toán đã chốt)
        @Query("""
                        SELECT SUM(qt.TongDoanhThu), COUNT(qt)
                        FROM QuyetToan qt
                        WHERE qt.TrangThai = 'DA_QUYET_TOAN'
                          AND qt.NgayQuyetToan BETWEEN :tuNgay AND :denNgay
                        """)
        List<Object[]> thongKeDoanhThu(
                        @Param("tuNgay") java.time.LocalDateTime tuNgay,
                        @Param("denNgay") java.time.LocalDateTime denNgay);

        // Top tour doanh thu
        @Query("""
                        SELECT qt.tourThucTe.MaTourThucTe, qt.tourThucTe.tourMau.TieuDe, COUNT(dt), SUM(qt.TongDoanhThu)
                        FROM QuyetToan qt
                        JOIN qt.tourThucTe tt
                        JOIN tt.tourMau tm
                        LEFT JOIN DonDatTour dt ON dt.tourThucTe.MaTourThucTe = qt.tourThucTe.MaTourThucTe
                        WHERE qt.TrangThai = 'DA_QUYET_TOAN'
                          AND qt.NgayQuyetToan BETWEEN :tuNgay AND :denNgay
                        GROUP BY qt.tourThucTe.MaTourThucTe, qt.tourThucTe.tourMau.TieuDe
                        ORDER BY SUM(qt.TongDoanhThu) DESC
                        """)
        List<Object[]> topTourDoanhThu(
                        @Param("tuNgay") java.time.LocalDateTime tuNgay,
                        @Param("denNgay") java.time.LocalDateTime denNgay);

        // UC51: Xuất dữ liệu doanh thu cho Power BI
        @Query("""
                        SELECT qt FROM QuyetToan qt
                        JOIN FETCH qt.tourThucTe tt
                        JOIN FETCH tt.tourMau
                        WHERE (:tuNgay IS NULL OR qt.NgayQuyetToan >= :tuNgay)
                          AND (:denNgay IS NULL OR qt.NgayQuyetToan < :denNgay)
                        ORDER BY qt.NgayQuyetToan DESC
                        """)
        java.util.List<QuyetToan> xuatDuLieuDoanhThu(
                        @Param("tuNgay") java.time.LocalDateTime tuNgay,
                        @Param("denNgay") java.time.LocalDateTime denNgay);
}
