package com.digitaltravel.erp.dto.responses;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DichVuThemResponse {
    String maDichVuThem;
    String ten;
    String donViTinh;
    BigDecimal donGia;
}
