package com.digitaltravel.erp.dto.requests;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class VoucherRequest {
    @NotBlank(message = "Mã code không được trống")
    String maCode;

    @NotBlank(message = "Loại ưu đãi không được trống")
    // PHAN_TRAM | SO_TIEN
    String loaiUuDai;

    @NotNull(message = "Giá trị giảm không được trống")
    @DecimalMin(value = "0", message = "Giá trị giảm phải >= 0")
    BigDecimal giaTriGiam;

    @DecimalMin(value = "0", inclusive = false, message = "Mức giảm tối đa phải > 0")
    BigDecimal mucGiamToiDa;

    String dieuKienApDung;

    @NotNull(message = "Số lượt phát hành không được trống")
    @Min(value = 1, message = "Số lượt phát hành phải >= 1")
    Integer soLuotPhatHanh;

    @NotNull(message = "Ngày hiệu lực không được trống")
    LocalDate ngayHieuLuc;

    @NotNull(message = "Ngày hết hạn không được trống")
    LocalDate ngayHetHan;
}
