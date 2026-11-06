package com.digitaltravel.erp.dto.requests;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DatTourRequest {

    @NotBlank(message = "Mã tour thực tế không được để trống")
    String maTourThucTe;

    // Tuỳ chọn: danh sách dịch vụ bổ sung
    @Valid
    List<DichVuThemDatRequest> danhSachDichVu;

    // Tuỳ chọn: người đồng hành không có tài khoản/hồ chiếu số
    @Valid
    List<NguoiDongHanhRequest> danhSachNguoiDongHanh;

    // Tuỳ chọn: hành động xanh khách chọn trước khi tham gia tour
    @Size(max = 20, message = "Chỉ được chọn tối đa 20 hành động xanh")
    List<String> danhSachHanhDongXanh;

    // Tuy chon moi: hanh dong xanh kem so luong cam ket
    @Valid
    @Size(max = 20, message = "Chỉ được chọn tối đa 20 hành động xanh")
    List<HanhDongXanhDatRequest> danhSachHanhDongXanhChiTiet;

    @Size(max = 2000, message = "Ghi chú không được vượt qua 2000 ký tự")
    String ghiChu;
}
