package com.digitaltravel.erp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.digitaltravel.erp.entity.LichTrinhTour;

@Repository
public interface LichTrinhTourRepository extends JpaRepository<LichTrinhTour, String> {

    @Query("SELECT lt FROM LichTrinhTour lt WHERE lt.tourMau.MaTourMau = :maTourMau ORDER BY lt.NgayThu")
    List<LichTrinhTour> findByMaTourMau(@Param("maTourMau") String maTourMau);

    @Query("""
            SELECT CASE WHEN COUNT(lt) > 0 THEN true ELSE false END
            FROM LichTrinhTour lt
            WHERE lt.tourMau.MaTourMau = :maTourMau AND lt.NgayThu = :ngayThu
            """)
    boolean existsByTourMauAndNgayThu(@Param("maTourMau") String maTourMau, @Param("ngayThu") Integer ngayThu);

    @Query("""
            SELECT CASE WHEN COUNT(lt) > 0 THEN true ELSE false END
            FROM LichTrinhTour lt
            WHERE lt.tourMau.MaTourMau = :maTourMau AND lt.NgayThu = :ngayThu AND lt.MaLichTrinhTour <> :excludeId
            """)
    boolean existsByTourMauAndNgayThuExcluding(
            @Param("maTourMau") String maTourMau,
            @Param("ngayThu") Integer ngayThu,
            @Param("excludeId") String excludeId
    );
}
