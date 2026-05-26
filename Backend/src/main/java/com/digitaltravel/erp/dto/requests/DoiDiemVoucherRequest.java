package com.digitaltravel.erp.dto.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class DoiDiemVoucherRequest {
    @NotBlank(message = "Mã voucher không được trống")
    String maVoucher;
}
