package com.digitaltravel.erp.dto.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LichTrinhRequest {

    @NotNull(message = "Ngày thứ không được để trống")
    @Min(value = 1, message = "Ngày thứ phải lớn hơn 0")
    Integer ngayThu;

    @NotBlank(message = "Hoạt động không được để trống")
    @Size(max = 1000, message = "Timeline hoạt động tối đa 1000 ký tự")
    @Pattern(
            regexp = "\\s*(?:[01]\\d|2[0-3]):[0-5]\\d\\s*[-–—]\\s*[^\\r\\n]+(?:\\R\\s*(?:[01]\\d|2[0-3]):[0-5]\\d\\s*[-–—]\\s*[^\\r\\n]+)+\\s*",
            message = "Mỗi ngày phải có ít nhất hai hoạt động theo định dạng HH:mm - Nội dung hoạt động")
    String hoatDong;

    String moTa;

    String thucDon;
}
