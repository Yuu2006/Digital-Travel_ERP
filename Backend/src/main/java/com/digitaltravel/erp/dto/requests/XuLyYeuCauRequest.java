package com.digitaltravel.erp.dto.requests;

import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class XuLyYeuCauRequest {

    @Size(max = 2000, message = "Ghi chú không quá 2000 ký tự")
    String ghiChu;
}
