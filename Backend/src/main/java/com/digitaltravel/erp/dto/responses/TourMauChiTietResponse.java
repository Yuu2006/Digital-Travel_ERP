package com.digitaltravel.erp.dto.responses;

import java.math.BigDecimal;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TourMauChiTietResponse {
    String maTourMau;
    String tieuDe;
    String moTa;
    Integer thoiLuong;
    BigDecimal giaSan;
    BigDecimal danhGia;
    Integer soDanhGia;
    List<LichTrinhResponse> lichTrinh;
}
