package com.digitaltravel.erp.config;

public final class VaiTroConst {

    private VaiTroConst() {}

    public static final String ADMIN       = "ADMIN";
    public static final String SANPHAM     = "SANPHAM";
    public static final String KINHDOANH   = "KINHDOANH";
    public static final String DIEUHANH    = "DIEUHANH";
    public static final String KETOAN      = "KETOAN";
    public static final String HDV         = "HDV";
    public static final String KHACHHANG   = "KHACHHANG";

    public static final String ROLE_ADMIN      = "ROLE_ADMIN";
    public static final String ROLE_SANPHAM    = "ROLE_SANPHAM";
    public static final String ROLE_KINHDOANH  = "ROLE_KINHDOANH";
    public static final String ROLE_DIEUHANH   = "ROLE_DIEUHANH";
    public static final String ROLE_KETOAN     = "ROLE_KETOAN";
    public static final String ROLE_HDV        = "ROLE_HDV";
    public static final String ROLE_KHACHHANG  = "ROLE_KHACHHANG";

    /** Roles assignable to internal staff (excludes ADMIN and KHACHHANG). */
    public static final java.util.Set<String> VAI_TRO_NHAN_VIEN = java.util.Set.of(
            SANPHAM, KINHDOANH, DIEUHANH, KETOAN, HDV
    );

    public static final String VAI_TRO_NHAN_VIEN_MSG =
            "Ma vai tro khong hop le. Chi chap nhan: SANPHAM, KINHDOANH, DIEUHANH, KETOAN, HDV";
}
