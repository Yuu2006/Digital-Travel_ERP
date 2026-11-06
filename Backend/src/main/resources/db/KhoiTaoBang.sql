-- ------------------------------------------------------------
-- DROP TABLE (thu tu nguoc phu thuoc FK)
-- ------------------------------------------------------------
BEGIN
    FOR t IN (
        SELECT table_name FROM user_tables
        WHERE table_name IN (
                             'DANHGIAKH','YEUCAUHOTRO','NHATKYDOIDIEM','QUYETTOAN',
                             'CHIPHITHUCTE','NHATKYSUCO','HANHDONG','DIEMDANH',
                             'PHANCONGTOUR','LICHSUTOUR','GIAODICH','DATTOUR_UUDAI',
                             'KHUYENMAI_KH','VOUCHER','CHITIETDICHVU','CHITIETDATTOUR',
                              'DSNGUOIDONGHANH','DONDATTOUR','HDX_TOURTHUCTE','DICHVU_TOURTHUCTE','TOURTHUCTE',
                              'LICHTRINHTOUR','NANGLUCNHANVIEN',
                               'NHANVIEN','HOCHIEUSO','HANHDONGXANH','DICHVUTHEM',
                              'TOURMAU','NHATKYHETHONG','VAITRO','TAIKHOAN'
            )
        ) LOOP
            EXECUTE IMMEDIATE 'DROP TABLE ' || t.table_name || ' CASCADE CONSTRAINTS PURGE';
        END LOOP;
END;
/

-- ============================================================
-- PHAN HE: DINH DANH VA KHACH HANG
-- ============================================================

-- Danh muc vai tro he thong
CREATE TABLE VAITRO (
                        MaVaiTro         VARCHAR2(50)  PRIMARY KEY,
                        TenHienThi       VARCHAR2(100) NOT NULL
);

-- Tai khoan dang nhap he thong
CREATE TABLE TAIKHOAN (
                          MaTaiKhoan       VARCHAR2(50)  PRIMARY KEY,
                          TenDangNhap      VARCHAR2(100) NOT NULL,
                          MatKhau          VARCHAR2(255) NOT NULL,
                          HoTen            VARCHAR2(200) NOT NULL,
                          CCCD             VARCHAR2(20),
                          NgaySinh         DATE,
                          Email            VARCHAR2(200),
                          SoDienThoai      VARCHAR2(20),
                          VaiTro           VARCHAR2(50)  NOT NULL,
                          TrangThai        VARCHAR2(20)  DEFAULT 'HOAT_DONG' NOT NULL,
                          CONSTRAINT UQ_TK_TenDangNhap      UNIQUE (TenDangNhap),
                          CONSTRAINT UQ_TK_Email            UNIQUE (Email),
                          CONSTRAINT UQ_TK_CCCD             UNIQUE (CCCD),
                          CONSTRAINT FK_TK_VaiTro           FOREIGN KEY (VaiTro) REFERENCES VAITRO(MaVaiTro),
    CONSTRAINT CK_TAIKHOAN_TTHAI      CHECK (TrangThai IN ('HOAT_DONG','KHOA'))
);


-- Nhat ky he thong: chi ghi nhan thao tac them, cap nhat, xoa
CREATE TABLE NHATKYHETHONG (
                               MaNhatKyHeThong  VARCHAR2(50)   PRIMARY KEY,
                               MaTaiKhoan      VARCHAR2(50),
                               HanhDong        VARCHAR2(100)  NOT NULL,
                               DoiTuong        VARCHAR2(100),
                               MaDoiTuong      VARCHAR2(50),
                               ThoiGian        TIMESTAMP      DEFAULT SYSTIMESTAMP NOT NULL,
                               CONSTRAINT FK_NKHT_TaiKhoan FOREIGN KEY (MaTaiKhoan) REFERENCES TAIKHOAN(MaTaiKhoan),
                               CONSTRAINT CK_NKHT_HanhDong CHECK (HanhDong IN ('THEM','CAP_NHAT','XOA','XUAT_DU_LIEU_POWERBI','POWER_BI_XUAT_FILE'))
);

-- Ho so nghiep vu cua khach hang (1-1 voi TAIKHOAN co VaiTro = KHACHHANG)
CREATE TABLE HOCHIEUSO (
                           MaKhachHang      VARCHAR2(50)   PRIMARY KEY,
                           MaTaiKhoan       VARCHAR2(50)   NOT NULL,
                           GhiChuYTe        CLOB,
                           DiUng            VARCHAR2(1000),
                           HangThanhVien    VARCHAR2(20)   DEFAULT 'THANH_VIEN' NOT NULL,
                           DiemXanh         NUMBER(15)     DEFAULT 0 NOT NULL,
                           CONSTRAINT UQ_HCS_TaiKhoan        UNIQUE (MaTaiKhoan),
                           CONSTRAINT FK_HCS_TaiKhoan        FOREIGN KEY (MaTaiKhoan)   REFERENCES TAIKHOAN(MaTaiKhoan),
                           CONSTRAINT CK_HCS_HangTV          CHECK (HangThanhVien IN ('THANH_VIEN','DONG','BAC','VANG','KIM_CUONG')),
                           CONSTRAINT CK_HCS_DiemXanh        CHECK (DiemXanh >= 0)
);

-- Ho so nhan vien noi bo (1-1 voi TAIKHOAN co VaiTro != KHACHHANG)
CREATE TABLE NHANVIEN (
                          MaNhanVien           VARCHAR2(50) PRIMARY KEY,
                          MaTaiKhoan           VARCHAR2(50) NOT NULL,
                          LoaiNhanVien         VARCHAR2(50),
                          NgayVaoLam           DATE,
                          TrangThaiLamViec     VARCHAR2(20) DEFAULT 'HOAT_DONG' NOT NULL,
                          CONSTRAINT UQ_NV_TaiKhoan          UNIQUE (MaTaiKhoan),
                          CONSTRAINT FK_NV_TaiKhoan          FOREIGN KEY (MaTaiKhoan)   REFERENCES TAIKHOAN(MaTaiKhoan),
                          CONSTRAINT CK_NV_TrangThai         CHECK (TrangThaiLamViec IN ('HOAT_DONG','BAN','NGHI'))
);

-- Nang luc va chung chi huong dan vien
CREATE TABLE NANGLUCNHANVIEN (
                                 MaNangLucNhanVien    VARCHAR2(50)  PRIMARY KEY,
                                 MaNhanVien           VARCHAR2(50)  NOT NULL,
                                 NgonNgu              VARCHAR2(200),
                                 ChungChi             VARCHAR2(500),
                                 ChuyenMon            VARCHAR2(500),
                                 DanhGia              NUMBER(3,2)   DEFAULT 0,
                                 SoDanhGia            NUMBER(10)    DEFAULT 0,
                                 CONSTRAINT FK_NLNV_NhanVien        FOREIGN KEY (MaNhanVien)   REFERENCES NHANVIEN(MaNhanVien),
                                 CONSTRAINT CK_NLNV_DanhGia         CHECK (DanhGia BETWEEN 0 AND 5),
                                 CONSTRAINT CK_NLNV_SoDanhGia       CHECK (SoDanhGia >= 0)
);

-- ============================================================
-- PHAN HE: SAN PHAM
-- ============================================================

-- Dinh nghia san pham tour mau
CREATE TABLE TOURMAU (
                         MaTourMau        VARCHAR2(50)  PRIMARY KEY,
                         TieuDe           VARCHAR2(500) NOT NULL,
                         MoTa             CLOB,
                         ThoiLuong        NUMBER(5)     NOT NULL,
                         GiaSan           NUMBER(18,2)  NOT NULL,
                         DanhGia          NUMBER(3,2)   DEFAULT 0,
                         SoDanhGia        NUMBER(10)    DEFAULT 0,
                         CONSTRAINT CK_TOURMAU_ThoiLuong    CHECK (ThoiLuong > 0),
                         CONSTRAINT CK_TOURMAU_GiaSan       CHECK (GiaSan >= 0),
                         CONSTRAINT CK_TOURMAU_DanhGia      CHECK (DanhGia BETWEEN 0 AND 5),
                         CONSTRAINT CK_TOURMAU_SoDanhGia    CHECK (SoDanhGia >= 0)
);

-- Lich trinh tung ngay cua tour mau
CREATE TABLE LICHTRINHTOUR (
                               MaLichTrinhTour  VARCHAR2(50)    PRIMARY KEY,
                               MaTourMau        VARCHAR2(50)    NOT NULL,
                               NgayThu          NUMBER(3)       NOT NULL,
                               HoatDong         VARCHAR2(1000),
                               MoTa             CLOB,
                               ThucDon          VARCHAR2(1000),
                               CONSTRAINT FK_LTT_TourMau              FOREIGN KEY (MaTourMau)   REFERENCES TOURMAU(MaTourMau),
                               CONSTRAINT CK_LICHTRINHTOUR_NgayThu    CHECK (NgayThu > 0)
);

-- Danh muc dich vu bo sung, bao gom phu thu phong
CREATE TABLE DICHVUTHEM (
                             MaDichVuThem     VARCHAR2(50)  PRIMARY KEY,
                             Ten              VARCHAR2(200) NOT NULL,
                             DonViTinh        VARCHAR2(100),
                             DonGia           NUMBER(18,2)  NOT NULL,
                             CONSTRAINT CK_DICHVUTHEM_DonGia      CHECK (DonGia >= 0)
);

