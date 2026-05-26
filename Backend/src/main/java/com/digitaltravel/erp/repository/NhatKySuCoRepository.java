package com.digitaltravel.erp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.digitaltravel.erp.entity.NhatKySuCo;

@Repository
public interface NhatKySuCoRepository extends JpaRepository<NhatKySuCo, String> {

    @Query("""
            SELECT n FROM NhatKySuCo n
            WHERE n.tourThucTe.MaTourThucTe = :maTour
              AND (:mucDo IS NULL OR n.MucDo = :mucDo)
            ORDER BY n.ThoiGianBaoCao DESC
            """)
    List<NhatKySuCo> findByMaTour(
            @Param("maTour") String maTour,
            @Param("mucDo") String mucDo
    );

    @Query("""
            SELECT n FROM NhatKySuCo n
            JOIN FETCH n.tourThucTe tt
            JOIN FETCH n.nhanVienBaoCao nv
            JOIN FETCH nv.taiKhoan tk
            WHERE tk.MaTaiKhoan = :maTaiKhoan
              AND (:mucDo IS NULL OR n.MucDo = :mucDo)
            ORDER BY n.ThoiGianBaoCao DESC
            """)
    List<NhatKySuCo> findByHdvTaiKhoan(
            @Param("maTaiKhoan") String maTaiKhoan,
            @Param("mucDo") String mucDo
    );

    @Query("""
            SELECT n FROM NhatKySuCo n
            JOIN FETCH n.tourThucTe tt
            JOIN FETCH n.nhanVienBaoCao nv
            JOIN FETCH nv.taiKhoan
            WHERE (:mucDo IS NULL OR n.MucDo = :mucDo)
            ORDER BY n.ThoiGianBaoCao DESC
            """)
    List<NhatKySuCo> findAllWithRelations(@Param("mucDo") String mucDo);
}
