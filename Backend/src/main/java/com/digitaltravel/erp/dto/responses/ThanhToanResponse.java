package com.digitaltravel.erp.dto.responses;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ThanhToanResponse {
    String maGiaoDich;
    String maDatTour;
    String trangThai;          // CHO_THANH_TOAN | THANH_CONG | THAT_BAI
    String phuongThuc;
    BigDecimal soTien;
    LocalDateTime ngayThanhToan;

    /** URL redirect sang MoMo (null nếu mock) */
    String payUrl;

    /** Thông báo kết quả */
    String thongBao;
}