-- Danh muc hanh dong xanh duoc cong diem loyalty
CREATE TABLE HANHDONGXANH (
                               MaHanhDongXanh   VARCHAR2(50)  PRIMARY KEY,
                               TenHanhDong      VARCHAR2(200) NOT NULL,
                               DiemCong         NUMBER(10)    NOT NULL,
                               CONSTRAINT CK_HANHDONGXANH_DiemCong    CHECK (DiemCong >= 0)
);

-- Dot khoi hanh cu the duoc tao tu tour mau
CREATE TABLE TOURTHUCTE (
                            MaTourThucTe         VARCHAR2(50) PRIMARY KEY,
                            MaTourMau            VARCHAR2(50) NOT NULL,
                            NgayKhoiHanh         DATE         NOT NULL,
                            GiaHienHanh          NUMBER(18,2) NOT NULL,
                            SoKhachToiDa         NUMBER(5)    NOT NULL,
                            SoKhachToiThieu      NUMBER(5)    DEFAULT 1 NOT NULL,
                            ChoConLai            NUMBER(5)    NOT NULL,
                            TrangThai            VARCHAR2(20) DEFAULT 'CHO_KICH_HOAT' NOT NULL,
                            CONSTRAINT FK_TTT_TourMau              FOREIGN KEY (MaTourMau)   REFERENCES TOURMAU(MaTourMau),
                            CONSTRAINT CK_TTT_GiaHienHanh          CHECK (GiaHienHanh >= 0),
                            CONSTRAINT CK_TTT_SoKhach              CHECK (SoKhachToiDa >= SoKhachToiThieu AND SoKhachToiThieu > 0),
                            CONSTRAINT CK_TTT_ChoConLai            CHECK (ChoConLai >= 0 AND ChoConLai <= SoKhachToiDa),
                            CONSTRAINT CK_TTT_TrangThai            CHECK (TrangThai IN (
                                                                                        'CHO_KICH_HOAT','MO_BAN','DANG_DIEN_RA',
                                                                                        'KET_THUC','HUY','DA_QUYET_TOAN'
                                ))
);

-- Dich vu bo sung ap dung cho tour thuc te
CREATE TABLE DICHVU_TOURTHUCTE (
                                    MaTourThucTe     VARCHAR2(50) NOT NULL,
                                    MaDichVuThem     VARCHAR2(50) NOT NULL,
                                    CONSTRAINT PK_DICHVU_TOURTHUCTE     PRIMARY KEY (MaTourThucTe, MaDichVuThem),
                                    CONSTRAINT FK_DVTTT_TourThucTe      FOREIGN KEY (MaTourThucTe) REFERENCES TOURTHUCTE(MaTourThucTe),
                                    CONSTRAINT FK_DVTTT_DichVuThem      FOREIGN KEY (MaDichVuThem) REFERENCES DICHVUTHEM(MaDichVuThem)
);

-- Hanh dong xanh ap dung cho tour thuc te
CREATE TABLE HDX_TOURTHUCTE (
                                MaTourThucTe     VARCHAR2(50) NOT NULL,
                                MaHanhDongXanh   VARCHAR2(50) NOT NULL,
                                CONSTRAINT PK_HDX_TOURTHUCTE        PRIMARY KEY (MaTourThucTe, MaHanhDongXanh),
                                CONSTRAINT FK_HDTTT_TourThucTe      FOREIGN KEY (MaTourThucTe)   REFERENCES TOURTHUCTE(MaTourThucTe),
                                CONSTRAINT FK_HDTTT_HanhDongXanh    FOREIGN KEY (MaHanhDongXanh) REFERENCES HANHDONGXANH(MaHanhDongXanh)
);

-- ============================================================
-- PHAN HE: DAT TOUR VA THANH TOAN
-- ============================================================

-- Don dat tour cua khach hang
CREATE TABLE DONDATTOUR (
                            MaDatTour        VARCHAR2(50)  PRIMARY KEY,
                            MaTourThucTe     VARCHAR2(50)  NOT NULL,
                            MaKhachHang      VARCHAR2(50)  NOT NULL,
                            NgayDat          TIMESTAMP     DEFAULT SYSTIMESTAMP NOT NULL,
                            TongTien         NUMBER(18,2)  NOT NULL,
                            TrangThai        VARCHAR2(30)  DEFAULT 'CHO_XAC_NHAN' NOT NULL,
                            ThoiGianHetHan   TIMESTAMP,
                            GhiChu           VARCHAR2(2000),
                            HanhDongXanh     VARCHAR2(1000),
                            CONSTRAINT FK_DDT_TourThucTe           FOREIGN KEY (MaTourThucTe) REFERENCES TOURTHUCTE(MaTourThucTe),
                            CONSTRAINT FK_DDT_KhachHang            FOREIGN KEY (MaKhachHang)  REFERENCES HOCHIEUSO(MaKhachHang),
                            CONSTRAINT CK_DDT_TongTien             CHECK (TongTien >= 0),
                            CONSTRAINT CK_DDT_TrangThai            CHECK (TrangThai IN (
                                                                                        'CHO_XAC_NHAN','DA_XAC_NHAN','DA_HUY','HET_HAN_GIU_CHO',
                                                                                        'CHO_HUY','TU_CHOI_HOAN_TIEN','THANH_TOAN_THAT_BAI'
                                ))
);

-- Danh sach nguoi dong hanh khong co tai khoan/ho chieu so
CREATE TABLE DSNGUOIDONGHANH (
                               MaNguoiDongHanh  VARCHAR2(50)  PRIMARY KEY,
                               MaDatTour        VARCHAR2(50)  NOT NULL,
                               HoTen            VARCHAR2(200) NOT NULL,
                               CCCD             VARCHAR2(20),
                               SoDienThoai      VARCHAR2(20),
                               NgaySinh         DATE,
                               GioiTinh         VARCHAR2(20),
                               GhiChu           VARCHAR2(1000),
                               CONSTRAINT UQ_DSNDH_DatTour_Nguoi      UNIQUE (MaDatTour, MaNguoiDongHanh),
                               CONSTRAINT FK_DSNDH_DatTour            FOREIGN KEY (MaDatTour) REFERENCES DONDATTOUR(MaDatTour)
);

-- Danh sach hanh khach trong 1 don dat (moi hang = 1 nguoi)
CREATE TABLE CHITIETDATTOUR (
                                MaChiTietDat         VARCHAR2(50) PRIMARY KEY,
                                MaDatTour            VARCHAR2(50) NOT NULL,
                                MaKhachHang          VARCHAR2(50),
                                MaNguoiDongHanh      VARCHAR2(50),
                                LoaiKhach            VARCHAR2(30) DEFAULT 'NGUOI_DAT' NOT NULL,
                                GiaTaiThoiDiemDat    NUMBER(18,2) NOT NULL,
                                CONSTRAINT FK_CTDT_DatTour             FOREIGN KEY (MaDatTour)   REFERENCES DONDATTOUR(MaDatTour),
                                CONSTRAINT FK_CTDT_KhachHang           FOREIGN KEY (MaKhachHang) REFERENCES HOCHIEUSO(MaKhachHang),
                                CONSTRAINT FK_CTDT_NguoiDongHanh       FOREIGN KEY (MaNguoiDongHanh) REFERENCES DSNGUOIDONGHANH(MaNguoiDongHanh),
                                CONSTRAINT FK_CTDT_NDH_DatTour         FOREIGN KEY (MaDatTour, MaNguoiDongHanh) REFERENCES DSNGUOIDONGHANH(MaDatTour, MaNguoiDongHanh),
                                CONSTRAINT CK_CTDT_LoaiKhach           CHECK (LoaiKhach IN ('NGUOI_DAT','NGUOI_DONG_HANH')),
                                CONSTRAINT CK_CTDT_Khach               CHECK (
                                    (MaKhachHang IS NOT NULL AND MaNguoiDongHanh IS NULL AND LoaiKhach = 'NGUOI_DAT') OR
                                    (MaKhachHang IS NULL AND MaNguoiDongHanh IS NOT NULL AND LoaiKhach = 'NGUOI_DONG_HANH')
                                ),
                                CONSTRAINT CK_CTDT_Gia                 CHECK (GiaTaiThoiDiemDat >= 0)
);

CREATE UNIQUE INDEX UQ_CTDT_DATTOUR_KHACHHANG
    ON CHITIETDATTOUR (
        CASE WHEN MaKhachHang IS NOT NULL THEN MaDatTour END,
        CASE WHEN MaKhachHang IS NOT NULL THEN MaKhachHang END
    );

CREATE UNIQUE INDEX UQ_CTDT_DATTOUR_NDH
    ON CHITIETDATTOUR (
        CASE WHEN MaNguoiDongHanh IS NOT NULL THEN MaDatTour END,
        CASE WHEN MaNguoiDongHanh IS NOT NULL THEN MaNguoiDongHanh END
    );

-- Dich vu bo sung trong 1 don dat
CREATE TABLE CHITIETDICHVU (
                               MaChiTietDichVu  VARCHAR2(50)  PRIMARY KEY,
                               MaDatTour        VARCHAR2(50)  NOT NULL,
                               MaDichVuThem     VARCHAR2(50)  NOT NULL,
                               SoLuong          NUMBER(10)    NOT NULL,
                               DonGia           NUMBER(18,2)  NOT NULL,
                               ThanhTien        NUMBER(18,2)  NOT NULL,
                               CONSTRAINT FK_CTDV_DatTour             FOREIGN KEY (MaDatTour)    REFERENCES DONDATTOUR(MaDatTour),
                               CONSTRAINT FK_CTDV_DichVuThem          FOREIGN KEY (MaDichVuThem) REFERENCES DICHVUTHEM(MaDichVuThem),
                               CONSTRAINT CK_CTDV_SoLuong             CHECK (SoLuong > 0),
                               CONSTRAINT CK_CTDV_DonGia              CHECK (DonGia >= 0),
                               CONSTRAINT CK_CTDV_ThanhTien           CHECK (ThanhTien >= 0),
                               CONSTRAINT CK_CTDV_ThanhTien_Tinh CHECK (ThanhTien = SoLuong * DonGia)
);

