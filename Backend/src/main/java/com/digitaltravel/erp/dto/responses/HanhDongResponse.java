package com.digitaltravel.erp.dto.responses;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HanhDongResponse {
    private String maGhiNhan;
    private String maKhachHang;
    private String hoTenKhachHang;
    private String maHanhDongXanh;
    private String tenHanhDong;
    private Long diemCong;
    private String minhChung;
    private LocalDateTime thoiGian;
}
