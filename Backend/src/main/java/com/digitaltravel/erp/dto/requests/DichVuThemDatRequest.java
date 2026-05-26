package com.digitaltravel.erp.dto.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class DichVuThemDatRequest {

    @NotBlank(message = "Mã dịch vụ thêm không được để trống")
    String maDichVuThem;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng phải ít nhất là 1")
    Long soLuong;
}
