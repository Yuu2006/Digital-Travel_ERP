package com.digitaltravel.erp.dto.responses;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DiemDanhResponse {
    private String maDiemDanh;
    private String loaiKhach;
    private String maKhachHang;
    private String maNguoiDongHanh;
    private String hoTenKhachHang;
    private String diaDiem;
    private String trangThai;
    private LocalDateTime thoiGian;
    private String soDienThoai;
    private String hangThanhVien;
    private String ghiChuYTe;
    private Long diemXanh;
}
