-- ============================================================
ROLLBACK;
ALTER SESSION SET ISOLATION_LEVEL = READ COMMITTED;

-- SEED DATA — Tài khoản nhân viên
-- Phạm vi    : Vai trò, tài khoản nhân viên, hồ sơ nhân viên
-- Mật khẩu mặc định cho tất cả tài khoản: password
-- BCrypt(cost=10): $2a$10$BBvBS1dGLV8lLRIF47sbfukbnxchs/ZbP6Gdb.JI2H5UZSeHOMmkK
-- ============================================================

-- ------------------------------------------------------------
-- 1. VAITRO — Vai trò hệ thống
-- ------------------------------------------------------------
INSERT INTO VAITRO (MaVaiTro, TenHienThi) VALUES ('ADMIN',      'Quản trị hệ thống');
INSERT INTO VAITRO (MaVaiTro, TenHienThi) VALUES ('SANPHAM',    'Nhân viên sản phẩm');
INSERT INTO VAITRO (MaVaiTro, TenHienThi) VALUES ('KINHDOANH',  'Nhân viên kinh doanh');
INSERT INTO VAITRO (MaVaiTro, TenHienThi) VALUES ('DIEUHANH',   'Nhân viên điều hành');
INSERT INTO VAITRO (MaVaiTro, TenHienThi) VALUES ('KETOAN',     'Kế toán');
INSERT INTO VAITRO (MaVaiTro, TenHienThi) VALUES ('HDV',        'Hướng dẫn viên');
INSERT INTO VAITRO (MaVaiTro, TenHienThi) VALUES ('KHACHHANG',  'Khách hàng');

-- ------------------------------------------------------------
-- 2. TAIKHOAN — Tài khoản nhân viên hệ thống
-- ------------------------------------------------------------
INSERT INTO TAIKHOAN (MaTaiKhoan, TenDangNhap, MatKhau, HoTen, CCCD, NgaySinh, Email, SoDienThoai, VaiTro, TrangThai)
VALUES ('TK_ADMIN01', 'admin',
        '$2a$10$BBvBS1dGLV8lLRIF47sbfukbnxchs/ZbP6Gdb.JI2H5UZSeHOMmkK',
        'Admin', '079099000001', DATE '1988-01-12', 'admin@digitaltravel.vn', '0900000001',
        'ADMIN', 'HOAT_DONG');

INSERT INTO TAIKHOAN (MaTaiKhoan, TenDangNhap, MatKhau, HoTen, CCCD, NgaySinh, Email, SoDienThoai, VaiTro, TrangThai)
VALUES ('TK_MGR01', 'manager01',
        '$2a$10$BBvBS1dGLV8lLRIF47sbfukbnxchs/ZbP6Gdb.JI2H5UZSeHOMmkK',
        'Lê Hoàng Phú', '079099000002', DATE '1990-03-08', 'dieuhanh01@digitaltravel.vn', '0900000002',
        'DIEUHANH', 'HOAT_DONG');

INSERT INTO TAIKHOAN (MaTaiKhoan, TenDangNhap, MatKhau, HoTen, CCCD, NgaySinh, Email, SoDienThoai, VaiTro, TrangThai)
VALUES ('TK_SP01', 'sanpham01',
        '$2a$10$BBvBS1dGLV8lLRIF47sbfukbnxchs/ZbP6Gdb.JI2H5UZSeHOMmkK',
        'Nguyễn Tuấn Anh', '079099000099', DATE '1992-06-18', 'sanpham01@digitaltravel.vn', '0900000099',
        'SANPHAM', 'HOAT_DONG');

INSERT INTO TAIKHOAN (MaTaiKhoan, TenDangNhap, MatKhau, HoTen, CCCD, NgaySinh, Email, SoDienThoai, VaiTro, TrangThai)
VALUES ('TK_SALES01', 'sales01',
        '$2a$10$BBvBS1dGLV8lLRIF47sbfukbnxchs/ZbP6Gdb.JI2H5UZSeHOMmkK',
        'Nguyễn Hoàng An', '079099000003', DATE '1989-09-21', 'kinhdoanh01@digitaltravel.vn', '0900000003',
        'KINHDOANH', 'HOAT_DONG');

