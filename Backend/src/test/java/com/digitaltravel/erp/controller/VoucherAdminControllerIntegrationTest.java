package com.digitaltravel.erp.controller;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class VoucherAdminControllerIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @LocalServerPort
    private int port;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @AfterEach
    void cleanup() {
        jdbcTemplate.update("DELETE FROM VOUCHER WHERE MaCode LIKE 'TEST_API_%'");
    }

    @Test
    void taoVoucher_quaApiTraVeCreated() throws Exception {
        String token = dangNhapSales();
        String code = "TEST_API_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String body = """
                {
                  "maCode": "%s",
                  "loaiUuDai": "PHAN_TRAM",
                  "giaTriGiam": 10,
                  "mucGiamToiDa": 500000,
                  "dieuKienApDung": "Test API persistence",
                  "soLuotPhatHanh": 5,
                  "ngayHieuLuc": "2026-06-01",
                  "ngayHetHan": "2026-06-30"
                }
                """.formatted(code);

        HttpResponse<String> result = postJson("/api/kinh-doanh/voucher", body, token);

        if (result.statusCode() != 201) {
            throw new AssertionError(result.body());
        }
    }

    private String dangNhapSales() throws Exception {
        HttpResponse<String> login = postJson("/api/auth/dang-nhap", """
                {
                  "tenDangNhap": "sales01",
                  "matKhau": "password"
                }
                """, null);
        JsonNode root = objectMapper.readTree(login.body());
        return root.path("data").path("accessToken").asText();
    }

    private HttpResponse<String> postJson(String path, String body, String token) throws Exception {
        HttpRequest.Builder request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + path))
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .POST(HttpRequest.BodyPublishers.ofString(body));
        if (token != null) {
            request.header("Authorization", "Bearer " + token);
        }
        return httpClient.send(request.build(), HttpResponse.BodyHandlers.ofString());
    }
}
