package com.digitaltravel.erp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.digitaltravel.erp.entity.DichVuTourThucTe;
import com.digitaltravel.erp.entity.DichVuTourThucTeId;

public interface DichVuTourThucTeRepository extends JpaRepository<DichVuTourThucTe, DichVuTourThucTeId> {
    @org.springframework.data.jpa.repository.Modifying
    @Query("DELETE FROM DichVuTourThucTe d WHERE d.tourThucTe.MaTourThucTe = :maTourThucTe")
    void deleteByTourThucTe_MaTourThucTe(@Param("maTourThucTe") String maTourThucTe);

    @Query("""
            select link from DichVuTourThucTe link
            join fetch link.dichVuThem
            where link.tourThucTe.MaTourThucTe = :maTourThucTe
            order by link.dichVuThem.MaDichVuThem
            """)
    List<DichVuTourThucTe> findByMaTourThucTe(@Param("maTourThucTe") String maTourThucTe);

    @Query("SELECT COUNT(d) > 0 FROM DichVuTourThucTe d WHERE d.dichVuThem.MaDichVuThem = :maDichVuThem AND d.tourThucTe.MaTourThucTe = :maTourThucTe")
    boolean existsByDichVuThem_MaDichVuThemAndTourThucTe_MaTourThucTe(
            @Param("maDichVuThem") String maDichVuThem, 
            @Param("maTourThucTe") String maTourThucTe);
}
