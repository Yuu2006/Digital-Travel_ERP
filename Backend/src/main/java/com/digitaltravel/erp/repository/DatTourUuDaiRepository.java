package com.digitaltravel.erp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.digitaltravel.erp.entity.DatTourUuDai;
import com.digitaltravel.erp.entity.DatTourUuDaiId;

@Repository
public interface DatTourUuDaiRepository extends JpaRepository<DatTourUuDai, DatTourUuDaiId> {

    @Query("SELECT d FROM DatTourUuDai d JOIN FETCH d.voucher WHERE d.donDatTour.maDatTour = :maDatTour")
    List<DatTourUuDai> findByMaDatTour(@Param("maDatTour") String maDatTour);

    @Query("""
            SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END
            FROM DatTourUuDai d
            WHERE d.donDatTour.maDatTour = :maDatTour
              AND d.voucher.MaVoucher = :maVoucher
            """)
    boolean existsByDatTourAndVoucher(
            @Param("maDatTour") String maDatTour,
            @Param("maVoucher") String maVoucher
    );
}
