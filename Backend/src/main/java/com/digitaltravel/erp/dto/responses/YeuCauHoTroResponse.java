package com.digitaltravel.erp.dto.responses;

import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class YeuCauHoTroResponse {
    String maYeuCau;
    String maDatTour;
    String loaiYeuCau;
    String noiDung;
    String trangThai;
    String maNhanVienXuLy;

    /** Số tiền hoàn (nếu là yêu cầu hoàn tiền) */
    BigDecimal soTienHoan;

    /** Tỉ lệ hoàn % */
    Integer tiLeHoan;
}
