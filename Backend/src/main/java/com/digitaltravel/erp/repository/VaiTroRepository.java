package com.digitaltravel.erp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.digitaltravel.erp.entity.VaiTro;

@Repository
public interface VaiTroRepository extends JpaRepository<VaiTro, String> {
}
