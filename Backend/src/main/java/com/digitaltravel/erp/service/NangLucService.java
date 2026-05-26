package com.digitaltravel.erp.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.digitaltravel.erp.dto.requests.NangLucRequest;
import com.digitaltravel.erp.dto.responses.NangLucResponse;
import com.digitaltravel.erp.entity.NangLucNhanVien;
import com.digitaltravel.erp.entity.NhanVien;
import com.digitaltravel.erp.exception.AppException;
import com.digitaltravel.erp.repository.NangLucNhanVienRepository;
import com.digitaltravel.erp.repository.NhanVienRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NangLucService {

    private final NangLucNhanVienRepository nangLucNhanVienRepository;
    private final NhanVienRepository nhanVienRepository;

    // ── UC63: Xem năng lực nhân viên ─────────────────────────────────────────
    public NangLucResponse layNangLuc(String maNhanVien) {
        NangLucNhanVien nl = nangLucNhanVienRepository.findByMaNhanVien(maNhanVien)
                .orElse(null);
        if (nl == null) {
            // Trả về bản trống nếu chưa có
            NhanVien nv = nhanVienRepository.findById(maNhanVien)
                    .orElseThrow(() -> AppException.notFound("Khong tim thay nhan vien: " + maNhanVien));
            return NangLucResponse.builder()
                    .maNhanVien(maNhanVien)
                    .danhGia(java.math.BigDecimal.ZERO)
                    .soDanhGia(0)
                    .build();
        }
        return toResponse(nl);
    }

    // ── UC63: Cập nhật năng lực nhân viên (Manager/Admin) ────────────────────
    @Transactional
    public NangLucResponse capNhatNangLuc(String maNhanVien, NangLucRequest request, String nguoiCapNhat) {
        NhanVien nv = nhanVienRepository.findById(maNhanVien)
                .orElseThrow(() -> AppException.notFound("Khong tim thay nhan vien: " + maNhanVien));

        NangLucNhanVien nl = nangLucNhanVienRepository.findByMaNhanVien(maNhanVien)
                .orElseGet(() -> {
                    NangLucNhanVien moi = new NangLucNhanVien();
                    moi.setMaNangLucNhanVien("NLNV_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
                    moi.setNhanVien(nv);
                    return moi;
                });

        if (request.getNgonNgu() != null) nl.setNgonNgu(request.getNgonNgu());
        if (request.getChungChi() != null) nl.setChungChi(request.getChungChi());
        if (request.getChuyenMon() != null) nl.setChuyenMon(request.getChuyenMon());
        nangLucNhanVienRepository.save(nl);
        return toResponse(nl);
    }

    // ── Mapper ───────────────────────────────────────────────────────────────
    private NangLucResponse toResponse(NangLucNhanVien nl) {
        return NangLucResponse.builder()
                .maNangLucNhanVien(nl.getMaNangLucNhanVien())
                .maNhanVien(nl.getNhanVien().getMaNhanVien())
                .ngonNgu(nl.getNgonNgu())
                .chungChi(nl.getChungChi())
                .chuyenMon(nl.getChuyenMon())
                .danhGia(nl.getDanhGia() != null ? nl.getDanhGia() : java.math.BigDecimal.ZERO)
                .soDanhGia(nl.getSoDanhGia() != null ? nl.getSoDanhGia() : 0)
                .build();
    }
}