INSERT INTO TAIKHOAN (MaTaiKhoan, TenDangNhap, MatKhau, HoTen, CCCD, NgaySinh, Email, SoDienThoai, VaiTro, TrangThai)
VALUES ('TK_KT01', 'ketoan01',
        '$2a$10$BBvBS1dGLV8lLRIF47sbfukbnxchs/ZbP6Gdb.JI2H5UZSeHOMmkK',
        'Lê Thị Minh Châu', '079099000004', DATE '1991-12-02', 'ketoan01@digitaltravel.vn', '0900000004',
        'KETOAN', 'HOAT_DONG');

INSERT INTO TAIKHOAN (MaTaiKhoan, TenDangNhap, MatKhau, HoTen, CCCD, NgaySinh, Email, SoDienThoai, VaiTro, TrangThai)
VALUES ('TK_HDV01', 'hdv01',
        '$2a$10$BBvBS1dGLV8lLRIF47sbfukbnxchs/ZbP6Gdb.JI2H5UZSeHOMmkK',
        'Nguyễn Hoàng An', '079099000005', DATE '1987-04-15', 'hdv01@digitaltravel.vn', '0900000005',
        'HDV', 'HOAT_DONG');

INSERT INTO TAIKHOAN (MaTaiKhoan, TenDangNhap, MatKhau, HoTen, CCCD, NgaySinh, Email, SoDienThoai, VaiTro, TrangThai)
VALUES ('TK_HDV02', 'hdv02',
        '$2a$10$BBvBS1dGLV8lLRIF47sbfukbnxchs/ZbP6Gdb.JI2H5UZSeHOMmkK',
        'Nguyễn Thị Hương', '079099000006', DATE '1993-07-27', 'hdv02@digitaltravel.vn', '0900000006',
        'HDV', 'HOAT_DONG');

INSERT INTO TAIKHOAN (MaTaiKhoan, TenDangNhap, MatKhau, HoTen, CCCD, NgaySinh, Email, SoDienThoai, VaiTro, TrangThai)
VALUES ('TK_HDV03', 'hdv03',
        '$2a$10$BBvBS1dGLV8lLRIF47sbfukbnxchs/ZbP6Gdb.JI2H5UZSeHOMmkK',
        'Trần Minh Khang', '079099000007', DATE '1991-11-09', 'hdv03@digitaltravel.vn', '0900000007',
        'HDV', 'HOAT_DONG');

INSERT INTO TAIKHOAN (MaTaiKhoan, TenDangNhap, MatKhau, HoTen, CCCD, NgaySinh, Email, SoDienThoai, VaiTro, TrangThai)
VALUES ('TK_HDV04', 'hdv04',
        '$2a$10$BBvBS1dGLV8lLRIF47sbfukbnxchs/ZbP6Gdb.JI2H5UZSeHOMmkK',
        'Phạm Thu Hà', '079099000008', DATE '1994-02-22', 'hdv04@digitaltravel.vn', '0900000008',
        'HDV', 'HOAT_DONG');

INSERT INTO TAIKHOAN (MaTaiKhoan, TenDangNhap, MatKhau, HoTen, CCCD, NgaySinh, Email, SoDienThoai, VaiTro, TrangThai)
VALUES ('TK_HDV05', 'hdv05',
        '$2a$10$BBvBS1dGLV8lLRIF47sbfukbnxchs/ZbP6Gdb.JI2H5UZSeHOMmkK',
        'Lê Quốc Bảo', '079099000009', DATE '1989-08-30', 'hdv05@digitaltravel.vn', '0900000009',
        'HDV', 'HOAT_DONG');

INSERT INTO TAIKHOAN (MaTaiKhoan, TenDangNhap, MatKhau, HoTen, CCCD, NgaySinh, Email, SoDienThoai, VaiTro, TrangThai)
VALUES ('TK_HDV06', 'hdv06',
        '$2a$10$BBvBS1dGLV8lLRIF47sbfukbnxchs/ZbP6Gdb.JI2H5UZSeHOMmkK',
        'Võ Ngọc Mai', '079099000010', DATE '1995-05-14', 'hdv06@digitaltravel.vn', '0900000010',
        'HDV', 'HOAT_DONG');

