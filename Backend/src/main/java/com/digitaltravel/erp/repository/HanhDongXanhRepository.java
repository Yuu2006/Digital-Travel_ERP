package com.digitaltravel.erp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.digitaltravel.erp.entity.HanhDongXanh;

@Repository
public interface HanhDongXanhRepository extends JpaRepository<HanhDongXanh, String> {

  @Query("""
      select hdx from HanhDongXanh hdx
      where exists (
          select 1 from HdxTourThucTe link
          where link.hanhDongXanh = hdx
            and link.tourThucTe.MaTourThucTe = :maTourThucTe
      )
      order by hdx.MaHanhDongXanh
      """)
  List<HanhDongXanh> findAvailableForTour(@Param("maTourThucTe") String maTourThucTe);

  @Query("""
      select count(hdx) > 0 from HanhDongXanh hdx
      where hdx.MaHanhDongXanh = :maHanhDongXanh
        and exists (
          select 1 from HdxTourThucTe link
          where link.hanhDongXanh = hdx
            and link.tourThucTe.MaTourThucTe = :maTourThucTe
        )
      """)
  boolean existsAvailableForTour(
      @Param("maHanhDongXanh") String maHanhDongXanh,
      @Param("maTourThucTe") String maTourThucTe);
}
