package com.digitaltravel.erp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.digitaltravel.erp.entity.ChiTietDichVu;

@Repository
public interface ChiTietDichVuRepository extends JpaRepository<ChiTietDichVu, String> {

    @Query("""
            SELECT cv FROM ChiTietDichVu cv
            JOIN FETCH cv.dichVuThem
            WHERE cv.donDatTour.maDatTour = :maDatTour
            """)
    List<ChiTietDichVu> findByMaDatTour(@Param("maDatTour") String maDatTour);
}