INSERT INTO TAIKHOAN (MaTaiKhoan, TenDangNhap, MatKhau, HoTen, CCCD, NgaySinh, Email, SoDienThoai, VaiTro, TrangThai)
VALUES ('TK_HDV07', 'hdv07',
        '$2a$10$BBvBS1dGLV8lLRIF47sbfukbnxchs/ZbP6Gdb.JI2H5UZSeHOMmkK',
        'Đỗ Hải Nam', '079099000011', DATE '1988-12-05', 'hdv07@digitaltravel.vn', '0900000011',
        'HDV', 'HOAT_DONG');

INSERT INTO TAIKHOAN (MaTaiKhoan, TenDangNhap, MatKhau, HoTen, CCCD, NgaySinh, Email, SoDienThoai, VaiTro, TrangThai)
VALUES ('TK_HDV08', 'hdv08',
        '$2a$10$BBvBS1dGLV8lLRIF47sbfukbnxchs/ZbP6Gdb.JI2H5UZSeHOMmkK',
        'Bùi Lan Anh', '079099000012', DATE '1992-10-18', 'hdv08@digitaltravel.vn', '0900000012',
        'HDV', 'HOAT_DONG');

INSERT INTO TAIKHOAN (MaTaiKhoan, TenDangNhap, MatKhau, HoTen, CCCD, NgaySinh, Email, SoDienThoai, VaiTro, TrangThai)
VALUES ('TK_HDV09', 'hdv09',
        '$2a$10$BBvBS1dGLV8lLRIF47sbfukbnxchs/ZbP6Gdb.JI2H5UZSeHOMmkK',
        'Hoàng Đức Tín', '079099000013', DATE '1990-04-26', 'hdv09@digitaltravel.vn', '0900000013',
        'HDV', 'HOAT_DONG');

INSERT INTO TAIKHOAN (MaTaiKhoan, TenDangNhap, MatKhau, HoTen, CCCD, NgaySinh, Email, SoDienThoai, VaiTro, TrangThai)
VALUES ('TK_HDV10', 'hdv10',
        '$2a$10$BBvBS1dGLV8lLRIF47sbfukbnxchs/ZbP6Gdb.JI2H5UZSeHOMmkK',
        'Ngô Thanh Vy', '079099000014', DATE '1996-01-19', 'hdv10@digitaltravel.vn', '0900000014',
        'HDV', 'HOAT_DONG');

