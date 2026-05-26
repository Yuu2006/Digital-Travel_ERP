package com.digitaltravel.erp.dto.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class XuLyHoTroRequest {
    // Optional: gán nhân viên xử lý
    String maNhanVienXuLy;

    @NotBlank(message = "Trạng thái không được trống")
    // CHUA_XU_LY | CHO_BO_SUNG | CHO_GIAI_TRINH | CHO_DUYET | DA_XU_LY | TU_CHOI
    String trangThai;

    String ghiChu;
}