-- Chuong trinh uu dai (voucher master)
CREATE TABLE VOUCHER (
                         MaVoucher            VARCHAR2(50)   PRIMARY KEY,
                         MaCode               VARCHAR2(50)   NOT NULL,
                         LoaiUuDai            VARCHAR2(20)   NOT NULL,
                         GiaTriGiam           NUMBER(18,2)   NOT NULL,
                         MucGiamToiDa         NUMBER(18,2),
                         DieuKienApDung       VARCHAR2(2000),
                         SoLuotPhatHanh       NUMBER(10)     DEFAULT 1 NOT NULL,
                          SoLuotDaDung         NUMBER(10)     DEFAULT 0 NOT NULL,
                          NgayHieuLuc          DATE           NOT NULL,
                          NgayHetHan           DATE           NOT NULL,
                          TrangThai            VARCHAR2(20)   DEFAULT 'SAN_SANG' NOT NULL,
                          CONSTRAINT UQ_VOUCHER_MaCode           UNIQUE (MaCode),
                          CONSTRAINT CK_VOUCHER_LoaiUuDai        CHECK (LoaiUuDai IN ('PHAN_TRAM','SO_TIEN')),
                          CONSTRAINT CK_VOUCHER_GiaTriGiam       CHECK (GiaTriGiam >= 0),
                          CONSTRAINT CK_VOUCHER_MucGiamToiDa     CHECK (MucGiamToiDa IS NULL OR MucGiamToiDa > 0),
                          CONSTRAINT CK_VOUCHER_SoLuot           CHECK (SoLuotPhatHanh > 0 AND SoLuotDaDung >= 0),
                          CONSTRAINT CK_VOUCHER_DaDung           CHECK (SoLuotDaDung <= SoLuotPhatHanh),
                          CONSTRAINT CK_VOUCHER_PhanTram         CHECK (LoaiUuDai <> 'PHAN_TRAM' OR GiaTriGiam <= 100),
                          CONSTRAINT CK_VOUCHER_NgayHL           CHECK (NgayHieuLuc <= NgayHetHan),
                          CONSTRAINT CK_VOUCHER_TrangThai        CHECK (TrangThai IN ('SAN_SANG','VO_HIEU_HOA'))
);

-- Vi voucher cua khach hang
CREATE TABLE KHUYENMAI_KH (
                              MaKhachHang      VARCHAR2(50)  NOT NULL,
                              MaVoucher        VARCHAR2(50)  NOT NULL,
                              NgayHetHan       DATE,
                              NgayNhan         TIMESTAMP     DEFAULT SYSTIMESTAMP NOT NULL,
                              TrangThai        VARCHAR2(20)  DEFAULT 'CO_HIEU_LUC' NOT NULL,
                              CONSTRAINT PK_KHUYENMAIKH              PRIMARY KEY (MaKhachHang, MaVoucher),
                              CONSTRAINT FK_KMKH_KhachHang           FOREIGN KEY (MaKhachHang) REFERENCES HOCHIEUSO(MaKhachHang),
                              CONSTRAINT FK_KMKH_Voucher             FOREIGN KEY (MaVoucher)   REFERENCES VOUCHER(MaVoucher),
                              CONSTRAINT CK_KMKH_TrangThai           CHECK (TrangThai IN ('CO_HIEU_LUC','DA_SU_DUNG','DA_THU_HOI','HET_HAN'))
);

-- Lich su ap dung voucher vao don dat
CREATE TABLE DATTOUR_UUDAI (
                               MaDatTour        VARCHAR2(50)  NOT NULL,
                               MaVoucher        VARCHAR2(50)  NOT NULL,
                               SoTienUuDai      NUMBER(18,2)  NOT NULL,
                               NgayApDung       TIMESTAMP     DEFAULT SYSTIMESTAMP NOT NULL,
                               CONSTRAINT PK_DATTOUR_UUDAI            PRIMARY KEY (MaDatTour, MaVoucher),
                               CONSTRAINT FK_DTUD_DatTour             FOREIGN KEY (MaDatTour)  REFERENCES DONDATTOUR(MaDatTour),
                               CONSTRAINT FK_DTUD_Voucher             FOREIGN KEY (MaVoucher)  REFERENCES VOUCHER(MaVoucher),
                               CONSTRAINT CK_DTUD_SoTienUD            CHECK (SoTienUuDai >= 0)
);

-- Giao dich thanh toan
CREATE TABLE GIAODICH (
                          MaGiaoDich       VARCHAR2(50)  PRIMARY KEY,
                          MaDatTour        VARCHAR2(50)  NOT NULL,
                          LoaiGiaoDich     VARCHAR2(50)  NOT NULL,
                          PhuongThuc       VARCHAR2(50)  NOT NULL,
                          SoTien           NUMBER(18,2)  NOT NULL,
                          MaGDNH           VARCHAR2(200),
                          TrangThai        VARCHAR2(30)  DEFAULT 'CHO_THANH_TOAN' NOT NULL,
                          NgayThanhToan    TIMESTAMP,
                          CONSTRAINT FK_GIAODICH_DatTour         FOREIGN KEY (MaDatTour)  REFERENCES DONDATTOUR(MaDatTour),
                          CONSTRAINT CK_GIAODICH_LoaiGiaoDich    CHECK (LoaiGiaoDich IN ('THANH_TOAN','HOAN_TIEN')),
                          CONSTRAINT CK_GIAODICH_SoTien          CHECK (SoTien >= 0),
                          CONSTRAINT CK_GIAODICH_TrangThai       CHECK (TrangThai IN (
                                                                                      'CHO_THANH_TOAN','THANH_CONG','THAT_BAI','DA_HOAN_TIEN'
                              ))
);

-- Lich su tham gia tour cua khach
CREATE TABLE LICHSUTOUR (
                            MaLichSuTour     VARCHAR2(50)  PRIMARY KEY,
                            MaKhachHang      VARCHAR2(50)  NOT NULL,
                            MaTourThucTe     VARCHAR2(50)  NOT NULL,
                            MaChiTietDat     VARCHAR2(50),
                            NgayThamGia      DATE,
                            CONSTRAINT UQ_LST_KhachHang_TourThucTe UNIQUE (MaKhachHang, MaTourThucTe),
                            CONSTRAINT FK_LST_KhachHang            FOREIGN KEY (MaKhachHang)  REFERENCES HOCHIEUSO(MaKhachHang),
                            CONSTRAINT FK_LST_TourThucTe           FOREIGN KEY (MaTourThucTe) REFERENCES TOURTHUCTE(MaTourThucTe),
                            CONSTRAINT FK_LST_ChiTietDat           FOREIGN KEY (MaChiTietDat) REFERENCES CHITIETDATTOUR(MaChiTietDat)
);

-- ============================================================
-- PHAN HE: VAN HANH VA HIEN TRUONG
-- ============================================================

-- Phan cong HDV theo tour thuc te
CREATE TABLE PHANCONGTOUR (
                              MaPhanCongTour   VARCHAR2(50)  PRIMARY KEY,
                              MaTourThucTe     VARCHAR2(50)  NOT NULL,
                              MaNhanVien       VARCHAR2(50)  NOT NULL,
                              NgayPhanCong     TIMESTAMP     DEFAULT SYSTIMESTAMP NOT NULL,
                              TrangThaiChapNhan VARCHAR2(30) DEFAULT 'CHO_PHAN_HOI' NOT NULL,
                              NgayPhanHoi       TIMESTAMP,
                              CONSTRAINT UQ_PCT_TourThucTe_NhanVien UNIQUE (MaTourThucTe, MaNhanVien),
                              CONSTRAINT FK_PCT_TourThucTe           FOREIGN KEY (MaTourThucTe) REFERENCES TOURTHUCTE(MaTourThucTe),
                              CONSTRAINT FK_PCT_NhanVien             FOREIGN KEY (MaNhanVien)   REFERENCES NHANVIEN(MaNhanVien),
                              CONSTRAINT CK_PCT_TrangThaiChapNhan    CHECK (TrangThaiChapNhan IN ('CHO_PHAN_HOI','DA_DONG_Y','TU_CHOI'))
);

-- Chỉ cho phép chuyển tour sang mở bán sau khi có HDV đã đồng ý phân công.
CREATE OR REPLACE TRIGGER TRG_TTT_OPEN_REQUIRE_HDV
BEFORE UPDATE OF TrangThai ON TOURTHUCTE
FOR EACH ROW
WHEN (NEW.TrangThai = 'MO_BAN' AND OLD.TrangThai <> 'MO_BAN')
DECLARE
    v_so_hdv NUMBER;
BEGIN
    SELECT COUNT(*)
      INTO v_so_hdv
      FROM PHANCONGTOUR
     WHERE MaTourThucTe = :NEW.MaTourThucTe
       AND TrangThaiChapNhan = 'DA_DONG_Y';

    IF v_so_hdv = 0 THEN
        RAISE_APPLICATION_ERROR(-20021,
            'Tour chỉ được mở bán khi có HDV đã xác nhận phân công.');
    END IF;
END;
/

