package com.digitaltravel.erp.dto.responses;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HanhDongXanhResponse {
    String maHanhDongXanh;
    String maTourThucTe;
    String tenHanhDong;
    Long diemCong;
}
