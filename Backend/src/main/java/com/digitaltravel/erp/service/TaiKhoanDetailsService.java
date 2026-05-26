package com.digitaltravel.erp.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.digitaltravel.erp.config.TaiKhoanDetails;
import com.digitaltravel.erp.entity.TaiKhoan;
import com.digitaltravel.erp.repository.TaiKhoanRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaiKhoanDetailsService implements UserDetailsService {

    private final TaiKhoanRepository taiKhoanRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String tenDangNhap) throws UsernameNotFoundException {
        TaiKhoan taiKhoan = taiKhoanRepository.findByTenDangNhapWithVaiTro(tenDangNhap)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài khoản: " + tenDangNhap));
        return new TaiKhoanDetails(taiKhoan);
    }
}
