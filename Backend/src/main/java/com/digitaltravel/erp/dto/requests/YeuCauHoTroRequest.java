package com.digitaltravel.erp.dto.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class YeuCauHoTroRequest {
    // Có thể null nếu yêu cầu không gắn với đơn nào
    String maDatTour;

    @NotBlank(message = "Loại yêu cầu không được trống")
    String loaiYeuCau; // KHIEU_NAI | HO_TRO | THONG_TIN

    @NotBlank(message = "Nội dung không được trống")
    String noiDung;
}
