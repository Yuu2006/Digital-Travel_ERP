# Digital Travel ERP — Backend API

**Công nghệ:** Java 21 · Spring Boot 4.0.5 · Oracle 19c/21c · JWT (HS256)
**Package gốc:** `com.digitaltravel.erp`
**Branch hiện tại:** `main`

---

## Mục lục

1. [Cài đặt & Chạy](#cài-đặt--chạy)
2. [Cấu trúc project](#cấu-trúc-project)
3. [Xác thực & Phân quyền](#xác-thực--phân-quyền)
4. [API Reference](#api-reference)
5. [Trạng thái cài đặt Use Case](#trạng-thái-cài-đặt-use-case)
6. [Biến môi trường](#biến-môi-trường)
7. [Ghi chú kỹ thuật](#ghi-chú-kỹ-thuật)
8. [Kiểm thử](#kiểm-thử)

---

## Cài đặt & Chạy

### Yêu cầu

- Java 21+
- Maven Wrapper đính kèm (`mvnw.cmd`, Maven 3.9.14)
- Oracle 19c/21c đang chạy và đã tạo schema

### Biến môi trường (`.env` hoặc OS env)

```
DB_HOST=localhost
DB_PORT=1521
DB_SERVICE=XEPDB1
DB_USERNAME=digital_travel
DB_PASSWORD=<mật khẩu>
JWT_SECRET=<chuỗi base64, tối thiểu 42 ký tự>
JWT_EXPIRATION=86400000
```

### Khởi tạo DB

Chạy script DDL:

```sql
-- Trong DBeaver hoặc SQL*Plus:
@src/main/resources/db/KhoiTaoBang.sql
```

Seed tài khoản nhân viên nếu cần:

```sql
@src/main/resources/db/data_v1.sql
```

### Chạy ứng dụng

```bash
# Windows
.\mvnw.cmd spring-boot:run

# Hoặc build JAR
.\mvnw.cmd clean package -DskipTests
java -jar target/digital-travel-erp-*.jar
```

Swagger UI: http://localhost:8080/swagger-ui/index.html

---

## Cấu trúc project

```
src/main/java/com/digitaltravel/erp/
├── config/
│   ├── JwtAuthFilter.java          -- Trích xuất & validate Bearer token
│   ├── JwtUtil.java                -- Generate / parse JWT (HS256)
│   ├── JwtAuthEntryPoint.java      -- 401 handler
│   ├── JwtAccessDeniedHandler.java -- 403 handler
│   ├── SecurityConfig.java         -- RBAC route rules
│   ├── TaiKhoanDetails.java        -- UserDetails impl
│   └── VaiTroConst.java            -- Role constants
├── controller/
│   ├── AuthController.java
│   ├── TourCongKhaiController.java
│   ├── TourMauController.java
│   ├── TourThucTeController.java
│   ├── DichVuThemController.java
│   ├── HanhDongXanhController.java
│   ├── KhachHangController.java
│   ├── VoucherController.java
│   ├── VoucherAdminController.java
│   ├── DanhGiaController.java
│   ├── KinhDoanhDatTourController.java
│   ├── ThanhToanController.java
│   ├── NhanVienController.java
│   ├── TaiKhoanAdminController.java
│   ├── PhanCongController.java
│   ├── VanHanhController.java
│   ├── QuanTriController.java
│   └── QuyetToanController.java
├── service/        -- Business logic
├── repository/     -- Spring Data JPA
├── entity/         -- JPA entities (Oracle mapping)
├── dto/
│   ├── requests/   -- Input DTOs (@Valid)
│   └── responses/  -- Output DTOs
└── exception/      -- GlobalExceptionHandler + AppException
```

---

## Xác thực & Phân quyền

### Roles

| Role | Mô tả |
|------|-------|
| `ADMIN` | Toàn quyền hệ thống |
| `SANPHAM` | Quản lý sản phẩm (tour mẫu, dịch vụ/phụ thu phòng, hành động xanh) |
| `KINHDOANH` | Xử lý đơn hàng, hủy tour, khiếu nại, voucher |
| `DIEUHANH` | Quản lý nhân sự, phân công HDV, tour thực tế |
| `KETOAN` | Quyết toán, chi phí, báo cáo tài chính |
| `HDV` | Vận hành thực địa, điểm danh, sự cố |
| `KHACHHANG` | Khách hàng — đặt tour, thanh toán |

### URL Pattern → Quyền

| Pattern | Roles |
|---------|-------|
| `/api/quan-tri/**` | ADMIN |
| `/api/san-pham/**` | SANPHAM |
| `/api/kinh-doanh/**` | KINHDOANH |
| `/api/dieu-hanh/**` | DIEUHANH |
| `/api/huong-dan-vien/**` | DIEUHANH, HDV |
| `/api/ke-toan/**` | KETOAN |
| `/api/khach-hang/**` | KHACHHANG |
| `/api/thanh-toan/**` | KHACHHANG |
| `/api/auth/**` | Public |
| `/api/public/**` | Public |
| `/swagger-ui/**`, `/v3/api-docs/**` | Public |

---

## API Reference

### Auth (`/api/auth`)

| Method | Path | Auth | UC | Trạng thái |
|--------|------|------|----|-----------|
| POST | `/api/auth/dang-ky` | Public | UC56 | DONE |
| POST | `/api/auth/dang-nhap` | Public | UC57 | DONE |
| POST | `/api/auth/dang-xuat` | Bearer | UC58 | DONE |
| POST | `/api/auth/doi-mat-khau` | Bearer | UC60 | DONE |
| POST | `/api/auth/quen-mat-khau` | Public | UC59 | DONE |
| POST | `/api/auth/dat-lai-mat-khau` | Public | UC59 | DONE |

### Tour Công khai (`/api/public`)

| Method | Path | Auth | UC | Trạng thái |
|--------|------|------|----|----------|
| GET | `/api/public/tour` | Public | UC25 | DONE |
| GET | `/api/public/tour/{maTourThucTe}` | Public | UC26 | DONE |
| GET | `/api/public/tour/{maTourThucTe}/danh-gia` | Public | UC26 | DONE |

> Filter hỗ trợ: `giaTu`, `giaDen`, `thoiLuongMin`, `thoiLuongMax`, phân trang.

### Admin — Tài khoản nhân viên (`/api/quan-tri`)

| Method | Path | Auth | UC | Trạng thái |
|--------|------|------|----|----------|
| POST | `/api/quan-tri/dang-ky-nhan-vien` | ADMIN | UC62 | DONE |
| GET | `/api/quan-tri/nhan-vien` | ADMIN | UC66 | DONE |
| GET | `/api/quan-tri/nhan-vien/{maNhanVien}` | ADMIN | UC66 | DONE |
| PUT | `/api/quan-tri/nhan-vien/{maNhanVien}/khoa` | ADMIN | UC64 | DONE |
| PUT | `/api/quan-tri/nhan-vien/{maNhanVien}/mo-khoa` | ADMIN | UC65 | DONE |
| PUT | `/api/quan-tri/nhan-vien/{maNhanVien}/vai-tro` | ADMIN | UC67 | DONE |
| GET | `/api/quan-tri/nhat-ky-he-thong` | ADMIN | UC68 | DONE |

> **Request body PUT /vai-tro:** `{"maVaiTro": "KETOAN"}` — Giá trị hợp lệ: `SANPHAM`, `KINHDOANH`, `DIEUHANH`, `KETOAN`, `HDV`

### Điều hành — Năng lực nhân viên (`/api/dieu-hanh`)

| Method | Path | Auth | UC | Trạng thái |
|--------|------|------|----|----------|
| GET | `/api/dieu-hanh/nhan-vien/{maNhanVien}/nang-luc` | DIEUHANH | UC63 | DONE |
| PUT | `/api/dieu-hanh/nhan-vien/{maNhanVien}/nang-luc` | DIEUHANH | UC63 | DONE |
| GET | `/api/huong-dan-vien/nang-luc` | HDV | UC63 | DONE |

### Kinh doanh — Khách hàng & Đơn đặt tour (`/api/kinh-doanh`)

| Method | Path | Auth | UC | Trạng thái |
|--------|------|------|----|----------|
| GET | `/api/kinh-doanh/khach-hang` | KINHDOANH | UC24 | DONE |
| GET | `/api/kinh-doanh/khach-hang/{maKhachHang}` | KINHDOANH | UC24 | DONE |
| GET | `/api/kinh-doanh/dat-tour` | KINHDOANH | UC34 | DONE |
| PUT | `/api/kinh-doanh/dat-tour/{maDatTour}/xac-nhan` | KINHDOANH | UC34 | DONE |
| GET | `/api/kinh-doanh/danh-gia` | KINHDOANH | UC35 | DONE |

### Tour Mẫu (`/api/san-pham/tour-mau`)

| Method | Path | Auth | UC | Trạng thái |
|--------|------|------|----|----------|
| GET | `/api/san-pham/tour-mau` | SANPHAM | UC06 | DONE |
| GET | `/api/san-pham/tour-mau/{id}` | SANPHAM | UC06 | DONE |
| POST | `/api/san-pham/tour-mau` | SANPHAM | UC02 | DONE |
| PUT | `/api/san-pham/tour-mau/{id}` | SANPHAM | UC04 | DONE |
| DELETE | `/api/san-pham/tour-mau/{id}` | SANPHAM | UC05 | DONE (soft delete) |
| POST | `/api/san-pham/tour-mau/{id}/sao-chep` | SANPHAM | UC03 | DONE |
| POST | `/api/san-pham/tour-mau/{id}/lich-trinh` | SANPHAM | UC08 | DONE |
| PUT | `/api/san-pham/tour-mau/{id}/lich-trinh/{maLT}` | SANPHAM | UC09 | DONE |
| DELETE | `/api/san-pham/tour-mau/{id}/lich-trinh/{maLT}` | SANPHAM | UC09 | DONE |

### Tour Thực tế (`/api/dieu-hanh/tour-thuc-te`)

| Method | Path | Auth | UC | Trạng thái |
|--------|------|------|----|----------|
| GET | `/api/dieu-hanh/tour-thuc-te` | DIEUHANH | UC14 | DONE |
| GET | `/api/dieu-hanh/tour-thuc-te/{id}` | DIEUHANH | UC14 | DONE |
| POST | `/api/dieu-hanh/tour-thuc-te` | DIEUHANH | UC11 | DONE |
| PUT | `/api/dieu-hanh/tour-thuc-te/{id}` | DIEUHANH | UC13 | DONE |
| DELETE | `/api/dieu-hanh/tour-thuc-te/{id}` | DIEUHANH | UC12 | DONE |

### Danh mục (`/api/san-pham`)

| Method | Path | Auth | UC | Trạng thái |
|--------|------|------|----|-----------|
| GET | `/api/san-pham/dich-vu-them/**` | SANPHAM | UC19 | DONE |
| POST | `/api/san-pham/dich-vu-them/**` | SANPHAM | UC16 | DONE, gồm phụ thu phòng |
| PUT | `/api/san-pham/dich-vu-them/**` | SANPHAM | UC17 | DONE |
| DELETE | `/api/san-pham/dich-vu-them/**` | SANPHAM | UC18 | DONE |
| GET/POST/PUT/DELETE | `/api/san-pham/hanh-dong-xanh/**` | SANPHAM | UC20 | DONE |

### Khách hàng (`/api/khach-hang`)

| Method | Path | Auth | UC | Trạng thái |
|--------|------|------|----|-----------|
| GET | `/api/khach-hang/ho-so` | KHACHHANG | UC21 | DONE |
| PUT | `/api/khach-hang/ho-so` | KHACHHANG | UC23 | DONE |
| GET | `/api/khach-hang/lich-su-tour` | KHACHHANG | UC22 | DONE |
| POST | `/api/khach-hang/dat-tour` | KHACHHANG | UC27 | DONE |
| GET | `/api/khach-hang/dat-tour` | KHACHHANG | UC27 | DONE |
| GET | `/api/khach-hang/dat-tour/{id}` | KHACHHANG | UC27 | DONE |
| DELETE | `/api/khach-hang/dat-tour/{id}` | KHACHHANG | UC32 | DONE (hủy CHO_XAC_NHAN) |
| POST | `/api/khach-hang/dat-tour/{id}/huy` | KHACHHANG | UC32 | DONE (yêu cầu hủy DA_XAC_NHAN) |
| GET | `/api/khach-hang/vi-voucher` | KHACHHANG | UC31 | DONE |
| POST | `/api/khach-hang/ap-voucher` | KHACHHANG | UC28 | DONE |
| POST | `/api/khach-hang/doi-diem` | KHACHHANG | UC30 | DONE |
| POST | `/api/khach-hang/danh-gia` | KHACHHANG | UC35 | DONE |
| POST | `/api/khach-hang/yeu-cau-ho-tro` | KHACHHANG | UC36 | DONE |
| GET | `/api/khach-hang/yeu-cau-ho-tro` | KHACHHANG | UC36 | DONE |

### Thanh toán (`/api/thanh-toan`)

| Method | Path | Auth | UC | Trạng thái |
|--------|------|------|----|-----------|
| POST | `/api/thanh-toan/khoi-tao` | KHACHHANG | UC29 | DONE (mock=true) |
| GET | `/api/thanh-toan/{maDatTour}/ket-qua` | KHACHHANG | UC29 | DONE |

> Thanh toán thực MoMo chưa tích hợp. Mock payment đang bật mặc định cho demo qua `PAYMENT_MOCK_ENABLED=true`.
> Khi mock bật, endpoint sẽ xác nhận thanh toán ngay; trường `"mock": true` vẫn có thể gửi để thể hiện rõ request demo.

**Request body mẫu:**
```json
{
  "maDonDatTour": "DDT_XXXXX",
  "phuongThuc": "MOCK",
  "mock": true
}
```

### Kinh doanh — Hủy tour & Hỗ trợ (`/api/kinh-doanh`)

| Method | Path | Auth | UC | Trạng thái |
|--------|------|------|----|----------|
| GET | `/api/kinh-doanh/yeu-cau-huy` | KINHDOANH | UC33 | DONE |
| POST | `/api/kinh-doanh/yeu-cau-huy/{maYeuCau}/duyet` | KINHDOANH | UC33 | DONE |
| POST | `/api/kinh-doanh/yeu-cau-huy/{maYeuCau}/tu-choi` | KINHDOANH | UC33 | DONE |
| GET | `/api/kinh-doanh/don-dat-tour` | KINHDOANH | UC34 | DONE |
| GET | `/api/kinh-doanh/yeu-cau-ho-tro` | KINHDOANH | UC39 | DONE |
| PUT | `/api/kinh-doanh/yeu-cau-ho-tro/{maYeuCau}` | KINHDOANH | UC39 | DONE |

### Điều hành — Phân công HDV (`/api/dieu-hanh`)

| Method | Path | Auth | UC | Trạng thái |
|--------|------|------|----|----------|
| GET | `/api/dieu-hanh/hdv-kha-dung?maTourThucTe=...` | DIEUHANH | UC38 | DONE |
| POST | `/api/dieu-hanh/phan-cong` | DIEUHANH | UC37 | DONE |
| DELETE | `/api/dieu-hanh/phan-cong/{maPhanCong}` | DIEUHANH | UC37 | DONE |

**Request body POST /phan-cong:**
```json
{
  "maTourThucTe": "TTT_XXXXX",
  "maNhanVien": "NV_XXXXX",
  "ghiChu": "Ghi chú phân công"
}
```

### HDV — Vận hành thực địa (`/api/huong-dan-vien`)

| Method | Path | Auth | UC | Trạng thái |
|--------|------|------|----|----------|
| GET | `/api/huong-dan-vien/tour-cua-toi` | HDV | UC40 | DONE |
| GET | `/api/huong-dan-vien/lich-cong-tac` | DIEUHANH, HDV | UC40 | DONE |
| GET | `/api/dieu-hanh/lich-cong-tac` | DIEUHANH, HDV | UC40 | DONE |
| GET | `/api/huong-dan-vien/tour/{maTour}/doan` | DIEUHANH, HDV | UC40 | DONE |
| GET | `/api/dieu-hanh/tour/{maTour}/doan` | DIEUHANH, HDV | UC40 | DONE |
| POST | `/api/huong-dan-vien/tour/{maTour}/diem-danh` | HDV | UC41 | DONE |
| POST | `/api/huong-dan-vien/tour/{maTour}/hanh-dong-xanh` | HDV | UC42 | DONE |
| GET | `/api/huong-dan-vien/tour/{maTour}/su-co` | DIEUHANH, HDV | UC43 | DONE, filter `mucDo` |
| GET | `/api/dieu-hanh/tour/{maTour}/su-co` | DIEUHANH, HDV | UC43 | DONE, filter `mucDo` |
| POST | `/api/huong-dan-vien/tour/{maTour}/su-co` | HDV | UC43 | DONE |
| PUT | `/api/huong-dan-vien/su-co/{maSuCo}` | HDV | UC43 | DONE |
| POST | `/api/huong-dan-vien/tour/{maTour}/chi-phi` | HDV | UC44 | DONE |
| GET | `/api/huong-dan-vien/tour/{maTour}/chi-phi` | DIEUHANH, HDV | UC44 | DONE |
| GET | `/api/dieu-hanh/tour/{maTour}/chi-phi` | DIEUHANH, HDV | UC44 | DONE |
| PUT | `/api/huong-dan-vien/chi-phi/{maChiPhi}/bo-sung` | HDV | UC44/UC47 | DONE |

### Kế toán — Chi phí & Quyết toán (`/api/ke-toan`)

| Method | Path | Auth | UC | Trạng thái |
|--------|------|------|----|-----------|
| GET | `/api/ke-toan/chi-phi?trangThai=CHO_DUYET` | KETOAN | UC47 | DONE |
| PUT | `/api/ke-toan/chi-phi/{maChiPhi}/duyet` | KETOAN | UC47 | DONE |
| PUT | `/api/ke-toan/chi-phi/{maChiPhi}/tu-choi` | KETOAN | UC47 | DONE |
| PUT | `/api/ke-toan/chi-phi/{maChiPhi}/yeu-cau-bo-sung` | KETOAN | UC47 | DONE |
| GET | `/api/ke-toan/canh-bao-chi-phi` | KETOAN | UC46 | DONE |
| GET | `/api/ke-toan/canh-bao-chi-phi/{maCanhBao}` | KETOAN | UC46 | DONE |
| GET | `/api/ke-toan/tour-can-quyet-toan` | KETOAN | UC49 | DONE |
| GET | `/api/ke-toan/tinh-toan/{maTour}` | KETOAN | UC45 | DONE (xem trước) |
| POST | `/api/ke-toan/quyet-toan/{maTour}` | KETOAN | UC48 | DONE |
| PUT | `/api/ke-toan/quyet-toan/{id}/chot` | KETOAN | UC48 | DONE (CHUA_QUYET_TOAN to DA_QUYET_TOAN) |
| GET | `/api/ke-toan/quyet-toan` | KETOAN | UC48 | DONE |
| GET | `/api/ke-toan/quyet-toan/{id}` | KETOAN | UC48 | DONE |
| GET | `/api/ke-toan/giao-dich-hoan` | KETOAN | UC50 | DONE |
| PUT | `/api/ke-toan/giao-dich-hoan/{id}/xac-nhan` | KETOAN | UC50 | DONE |
| PUT | `/api/ke-toan/giao-dich-hoan/{id}/tu-choi` | KETOAN | UC50 | DONE |
| GET | `/api/ke-toan/bao-cao/doanh-thu` | KETOAN | UC51 | DONE (API báo cáo doanh thu) |

> Filter báo cáo: `tuNgay`, `denNgay` (ISO date format: `yyyy-MM-dd`).

### Voucher Admin (`/api/kinh-doanh/voucher`)

| Method | Path | Auth | UC | Trạng thái |
|--------|------|------|----|----------|
| GET | `/api/kinh-doanh/voucher` | KINHDOANH | UC52 | DONE |
| GET | `/api/kinh-doanh/voucher/{maVoucher}` | KINHDOANH | UC52 | DONE |
| POST | `/api/kinh-doanh/voucher` | KINHDOANH | UC53 | DONE |
| PUT | `/api/kinh-doanh/voucher/{maVoucher}` | KINHDOANH | UC52 | DONE |
| PUT | `/api/kinh-doanh/voucher/{maVoucher}/vo-hieu-hoa` | KINHDOANH | UC52 | DONE |
| POST | `/api/kinh-doanh/voucher/{maVoucher}/phat-hanh` | KINHDOANH | UC54 | DONE |
| PUT | `/api/kinh-doanh/voucher/{maVoucher}/khach-hang/{maKhachHang}/thu-hoi` | KINHDOANH | UC54 | DONE |

---

## Trạng thái cài đặt Use Case

Các UC cấp cha dùng để gom nhóm nghiệp vụ, không nhất thiết ánh xạ trực tiếp 1 endpoint.

| UC | Tên | Module | Trạng thái |
|----|-----|--------|------------|
| UC01 | Quản lý tour mẫu | Sản phẩm tour | Nhóm UC |
| UC02 | Thêm mới tour mẫu | Tour mẫu | DONE |
| UC03 | Sao chép tour mẫu | Tour mẫu | DONE |
| UC04 | Sửa thông tin tour mẫu | Tour mẫu | DONE |
| UC05 | Xóa tour mẫu | Tour mẫu | DONE |
| UC06 | Tra cứu tour mẫu | Tour mẫu | DONE |
| UC07 | Quản lý lịch trình tour | Lịch trình tour | Nhóm UC |
| UC08 | Thêm lịch trình tour | Lịch trình tour | DONE |
| UC09 | Sửa lịch trình tour | Lịch trình tour | DONE |
| UC10 | Quản lý tour thực tế | Tour thực tế | Nhóm UC |
| UC11 | Khởi tạo tour thực tế từ tour mẫu | Tour thực tế | DONE |
| UC12 | Xóa tour thực tế | Tour thực tế | DONE |
| UC13 | Sửa tour thực tế | Tour thực tế | DONE |
| UC14 | Tra cứu tour thực tế | Tour thực tế | DONE |
| UC15 | Quản lý dịch vụ bổ sung | Dịch vụ bổ sung | Nhóm UC |
| UC16 | Thêm dịch vụ | Dịch vụ bổ sung | DONE |
| UC17 | Sửa thông tin dịch vụ | Dịch vụ bổ sung | DONE |
| UC18 | Xóa dịch vụ | Dịch vụ bổ sung | DONE |
| UC19 | Tra cứu dịch vụ | Dịch vụ bổ sung | DONE |
| UC20 | Cấu hình hành động xanh cho tour | Hành động xanh | DONE |
| UC21 | Xem thông tin hồ sơ số | Hộ chiếu số | DONE |
| UC22 | Xem chi tiết lịch sử hành trình | Hộ chiếu số | DONE |
| UC23 | Cập nhật hồ sơ số | Hộ chiếu số | DONE |
| UC24 | Tra cứu khách hàng | Hộ chiếu số | DONE |
| UC25 | Tra cứu tour | Đặt tour | DONE |
| UC26 | Xem chi tiết tour | Đặt tour | DONE |
| UC27 | Đặt tour | Đặt tour | DONE |
| UC28 | Áp dụng voucher | Đặt tour | DONE |
| UC29 | Thanh toán | Thanh toán | PARTIAL: mock mode |
| UC30 | Quy đổi voucher | Voucher khách hàng | DONE |
| UC31 | Xem danh sách voucher | Voucher khách hàng | DONE |
| UC32 | Hủy tour | Đặt tour | DONE |
| UC33 | Hoàn tiền | Thanh toán | DONE |
| UC34 | Tra cứu đơn hàng | Kinh doanh | DONE |
| UC35 | Đánh giá | Khách hàng | DONE |
| UC36 | Khiếu nại | Khách hàng | DONE |
| UC37 | Điều phối HDV | Điều phối | DONE |
| UC38 | Tra cứu HDV | Điều phối | DONE |
| UC39 | Giải quyết khiếu nại | Điều phối | DONE |
| UC40 | Xem lịch trình và thông tin đoàn | Vận hành tour | DONE |
| UC41 | Điểm danh khách hàng | Vận hành tour | DONE |
| UC42 | Xác nhận hành động xanh | Vận hành tour | DONE |
| UC43 | Báo cáo sự cố | Vận hành tour | DONE |
| UC44 | Cập nhật chi phí thực tế | Vận hành tour | DONE |
| UC45 | Tính lợi nhuận gộp | Tài chính | DONE |
| UC46 | Xem cảnh báo chi phí | Tài chính | DONE |
| UC47 | Phê duyệt chi phí thực tế | Tài chính | DONE |
| UC48 | Quyết toán tour | Quyết toán | DONE |
| UC49 | Tra cứu tour cần quyết toán | Quyết toán | DONE |
| UC50 | Xử lý hoàn tiền | Tài chính | DONE |
| UC51 | Trích xuất báo cáo Power BI | Báo cáo | PARTIAL: API báo cáo doanh thu |
| UC52 | Quản lý voucher | Voucher, khuyến mãi | DONE |
| UC53 | Tạo voucher | Voucher, khuyến mãi | DONE |
| UC54 | Phân phối voucher vào ví khách hàng | Voucher, khuyến mãi | DONE |
| UC55 | Quản lý truy cập tài khoản | Quản trị hệ thống | Nhóm UC |
| UC56 | Đăng ký | Auth | DONE |
| UC57 | Đăng nhập | Auth | DONE |
| UC58 | Đăng xuất | Auth | DONE |
| UC59 | Quên mật khẩu | Auth | DONE |
| UC60 | Đổi mật khẩu | Auth | DONE |
| UC61 | Quản lý tài khoản người dùng | Quản trị hệ thống | Nhóm UC |
| UC62 | Tạo tài khoản nhân viên | Quản trị tài khoản | DONE |
| UC63 | Cập nhật năng lực nhân viên | Quản trị tài khoản | DONE |
| UC64 | Xóa/khóa tài khoản | Quản trị tài khoản | DONE |
| UC65 | Mở khóa tài khoản | Quản trị tài khoản | DONE |
| UC66 | Tìm kiếm tài khoản | Quản trị tài khoản | DONE |
| UC67 | Phân quyền tài khoản RBAC | Quản trị hệ thống | DONE |
| UC68 | Giám sát nhật ký hệ thống | Quản trị hệ thống | DONE |
| -- | Scheduler tự hủy đơn hết hạn | Đặt tour | DONE, chạy mỗi 60 giây |
| -- | Scheduler cập nhật trạng thái tour và giá động | Tour thực tế | DONE, chạy mỗi giờ |

### Chưa hoàn thiện đầy đủ

| UC | Tên | Ghi chú |
|----|-----|---------|
| UC29 | Thanh toán cổng thật | Hiện chỉ có mock mode |
| UC51 | Trích xuất Power BI | Hiện có API báo cáo doanh thu, chưa có file/export Power BI |
| -- | Seed data đầy đủ cho demo | Chưa làm |

---

## Biến môi trường

| Biến | Mô tả | Bắt buộc |
|------|-------|----------|
| `DB_HOST` | Host Oracle | Có |
| `DB_PORT` | Port Oracle | Có |
| `DB_SERVICE` | Service name Oracle | Có |
| `DB_USERNAME` | Username DB | Có |
| `DB_PASSWORD` | Password DB | Có |
| `JWT_SECRET` | Khóa ký JWT (Base64, tối thiểu 42 ký tự) | Có |
| `JWT_EXPIRATION` | TTL access token (ms), mặc định 86400000 (24h) | Không |
| `PAYMENT_MOCK_ENABLED` | Bật mock thanh toán demo, mặc định `true`; đặt `false` để chặn bypass | Không |

Tạo file `.env` ở root project (đã có trong `.gitignore`):

```dotenv
DB_HOST=localhost
DB_PORT=1521
DB_SERVICE=XEPDB1
DB_USERNAME=digital_travel
DB_PASSWORD=your_password
JWT_SECRET=your_base64_secret_key_here_at_least_42_chars
```

---

## Ghi chú kỹ thuật

### Trạng thái hợp lệ

| Entity | Giá trị hợp lệ |
|--------|---------------|
| `TaiKhoan.TrangThai` | HOAT_DONG · KHOA |
| `TourThucTe.TrangThai` | CHO_KICH_HOAT · MO_BAN · DANG_DIEN_RA · KET_THUC · HUY · DA_QUYET_TOAN |
| `DonDatTour.TrangThai` | CHO_XAC_NHAN · DA_XAC_NHAN · CHO_HUY · DA_HUY · TU_CHOI_HOAN_TIEN · HET_HAN_GIU_CHO · THANH_TOAN_THAT_BAI |
| `GiaoDich.LoaiGiaoDich` | THANH_TOAN · HOAN_TIEN |
| `GiaoDich.TrangThai` | CHO_THANH_TOAN · THANH_CONG · THAT_BAI · DA_HOAN_TIEN |
| `YeuCauHoTro/KhieuNai.TrangThai` | CHUA_XU_LY · CHO_BO_SUNG · CHO_GIAI_TRINH · CHO_DUYET · DA_XU_LY · TU_CHOI |
| `DiemDanh.TrangThai` | DA_DIEM_DANH · CHUA_DIEM_DANH · VANG |
| `ChiPhiThucTe.TrangThaiDuyet` | CHO_DUYET · DA_DUYET · TU_CHOI |
| `QuyetToan.TrangThai` | CHUA_QUYET_TOAN · DA_QUYET_TOAN |
| `NhanVien.TrangThaiLamViec` | HOAT_DONG · BAN · NGHI |
| `HoChieuSo.HangThanhVien` | THANH_VIEN · DONG · BAC · VANG · KIM_CUONG |
| `Voucher.TrangThai` | SAN_SANG · VO_HIEU_HOA |
| `KhuyenMaiKh.TrangThai` | CO_HIEU_LUC · DA_SU_DUNG · DA_THU_HOI · HET_HAN |
| `NhatKySuCo.MucDo` | THAP · TRUNG_BINH · CAO |

`KHUYENMAI_KH` dùng khóa chính `(MaKhachHang, MaVoucher)`, nên mỗi khách chỉ có một bản ghi cho một voucher; các trạng thái `DA_SU_DUNG`, `DA_THU_HOI`, `HET_HAN` được giữ lại làm lịch sử thay vì xóa khỏi ví.

`NhatKySuCo.MucDo` dùng để ưu tiên xử lý; chi tiết như tai nạn, y tế, mất giấy tờ hoặc phát sinh lịch trình được ghi trong `MoTa`.

### Luồng đặt tour (business rules)

1. `datTour()` — Tạo `DonDatTour` (CHO_XAC_NHAN), `ThoiGianHetHan = NOW() + 2 ngày`, KHÔNG trừ `ChoConLai`
2. Scheduler chạy mỗi 60 giây — chuyển đơn hết hạn sang HET_HAN_GIU_CHO
3. Khách xác nhận đã chuyển khoản — đánh dấu trên giao dịch QR để nhân viên đối soát, không tạo trạng thái mới; QR quá 5 phút chưa xác nhận — giao dịch → THAT_BAI, đơn → HET_HAN_GIU_CHO
4. Nhân viên từ chối thanh toán của đơn CHO_XAC_NHAN — giao dịch → THAT_BAI, đơn → THANH_TOAN_THAT_BAI
5. `khoiTaoThanhToan(mock=true)` — Tạo GiaoDich (THANH_CONG), đơn → DA_XAC_NHAN, trừ `ChoConLai`, tạo `LichSuTour`
6. `yeuCauHuyTour()` — cho đơn CHO_XAC_NHAN hoặc DA_XAC_NHAN còn hơn 2 ngày trước khởi hành; tính tỉ lệ hoàn theo ngày trước khởi hành, đơn → CHO_HUY
7. `duyetHuyTour()` — kinh doanh duyệt yêu cầu, tạo GiaoDich (HOAN_TIEN, CHO_THANH_TOAN), đơn vẫn ở CHO_HUY chờ kế toán
8. `xacNhanHoanTien()` — kế toán xác nhận hoàn tiền, hoàn `ChoConLai`, giao dịch → DA_HOAN_TIEN, đơn → DA_HUY
9. `tuChoiHoanTien()` — kế toán từ chối hoàn tiền, giao dịch → THAT_BAI, đơn → TU_CHOI_HOAN_TIEN và vẫn còn hiệu lực

### Dynamic pricing

Scheduler `TourThucTeScheduler` chạy đầu mỗi giờ (`0 0 * * * *`) và cập nhật `GiaHienHanh` cho tour đủ điều kiện:

| Điều kiện | Điều chỉnh |
|-----------|------------|
| Còn tối đa 7 ngày, tỷ lệ lấp >= 80% | Tăng 10%, tối đa gấp 2 `GiaSan` |
| Còn ít nhất 30 ngày, tỷ lệ lấp < 30% | Giảm 5%, tối thiểu bằng `GiaSan` |

### Tỉ lệ hoàn tiền khi hủy

| Thời điểm trước khởi hành | Hoàn |
|---------------------------|------|
| Trên 15 ngày | 90% |
| 7 đến 15 ngày | 70% |
| 3 đến 7 ngày | 50% |
| Từ 0 đến 2 ngày | Không được gửi yêu cầu hoàn tiền |

### Naming conventions

- Lombok `@FieldDefaults(level = PRIVATE)` — field uppercase (e.g. `MaDatTour`) → getter `getMaDatTour()`
- `PhysicalNamingStrategyStandardImpl` — tên field Java == tên column Oracle (case-sensitive)
- ID prefix convention: `DDT_`, `TTT_`, `QT_`, `PC_`, `DD_`, `HD_`, `SC_`, `CP_`, `NV_`

### Tech Stack

| Layer | Công nghệ |
|-------|-----------|
| Language | Java 21 |
| Framework | Spring Boot 4.0.5 |
| Security | Spring Security + JWT (jjwt) |
| ORM | Spring Data JPA / Hibernate |
| Database | Oracle 19c/21c (ojdbc11) |
| Validation | Jakarta Bean Validation |
| Connection Pool | HikariCP (max 10) |
| Utilities | Lombok |
| API Docs | Springdoc OpenAPI (Swagger UI) |
| Build | Maven Wrapper 3.9.14 |
| Scheduler | Spring @EnableScheduling |

---

## Kiểm thử

```bash
# Chạy toàn bộ test; cần Oracle schema reachable vì @SpringBootTest load context thật
.\mvnw.cmd test

# Chạy unit test không cần DB
.\mvnw.cmd -Dtest=NhanVienServiceTest test
```
