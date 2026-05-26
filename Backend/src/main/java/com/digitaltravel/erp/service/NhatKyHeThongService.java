package com.digitaltravel.erp.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.digitaltravel.erp.dto.responses.NhatKyHeThongResponse;
import com.digitaltravel.erp.entity.NhatKyHeThong;
import com.digitaltravel.erp.entity.TaiKhoan;
import com.digitaltravel.erp.repository.NhatKyHeThongRepository;
import com.digitaltravel.erp.repository.TaiKhoanRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NhatKyHeThongService {

    public static final String THEM = "THEM";
    public static final String CAP_NHAT = "CAP_NHAT";
    public static final String XOA = "XOA";

    private final NhatKyHeThongRepository nhatKyHeThongRepository;
    private final TaiKhoanRepository taiKhoanRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void ghiNhan(String maTaiKhoan, String hanhDong, String doiTuong, String maDoiTuong) {
        TaiKhoan taiKhoan = maTaiKhoan != null
                ? taiKhoanRepository.findById(maTaiKhoan).orElse(null)
                : null;
        NhatKyHeThong log = new NhatKyHeThong();
        log.setMaNhatKyHeThong("NKHT_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        log.setTaiKhoan(taiKhoan);
        log.setHanhDong(hanhDong);
        log.setDoiTuong(truncate(doiTuong, 100));
        log.setMaDoiTuong(truncate(maDoiTuong, 50));
        nhatKyHeThongRepository.save(log);
    }

    @Transactional(readOnly = true)
    public Page<NhatKyHeThongResponse> timKiem(
            String maTaiKhoan,
            String hanhDong,
            String doiTuong,
            String maDoiTuong,
            LocalDateTime tuThoiGian,
            LocalDateTime denThoiGian,
            Pageable pageable) {
        return nhatKyHeThongRepository
                .timKiem(maTaiKhoan, hanhDong, doiTuong, maDoiTuong, tuThoiGian, denThoiGian, pageable)
                .map(this::toResponse);
    }

    private NhatKyHeThongResponse toResponse(NhatKyHeThong log) {
        TaiKhoan tk = log.getTaiKhoan();
        return NhatKyHeThongResponse.builder()
                .maNhatKyHeThong(log.getMaNhatKyHeThong())
                .maTaiKhoan(tk != null ? tk.getMaTaiKhoan() : null)
                .tenDangNhap(tk != null ? tk.getTenDangNhap() : null)
                .hanhDong(log.getHanhDong())
                .doiTuong(log.getDoiTuong())
                .maDoiTuong(log.getMaDoiTuong())
                .thoiGian(log.getThoiGian())
                .build();
    }

    private String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }
}
