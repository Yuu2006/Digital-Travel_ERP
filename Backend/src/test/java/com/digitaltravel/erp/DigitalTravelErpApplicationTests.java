package com.digitaltravel.erp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.List;
import java.util.Map;

@SpringBootTest
class DigitalTravelErpApplicationTests {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Test
	void testDatabaseDataEncoding() {
		System.out.println("=== DIAGNOSING DATABASE ENCODING ===");
		try {
			List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT HoTen FROM TAIKHOAN WHERE ROWNUM <= 5");
			for (Map<String, Object> row : rows) {
				String hoTen = (String) row.get("HOTEN");
				System.out.println("HoTen: " + hoTen);
				System.out.print("Bytes (UTF-8): ");
				if (hoTen != null) {
					for (byte b : hoTen.getBytes(java.nio.charset.StandardCharsets.UTF_8)) {
						System.out.print(String.format("%02X ", b));
					}
				}
				System.out.println();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("=====================================");
	}

}
