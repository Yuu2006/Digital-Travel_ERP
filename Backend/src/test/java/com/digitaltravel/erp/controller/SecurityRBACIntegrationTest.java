package com.digitaltravel.erp.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.digitaltravel.erp.config.JwtUtil;
import com.digitaltravel.erp.config.TaiKhoanDetails;
import com.digitaltravel.erp.entity.TaiKhoan;
import com.digitaltravel.erp.entity.VaiTro;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.mockito.Mockito;
import static org.mockito.ArgumentMatchers.anyString;
import org.junit.jupiter.api.BeforeEach;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SecurityRBACIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private JwtUtil jwtUtil;

    @MockitoBean
    private com.digitaltravel.erp.service.TaiKhoanDetailsService taiKhoanDetailsService;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    private TaiKhoanDetails testUserDetails;

    @BeforeEach
    public void setup() {
        TaiKhoan tk = new TaiKhoan();
        tk.setTenDangNhap("testuser");
        tk.setHoTen("Test User");
        VaiTro vt = new VaiTro();
        vt.setMaVaiTro("TEST"); // Will be overridden in generateToken
        vt.setTenHienThi("Test");
        tk.setVaiTro(vt);
        testUserDetails = new TaiKhoanDetails(tk);
        Mockito.when(taiKhoanDetailsService.loadUserByUsername(anyString())).thenReturn(testUserDetails);
    }

    private String generateToken(String roleId, String roleName) {
        testUserDetails.getTaiKhoan().getVaiTro().setMaVaiTro(roleId);
        testUserDetails.getTaiKhoan().getVaiTro().setTenHienThi(roleName);
        return jwtUtil.generateToken(testUserDetails);
    }

    @Test
    public void givenRoleKhachHang_whenGetQuanTri_thenForbidden() throws Exception {
        String token = generateToken("KHACHHANG", "Khách Hàng");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/quan-tri/dang-ky-nhan-vien"))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .POST(HttpRequest.BodyPublishers.ofString("{}"))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(403, response.statusCode());
    }

    @Test
    public void givenRoleAdmin_whenGetQuanTri_thenOk() throws Exception {
        String token = generateToken("ADMIN", "Quản Trị");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/quan-tri/dang-ky-nhan-vien"))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .POST(HttpRequest.BodyPublishers.ofString("{}"))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        // Since the body is empty/invalid, it should return 400 Bad Request, NOT 403 or 401.
        assertEquals(400, response.statusCode());
    }

    @Test
    public void givenNoToken_whenGetQuanTri_thenUnauthorized() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/quan-tri/dang-ky-nhan-vien"))
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .POST(HttpRequest.BodyPublishers.ofString("{}"))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(401, response.statusCode());
    }
}
