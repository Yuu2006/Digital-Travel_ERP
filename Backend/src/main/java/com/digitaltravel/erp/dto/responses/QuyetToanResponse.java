package com.digitaltravel.erp.dto.responses;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuyetToanResponse {
    private String maQuyetToan;
    private String maTour;
    private String tenTour;
    private BigDecimal tongDoanhThu;
    private BigDecimal tongChiPhi;
    private BigDecimal giaCamKet;
    private BigDecimal loiNhuan;
    private String trangThai;
    private String ghiChu;
    private String hoaDonAnh;
    private LocalDateTime ngayQuyetToan;
    private String maNhanVien;
    private String tenNhanVien;
}
