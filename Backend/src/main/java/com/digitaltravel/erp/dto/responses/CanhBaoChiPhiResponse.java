package com.digitaltravel.erp.dto.responses;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CanhBaoChiPhiResponse {
    private String maCanhBao;
    private String maChiPhi;
    private String maTour;
    private String maNhanVien;
    private String tenNhanVien;
    private String danhMuc;
    private BigDecimal thanhTien;
    private String loaiCanhBao;
    private String mucDo;
    private String noiDung;
    private String trangThai;
    private LocalDateTime ngayTao;
}
