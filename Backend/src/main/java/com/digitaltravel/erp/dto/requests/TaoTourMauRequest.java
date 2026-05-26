package com.digitaltravel.erp.dto.requests;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class TaoTourMauRequest {

    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(max = 500, message = "Tiêu đề tối đa 500 ký tự")
    String tieuDe;

    String moTa;

    @NotNull(message = "Thời lượng không được để trống")
    @Min(value = 1, message = "Thời lượng phải ít nhất 1 ngày")
    Integer thoiLuong;

    @NotNull(message = "Giá sàn không được để trống")
    @DecimalMin(value = "0.01", message = "Giá sàn phải lớn hơn 0")
    BigDecimal giaSan;

    @Valid
    List<LichTrinhRequest> lichTrinh;
}
