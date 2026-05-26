package com.digitaltravel.erp.dto.responses;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NhatKyHeThongResponse {
    String maNhatKyHeThong;
    String maTaiKhoan;
    String tenDangNhap;
    String hanhDong;
    String doiTuong;
    String maDoiTuong;
    LocalDateTime thoiGian;
}
