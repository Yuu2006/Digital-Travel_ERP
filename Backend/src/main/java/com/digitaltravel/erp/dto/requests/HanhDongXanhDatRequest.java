package com.digitaltravel.erp.dto.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class HanhDongXanhDatRequest {

    @NotBlank(message = "Mã hành động xanh không được để trống")
    String maHanhDongXanh;

    @NotNull(message = "Số lượng hành động xanh không được để trống")
    @Min(value = 1, message = "Số lượng hành động xanh phải ít nhất là 1")
    Long soLuong;
}
