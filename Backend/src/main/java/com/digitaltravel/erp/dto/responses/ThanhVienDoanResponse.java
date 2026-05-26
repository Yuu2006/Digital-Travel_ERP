package com.digitaltravel.erp.dto.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ThanhVienDoanResponse {
    private String maDatTour;
    private String loaiKhach;
    private String maKhachHang;
    private String maNguoiDongHanh;
    private String hoTenKhachHang;
    private String soDienThoai;
    private String hangThanhVien;
    private String ghiChuYTe;
    private String diUng;
    private String ghiChuDatTour;
    private String hanhDongXanh;
    private Long diemXanh;
    private String trangThai;
    private String ghiChuDiemDanh;
}
