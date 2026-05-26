package com.digitaltravel.erp.dto.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class HanhDongXanhRequest {

    @Size(max = 50, message = "Mã tour thực tế tối đa 50 ký tự")
    String maTourThucTe;

    @NotBlank(message = "Tên hành động không được để trống")
    @Size(max = 200, message = "Tên hành động tối đa 200 ký tự")
    String tenHanhDong;

    @NotNull(message = "Điểm cộng không được để trống")
    @Min(value = 0, message = "Điểm cộng không được âm")
    Long diemCong;
}
