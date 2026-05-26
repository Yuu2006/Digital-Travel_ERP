package com.digitaltravel.erp.dto.requests;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CapNhatTourMauRequest {

    @Size(max = 500, message = "Tiêu đề tối đa 500 ký tự")
    String tieuDe;

    String moTa;

    @Min(value = 1, message = "Thời lượng phải ít nhất 1 ngày")
    Integer thoiLuong;

    @DecimalMin(value = "0.01", message = "Giá sàn phải lớn hơn 0")
    BigDecimal giaSan;
}