-- Nhat ky diem danh khach trong tour
CREATE TABLE DIEMDANH (
                          MaDiemDanh       VARCHAR2(50)  PRIMARY KEY,
                          MaTourThucTe     VARCHAR2(50)  NOT NULL,
                          MaKhachHang      VARCHAR2(50),
                          MaNguoiDongHanh  VARCHAR2(50),
                          LoaiKhach        VARCHAR2(30)  DEFAULT 'NGUOI_DAT' NOT NULL,
                          MaNhanVien       VARCHAR2(50)  NOT NULL,
                          ThoiGian         TIMESTAMP     DEFAULT SYSTIMESTAMP NOT NULL,
                          DiaDiem          VARCHAR2(500),
                          TrangThai        VARCHAR2(30)  DEFAULT 'CHUA_DIEM_DANH' NOT NULL,
                          CONSTRAINT FK_DIEMDANH_TourThucTe      FOREIGN KEY (MaTourThucTe) REFERENCES TOURTHUCTE(MaTourThucTe),
                          CONSTRAINT FK_DIEMDANH_KhachHang       FOREIGN KEY (MaKhachHang)  REFERENCES HOCHIEUSO(MaKhachHang),
                          CONSTRAINT FK_DIEMDANH_NguoiDongHanh   FOREIGN KEY (MaNguoiDongHanh) REFERENCES DSNGUOIDONGHANH(MaNguoiDongHanh),
                          CONSTRAINT FK_DIEMDANH_NhanVien        FOREIGN KEY (MaNhanVien)   REFERENCES NHANVIEN(MaNhanVien),
                          CONSTRAINT FK_DIEMDANH_PhanCong        FOREIGN KEY (MaTourThucTe, MaNhanVien)
                              REFERENCES PHANCONGTOUR(MaTourThucTe, MaNhanVien),
                          CONSTRAINT CK_DIEMDANH_LoaiKhach       CHECK (LoaiKhach IN ('NGUOI_DAT','NGUOI_DONG_HANH')),
                          CONSTRAINT CK_DIEMDANH_Khach           CHECK (
                              (MaKhachHang IS NOT NULL AND MaNguoiDongHanh IS NULL AND LoaiKhach = 'NGUOI_DAT') OR
                              (MaKhachHang IS NULL AND MaNguoiDongHanh IS NOT NULL AND LoaiKhach = 'NGUOI_DONG_HANH')
                          ),
                          CONSTRAINT CK_DIEMDANH_TrangThai       CHECK (TrangThai IN ('DA_DIEM_DANH','CHUA_DIEM_DANH','VANG'))
);

-- Nhat ky xac nhan hanh dong xanh
CREATE TABLE HANHDONG (
                          MaGhiNhanHanhDong    VARCHAR2(50) PRIMARY KEY,
                          MaTourThucTe         VARCHAR2(50) NOT NULL,
                          MaKhachHang          VARCHAR2(50) NOT NULL,
                          MaHanhDongXanh       VARCHAR2(50) NOT NULL,
                          MaNhanVienXacMinh    VARCHAR2(50) NOT NULL,
                          ThoiGian             TIMESTAMP    DEFAULT SYSTIMESTAMP NOT NULL,
                          MinhChung            VARCHAR2(1000),
                          CONSTRAINT FK_HANHDONG_TourThucTe      FOREIGN KEY (MaTourThucTe)      REFERENCES TOURTHUCTE(MaTourThucTe),
                          CONSTRAINT FK_HANHDONG_KhachHang       FOREIGN KEY (MaKhachHang)       REFERENCES HOCHIEUSO(MaKhachHang),
                          CONSTRAINT FK_HANHDONG_HanhDongXanh    FOREIGN KEY (MaHanhDongXanh)    REFERENCES HANHDONGXANH(MaHanhDongXanh),
                          CONSTRAINT FK_HANHDONG_NhanVienXM      FOREIGN KEY (MaNhanVienXacMinh) REFERENCES NHANVIEN(MaNhanVien),
                          CONSTRAINT FK_HANHDONG_PhanCong        FOREIGN KEY (MaTourThucTe, MaNhanVienXacMinh)
                              REFERENCES PHANCONGTOUR(MaTourThucTe, MaNhanVien),
                          CONSTRAINT UQ_HANHDONG_TOUR_KH_HDX    UNIQUE (MaTourThucTe, MaKhachHang, MaHanhDongXanh)
);

-- Nhat ky su co trong tour
CREATE TABLE NHATKYSUCO (
                            MaNhatKySuCo         VARCHAR2(50) PRIMARY KEY,
                            MaTourThucTe         VARCHAR2(50) NOT NULL,
                            MaNhanVienBaoCao     VARCHAR2(50) NOT NULL,
                            MaKhachHang          VARCHAR2(50),
                            MaNguoiDongHanh      VARCHAR2(50),
                            MoTa                 CLOB         NOT NULL,
                            GiaiPhap             CLOB,
                            MucDo                VARCHAR2(20) DEFAULT 'THAP' NOT NULL,
                            LoaiSuCo             VARCHAR2(30) DEFAULT 'KHAC' NOT NULL,
                            ThoiGianBaoCao       TIMESTAMP    DEFAULT SYSTIMESTAMP NOT NULL,
                            CONSTRAINT FK_NKSC_TourThucTe          FOREIGN KEY (MaTourThucTe)     REFERENCES TOURTHUCTE(MaTourThucTe),
                            CONSTRAINT FK_NKSC_NhanVienBC          FOREIGN KEY (MaNhanVienBaoCao) REFERENCES NHANVIEN(MaNhanVien),
                            CONSTRAINT FK_NKSC_KhachHang           FOREIGN KEY (MaKhachHang)      REFERENCES HOCHIEUSO(MaKhachHang),
                            CONSTRAINT FK_NKSC_NguoiDongHanh       FOREIGN KEY (MaNguoiDongHanh)  REFERENCES DSNGUOIDONGHANH(MaNguoiDongHanh),
                            CONSTRAINT CK_NKSC_MucDo               CHECK (MucDo IN ('THAP','SOS')),
                            CONSTRAINT CK_NKSC_LoaiSuCo            CHECK (LoaiSuCo IN ('Y_TE','THOI_TIET','PHUONG_TIEN','AN_UONG','KHAC')),
                            CONSTRAINT CK_NKSC_MotNguoiLienQuan    CHECK (
                                (MaKhachHang IS NULL AND MaNguoiDongHanh IS NULL)
                                OR (MaKhachHang IS NOT NULL AND MaNguoiDongHanh IS NULL)
                                OR (MaKhachHang IS NULL AND MaNguoiDongHanh IS NOT NULL)
                            )
);

-- Chi phi thuc te do HDV khai bao
CREATE TABLE CHIPHITHUCTE (
                              MaChiPhiThucTe   VARCHAR2(50)  PRIMARY KEY,
                              MaTourThucTe     VARCHAR2(50)  NOT NULL,
                              MaNhanVien       VARCHAR2(50)  NOT NULL,
                              DanhMuc          VARCHAR2(200) NOT NULL,
                              ThanhTien        NUMBER(18,2)  NOT NULL,
                              HoaDonAnh        VARCHAR2(1000),
                              TrangThaiDuyet   VARCHAR2(20)  DEFAULT 'CHO_DUYET' NOT NULL,
                              NgayKhai         TIMESTAMP     DEFAULT SYSTIMESTAMP NOT NULL,
                              CONSTRAINT FK_CPTT_TourThucTe          FOREIGN KEY (MaTourThucTe) REFERENCES TOURTHUCTE(MaTourThucTe),
                              CONSTRAINT FK_CPTT_NhanVien            FOREIGN KEY (MaNhanVien)   REFERENCES NHANVIEN(MaNhanVien),
                              CONSTRAINT CK_CPTT_ThanhTien           CHECK (ThanhTien >= 0),
                              CONSTRAINT CK_CPTT_TrangThaiDuyet      CHECK (TrangThaiDuyet IN ('CHO_DUYET','DA_DUYET','TU_CHOI','YEU_CAU_BO_SUNG'))
);

-- ============================================================
-- PHAN HE: TAI CHINH VA HO TRO
-- ============================================================

-- Tong hop quyet toan doanh thu - chi phi theo tour (1-1 voi TOURTHUCTE)
CREATE TABLE QUYETTOAN (
                           MaQuyetToan      VARCHAR2(50)  PRIMARY KEY,
                            MaTourThucTe     VARCHAR2(50)  NOT NULL,
                            TongDoanhThu     NUMBER(18,2)  NOT NULL,
                            TongChiPhi       NUMBER(18,2)  NOT NULL,
                            GiaCamKet        NUMBER(18,2),
                            LoiNhuan         NUMBER(18,2)  NOT NULL,
                           MaNhanVien       VARCHAR2(50)  NOT NULL,
                           NgayQuyetToan    TIMESTAMP     DEFAULT SYSTIMESTAMP NOT NULL,
                           TrangThai        VARCHAR2(20)  DEFAULT 'CHUA_QUYET_TOAN' NOT NULL,
                           GhiChu           CLOB,
                           HoaDonAnh        VARCHAR2(1000),
                           CONSTRAINT UQ_QUYETTOAN_TourThucTe     UNIQUE (MaTourThucTe),
                           CONSTRAINT FK_QT_TourThucTe            FOREIGN KEY (MaTourThucTe) REFERENCES TOURTHUCTE(MaTourThucTe),
                           CONSTRAINT FK_QT_NhanVien              FOREIGN KEY (MaNhanVien)   REFERENCES NHANVIEN(MaNhanVien),
                            CONSTRAINT CK_QT_TongDoanhThu          CHECK (TongDoanhThu >= 0),
                            CONSTRAINT CK_QT_TongChiPhi            CHECK (TongChiPhi >= 0),
                            CONSTRAINT CK_QT_GiaCamKet             CHECK (GiaCamKet IS NULL OR GiaCamKet >= 0),
                            CONSTRAINT CK_QT_TrangThai             CHECK (TrangThai IN ('CHUA_QUYET_TOAN', 'DA_QUYET_TOAN')),
                           CONSTRAINT CK_QT_LoiNhuan CHECK (LoiNhuan = TongDoanhThu - TongChiPhi)
);

