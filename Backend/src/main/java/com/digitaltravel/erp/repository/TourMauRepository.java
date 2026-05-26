package com.digitaltravel.erp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.digitaltravel.erp.entity.TourMau;

@Repository
public interface TourMauRepository extends JpaRepository<TourMau, String> {

    @Query("""
            SELECT tm FROM TourMau tm
            WHERE (:tieuDe IS NULL OR LOWER(tm.TieuDe) LIKE LOWER(CONCAT('%', :tieuDe, '%')))
              AND (:thoiLuongMin IS NULL OR tm.ThoiLuong >= :thoiLuongMin)
              AND (:thoiLuongMax IS NULL OR tm.ThoiLuong <= :thoiLuongMax)
            """)
    Page<TourMau> timKiem(
            @Param("tieuDe") String tieuDe,
            @Param("thoiLuongMin") Integer thoiLuongMin,
            @Param("thoiLuongMax") Integer thoiLuongMax,
            Pageable pageable
    );
}
