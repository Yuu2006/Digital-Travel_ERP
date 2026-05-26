package com.digitaltravel.erp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.digitaltravel.erp.entity.DichVuThem;

@Repository
public interface DichVuThemRepository extends JpaRepository<DichVuThem, String> {
}
