package com.digitaltravel.erp.dto.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KhoiTaoThanhToanRequest {

    @NotBlank(message = "Mã đơn đặt tour không được để trống")
    String maDonDatTour;

    /**
     * Phương thức: "MOMO_WALLET" | "MOMO_ATM" | "MOCK" (dev bypass)
     */
    String phuongThuc;

    /**
     * true = bypass MoMo, trả kết quả thành công ngay (dùng trong dev/test)
     */
    boolean mock;
}
