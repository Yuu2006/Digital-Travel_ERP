package com.digitaltravel.erp.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.digitaltravel.erp.entity.ChiPhiThucTe;

@Repository
public interface ChiPhiThucTeRepository extends JpaRepository<ChiPhiThucTe, String> {

    @Query("SELECT c FROM ChiPhiThucTe c JOIN FETCH c.nhanVien nv JOIN FETCH nv.taiKhoan WHERE c.tourThucTe.MaTourThucTe = :maTour ORDER BY c.NgayKhai DESC")
    // UC44 - Cập nhật chi phí thực tế: Lấy chi phí theo tour
    List<ChiPhiThucTe> findByMaTour(@Param("maTour") String maTour);

    @Query("""
            SELECT c FROM ChiPhiThucTe c
            JOIN FETCH c.tourThucTe tt
            JOIN FETCH c.nhanVien nv
            JOIN FETCH nv.taiKhoan tk
            WHERE tk.MaTaiKhoan = :maTaiKhoan
            ORDER BY c.NgayKhai DESC
            """)
    // UC44 - Cập nhật chi phí thực tế: Lấy chi phí theo HDV
    List<ChiPhiThucTe> findByHdvTaiKhoan(@Param("maTaiKhoan") String maTaiKhoan);

    // Kế toán: DS chi phí chờ duyệt
    @Query("""
            SELECT c FROM ChiPhiThucTe c JOIN FETCH c.nhanVien nv JOIN FETCH nv.taiKhoan
            WHERE (:trangThai IS NULL OR c.TrangThaiDuyet = :trangThai)
            AND (:maTour IS NULL OR c.tourThucTe.MaTourThucTe = :maTour)
            ORDER BY c.NgayKhai DESC
            """)
    // UC47 - Phê duyệt chi phí thực tế: Lọc chi phí chờ xử lý
    Page<ChiPhiThucTe> findByTrangThai(@Param("trangThai") String trangThai, @Param("maTour") String maTour, Pageable pageable);

    // Tổng chi phí đã duyệt của 1 tour (dùng cho quyết toán)
    @Query("SELECT c FROM ChiPhiThucTe c WHERE c.tourThucTe.MaTourThucTe = :maTour AND c.TrangThaiDuyet = 'DA_DUYET'")
    // UC48 - Quyết toán tour: Lấy chi phí đã duyệt
    List<ChiPhiThucTe> findDaDuyetByMaTour(@Param("maTour") String maTour);

    @Query("""
            SELECT c FROM ChiPhiThucTe c
            JOIN FETCH c.tourThucTe tt
            JOIN FETCH c.nhanVien nv
            JOIN FETCH nv.taiKhoan
            ORDER BY c.NgayKhai DESC
            """)
    List<ChiPhiThucTe> findAllWithRelations();

    @Query("""
            SELECT c FROM ChiPhiThucTe c
            JOIN FETCH c.tourThucTe
            WHERE (:tuNgay IS NULL OR c.NgayKhai >= :tuNgay)
              AND (:denNgay IS NULL OR c.NgayKhai < :denNgay)
            ORDER BY c.NgayKhai DESC
            """)
    // UC51 - Trích xuất dữ liệu phân tích: Xuất dữ liệu chi phí
    List<ChiPhiThucTe> xuatDuLieu(
            @Param("tuNgay") java.time.LocalDateTime tuNgay,
            @Param("denNgay") java.time.LocalDateTime denNgay
    );
}
