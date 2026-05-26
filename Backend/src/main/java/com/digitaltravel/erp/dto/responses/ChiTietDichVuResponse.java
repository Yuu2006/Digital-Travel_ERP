package com.digitaltravel.erp.dto.responses;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChiTietDichVuResponse {
    String maChiTietDichVu;
    String maDichVuThem;
    String tenDichVu;
    String donViTinh;
    Long soLuong;
    BigDecimal donGia;
    BigDecimal thanhTien;
}