-- ------------------------------------------------------------
-- 3. NHANVIEN — Hồ sơ nhân viên nội bộ
-- ------------------------------------------------------------
INSERT INTO NHANVIEN (MaNhanVien, MaTaiKhoan, LoaiNhanVien, NgayVaoLam, TrangThaiLamViec)
VALUES ('NV_ADMIN01',  'TK_ADMIN01',  'ADMIN',     DATE '2022-01-01', 'HOAT_DONG');
INSERT INTO NHANVIEN (MaNhanVien, MaTaiKhoan, LoaiNhanVien, NgayVaoLam, TrangThaiLamViec)
VALUES ('NV_MGR01',    'TK_MGR01',    'DIEUHANH',  DATE '2022-01-15', 'HOAT_DONG');
INSERT INTO NHANVIEN (MaNhanVien, MaTaiKhoan, LoaiNhanVien, NgayVaoLam, TrangThaiLamViec)
VALUES ('NV_SP01',     'TK_SP01',     'SANPHAM',   DATE '2022-02-01', 'HOAT_DONG');
INSERT INTO NHANVIEN (MaNhanVien, MaTaiKhoan, LoaiNhanVien, NgayVaoLam, TrangThaiLamViec)
VALUES ('NV_SALES01',  'TK_SALES01',  'KINHDOANH', DATE '2023-03-01', 'HOAT_DONG');
INSERT INTO NHANVIEN (MaNhanVien, MaTaiKhoan, LoaiNhanVien, NgayVaoLam, TrangThaiLamViec)
VALUES ('NV_KT01',     'TK_KT01',     'KETOAN',    DATE '2023-06-01', 'HOAT_DONG');
INSERT INTO NHANVIEN (MaNhanVien, MaTaiKhoan, LoaiNhanVien, NgayVaoLam, TrangThaiLamViec)
VALUES ('NV_HDV01',    'TK_HDV01',    'HDV',       DATE '2021-09-15', 'HOAT_DONG');
INSERT INTO NHANVIEN (MaNhanVien, MaTaiKhoan, LoaiNhanVien, NgayVaoLam, TrangThaiLamViec)
VALUES ('NV_HDV02',    'TK_HDV02',    'HDV',       DATE '2022-05-10', 'HOAT_DONG');
INSERT INTO NHANVIEN (MaNhanVien, MaTaiKhoan, LoaiNhanVien, NgayVaoLam, TrangThaiLamViec)
VALUES ('NV_HDV03',    'TK_HDV03',    'HDV',       DATE '2022-08-20', 'HOAT_DONG');
INSERT INTO NHANVIEN (MaNhanVien, MaTaiKhoan, LoaiNhanVien, NgayVaoLam, TrangThaiLamViec)
VALUES ('NV_HDV04',    'TK_HDV04',    'HDV',       DATE '2023-01-10', 'HOAT_DONG');
INSERT INTO NHANVIEN (MaNhanVien, MaTaiKhoan, LoaiNhanVien, NgayVaoLam, TrangThaiLamViec)
VALUES ('NV_HDV05',    'TK_HDV05',    'HDV',       DATE '2021-12-01', 'HOAT_DONG');
INSERT INTO NHANVIEN (MaNhanVien, MaTaiKhoan, LoaiNhanVien, NgayVaoLam, TrangThaiLamViec)
VALUES ('NV_HDV06',    'TK_HDV06',    'HDV',       DATE '2023-04-15', 'HOAT_DONG');
INSERT INTO NHANVIEN (MaNhanVien, MaTaiKhoan, LoaiNhanVien, NgayVaoLam, TrangThaiLamViec)
VALUES ('NV_HDV07',    'TK_HDV07',    'HDV',       DATE '2020-11-25', 'HOAT_DONG');
INSERT INTO NHANVIEN (MaNhanVien, MaTaiKhoan, LoaiNhanVien, NgayVaoLam, TrangThaiLamViec)
VALUES ('NV_HDV08',    'TK_HDV08',    'HDV',       DATE '2022-09-05', 'HOAT_DONG');
INSERT INTO NHANVIEN (MaNhanVien, MaTaiKhoan, LoaiNhanVien, NgayVaoLam, TrangThaiLamViec)
VALUES ('NV_HDV09',    'TK_HDV09',    'HDV',       DATE '2021-06-12', 'HOAT_DONG');
INSERT INTO NHANVIEN (MaNhanVien, MaTaiKhoan, LoaiNhanVien, NgayVaoLam, TrangThaiLamViec)
VALUES ('NV_HDV10',    'TK_HDV10',    'HDV',       DATE '2023-07-01', 'HOAT_DONG');

-- ------------------------------------------------------------
-- 4. NANGLUCNHANVIEN — Đánh giá và năng lực
-- ------------------------------------------------------------
INSERT INTO NANGLUCNHANVIEN (MaNangLucNhanVien, MaNhanVien, NgonNgu, ChungChi, ChuyenMon, DanhGia, SoDanhGia)
VALUES ('NLNV_HDV01', 'NV_HDV01', 'Tiếng Anh, Tiếng Pháp', 'Thẻ HDV Quốc tế, Sơ cấp cứu', 'Chuyên thuyết minh lịch sử - văn hóa: Có khả năng kể chuyện hấp dẫn về di tích, lịch sử, phong tục và đời sống địa phương.', 4.8, 126);

INSERT INTO NANGLUCNHANVIEN (MaNangLucNhanVien, MaNhanVien, NgonNgu, ChungChi, ChuyenMon, DanhGia, SoDanhGia)
VALUES ('NLNV_HDV02', 'NV_HDV02', 'Tiếng Anh, Tiếng Trung', 'Thẻ HDV Quốc tế', 'Chuyên chăm sóc khách gia đình: Biết cách hỗ trợ đoàn có trẻ em, người lớn tuổi và khách cần sự quan tâm đặc biệt.', 4.5, 89);

INSERT INTO NANGLUCNHANVIEN (MaNangLucNhanVien, MaNhanVien, NgonNgu, ChungChi, ChuyenMon, DanhGia, SoDanhGia)
VALUES ('NLNV_HDV03', 'NV_HDV03', 'Tiếng Anh, Tiếng Nhật', 'Thẻ HDV Quốc tế, Nghiệp vụ lữ hành', 'Chuyên thuyết minh lịch sử - văn hóa: Có khả năng kể chuyện hấp dẫn về di sản miền Trung, văn hóa Chăm và đời sống địa phương.', 4.7, 102);

