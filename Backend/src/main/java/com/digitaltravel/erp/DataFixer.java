package com.digitaltravel.erp;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DataFixer {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void init() {
        try {
            // Update the ThucDon field to remove "Tối: Đặc sản địa phương"
            String sql = "UPDATE LICHTRINHTOUR SET ThucDon = REPLACE(ThucDon, ' | Tối: Đặc sản địa phương', '') WHERE ThucDon LIKE '% | Tối: Đặc sản địa phương%'";
            int rows = jdbcTemplate.update(sql);
            System.out.println(">>> FIXED " + rows + " ROWS IN LICHTRINHTOUR, REMOVED TOI!");
        } catch (Exception e) {
            System.err.println(">>> ERROR FIXING DATA: " + e.getMessage());
        }
    }
}
