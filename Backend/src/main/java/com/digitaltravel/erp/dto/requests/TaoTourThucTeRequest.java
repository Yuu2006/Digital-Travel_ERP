package com.digitaltravel.erp.dto.requests;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class TaoTourThucTeRequest {

    @NotBlank(message = "Mã tour mẫu không được để trống")
    String maTourMau;

    @NotNull(message = "Ngày khởi hành không được để trống")
    @Future(message = "Ngày khởi hành phải ở tương lai")
    LocalDate ngayKhoiHanh;

    @NotNull(message = "Số khách tối đa không được để trống")
    @Min(value = 1, message = "Số khách tối đa phải ít nhất 1")
    Integer soKhachToiDa;

    @Min(value = 1, message = "Số khách tối thiểu phải ít nhất 1")
    Integer soKhachToiThieu;

    @NotNull(message = "Giá hiện hành không được để trống")
    @DecimalMin(value = "0.01", message = "Giá hiện hành phải lớn hơn 0")
    BigDecimal giaHienHanh;

    String trangThai;

    List<String> maDichVuThem;

    List<String> maHanhDongXanh;
}
