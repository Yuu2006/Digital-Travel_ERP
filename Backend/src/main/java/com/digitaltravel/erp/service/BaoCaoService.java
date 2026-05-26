package com.digitaltravel.erp.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.digitaltravel.erp.dto.responses.BaoCaoDoanhThuResponse;
import com.digitaltravel.erp.repository.DonDatTourRepository;
import com.digitaltravel.erp.repository.QuyetToanRepository;
import com.digitaltravel.erp.repository.TourThucTeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BaoCaoService {

    private final QuyetToanRepository quyetToanRepository;
    private final DonDatTourRepository donDatTourRepository;
    private final TourThucTeRepository tourThucTeRepository;

    // ── UC53: Báo cáo doanh thu ──────────────────────────────────────────────
    public BaoCaoDoanhThuResponse baoCaoDoanhThu(LocalDate tuNgay, LocalDate denNgay) {
        LocalDateTime from = tuNgay != null ? tuNgay.atStartOfDay() : LocalDateTime.MIN;
        LocalDateTime to = denNgay != null ? denNgay.plusDays(1).atStartOfDay() : LocalDateTime.now();

        // Tổng doanh thu từ các quyết toán đã chốt
        List<Object[]> tongHop = quyetToanRepository.thongKeDoanhThu(from, to);

        BigDecimal tongDoanhThu = BigDecimal.ZERO;
        long soTourDaQuyetToan = 0;
        for (Object[] row : tongHop) {
            if (row[0] != null) tongDoanhThu = tongDoanhThu.add((BigDecimal) row[0]);
            if (row[1] != null) soTourDaQuyetToan = ((Number) row[1]).longValue();
        }

        // Số đơn đặt tour trong kỳ
        long soDonDatTour = donDatTourRepository.demDonDatTour(from, to);

        // Số tour kết thúc
        long soTourHoanThanh = tourThucTeRepository.demTourKetThuc(from, to);

        // Top tour doanh thu
        List<Object[]> rawTopTour = quyetToanRepository.topTourDoanhThu(from, to);
        List<BaoCaoDoanhThuResponse.TopTourItem> topTour = rawTopTour.stream()
                .map(r -> BaoCaoDoanhThuResponse.TopTourItem.builder()
                        .maTourThucTe((String) r[0])
                        .tieuDeTour((String) r[1])
                        .soDon(((Number) r[2]).longValue())
                        .doanhThu((BigDecimal) r[3])
                        .build())
                .collect(Collectors.toList());

        return BaoCaoDoanhThuResponse.builder()
                .tongDoanhThu(tongDoanhThu)
                .soTourHoanThanh(soTourHoanThanh)
                .soTourDaQuyetToan(soTourDaQuyetToan)
                .soDonDatTour(soDonDatTour)
                .topTour(topTour)
                .build();
    }
}
