package com.digitaltravel.erp.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.digitaltravel.erp.entity.DonDatTour;
import com.digitaltravel.erp.entity.GiaoDich;
import com.digitaltravel.erp.repository.DonDatTourRepository;
import com.digitaltravel.erp.repository.GiaoDichRepository;

import lombok.RequiredArgsConstructor;

/**
 * Scheduler tự động hủy đơn đặt tour hết thời gian giữ chỗ.
 * Chạy mỗi 60 giây, tìm đơn CHO_XAC_NHAN có ThoiGianHetHan < NOW()
 * và đặt TrangThai = HET_HAN_GIU_CHO.
 * Không cần hoàn ChoConLai vì ChoConLai chưa bị trừ (chỉ trừ sau thanh toán).
 */
@Component
@RequiredArgsConstructor
public class DatTourScheduler {

    static final long THOI_HAN_QR_PHUT = 5;

    private static final Logger log = LoggerFactory.getLogger(DatTourScheduler.class);

    private final DonDatTourRepository donDatTourRepository;
    private final GiaoDichRepository giaoDichRepository;

    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void huyDonHetHan() {
        List<DonDatTour> donHetHan = donDatTourRepository
                .findAllByTrangThaiAndThoiGianHetHanBefore("CHO_XAC_NHAN", LocalDateTime.now());

        if (donHetHan.isEmpty()) return;

        List<GiaoDich> giaoDichThatBai = new ArrayList<>();
        for (DonDatTour don : donHetHan) {
            don.setTrangThai("HET_HAN_GIU_CHO");
            giaoDichRepository.findByMaDatTour(don.getMaDatTour()).stream()
                    .filter(giaoDich -> "CHO_THANH_TOAN".equals(giaoDich.getTrangThai()))
                    .forEach(giaoDich -> {
                        giaoDich.setTrangThai("THAT_BAI");
                        giaoDichThatBai.add(giaoDich);
                    });
        }
        if (!giaoDichThatBai.isEmpty()) {
            giaoDichRepository.saveAll(giaoDichThatBai);
        }
        donDatTourRepository.saveAll(donHetHan);
        log.info("[Scheduler] Da huy {} don dat tour het han giu cho", donHetHan.size());
    }

    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void danhDauQrHetHan() {
        List<GiaoDich> giaoDichHetHan = giaoDichRepository.findQrChoThanhToanQuaHan(
                LocalDateTime.now().minusMinutes(THOI_HAN_QR_PHUT));
        if (giaoDichHetHan.isEmpty()) return;

        List<DonDatTour> donHetHan = giaoDichHetHan.stream()
                .map(GiaoDich::getDonDatTour)
                .distinct()
                .toList();
        giaoDichHetHan.forEach(giaoDich -> giaoDich.setTrangThai("THAT_BAI"));
        donHetHan.forEach(don -> don.setTrangThai("HET_HAN_GIU_CHO"));
        giaoDichRepository.saveAll(giaoDichHetHan);
        donDatTourRepository.saveAll(donHetHan);
        log.info("[Scheduler] Da cap nhat {} don het han giu cho do QR het han", giaoDichHetHan.size());
    }
}