-- Nhat ky quy doi diem xanh sang voucher
CREATE TABLE NHATKYDOIDIEM (
                               MaNhatKyDoiDiem  VARCHAR2(50)  PRIMARY KEY,
                               MaKhachHang      VARCHAR2(50)  NOT NULL,
                               MaVoucher        VARCHAR2(50)  NOT NULL,
                               DiemQuyDoi       NUMBER(15)    NOT NULL,
                               NgayQuyDoi       TIMESTAMP     DEFAULT SYSTIMESTAMP NOT NULL,
                               CONSTRAINT FK_NKDD_KhachHang           FOREIGN KEY (MaKhachHang) REFERENCES HOCHIEUSO(MaKhachHang),
                               CONSTRAINT FK_NKDD_Voucher             FOREIGN KEY (MaVoucher)   REFERENCES VOUCHER(MaVoucher),
                               CONSTRAINT CK_NKDD_DiemQuyDoi         CHECK (DiemQuyDoi > 0)
);

-- Ticket ho tro / khieu nai cua khach
CREATE TABLE YEUCAUHOTRO (
                             MaYeuCauHoTro    VARCHAR2(50)  PRIMARY KEY,
                             MaDatTour        VARCHAR2(50),
                             MaKhachHang      VARCHAR2(50)  NOT NULL,
                             LoaiYeuCau       VARCHAR2(100) NOT NULL,
                             NoiDung          CLOB          NOT NULL,
                             TrangThai        VARCHAR2(20)  DEFAULT 'CHUA_XU_LY' NOT NULL,
                             MaNhanVienXuLy   VARCHAR2(50),
                             CONSTRAINT FK_YCHT_DatTour             FOREIGN KEY (MaDatTour)      REFERENCES DONDATTOUR(MaDatTour),
                             CONSTRAINT FK_YCHT_KhachHang           FOREIGN KEY (MaKhachHang)    REFERENCES HOCHIEUSO(MaKhachHang),
                             CONSTRAINT FK_YCHT_NhanVienXL          FOREIGN KEY (MaNhanVienXuLy) REFERENCES NHANVIEN(MaNhanVien),
                             CONSTRAINT CK_YCHT_TrangThai           CHECK (TrangThai IN (
                                                                                         'CHUA_XU_LY','CHO_BO_SUNG','CHO_GIAI_TRINH','CHO_DUYET','DA_XU_LY','TU_CHOI'
                                 ))
);

-- Danh gia sau tour cua khach hang
CREATE TABLE DANHGIAKH (
                           MaDanhGiaKhachHang   VARCHAR2(50) PRIMARY KEY,
                           MaTourThucTe         VARCHAR2(50) NOT NULL,
                           MaKhachHang          VARCHAR2(50) NOT NULL,
                           SoSao                NUMBER(1)    NOT NULL,
                           NhanXet              CLOB,
                           NgayDanhGia          TIMESTAMP    DEFAULT SYSTIMESTAMP NOT NULL,
                           CONSTRAINT FK_DGKH_TourThucTe          FOREIGN KEY (MaTourThucTe) REFERENCES TOURTHUCTE(MaTourThucTe),
                           CONSTRAINT FK_DGKH_KhachHang           FOREIGN KEY (MaKhachHang)  REFERENCES HOCHIEUSO(MaKhachHang),
                           CONSTRAINT FK_DGKH_LichSuTour          FOREIGN KEY (MaKhachHang, MaTourThucTe)
                               REFERENCES LICHSUTOUR(MaKhachHang, MaTourThucTe),
                           CONSTRAINT CK_DANHGIAKH_SoSao          CHECK (SoSao BETWEEN 1 AND 5)
);


-- Thêm vào cuối file DDL, sau tất cả CREATE TABLE
CREATE INDEX IDX_DONDATTOUR_KHACHHANG    ON DONDATTOUR(MaKhachHang);
CREATE INDEX IDX_DONDATTOUR_TOURTHUCTE   ON DONDATTOUR(MaTourThucTe);
CREATE INDEX IDX_DONDATTOUR_TRANGTHAI    ON DONDATTOUR(TrangThai);
CREATE INDEX IDX_DSNGUOIDONGHANH_DATTOUR ON DSNGUOIDONGHANH(MaDatTour);
CREATE INDEX IDX_CTDATTOUR_DATTOUR       ON CHITIETDATTOUR(MaDatTour);
CREATE INDEX IDX_CTDICHVU_DATTOUR        ON CHITIETDICHVU(MaDatTour);
CREATE INDEX IDX_DVTTT_DICHVUTHEM        ON DICHVU_TOURTHUCTE(MaDichVuThem);
CREATE INDEX IDX_HDTTT_HANHDONGXANH      ON HDX_TOURTHUCTE(MaHanhDongXanh);
CREATE INDEX IDX_GIAODICH_DATTOUR        ON GIAODICH(MaDatTour);
CREATE INDEX IDX_PHANCONGTOUR_NHANVIEN   ON PHANCONGTOUR(MaNhanVien);
CREATE INDEX IDX_LICHSUTOUR_KHACHHANG    ON LICHSUTOUR(MaKhachHang);
CREATE INDEX IDX_HANHDONG_KHACHHANG      ON HANHDONG(MaKhachHang);
CREATE INDEX IDX_DIEMDANH_KHACHHANG      ON DIEMDANH(MaKhachHang);
CREATE INDEX IDX_CHIPHITHUCTE_TOURTT     ON CHIPHITHUCTE(MaTourThucTe);
CREATE INDEX IDX_KMKH_VOUCHER            ON KHUYENMAI_KH(MaVoucher);
CREATE INDEX IDX_NKHT_TAIKHOAN           ON NHATKYHETHONG(MaTaiKhoan);
CREATE INDEX IDX_NKHT_HANHDONG           ON NHATKYHETHONG(HanhDong);
CREATE INDEX IDX_NKHT_DOITUONG           ON NHATKYHETHONG(DoiTuong, MaDoiTuong);
CREATE INDEX IDX_NKHT_THOIGIAN           ON NHATKYHETHONG(ThoiGian);

-- ============================================================
-- BUSINESS FUNCTIONS, PROCEDURES, TRIGGERS
-- ============================================================

CREATE OR REPLACE FUNCTION FN_TINH_TIEN_UU_DAI (
    p_MaVoucher   IN VARCHAR2,
    p_TongTienDon IN NUMBER
) RETURN NUMBER
IS
    v_LoaiUuDai  VOUCHER.LoaiUuDai%TYPE;
    v_GiaTriGiam VOUCHER.GiaTriGiam%TYPE;
    v_MucGiamToiDa VOUCHER.MucGiamToiDa%TYPE;
BEGIN
    SELECT LoaiUuDai, GiaTriGiam, MucGiamToiDa
    INTO v_LoaiUuDai, v_GiaTriGiam, v_MucGiamToiDa
    FROM VOUCHER
    WHERE MaVoucher = p_MaVoucher;

    IF v_LoaiUuDai = 'PHAN_TRAM' THEN
        RETURN LEAST(NVL(p_TongTienDon, 0) * v_GiaTriGiam / 100,
                     NVL(v_MucGiamToiDa, NVL(p_TongTienDon, 0) * v_GiaTriGiam / 100));
    END IF;

    RETURN LEAST(v_GiaTriGiam, NVL(p_TongTienDon, 0));
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN 0;
END;
/

CREATE OR REPLACE FUNCTION FN_TINH_TONG_DOANH_THU (
    p_MaTourThucTe IN VARCHAR2
) RETURN NUMBER
IS
    v_Tong NUMBER(18,2);
BEGIN
    SELECT NVL(SUM(gd.SoTien), 0)
    INTO v_Tong
    FROM GIAODICH gd
    JOIN DONDATTOUR ddt ON ddt.MaDatTour = gd.MaDatTour
    WHERE ddt.MaTourThucTe = p_MaTourThucTe
      AND gd.LoaiGiaoDich = 'THANH_TOAN'
      AND gd.TrangThai = 'THANH_CONG';

    RETURN v_Tong;
END;
/

CREATE OR REPLACE FUNCTION FN_TINH_TONG_CHI_PHI (
    p_MaTourThucTe IN VARCHAR2
) RETURN NUMBER
IS
    v_Tong NUMBER(18,2);
BEGIN
    SELECT NVL(SUM(ThanhTien), 0)
    INTO v_Tong
    FROM CHIPHITHUCTE
    WHERE MaTourThucTe = p_MaTourThucTe
      AND TrangThaiDuyet = 'DA_DUYET';

    RETURN v_Tong;
END;
/

CREATE OR REPLACE FUNCTION FN_TINH_LOI_NHUAN (
    p_MaTourThucTe IN VARCHAR2
) RETURN NUMBER
IS
BEGIN
    RETURN FN_TINH_TONG_DOANH_THU(p_MaTourThucTe) - FN_TINH_TONG_CHI_PHI(p_MaTourThucTe);
END;
/

CREATE OR REPLACE FUNCTION FN_XEP_HANG_THANH_VIEN (
    p_DiemXanh IN NUMBER
) RETURN VARCHAR2
IS
    v_Diem NUMBER := NVL(p_DiemXanh, 0);
