package com.digitaltravel.erp.dto.responses;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LichTrinhResponse {
    String maLichTrinhTour;
    Integer ngayThu;
    String hoatDong;
    String moTa;
    String thucDon;
}
