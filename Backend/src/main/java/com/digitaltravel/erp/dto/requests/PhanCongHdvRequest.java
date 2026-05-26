package com.digitaltravel.erp.dto.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PhanCongHdvRequest {

    @NotBlank(message = "Mã tour thực tế không được để trống")
    String maTourThucTe;

    @NotBlank(message = "Mã nhân viên không được để trống")
    String maNhanVien;

    String ghiChu;
}
