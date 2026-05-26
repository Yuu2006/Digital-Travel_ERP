package com.digitaltravel.erp.config;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.digitaltravel.erp.entity.TaiKhoan;

public class TaiKhoanDetails implements UserDetails {

    private final TaiKhoan taiKhoan;

    public TaiKhoanDetails(TaiKhoan taiKhoan) {
        this.taiKhoan = taiKhoan;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // MaVaiTro = "ADMIN" → GrantedAuthority = "ROLE_ADMIN"
        return List.of(new SimpleGrantedAuthority("ROLE_" + taiKhoan.getVaiTro().getMaVaiTro()));
    }

    @Override
    public String getPassword() {
        return taiKhoan.getMatKhau() == null ? null : taiKhoan.getMatKhau().trim();
    }

    @Override
    public String getUsername() {
        return taiKhoan.getTenDangNhap() == null ? null : taiKhoan.getTenDangNhap().trim();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !"BI_KHOA".equals(taiKhoan.getTrangThai()) && !"KHOA".equals(taiKhoan.getTrangThai());
    }

    @Override
    public boolean isEnabled() {
        return "HOAT_DONG".equals(taiKhoan.getTrangThai());
    }

    public TaiKhoan getTaiKhoan() {
        return taiKhoan;
    }
}
