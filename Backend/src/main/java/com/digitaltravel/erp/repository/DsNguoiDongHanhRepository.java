package com.digitaltravel.erp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

import com.digitaltravel.erp.entity.DsNguoiDongHanh;

@Repository
public interface DsNguoiDongHanhRepository extends JpaRepository<DsNguoiDongHanh, String> {
    List<DsNguoiDongHanh> findByDonDatTour_MaDatTour(String maDatTour);
}
