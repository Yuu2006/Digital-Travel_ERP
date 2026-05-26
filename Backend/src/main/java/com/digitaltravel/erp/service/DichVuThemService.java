package com.digitaltravel.erp.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.digitaltravel.erp.dto.requests.DichVuThemRequest;
import com.digitaltravel.erp.dto.responses.DichVuThemResponse;
import com.digitaltravel.erp.entity.DichVuThem;
import com.digitaltravel.erp.exception.AppException;
import com.digitaltravel.erp.repository.DichVuTourThucTeRepository;
import com.digitaltravel.erp.repository.DichVuThemRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DichVuThemService {

    private final DichVuThemRepository dichVuThemRepository;
    private final DichVuTourThucTeRepository dichVuTourThucTeRepository;
    private final MaTuDongService maTuDongService;

    public List<DichVuThemResponse> danhSach() {
        return danhSach(null);
    }

    public List<DichVuThemResponse> danhSach(String maTourThucTe) {
        if (maTourThucTe != null && !maTourThucTe.isBlank()) {
            return dichVuTourThucTeRepository.findByMaTourThucTe(maTourThucTe).stream()
                    .map(link -> toResponse(link.getDichVuThem()))
                    .toList();
        }
        return dichVuThemRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public DichVuThemResponse chiTiet(String id) {
        DichVuThem dv = dichVuThemRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Khong tim thay dich vu: " + id));
        return toResponse(dv);
    }

    @Transactional
    public DichVuThemResponse taoMoi(DichVuThemRequest request) {
        DichVuThem dv = new DichVuThem();
        dv.setMaDichVuThem(maTuDongService.taoMaDichVuThem());
        dv.setTen(request.getTen());
        dv.setDonViTinh(request.getDonViTinh());
        dv.setDonGia(request.getDonGia());
        dichVuThemRepository.save(dv);
        return toResponse(dv);
    }

    @Transactional
    public DichVuThemResponse capNhat(String id, DichVuThemRequest request) {
        DichVuThem dv = dichVuThemRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Khong tim thay dich vu: " + id));

        if (request.getTen() != null) dv.setTen(request.getTen());
        if (request.getDonViTinh() != null) dv.setDonViTinh(request.getDonViTinh());
        if (request.getDonGia() != null) dv.setDonGia(request.getDonGia());
        dichVuThemRepository.save(dv);
        return toResponse(dv);
    }

    @Transactional
    public void xoa(String id) {
        DichVuThem dv = dichVuThemRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Khong tim thay dich vu: " + id));
        dichVuThemRepository.delete(dv);
    }

    private DichVuThemResponse toResponse(DichVuThem dv) {
        return DichVuThemResponse.builder()
                .maDichVuThem(dv.getMaDichVuThem())
                .ten(dv.getTen())
                .donViTinh(dv.getDonViTinh())
                .donGia(dv.getDonGia())
                .build();
    }
}
