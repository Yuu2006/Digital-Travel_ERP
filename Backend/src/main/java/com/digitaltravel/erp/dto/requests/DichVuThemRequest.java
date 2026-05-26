package com.digitaltravel.erp.dto.requests;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class DichVuThemRequest {

    @NotBlank(message = "Tên dịch vụ không được để trống")
    @Size(max = 200, message = "Tên dịch vụ tối đa 200 ký tự")
    String ten;

    @Size(max = 100, message = "Đơn vị tính tối đa 100 ký tự")
    String donViTinh;

    @NotNull(message = "Đơn giá không được để trống")
    @DecimalMin(value = "0", message = "Đơn giá không được âm")
    BigDecimal donGia;
}
