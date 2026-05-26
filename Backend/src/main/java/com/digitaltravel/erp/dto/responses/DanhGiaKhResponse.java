package com.digitaltravel.erp.dto.responses;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DanhGiaKhResponse {
    String maDanhGia;
    String maTourThucTe;
    String tieuDeTour;
    String maKhachHang;
    String hoTenKhachHang;
    Integer soSao;
    String nhanXet;
    LocalDateTime ngayDanhGia;
}
