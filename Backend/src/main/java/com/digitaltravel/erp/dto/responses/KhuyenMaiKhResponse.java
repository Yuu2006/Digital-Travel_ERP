package com.digitaltravel.erp.dto.responses;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KhuyenMaiKhResponse {
    String maKhachHang;
    String hoTenKhachHang;
    String emailKhachHang;
    String soDienThoaiKhachHang;
    String hangThanhVien;
    String maVoucher;
    String maCode;
    String loaiUuDai;
    BigDecimal giaTriGiam;
    BigDecimal mucGiamToiDa;
    Long diemCanDoi;
    String dieuKienApDung;
    LocalDate ngayHetHan;
    LocalDateTime ngayNhan;
    String trangThai;
    String trangThaiVoucher;
}
