package com.digitaltravel.erp.dto.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PhatHanhVoucherRequest {
    @NotBlank(message = "Mã khách hàng không được trống")
    String maKhachHang;
}
