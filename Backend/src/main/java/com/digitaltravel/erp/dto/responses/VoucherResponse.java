package com.digitaltravel.erp.dto.responses;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VoucherResponse {
    String maVoucher;
    String maCode;
    String loaiUuDai;
    BigDecimal giaTriGiam;
    BigDecimal mucGiamToiDa;
    Long diemCanDoi;
    String dieuKienApDung;
    Integer soLuotPhatHanh;
    Integer soLuotDaDung;
    Long soLuotDaPhanBo;
    LocalDate ngayHieuLuc;
    LocalDate ngayHetHan;
    String trangThai;
}
