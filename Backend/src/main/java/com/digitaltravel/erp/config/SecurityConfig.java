package com.digitaltravel.erp.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static com.digitaltravel.erp.config.VaiTroConst.ADMIN;
import static com.digitaltravel.erp.config.VaiTroConst.DIEUHANH;
import static com.digitaltravel.erp.config.VaiTroConst.HDV;
import static com.digitaltravel.erp.config.VaiTroConst.KETOAN;
import static com.digitaltravel.erp.config.VaiTroConst.KHACHHANG;
import static com.digitaltravel.erp.config.VaiTroConst.KINHDOANH;
import static com.digitaltravel.erp.config.VaiTroConst.SANPHAM;
import com.digitaltravel.erp.service.TaiKhoanDetailsService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final TaiKhoanDetailsService taiKhoanDetailsService;
    private final JwtAuthEntryPoint jwtAuthEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    @Order(1)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // Cho phép tất cả các ROLE nội bộ xem thông tin (GET) để phục vụ Dashboard và
                        // tra cứu chéo
                        .requestMatchers(HttpMethod.GET,
                                "/api/kinh-doanh/khach-hang",
                                "/api/kinh-doanh/dat-tour",
                                "/api/kinh-doanh/dat-tour/**",
                                "/api/san-pham/tour-thuc-te",
                                "/api/san-pham/tour-thuc-te/**",
                                "/api/dieu-hanh/tour-thuc-te",
                                "/api/dieu-hanh/tour-thuc-te/**",
                                "/api/san-pham/tour-mau",
                                "/api/san-pham/tour-mau/**",
                                "/api/san-pham/dich-vu-them",
                                "/api/san-pham/dich-vu-them/**",
                                "/api/san-pham/hanh-dong-xanh",
                                "/api/san-pham/hanh-dong-xanh/**",
                                "/api/huong-dan-vien/su-co",
                                "/api/quan-tri/tai-khoan/**",
                                "/api/quan-tri/nhan-vien",
                                "/api/quan-tri/nhan-vien/**")
                        .hasAnyRole(ADMIN, SANPHAM, KINHDOANH, DIEUHANH, KETOAN, HDV)
                        .requestMatchers("/api/quan-tri/**").hasRole(ADMIN)
                        .requestMatchers("/api/san-pham/**").hasAnyRole(ADMIN, SANPHAM)
                        .requestMatchers("/api/kinh-doanh/**").hasAnyRole(ADMIN, KINHDOANH)
                        .requestMatchers("/api/dieu-hanh/**").hasAnyRole(ADMIN, DIEUHANH, SANPHAM)
                        .requestMatchers("/api/huong-dan-vien/**").hasAnyRole(ADMIN, DIEUHANH, HDV)
                        .requestMatchers("/api/ke-toan/**").hasAnyRole(ADMIN, KETOAN)
                        .requestMatchers("/api/khach-hang/**").hasAnyRole(ADMIN, KHACHHANG)
                        .requestMatchers("/api/thanh-toan/**").hasAnyRole(ADMIN, KHACHHANG)
                        .anyRequest().authenticated())
                .authenticationProvider(authenticationProvider())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jwtAuthEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of(
                "http://localhost:*",
                "http://127.0.0.1:*",
                "http://10.53.153.35:*",
                "http://192.168.*:*",
                "http://10.*:*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(taiKhoanDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