BEGIN
    IF v_Diem >= 10000 THEN
        RETURN 'KIM_CUONG';
    ELSIF v_Diem >= 5000 THEN
        RETURN 'VANG';
    ELSIF v_Diem >= 2000 THEN
        RETURN 'BAC';
    ELSIF v_Diem >= 500 THEN
        RETURN 'DONG';
    END IF;

    RETURN 'THANH_VIEN';
END;
/

CREATE OR REPLACE FUNCTION FN_TINH_PHI_HUY_TOUR (
    p_MaDatTour IN VARCHAR2
) RETURN NUMBER
IS
    v_TongTien DONDATTOUR.TongTien%TYPE;
    v_NgayKhoiHanh TOURTHUCTE.NgayKhoiHanh%TYPE;
    v_SoNgay NUMBER;
BEGIN
    SELECT ddt.TongTien, ttt.NgayKhoiHanh
    INTO v_TongTien, v_NgayKhoiHanh
    FROM DONDATTOUR ddt
    JOIN TOURTHUCTE ttt ON ttt.MaTourThucTe = ddt.MaTourThucTe
    WHERE ddt.MaDatTour = p_MaDatTour;

    v_SoNgay := TRUNC(v_NgayKhoiHanh) - TRUNC(SYSDATE);

    IF v_SoNgay > 15 THEN
        RETURN v_TongTien * 0.10;
    ELSIF v_SoNgay >= 7 THEN
        RETURN v_TongTien * 0.30;
    ELSIF v_SoNgay >= 3 THEN
        RETURN v_TongTien * 0.50;
    END IF;

    RETURN v_TongTien;
END;
/

CREATE OR REPLACE FUNCTION FN_LAY_DIEM_XANH_HIEN_TAI (
    p_MaKhachHang IN VARCHAR2
) RETURN NUMBER
IS
    v_Diem HOCHIEUSO.DiemXanh%TYPE;
BEGIN
    SELECT DiemXanh
    INTO v_Diem
    FROM HOCHIEUSO
    WHERE MaKhachHang = p_MaKhachHang;

    RETURN v_Diem;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN 0;
END;
/

CREATE OR REPLACE FUNCTION FN_CHECK_CAN_DANH_GIA (
    p_MaTourThucTe IN VARCHAR2,
    p_MaKhachHang  IN VARCHAR2
) RETURN VARCHAR2
IS
    v_Count NUMBER;
BEGIN
    SELECT COUNT(*)
    INTO v_Count
    FROM LICHSUTOUR lst
    JOIN TOURTHUCTE ttt ON ttt.MaTourThucTe = lst.MaTourThucTe
    JOIN TOURMAU tm ON tm.MaTourMau = ttt.MaTourMau
    WHERE lst.MaTourThucTe = p_MaTourThucTe
      AND lst.MaKhachHang = p_MaKhachHang
      AND ttt.TrangThai = 'KET_THUC'
      AND TRUNC(SYSDATE) <= TRUNC(ttt.NgayKhoiHanh) + tm.ThoiLuong + 30;

    RETURN CASE WHEN v_Count > 0 THEN 'YES' ELSE 'NO' END;
END;
/

CREATE OR REPLACE FUNCTION FN_LAY_GIA_TRI_VOUCHER (
    p_MaVoucher IN VARCHAR2
) RETURN NUMBER
IS
    v_GiaTri VOUCHER.GiaTriGiam%TYPE;
BEGIN
    SELECT GiaTriGiam
    INTO v_GiaTri
    FROM VOUCHER
    WHERE MaVoucher = p_MaVoucher;

    RETURN v_GiaTri;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN 0;
END;
/

CREATE OR REPLACE FUNCTION FN_TINH_TONG_TIEN_DONDAT (
    p_MaDatTour IN VARCHAR2
) RETURN NUMBER
IS
    v_TienKhach NUMBER(18,2);
    v_TienDichVu NUMBER(18,2);
    v_TienUuDai NUMBER(18,2);
BEGIN
    SELECT NVL(SUM(GiaTaiThoiDiemDat), 0)
    INTO v_TienKhach
    FROM CHITIETDATTOUR
    WHERE MaDatTour = p_MaDatTour;

    SELECT NVL(SUM(ThanhTien), 0)
    INTO v_TienDichVu
    FROM CHITIETDICHVU
    WHERE MaDatTour = p_MaDatTour;

    SELECT NVL(SUM(SoTienUuDai), 0)
    INTO v_TienUuDai
    FROM DATTOUR_UUDAI
    WHERE MaDatTour = p_MaDatTour;

    RETURN GREATEST(v_TienKhach + v_TienDichVu - v_TienUuDai, 0);
END;
/

CREATE OR REPLACE PROCEDURE PROC_HET_HAN_GIU_CHO
IS
BEGIN
    UPDATE DONDATTOUR
    SET TrangThai = 'HET_HAN_GIU_CHO'
    WHERE TrangThai = 'CHO_XAC_NHAN'
      AND ThoiGianHetHan IS NOT NULL
      AND ThoiGianHetHan < SYSTIMESTAMP;
END;
/

CREATE OR REPLACE PROCEDURE PROC_XAC_NHAN_THANH_TOAN (
    p_MaDatTour  IN VARCHAR2,
    p_PhuongThuc IN VARCHAR2,
    p_MaGDNH     IN VARCHAR2,
    p_SoTien     IN NUMBER
)
IS
BEGIN
    INSERT INTO GIAODICH (MaGiaoDich, MaDatTour, LoaiGiaoDich, PhuongThuc, SoTien, MaGDNH, TrangThai, NgayThanhToan)
    VALUES (RAWTOHEX(SYS_GUID()), p_MaDatTour, 'THANH_TOAN', p_PhuongThuc, p_SoTien, p_MaGDNH, 'THANH_CONG', SYSTIMESTAMP);
END;
/

CREATE OR REPLACE PROCEDURE PROC_HUY_DAT_TOUR (
    p_MaDatTour IN VARCHAR2,
    p_LyDo      IN VARCHAR2
)
IS
    v_TrangThai DONDATTOUR.TrangThai%TYPE;
    v_TongTien DONDATTOUR.TongTien%TYPE;
BEGIN
    SELECT TrangThai, TongTien
    INTO v_TrangThai, v_TongTien
    FROM DONDATTOUR
    WHERE MaDatTour = p_MaDatTour
    FOR UPDATE;

    IF v_TrangThai = 'DA_XAC_NHAN' THEN
        INSERT INTO GIAODICH (MaGiaoDich, MaDatTour, LoaiGiaoDich, PhuongThuc, SoTien, TrangThai, NgayThanhToan)
        VALUES (RAWTOHEX(SYS_GUID()), p_MaDatTour, 'HOAN_TIEN', 'HE_THONG', v_TongTien - FN_TINH_PHI_HUY_TOUR(p_MaDatTour), 'CHO_THANH_TOAN', SYSTIMESTAMP);
    END IF;

    UPDATE DONDATTOUR
    SET TrangThai = 'DA_HUY', GhiChu = SUBSTR(NVL(GhiChu, '') || ' ' || p_LyDo, 1, 2000)
    WHERE MaDatTour = p_MaDatTour;
END;
/

CREATE OR REPLACE PROCEDURE PROC_AP_DUNG_VOUCHER (
    p_MaDatTour IN VARCHAR2,
    p_MaVoucher IN VARCHAR2
)
IS
    v_MaKhachHang DONDATTOUR.MaKhachHang%TYPE;
    v_TongTien DONDATTOUR.TongTien%TYPE;
    v_SoTienUuDai NUMBER(18,2);
    v_Count NUMBER;
BEGIN
    SELECT MaKhachHang, TongTien
    INTO v_MaKhachHang, v_TongTien
    FROM DONDATTOUR
    WHERE MaDatTour = p_MaDatTour
    FOR UPDATE;

    SELECT COUNT(*)
    INTO v_Count
    FROM VOUCHER v
    JOIN KHUYENMAI_KH km ON km.MaVoucher = v.MaVoucher
    WHERE v.MaVoucher = p_MaVoucher
      AND km.MaKhachHang = v_MaKhachHang
      AND v.TrangThai = 'SAN_SANG'
      AND km.TrangThai = 'CO_HIEU_LUC'
      AND TRUNC(SYSDATE) BETWEEN TRUNC(v.NgayHieuLuc) AND TRUNC(v.NgayHetHan)
      AND (km.NgayHetHan IS NULL OR TRUNC(SYSDATE) <= TRUNC(km.NgayHetHan))
      AND v.SoLuotDaDung < v.SoLuotPhatHanh;

    IF v_Count = 0 THEN
        RAISE_APPLICATION_ERROR(-20041, 'Voucher khong hop le hoac khong con hieu luc');
    END IF;

    v_SoTienUuDai := FN_TINH_TIEN_UU_DAI(p_MaVoucher, v_TongTien);

    INSERT INTO DATTOUR_UUDAI (MaDatTour, MaVoucher, SoTienUuDai)
    VALUES (p_MaDatTour, p_MaVoucher, v_SoTienUuDai);

    UPDATE DONDATTOUR
    SET TongTien = GREATEST(TongTien - v_SoTienUuDai, 0)
    WHERE MaDatTour = p_MaDatTour;

    UPDATE KHUYENMAI_KH
    SET TrangThai = 'DA_SU_DUNG'
    WHERE MaKhachHang = v_MaKhachHang
      AND MaVoucher = p_MaVoucher;
END;
/

CREATE OR REPLACE PROCEDURE PROC_TINH_LAI_QUYET_TOAN (
    p_MaTourThucTe IN VARCHAR2,
    p_MaNhanVien   IN VARCHAR2
)
IS
    v_DoanhThu NUMBER(18,2);
    v_ChiPhi NUMBER(18,2);