INSERT INTO NANGLUCNHANVIEN (MaNangLucNhanVien, MaNhanVien, NgonNgu, ChungChi, ChuyenMon, DanhGia, SoDanhGia)
VALUES ('NLNV_HDV04', 'NV_HDV04', 'Tiếng Anh, Tiếng Hàn', 'Thẻ HDV Quốc tế, Sơ cấp cứu', 'Chuyên chăm sóc khách gia đình: Biết cách hỗ trợ đoàn nghỉ dưỡng biển có trẻ em, người lớn tuổi và khách cần sự quan tâm đặc biệt.', 4.6, 94);

INSERT INTO NANGLUCNHANVIEN (MaNangLucNhanVien, MaNhanVien, NgonNgu, ChungChi, ChuyenMon, DanhGia, SoDanhGia)
VALUES ('NLNV_HDV05', 'NV_HDV05', 'Tiếng Anh, Tiếng Đức', 'Thẻ HDV Quốc tế', 'Chuyên thuyết minh lịch sử - văn hóa: Am hiểu kiến trúc, di tích và biết phục vụ các đoàn trải nghiệm cao cấp.', 4.9, 138);

INSERT INTO NANGLUCNHANVIEN (MaNangLucNhanVien, MaNhanVien, NgonNgu, ChungChi, ChuyenMon, DanhGia, SoDanhGia)
VALUES ('NLNV_HDV06', 'NV_HDV06', 'Tiếng Anh, Tiếng Thái', 'Thẻ HDV Nội địa, Kỹ năng hoạt náo', 'Chuyên tour học sinh - sinh viên: Biết cách truyền đạt dễ hiểu, tổ chức hoạt động tập thể và quản lý đoàn trẻ.', 4.4, 76);

INSERT INTO NANGLUCNHANVIEN (MaNangLucNhanVien, MaNhanVien, NgonNgu, ChungChi, ChuyenMon, DanhGia, SoDanhGia)
VALUES ('NLNV_HDV07', 'NV_HDV07', 'Tiếng Anh, Tiếng Nga', 'Thẻ HDV Quốc tế, Cứu hộ du lịch', 'Chuyên dẫn tour mạo hiểm: Có kinh nghiệm hỗ trợ khách trong các hoạt động như leo núi, trekking, chèo thuyền và cắm trại.', 4.75, 117);

INSERT INTO NANGLUCNHANVIEN (MaNangLucNhanVien, MaNhanVien, NgonNgu, ChungChi, ChuyenMon, DanhGia, SoDanhGia)
VALUES ('NLNV_HDV08', 'NV_HDV08', 'Tiếng Anh, Tiếng Trung', 'Thẻ HDV Quốc tế, Nghiệp vụ chăm sóc khách hàng', 'Chuyên chăm sóc đoàn doanh nghiệp: Thành thạo tổ chức lịch trình MICE, hỗ trợ sự kiện và yêu cầu dịch vụ theo đoàn.', 4.55, 83);

INSERT INTO NANGLUCNHANVIEN (MaNangLucNhanVien, MaNhanVien, NgonNgu, ChungChi, ChuyenMon, DanhGia, SoDanhGia)
VALUES ('NLNV_HDV09', 'NV_HDV09', 'Tiếng Anh, Tiếng Pháp', 'Thẻ HDV Quốc tế', 'Chuyên dẫn tour sinh thái: Am hiểu thiên nhiên, rừng, cảnh quan Tây Nguyên và các hoạt động bảo vệ môi trường.', 4.65, 91);

INSERT INTO NANGLUCNHANVIEN (MaNangLucNhanVien, MaNhanVien, NgonNgu, ChungChi, ChuyenMon, DanhGia, SoDanhGia)
VALUES ('NLNV_HDV10', 'NV_HDV10', 'Tiếng Anh, Tiếng Hàn', 'Thẻ HDV Nội địa, Sơ cấp cứu', 'Chuyên dẫn tour sinh thái: Am hiểu thiên nhiên, hệ sinh thái và trải nghiệm cộng đồng gắn với bảo vệ môi trường.', 4.5, 68);

COMMIT;
-- ============================================================
-- END SEED DATA
-- ============================================================
