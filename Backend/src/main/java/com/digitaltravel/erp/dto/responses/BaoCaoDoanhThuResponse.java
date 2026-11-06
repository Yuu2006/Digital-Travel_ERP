package com.digitaltravel.erp.dto.responses;

import java.math.BigDecimal;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BaoCaoDoanhThuResponse {
    BigDecimal tongDoanhThu;
    Long soTourHoanThanh;
    Long soTourDaQuyetToan;
    Long soDonDatTour;
    List<TopTourItem> topTour;

    @Getter
    @Builder
    public static class TopTourItem {
        String maTourThucTe;
        String tieuDeTour;
        Long soDon;
        BigDecimal doanhThu;
    }
}
