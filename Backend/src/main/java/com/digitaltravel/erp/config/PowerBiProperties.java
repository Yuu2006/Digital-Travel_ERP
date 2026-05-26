package com.digitaltravel.erp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

/**
 * Cấu hình Power BI – bind từ {@code app.powerbi.*} trong application.yaml.
 */
@Component
@ConfigurationProperties(prefix = "app.powerbi")
@Getter
@Setter
public class PowerBiProperties {

    /** Host của Oracle DB dùng cho Power BI. */
    private String dbHost;

    /** Port Oracle DB. */
    private int dbPort;

    /** Oracle Service Name. */
    private String dbService;

    /** Schema chứa các bảng ERP (dùng cho GRANT SELECT). */
    private String dbSchema;

    private String dbUsername;   
    private String dbPassword; 

    /** Thời gian sống của user tạm (giây). Mặc định 8 giờ = 28800s. */
    private long tempUserTtlSeconds = 28800;
}
