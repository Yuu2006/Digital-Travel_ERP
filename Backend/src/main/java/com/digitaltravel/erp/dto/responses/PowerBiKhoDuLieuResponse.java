package com.digitaltravel.erp.dto.responses;

import lombok.Builder;
import lombok.Getter;

/**
 * Mô tả 1 kho dữ liệu khả dụng cho Power BI.
 */
@Getter
@Builder
public class PowerBiKhoDuLieuResponse {
    /** Mã kho (vd: DOANH_THU, DON_DAT_TOUR, ...). */
    String maKho;
    /** Tên hiển thị. */
    String tenKho;
    /** Mô tả nội dung dữ liệu. */
    String moTa;
}