BEGIN
    v_DoanhThu := FN_TINH_TONG_DOANH_THU(p_MaTourThucTe);
    v_ChiPhi := FN_TINH_TONG_CHI_PHI(p_MaTourThucTe);

    MERGE INTO QUYETTOAN qt
    USING (SELECT p_MaTourThucTe MaTourThucTe FROM dual) src
    ON (qt.MaTourThucTe = src.MaTourThucTe)
    WHEN MATCHED THEN
        UPDATE SET TongDoanhThu = v_DoanhThu,
                   TongChiPhi = v_ChiPhi,
                   LoiNhuan = v_DoanhThu - v_ChiPhi,
                   MaNhanVien = p_MaNhanVien,
                   NgayQuyetToan = SYSTIMESTAMP
    WHEN NOT MATCHED THEN
        INSERT (MaQuyetToan, MaTourThucTe, TongDoanhThu, TongChiPhi, LoiNhuan, MaNhanVien, TrangThai)
        VALUES (RAWTOHEX(SYS_GUID()), p_MaTourThucTe, v_DoanhThu, v_ChiPhi, v_DoanhThu - v_ChiPhi, p_MaNhanVien, 'CHUA_QUYET_TOAN');
END;
/

CREATE OR REPLACE PROCEDURE PROC_CAP_NHAT_HANG_THANH_VIEN (
    p_MaKhachHang IN VARCHAR2
)
IS
BEGIN
    UPDATE HOCHIEUSO
    SET HangThanhVien = FN_XEP_HANG_THANH_VIEN(DiemXanh)
    WHERE MaKhachHang = p_MaKhachHang;
END;
/

CREATE OR REPLACE PROCEDURE PROC_DOI_SOAT_THANH_TOAN (
    p_MaGDNH             IN VARCHAR2,
    p_TrangThaiNganHang  IN VARCHAR2
)
IS
BEGIN
    UPDATE GIAODICH
    SET TrangThai = CASE WHEN p_TrangThaiNganHang = 'THANH_CONG' THEN 'THANH_CONG' ELSE 'THAT_BAI' END,
        NgayThanhToan = CASE WHEN p_TrangThaiNganHang = 'THANH_CONG' THEN NVL(NgayThanhToan, SYSTIMESTAMP) ELSE NgayThanhToan END
    WHERE MaGDNH = p_MaGDNH;
END;
/

CREATE OR REPLACE TRIGGER TRG_KT_VAITRO_HOCHIEUSO
BEFORE INSERT OR UPDATE OF MaTaiKhoan ON HOCHIEUSO
FOR EACH ROW
DECLARE
    v_VaiTro TAIKHOAN.VaiTro%TYPE;
BEGIN
    SELECT VaiTro INTO v_VaiTro FROM TAIKHOAN WHERE MaTaiKhoan = :NEW.MaTaiKhoan;
    IF v_VaiTro <> 'KHACHHANG' THEN
        RAISE_APPLICATION_ERROR(-20001, 'Tai khoan cua HOCHIEUSO phai co VaiTro = KHACHHANG');
    END IF;
END;
/

CREATE OR REPLACE TRIGGER TRG_KT_VAITRO_NHANVIEN
BEFORE INSERT OR UPDATE OF MaTaiKhoan ON NHANVIEN
FOR EACH ROW
DECLARE
    v_VaiTro TAIKHOAN.VaiTro%TYPE;
BEGIN
    SELECT VaiTro INTO v_VaiTro FROM TAIKHOAN WHERE MaTaiKhoan = :NEW.MaTaiKhoan;
    IF v_VaiTro = 'KHACHHANG' THEN
        RAISE_APPLICATION_ERROR(-20002, 'Tai khoan cua NHANVIEN khong duoc co VaiTro = KHACHHANG');
    END IF;
END;
/

CREATE OR REPLACE TRIGGER TRG_CHAN_DOI_VAITRO_TK
BEFORE UPDATE OF VaiTro ON TAIKHOAN
FOR EACH ROW
DECLARE
    v_Count NUMBER;
BEGIN
    IF NVL(:OLD.VaiTro, '#') <> NVL(:NEW.VaiTro, '#') THEN
        SELECT COUNT(*) INTO v_Count FROM HOCHIEUSO WHERE MaTaiKhoan = :OLD.MaTaiKhoan;
        IF v_Count > 0 THEN
            RAISE_APPLICATION_ERROR(-20003, 'Khong duoc doi VaiTro cua tai khoan da lien ket HOCHIEUSO');
        END IF;

        SELECT COUNT(*) INTO v_Count FROM NHANVIEN WHERE MaTaiKhoan = :OLD.MaTaiKhoan;
        IF v_Count > 0 THEN
            RAISE_APPLICATION_ERROR(-20004, 'Khong duoc doi VaiTro cua tai khoan da lien ket NHANVIEN');
        END IF;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER TRG_KHOA_CP_QUYETTOAN
BEFORE INSERT OR UPDATE OR DELETE ON CHIPHITHUCTE
FOR EACH ROW
DECLARE
    v_MaTourThucTe CHIPHITHUCTE.MaTourThucTe%TYPE;
    v_Count NUMBER;
BEGIN
    v_MaTourThucTe := CASE WHEN DELETING THEN :OLD.MaTourThucTe ELSE :NEW.MaTourThucTe END;

    SELECT COUNT(*)
    INTO v_Count
    FROM TOURTHUCTE ttt
    WHERE ttt.MaTourThucTe = v_MaTourThucTe
      AND ttt.TrangThai = 'DA_QUYET_TOAN';

    IF v_Count > 0 THEN
        RAISE_APPLICATION_ERROR(-20005, 'Khong duoc thay doi chi phi cua tour da quyet toan');
    END IF;

    SELECT COUNT(*) INTO v_Count FROM QUYETTOAN WHERE MaTourThucTe = v_MaTourThucTe;
    IF v_Count > 0 THEN
        RAISE_APPLICATION_ERROR(-20006, 'Khong duoc thay doi chi phi khi tour da co quyet toan');
    END IF;
END;
/

CREATE OR REPLACE TRIGGER TRG_CHOT_TOUR_QUYETTOAN
AFTER INSERT ON QUYETTOAN
FOR EACH ROW
BEGIN
    UPDATE TOURTHUCTE
    SET TrangThai = 'DA_QUYET_TOAN'
    WHERE MaTourThucTe = :NEW.MaTourThucTe;
END;
/

CREATE OR REPLACE TRIGGER TRG_KT_TOUR_MO_BAN
BEFORE INSERT ON DONDATTOUR
FOR EACH ROW
DECLARE
    v_Count NUMBER;
BEGIN
    SELECT COUNT(*)
    INTO v_Count
    FROM TOURTHUCTE
    WHERE MaTourThucTe = :NEW.MaTourThucTe
      AND TrangThai = 'MO_BAN'
      AND NgayKhoiHanh > :NEW.NgayDat;

    IF v_Count = 0 THEN
        RAISE_APPLICATION_ERROR(-20007, 'Chỉ được đặt tour đang MO_BAN và chưa khởi hành');
    END IF;
END;
/

CREATE OR REPLACE TRIGGER TRG_KT_HDV_XACMINH_HD
BEFORE INSERT ON HANHDONG
FOR EACH ROW
DECLARE
    v_Count NUMBER;
BEGIN
    SELECT COUNT(*)
    INTO v_Count
    FROM PHANCONGTOUR pct
    JOIN TOURTHUCTE ttt ON ttt.MaTourThucTe = pct.MaTourThucTe
    WHERE pct.MaTourThucTe = :NEW.MaTourThucTe
      AND pct.MaNhanVien = :NEW.MaNhanVienXacMinh
      AND pct.TrangThaiChapNhan = 'DA_DONG_Y'
      AND ttt.TrangThai = 'DANG_DIEN_RA';

    IF v_Count = 0 THEN
        RAISE_APPLICATION_ERROR(-20008, 'HDV xac minh phai duoc phan cong va tour dang dien ra');
    END IF;
END;
/

CREATE OR REPLACE TRIGGER TRG_KT_SUC_CHUA_DATTOUR
FOR INSERT OR UPDATE OF MaDatTour ON CHITIETDATTOUR
COMPOUND TRIGGER
    TYPE t_keys IS TABLE OF CHITIETDATTOUR.MaDatTour%TYPE INDEX BY PLS_INTEGER;
    g_MaDatTour t_keys;
    g_Count PLS_INTEGER := 0;

    PROCEDURE add_key(p_MaDatTour IN CHITIETDATTOUR.MaDatTour%TYPE) IS
    BEGIN
        IF p_MaDatTour IS NOT NULL THEN
            g_Count := g_Count + 1;
            g_MaDatTour(g_Count) := p_MaDatTour;
        END IF;
    END;

    AFTER EACH ROW IS
    BEGIN
        add_key(:NEW.MaDatTour);
    END AFTER EACH ROW;

    AFTER STATEMENT IS
        v_VuotSucChua NUMBER;
    BEGIN
        FOR i IN 1 .. g_Count LOOP
            SELECT COUNT(*)
            INTO v_VuotSucChua
            FROM (
                SELECT COUNT(*) SoKhach, ttt.SoKhachToiDa
                FROM DONDATTOUR ddt
                JOIN TOURTHUCTE ttt ON ttt.MaTourThucTe = ddt.MaTourThucTe
                JOIN CHITIETDATTOUR ctdt ON ctdt.MaDatTour = ddt.MaDatTour
                WHERE ddt.MaDatTour = g_MaDatTour(i)
                GROUP BY ttt.SoKhachToiDa
                HAVING COUNT(*) > ttt.SoKhachToiDa
            );

            IF v_VuotSucChua > 0 THEN
                RAISE_APPLICATION_ERROR(-20009, 'So khach cua don vuot qua suc chua tour');
            END IF;
        END LOOP;
    END AFTER STATEMENT;
