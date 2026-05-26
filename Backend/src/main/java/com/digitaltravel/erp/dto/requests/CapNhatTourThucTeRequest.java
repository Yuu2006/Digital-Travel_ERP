package com.digitaltravel.erp.dto.requests;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.Getter;

@Getter
public class CapNhatTourThucTeRequest {

    @DecimalMin(value = "0.01", message = "Giá hiện hành phải lớn hơn 0")
    BigDecimal giaHienHanh;

    @Min(value = 1, message = "Số khách tối đa phải ít nhất 1")
    Integer soKhachToiDa;

    @Min(value = 1, message = "Số khách tối thiểu phải ít nhất 1")
    Integer soKhachToiThieu;

    String trangThai;

    List<String> maDichVuThem;

    List<String> maHanhDongXanh;
}
