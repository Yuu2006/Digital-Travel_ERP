package com.digitaltravel.erp.dto.responses;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NhatKySuCoResponse {
    private String maNhatKySuCo;
    private String maTour;
    private String moTa;
    private String giaiPhap;
    private String mucDo;
    private String loaiSuCo;
    private String maHdvBaoCao;
    private String maKhachHang;
    private String maNguoiDongHanh;
    private String hoTenKhachHang;
    private String ghiChuYTe;
    private String diUng;
    private LocalDateTime thoiGianBaoCao;
}
