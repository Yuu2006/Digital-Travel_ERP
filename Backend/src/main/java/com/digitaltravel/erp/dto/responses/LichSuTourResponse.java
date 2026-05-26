package com.digitaltravel.erp.dto.responses;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LichSuTourResponse {
    String maLichSuTour;
    String maDatTour;
    String maTourThucTe;
    String tieuDeTour;
    LocalDate ngayKhoiHanh;
    Integer thoiLuong;
    LocalDateTime ngayDat;
    LocalDate ngayThamGia;
    String trangThai;
    String trangThaiTour;
    BigDecimal tongTien;
    Boolean daDanhGia;
    Boolean daKhieuNai;
    String trangThaiKhieuNai;
}
