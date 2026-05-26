package com.digitaltravel.erp.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.digitaltravel.erp.entity.PhanCongTour;

@Repository
public interface PhanCongTourRepository extends JpaRepository<PhanCongTour, String> {

    // Lấy tất cả phân công của 1 tour
    @Query("""
            SELECT pc FROM PhanCongTour pc
            JOIN FETCH pc.nhanVien nv
            JOIN FETCH nv.taiKhoan
            WHERE pc.tourThucTe.MaTourThucTe = :maTour
              AND pc.TrangThaiChapNhan = 'DA_DONG_Y'
            """)
    List<PhanCongTour> findActiveByMaTour(@Param("maTour") String maTour);

    // HDV xem yêu cầu phân công và tour đã nhận, không trả về yêu cầu đã từ chối.
    @Query("""
            SELECT pc FROM PhanCongTour pc
            JOIN FETCH pc.tourThucTe tt
            JOIN FETCH tt.tourMau
            WHERE pc.nhanVien.MaNhanVien = :maNhanVien
              AND pc.TrangThaiChapNhan <> 'TU_CHOI'
            ORDER BY tt.NgayKhoiHanh DESC
            """)
    List<PhanCongTour> findByMaNhanVien(@Param("maNhanVien") String maNhanVien);

    @Query("""
            SELECT COUNT(pc) > 0 FROM PhanCongTour pc
            WHERE pc.tourThucTe.MaTourThucTe = :maTourThucTe
            """)
    boolean existsByTourThucTe_MaTourThucTe(@Param("maTourThucTe") String maTourThucTe);

    @Query("""
            SELECT COUNT(pc) > 0 FROM PhanCongTour pc
            WHERE pc.tourThucTe.MaTourThucTe = :maTourThucTe
              AND pc.TrangThaiChapNhan = 'DA_DONG_Y'
            """)
    boolean existsAcceptedByTourThucTe_MaTourThucTe(@Param("maTourThucTe") String maTourThucTe);

    @Query("""
            SELECT COUNT(pc) > 0 FROM PhanCongTour pc
            WHERE pc.tourThucTe.MaTourThucTe = :maTour
              AND pc.nhanVien.taiKhoan.MaTaiKhoan = :maTaiKhoan
              AND pc.TrangThaiChapNhan = 'DA_DONG_Y'
            """)
    boolean existsByMaTourAndMaTaiKhoan(
            @Param("maTour") String maTour,
            @Param("maTaiKhoan") String maTaiKhoan
    );

    @Query("""
            SELECT COUNT(pc) > 0 FROM PhanCongTour pc
            WHERE pc.tourThucTe.MaTourThucTe = :maTour
              AND pc.nhanVien.taiKhoan.MaTaiKhoan = :maTaiKhoan
              AND pc.TrangThaiChapNhan <> 'TU_CHOI'
            """)
    boolean existsViewableByMaTourAndMaTaiKhoan(
            @Param("maTour") String maTour,
            @Param("maTaiKhoan") String maTaiKhoan
    );

    // Tìm HDV khả dụng: không bị phân công trùng lịch theo toàn bộ thời lượng tour.
    @Query(value = """
            SELECT nv.MaNhanVien
            FROM NHANVIEN nv
            JOIN TAIKHOAN tk ON tk.MaTaiKhoan = nv.MaTaiKhoan
            WHERE nv.TrangThaiLamViec = 'HOAT_DONG'
              AND tk.VaiTro = 'HDV'
              AND tk.TrangThai = 'HOAT_DONG'
              AND NOT EXISTS (
                  SELECT 1
                  FROM PHANCONGTOUR pc
                  JOIN TOURTHUCTE tt ON tt.MaTourThucTe = pc.MaTourThucTe
                  JOIN TOURMAU tm ON tm.MaTourMau = tt.MaTourMau
                  WHERE pc.MaNhanVien = nv.MaNhanVien
                    AND pc.TrangThaiChapNhan <> 'TU_CHOI'
                    AND tt.NgayKhoiHanh < :ngayKetThuc + (12/24)
                    AND :ngayKhoiHanh < tt.NgayKhoiHanh + tm.ThoiLuong + (12/24)
              )
            """, nativeQuery = true)
    List<String> findMaNhanVienKhaDung(
            @Param("ngayKhoiHanh") LocalDate ngayKhoiHanh,
            @Param("ngayKetThuc") LocalDate ngayKetThuc
    );
}
