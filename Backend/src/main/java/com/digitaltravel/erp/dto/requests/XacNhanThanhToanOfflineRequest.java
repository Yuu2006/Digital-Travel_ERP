package com.digitaltravel.erp.dto.requests;

import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class XacNhanThanhToanOfflineRequest {

    @Size(max = 50, message = "Phương thức không được vượt qua 50 ký tự")
    String phuongThuc;

    @Size(max = 200, message = "Mã giao dịch ngân hàng không được vượt qua 200 ký tự")
    String maGiaoDichNganHang;

    @Size(max = 500, message = "Ghi chú không được vượt qua 500 ký tự")
    String ghiChu;
}
