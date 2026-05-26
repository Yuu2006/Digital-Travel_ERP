package com.digitaltravel.erp.dto.responses;

import lombok.Builder;
import lombok.Getter;

/**
 * Thông tin kết nối Oracle DB trả về cho Power BI.
 */
@Getter
@Builder
public class PowerBiKetNoiResponse {
    /** Host Oracle DB. */
    String host;
    /** Port Oracle DB. */
    int port;
    /** Oracle Service Name. */
    String serviceName;
    /** Username read-only tạm thời. */
    String username;
    /** Password tạm thời. */
    String password;
    /** Full JDBC URL (tiện sao chép). */
    String jdbcUrl;
    /** Thời gian hết hạn (ISO-8601). */
    String hetHan;
    /** Hướng dẫn sử dụng trong Power BI Desktop. */
    String huongDan;
}
