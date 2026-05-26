package com.digitaltravel.erp.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BaoCaoSuCoRequest {
    @NotBlank
    @Size(max = 4000)
    private String moTa;
    @Size(max = 4000)
    private String giaiPhap;
    private String mucDo;
    private String loaiSuCo;
    private String maKhachHang;
    private String maNguoiDongHanh;
}
