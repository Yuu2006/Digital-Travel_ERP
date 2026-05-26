package com.digitaltravel.erp.dto.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GhiNhanHanhDongRequest {
    @NotBlank
    private String maKhachHang;
    @NotBlank
    private String maHanhDongXanh;
    private String minhChung;
}
