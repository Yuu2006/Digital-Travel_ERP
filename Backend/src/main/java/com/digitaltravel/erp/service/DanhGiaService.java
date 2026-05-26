package com.digitaltravel.erp.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.digitaltravel.erp.dto.requests.DanhGiaRequest;
import com.digitaltravel.erp.dto.responses.DanhGiaKhResponse;
import com.digitaltravel.erp.entity.DanhGiaKh;
import com.digitaltravel.erp.entity.HoChieuSo;
import com.digitaltravel.erp.entity.NangLucNhanVien;
import com.digitaltravel.erp.entity.PhanCongTour;
import com.digitaltravel.erp.entity.TourMau;
import com.digitaltravel.erp.entity.TourThucTe;
import com.digitaltravel.erp.exception.AppException;
import com.digitaltravel.erp.repository.DanhGiaKhRepository;
import com.digitaltravel.erp.repository.HoChieuSoRepository;
import com.digitaltravel.erp.repository.LichSuTourRepository;
import com.digitaltravel.erp.repository.NangLucNhanVienRepository;
import com.digitaltravel.erp.repository.PhanCongTourRepository;
import com.digitaltravel.erp.repository.TourMauRepository;
import com.digitaltravel.erp.repository.TourThucTeRepository;
import com.digitaltravel.erp.repository.YeuCauHoTroRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DanhGiaService {

    private final DanhGiaKhRepository danhGiaKhRepository;
    private final HoChieuSoRepository hoChieuSoRepository;
    private final LichSuTourRepository lichSuTourRepository;
    private final TourThucTeRepository tourThucTeRepository;
    private final TourMauRepository tourMauRepository;
    private final PhanCongTourRepository phanCongTourRepository;
    private final NangLucNhanVienRepository nangLucNhanVienRepository;
    private final YeuCauHoTroRepository yeuCauHoTroRepository;

    @Transactional
    public DanhGiaKhResponse guiDanhGia(String maTaiKhoan, DanhGiaRequest request) {
        HoChieuSo hcs = hoChieuSoRepository.findByMaTaiKhoan(maTaiKhoan)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy hồ sơ khách hàng"));

        TourThucTe tour = tourThucTeRepository.findById(request.getMaTourThucTe())
                .orElseThrow(() -> AppException.notFound("Không tìm thấy tour: " + request.getMaTourThucTe()));

        if (!"KET_THUC".equals(tour.getTrangThai()) && !"DA_QUYET_TOAN".equals(tour.getTrangThai())) {
            throw AppException.badRequest("Chỉ có thể đánh giá tour đã kết thúc");
        }

        lichSuTourRepository.findByMaKhachHangAndMaTourThucTe(hcs.getMaKhachHang(), tour.getMaTourThucTe())
                .orElseThrow(() -> AppException.badRequest("Bạn chưa tham gia tour này nên không thể đánh giá"));

        if (danhGiaKhRepository.existsByKhachHangAndTour(hcs.getMaKhachHang(), tour.getMaTourThucTe())) {
            throw AppException.badRequest("Bạn đã đánh giá tour này rồi");
        }

        if (yeuCauHoTroRepository.existsActiveKhieuNaiByKhachHangAndTour(
                hcs.getMaKhachHang(), tour.getMaTourThucTe())) {
            throw AppException.badRequest("Khiếu nại của tour này chưa được giải quyết, vui lòng chờ xử lý trước khi đánh giá");
        }

        DanhGiaKh dg = new DanhGiaKh();
        dg.setMaDanhGiaKhachHang("DG_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        dg.setTourThucTe(tour);
        dg.setKhachHang(hcs);
        dg.setSoSao(request.getSoSao());
        dg.setNhanXet(request.getNhanXet());
        dg.setNgayDanhGia(LocalDateTime.now());
        danhGiaKhRepository.save(dg);

        capNhatDiemTrungBinhTourMau(tour.getTourMau().getMaTourMau());
        capNhatDiemTrungBinhHdv(tour.getMaTourThucTe(), request.getSoSaoHdv());

        return toResponse(dg);
    }

    public Page<DanhGiaKhResponse> danhSachDanhGia(String maTourThucTe, Pageable pageable) {
        TourThucTe tour = tourThucTeRepository.findById(maTourThucTe)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy tour: " + maTourThucTe));
        return danhGiaKhRepository.findByMaTourMau(tour.getTourMau().getMaTourMau(), pageable)
                .map(this::toResponse);
    }

    public Page<DanhGiaKhResponse> tatCaDanhGia(Pageable pageable) {
        return danhGiaKhRepository.findAllWithDetails(pageable).map(this::toResponse);
    }

    private void capNhatDiemTrungBinhTourMau(String maTourMau) {
        TourMau tm = tourMauRepository.findById(maTourMau).orElse(null);
        if (tm == null) {
            return;
        }

        long soDanhGia = danhGiaKhRepository.countByMaTourMau(maTourMau);
        BigDecimal diemMoi = danhGiaKhRepository.averageSoSaoByMaTourMau(maTourMau)
                .setScale(2, RoundingMode.HALF_UP);
        tm.setSoDanhGia((int) soDanhGia);
        tm.setDanhGia(diemMoi);
        tourMauRepository.save(tm);
    }

    private void capNhatDiemTrungBinhHdv(String maTourThucTe, Integer soSaoHdv) {
        if (soSaoHdv == null) {
            return;
        }

        for (PhanCongTour pc : phanCongTourRepository.findActiveByMaTour(maTourThucTe)) {
            NangLucNhanVien nl = nangLucNhanVienRepository.findByMaNhanVien(pc.getNhanVien().getMaNhanVien())
                    .orElseGet(() -> {
                        NangLucNhanVien moi = new NangLucNhanVien();
                        moi.setMaNangLucNhanVien("NLNV_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
                        moi.setNhanVien(pc.getNhanVien());
                        moi.setDanhGia(BigDecimal.ZERO);
                        moi.setSoDanhGia(0);
                        return moi;
                    });

            int soDanhGia = (nl.getSoDanhGia() == null ? 0 : nl.getSoDanhGia()) + 1;
            BigDecimal diemHienTai = nl.getDanhGia() == null ? BigDecimal.ZERO : nl.getDanhGia();
            BigDecimal diemMoi = diemHienTai.multiply(BigDecimal.valueOf(soDanhGia - 1))
                    .add(BigDecimal.valueOf(soSaoHdv))
                    .divide(BigDecimal.valueOf(soDanhGia), 2, RoundingMode.HALF_UP);
            nl.setSoDanhGia(soDanhGia);
            nl.setDanhGia(diemMoi);
            nangLucNhanVienRepository.save(nl);
        }
    }

    public DanhGiaKhResponse toResponse(DanhGiaKh dg) {
        return DanhGiaKhResponse.builder()
                .maDanhGia(dg.getMaDanhGiaKhachHang())
                .maTourThucTe(dg.getTourThucTe().getMaTourThucTe())
                .tieuDeTour(dg.getTourThucTe().getTourMau().getTieuDe())
                .maKhachHang(dg.getKhachHang().getMaKhachHang())
                .hoTenKhachHang(dg.getKhachHang().getTaiKhoan().getHoTen())
                .soSao(dg.getSoSao())
                .nhanXet(dg.getNhanXet())
                .ngayDanhGia(dg.getNgayDanhGia())
                .build();
    }
}
