package com.digitaltravel.erp.dto.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ApVoucherRequest {
    @NotBlank(message = "Mã đặt tour không được trống")
    String maDatTour;

    @NotBlank(message = "Mã voucher không được trống")
    String maVoucher;
}
