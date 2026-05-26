package com.digitaltravel.erp.dto.responses;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DangNhapResponse {
    String accessToken;
    String tokenType;
    String maVaiTro;
    String tenHienThi;
    String hoTen;
}
