package com.digitaltravel.erp.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.digitaltravel.erp.entity.TourThucTe;
import com.digitaltravel.erp.repository.TourThucTeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Dynamic Pricing Scheduler — chạy mỗi giờ
 *
 * Quy tắc:
 *  - daysLeft <= 7  AND fillRate >= 0.80 → tăng giá 10% (tối đa gấp 2 GiaSan)
 *  - daysLeft >= 30 AND fillRate < 0.30  → giảm giá 5%  (tối thiểu GiaSan)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TourThucTeScheduler {

    private final TourThucTeRepository tourThucTeRepository;

    @Scheduled(cron = "0 0 * * * *") // Mỗi đầu giờ
    @Transactional
    public void capNhatGiaDong() {
        List<TourThucTe> tours = tourThucTeRepository.findForDynamicPricing();
        LocalDate today = LocalDate.now();

        for (TourThucTe tour : tours) {
            try {
                apDungDynamicPricing(tour, today);
            } catch (Exception e) {
                log.warn("Dynamic pricing error for tour {}: {}", tour.getMaTourThucTe(), e.getMessage());
            }
        }

        if (!tours.isEmpty()) {
            tourThucTeRepository.saveAll(tours);
        }
    }

    private void apDungDynamicPricing(TourThucTe tour, LocalDate today) {
        if (tour.getNgayKhoiHanh() == null || tour.getTourMau() == null || tour.getTourMau().getGiaSan() == null) return;

        long daysLeft = ChronoUnit.DAYS.between(today, tour.getNgayKhoiHanh());
        if (daysLeft < 0) return; // tour đã qua

        int soKhachToiDa = tour.getSoKhachToiDa() != null ? tour.getSoKhachToiDa() : 0;
        int choConLai = tour.getChoConLai() != null ? tour.getChoConLai() : soKhachToiDa;
        if (soKhachToiDa == 0) return;

        double fillRate = (double) (soKhachToiDa - choConLai) / soKhachToiDa;
        BigDecimal giaSan = tour.getTourMau().getGiaSan();
        BigDecimal giaHienHanh = tour.getGiaHienHanh();
        BigDecimal giaTranToiDa = giaSan.multiply(BigDecimal.valueOf(2));

        if (daysLeft <= 7 && fillRate >= 0.80) {
            // Sắp đầy chỗ, gần khởi hành → tăng 10%
            BigDecimal giaMoi = giaHienHanh.multiply(BigDecimal.valueOf(1.10))
                    .setScale(0, RoundingMode.HALF_UP);
            if (giaMoi.compareTo(giaTranToiDa) <= 0) {
                tour.setGiaHienHanh(giaMoi);
                log.info("Dynamic pricing UP: tour={} {} -> {}", tour.getMaTourThucTe(), giaHienHanh, giaMoi);
            }
        } else if (daysLeft >= 30 && fillRate < 0.30) {
            // Còn nhiều ngày, tỷ lệ lấp thấp → giảm 5%
            BigDecimal giaMoi = giaHienHanh.multiply(BigDecimal.valueOf(0.95))
                    .setScale(0, RoundingMode.HALF_UP);
            if (giaMoi.compareTo(giaSan) >= 0) {
                tour.setGiaHienHanh(giaMoi);
                log.info("Dynamic pricing DOWN: tour={} {} -> {}", tour.getMaTourThucTe(), giaHienHanh, giaMoi);
            }
        }
    }
}
