package com.digitaltravel.erp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.digitaltravel.erp.entity.NangLucNhanVien;

@Repository
public interface NangLucNhanVienRepository extends JpaRepository<NangLucNhanVien, String> {

    @Query("SELECT n FROM NangLucNhanVien n WHERE n.nhanVien.MaNhanVien = :maNhanVien")
    Optional<NangLucNhanVien> findByMaNhanVien(@Param("maNhanVien") String maNhanVien);
}
