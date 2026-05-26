package com.digitaltravel.erp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.digitaltravel.erp.entity.ChiTietDatTour;

@Repository
public interface ChiTietDatTourRepository extends JpaRepository<ChiTietDatTour, String> {

    @Query("""
            SELECT ct FROM ChiTietDatTour ct
            LEFT JOIN FETCH ct.khachHang hcs
            LEFT JOIN FETCH hcs.taiKhoan
            LEFT JOIN FETCH ct.nguoiDongHanh
            WHERE ct.donDatTour.maDatTour = :maDatTour
            """)
    List<ChiTietDatTour> findByMaDatTour(@Param("maDatTour") String maDatTour);

    boolean existsByDonDatTour_MaDatTourAndKhachHang_MaKhachHang(
            String maDatTour, String maKhachHang);

    long countByDonDatTour_MaDatTour(String maDatTour);
}
