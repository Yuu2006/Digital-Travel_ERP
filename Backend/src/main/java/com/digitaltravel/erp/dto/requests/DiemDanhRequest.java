package com.digitaltravel.erp.dto.requests;

import lombok.Data;

@Data
public class DiemDanhRequest {
    private String maKhachHang;

    private String maNguoiDongHanh;

    // DA_DIEM_DANH | CHUA_DIEM_DANH | VANG; null mac dinh DA_DIEM_DANH
    private String trangThai;

    private String diaDiem;
}