END TRG_KT_SUC_CHUA_DATTOUR;
/

CREATE OR REPLACE TRIGGER TRG_CAPNHAT_CHO_CONLAI
AFTER INSERT OR UPDATE OR DELETE ON CHITIETDATTOUR
DECLARE
BEGIN
    UPDATE TOURTHUCTE ttt
    SET ChoConLai = SoKhachToiDa - (
        SELECT COUNT(*)
        FROM CHITIETDATTOUR ctdt
        JOIN DONDATTOUR ddt ON ddt.MaDatTour = ctdt.MaDatTour
        WHERE ddt.MaTourThucTe = ttt.MaTourThucTe
          AND ddt.TrangThai IN ('CHO_XAC_NHAN','DA_XAC_NHAN')
    )
    WHERE ttt.MaTourThucTe IS NOT NULL;
END;
/

CREATE OR REPLACE TRIGGER TRG_DAT_HAN_GIU_CHO
BEFORE INSERT ON DONDATTOUR
FOR EACH ROW
BEGIN
    :NEW.ThoiGianHetHan := NVL(:NEW.ThoiGianHetHan, SYSTIMESTAMP + INTERVAL '15' MINUTE);
END;
/

CREATE OR REPLACE TRIGGER TRG_KT_DANHGIA_SAU_TOUR
BEFORE INSERT ON DANHGIAKH
FOR EACH ROW
BEGIN
    IF FN_CHECK_CAN_DANH_GIA(:NEW.MaTourThucTe, :NEW.MaKhachHang) <> 'YES' THEN
        RAISE_APPLICATION_ERROR(-20010, 'Khach hang khong du dieu kien danh gia tour');
    END IF;
END;
/

CREATE OR REPLACE TRIGGER TRG_KT_TRUNG_LICH_HDV
FOR INSERT OR UPDATE OF MaTourThucTe, MaNhanVien ON PHANCONGTOUR
COMPOUND TRIGGER
    AFTER STATEMENT IS
        v_Count NUMBER;
    BEGIN
        SELECT COUNT(*)
        INTO v_Count
        FROM PHANCONGTOUR pct1
        JOIN PHANCONGTOUR pct2 ON pct2.MaNhanVien = pct1.MaNhanVien
                              AND pct2.MaPhanCongTour > pct1.MaPhanCongTour
        JOIN TOURTHUCTE ttt1 ON ttt1.MaTourThucTe = pct1.MaTourThucTe
        JOIN TOURMAU tm1 ON tm1.MaTourMau = ttt1.MaTourMau
        JOIN TOURTHUCTE ttt2 ON ttt2.MaTourThucTe = pct2.MaTourThucTe
        JOIN TOURMAU tm2 ON tm2.MaTourMau = ttt2.MaTourMau
        WHERE ttt1.NgayKhoiHanh < ttt2.NgayKhoiHanh + tm2.ThoiLuong + (12/24)
          AND ttt2.NgayKhoiHanh < ttt1.NgayKhoiHanh + tm1.ThoiLuong + (12/24)
          AND pct1.TrangThaiChapNhan <> 'TU_CHOI'
          AND pct2.TrangThaiChapNhan <> 'TU_CHOI';

        IF v_Count > 0 THEN
            RAISE_APPLICATION_ERROR(-20011, 'HDV bi trung lich phan cong tour');
        END IF;
    END AFTER STATEMENT;
END TRG_KT_TRUNG_LICH_HDV;
/

CREATE OR REPLACE TRIGGER TRG_KT_GIA_TOUR_THUCTE
BEFORE INSERT OR UPDATE OF GiaHienHanh, MaTourMau ON TOURTHUCTE
FOR EACH ROW
DECLARE
    v_GiaSan TOURMAU.GiaSan%TYPE;
BEGIN
    SELECT GiaSan INTO v_GiaSan FROM TOURMAU WHERE MaTourMau = :NEW.MaTourMau;
    IF :NEW.GiaHienHanh < v_GiaSan THEN
        RAISE_APPLICATION_ERROR(-20012, 'Gia hien hanh phai lon hon hoac bang GiaSan cua tour mau');
    END IF;
END;
/

CREATE OR REPLACE TRIGGER TRG_TANG_LUOT_DUNG_VC
AFTER INSERT ON DATTOUR_UUDAI
FOR EACH ROW
BEGIN
    UPDATE VOUCHER
    SET SoLuotDaDung = SoLuotDaDung + 1
    WHERE MaVoucher = :NEW.MaVoucher;
END;
/

CREATE OR REPLACE TRIGGER TRG_KT_THANHTOAN_QUA_HAN
BEFORE INSERT ON GIAODICH
FOR EACH ROW
DECLARE
    v_ThoiGianHetHan DONDATTOUR.ThoiGianHetHan%TYPE;
BEGIN
    SELECT ThoiGianHetHan INTO v_ThoiGianHetHan FROM DONDATTOUR WHERE MaDatTour = :NEW.MaDatTour;
    IF :NEW.LoaiGiaoDich = 'THANH_TOAN'
       AND :NEW.TrangThai = 'THANH_CONG'
       AND v_ThoiGianHetHan IS NOT NULL
       AND NVL(:NEW.NgayThanhToan, SYSTIMESTAMP) > v_ThoiGianHetHan THEN
        RAISE_APPLICATION_ERROR(-20013, 'Thanh toan qua han giu cho, can xu ly hoan tien');
    END IF;
END;
/

CREATE OR REPLACE TRIGGER TRG_XACNHAN_DON_THANHTOAN
FOR INSERT OR UPDATE OF TrangThai, SoTien ON GIAODICH
COMPOUND TRIGGER
    TYPE t_keys IS TABLE OF GIAODICH.MaDatTour%TYPE INDEX BY PLS_INTEGER;
    g_MaDatTour t_keys;
    g_Count PLS_INTEGER := 0;

    AFTER EACH ROW IS
    BEGIN
        IF :NEW.LoaiGiaoDich = 'THANH_TOAN' THEN
            g_Count := g_Count + 1;
            g_MaDatTour(g_Count) := :NEW.MaDatTour;
        END IF;
    END AFTER EACH ROW;

    AFTER STATEMENT IS
        v_TongThanhToan NUMBER(18,2);
        v_TongTien DONDATTOUR.TongTien%TYPE;
    BEGIN
        FOR i IN 1 .. g_Count LOOP
            SELECT TongTien INTO v_TongTien FROM DONDATTOUR WHERE MaDatTour = g_MaDatTour(i);

            SELECT NVL(SUM(SoTien), 0)
            INTO v_TongThanhToan
            FROM GIAODICH
            WHERE MaDatTour = g_MaDatTour(i)
              AND LoaiGiaoDich = 'THANH_TOAN'
              AND TrangThai = 'THANH_CONG';

            IF v_TongThanhToan >= v_TongTien THEN
                UPDATE DONDATTOUR
                SET TrangThai = 'DA_XAC_NHAN'
                WHERE MaDatTour = g_MaDatTour(i)
                  AND TrangThai = 'CHO_XAC_NHAN';
            END IF;
        END LOOP;
    END AFTER STATEMENT;
END TRG_XACNHAN_DON_THANHTOAN;
/

CREATE OR REPLACE TRIGGER TRG_HUY_DON_KHI_HUY_TOUR
AFTER UPDATE OF TrangThai ON TOURTHUCTE
FOR EACH ROW
BEGIN
    IF :NEW.TrangThai = 'HUY' AND :OLD.TrangThai <> 'HUY' THEN
        FOR r IN (
            SELECT MaDatTour, MaKhachHang
            FROM DONDATTOUR
            WHERE MaTourThucTe = :NEW.MaTourThucTe
              AND TrangThai IN ('CHO_XAC_NHAN','DA_XAC_NHAN','CHO_HUY')
        ) LOOP
            UPDATE DONDATTOUR SET TrangThai = 'DA_HUY' WHERE MaDatTour = r.MaDatTour;

            INSERT INTO YEUCAUHOTRO (MaYeuCauHoTro, MaDatTour, MaKhachHang, LoaiYeuCau, NoiDung, TrangThai)
            VALUES (RAWTOHEX(SYS_GUID()), r.MaDatTour, r.MaKhachHang, 'HOAN_TIEN', 'Tour bi huy, can xu ly hoan tien', 'CHUA_XU_LY');
        END LOOP;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER TRG_TINH_TIEN_DICHVU
BEFORE INSERT OR UPDATE OF SoLuong, DonGia ON CHITIETDICHVU
FOR EACH ROW
BEGIN
    :NEW.ThanhTien := :NEW.SoLuong * :NEW.DonGia;
END;
/

CREATE OR REPLACE TRIGGER TRG_TINH_TONG_QUYETTOAN
BEFORE INSERT ON QUYETTOAN
FOR EACH ROW
BEGIN
    :NEW.TongDoanhThu := FN_TINH_TONG_DOANH_THU(:NEW.MaTourThucTe);
    :NEW.TongChiPhi := FN_TINH_TONG_CHI_PHI(:NEW.MaTourThucTe);
    :NEW.LoiNhuan := :NEW.TongDoanhThu - :NEW.TongChiPhi;
END;
/


-- Seed data scripts explicitly set READ COMMITTED before inserting rows
-- through role-check triggers immediately after schema creation.
