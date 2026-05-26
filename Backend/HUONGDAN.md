# Hướng Dẫn Backend Digital Travel ERP

Base URL khi chạy local: `http://localhost:8080`

Swagger UI: `http://localhost:8080/swagger-ui/index.html`

OpenAPI JSON: `http://localhost:8080/v3/api-docs`

Tài liệu trực quan: mở file `HUONGDAN.html` trong thư mục gốc dự án.

---

## 1. Tổng Quan Hệ Thống

Digital Travel ERP là backend Spring Boot quản lý nghiệp vụ du lịch theo các phân hệ chính:

| Phân hệ | Vai trò chính | Nhóm bảng liên quan | Prefix API |
|---|---|---|---|
| Định danh và phân quyền | `ADMIN`, tất cả người dùng | `TAIKHOAN`, `VAITRO`, `NHANVIEN`, `HOCHIEUSO`, `NHATKYHETHONG` | `/api/auth`, `/api/quan-tri` |
| Sản phẩm tour | `SANPHAM` | `TOURMAU`, `LICHTRINHTOUR`, `DICHVUTHEM` (gồm phụ thu phòng), `HANHDONGXANH` | `/api/san-pham` |
| Điều hành tour | `DIEUHANH` | `TOURTHUCTE`, `PHANCONGTOUR`, `NANGLUCNHANVIEN` | `/api/dieu-hanh` |
| Bán hàng và CSKH | `KINHDOANH` | `DONDATTOUR`, `YEUCAUHOTRO`, `VOUCHER`, `KHUYENMAI_KH` | `/api/kinh-doanh` |
| Khách hàng | `KHACHHANG` | `HOCHIEUSO`, `DONDATTOUR`, `LICHSUTOUR`, `DANHGIAKH`, `DATTOUR_UUDAI` | `/api/khach-hang`, `/api/thanh-toan` |
| Hướng dẫn viên | `HDV` | `DIEMDANH`, `HANHDONG`, `NHATKYSUCO`, `CHIPHITHUCTE` | `/api/huong-dan-vien` |
| Kế toán | `KETOAN` | `GIAODICH`, `CHIPHITHUCTE`, `QUYETTOAN` | `/api/ke-toan` |
| Công khai | Không cần đăng nhập | `TOURTHUCTE`, `TOURMAU`, `LICHTRINHTOUR`, `DANHGIAKH` | `/api/public` |

Kiến trúc source chính:

```text
src/main/java/com/digitaltravel/erp
├─ config        # Security, JWT, role constants, audit config
├─ controller    # REST API theo phân hệ
├─ dto           # Request/response DTO
├─ entity        # Mapping Oracle table/column
├─ exception     # Global exception handler
├─ repository    # Spring Data JPA repositories
├─ service       # Business logic và scheduler
└─ DigitalTravelErpApplication.java
```

---

## 2. Yêu Cầu Môi Trường

| Thành phần | Phiên bản / ghi chú |
|---|---|
| JDK | Java 21 |
| Maven | Dùng Maven Wrapper `./mvnw.cmd`, tự tải Maven 3.9.14 |
| Database | Oracle, dùng JDBC `ojdbc11` |
| Framework | Spring Boot 4.0.5, Spring Security, Spring Data JPA, Springdoc OpenAPI |

Các lệnh nên chạy từ thư mục gốc repository.

---

## 3. Cấu Hình Kết Nối Database

`src/main/resources/application.yaml` import file `.env` ở thư mục gốc:

```yaml
spring:
  config:
    import: optional:file:.env[.properties]
```

Tạo file `.env` cục bộ, không commit file này:

```properties
DB_HOST=localhost
DB_PORT=1521
DB_SERVICE=XEPDB1
DB_USERNAME=YOUR_SCHEMA
DB_PASSWORD=YOUR_PASSWORD

# Có thể dùng DB_URL thay cho DB_HOST/DB_PORT/DB_SERVICE
# DB_URL=jdbc:oracle:thin:@//localhost:1521/XEPDB1

# Nên đổi khi chạy môi trường thật
JWT_SECRET=ZGlnaXRhbC10cmF2ZWwtZXJwLXNlY3JldC1rZXktbXVzdC1iZS00Mi1jaGFycy1sb25nLW1pbg==
JWT_EXPIRATION=86400000
```

Lưu ý quan trọng:

| Cấu hình | Ý nghĩa |
|---|---|
| `spring.jpa.hibernate.ddl-auto=validate` | App chỉ kiểm tra schema, không tự tạo bảng |
| `spring.sql.init.mode=never` | App không tự chạy seed data |
| `PhysicalNamingStrategyStandardImpl` | Tên cột/table giữ nguyên theo annotation, không tự convert snake_case |

---

## 4. Khởi Tạo Database

Chạy script SQL thủ công bằng SQL Developer, SQL*Plus hoặc tool Oracle tương đương theo thứ tự:

```sql
@src/main/resources/db/KhoiTaoBang.sql
@src/main/resources/db/data_v1.sql
@src/main/resources/db/data_lien_ket.sql
```

Ý nghĩa từng file:

| File | Nội dung |
|---|---|
| `KhoiTaoBang.sql` | Drop và tạo lại toàn bộ bảng, khóa ngoại, check constraint, index |
| `data_v1.sql` | Dữ liệu tài khoản nhân viên: vai trò, tài khoản nhân viên, hồ sơ nhân viên |
| `CapNhatHanhDongXanhMotLan.sql` | Thêm ràng buộc chống ghi nhận trùng hành động xanh và bỏ trigger cộng điểm cũ |

Cấu trúc database theo luồng nghiệp vụ:

```text
TAIKHOAN ── VAITRO
   ├─ NHANVIEN ── NANGLUCNHANVIEN
   └─ HOCHIEUSO ── DONDATTOUR ── GIAODICH
                     ├─ CHITIETDATTOUR
                     ├─ CHITIETDICHVU
                     └─ DATTOUR_UUDAI ── VOUCHER ── KHUYENMAI_KH

TOURMAU ── LICHTRINHTOUR
   └─ TOURTHUCTE ── PHANCONGTOUR ── DIEMDANH / HANHDONG / NHATKYSUCO / CHIPHITHUCTE
          ├─ LICHSUTOUR ── DANHGIAKH
          └─ QUYETTOAN
```

---

## 5. Build, Run Và Test

Chạy backend:

```powershell
./mvnw.cmd spring-boot:run
```

Chạy test:

```powershell
./mvnw.cmd test
```

Chạy một test class:

```powershell
./mvnw.cmd -Dtest=NhanVienServiceTest test
```

Build jar không chạy test:

```powershell
./mvnw.cmd clean package -DskipTests
java -jar target/digital-travel-erp-*.jar
```

Ghi chú: test `@SpringBootTest` cần Oracle schema thật vì context sẽ validate database.

---

## 6. Tài Khoản Seed

Mật khẩu mặc định cho các tài khoản trong `data_v1.sql`: `password`

| Vai trò | Username | Mã nghiệp vụ | Ghi chú test nhanh |
|---|---|---|---|
| `ADMIN` | `admin` | `NV_ADMIN01` | Quản trị nhân viên, vai trò, nhật ký bảo mật |
| `SANPHAM` | `sanpham01` | `NV_SP01` | Tour mẫu, dịch vụ thêm/phụ thu phòng, hành động xanh |
| `DIEUHANH` | `manager01` | `NV_MGR01` | Tour thực tế, phân công HDV, năng lực HDV |
| `KINHDOANH` | `sales01` | `NV_SALES01` | Đơn đặt tour, khách hàng, voucher, yêu cầu hỗ trợ |
| `KETOAN` | `ketoan01` | `NV_KT01` | Chi phí, hoàn tiền, quyết toán, doanh thu |
| `HDV` | `hdv01` | `NV_HDV01` | Tài khoản hướng dẫn viên |
| `HDV` | `hdv02` | `NV_HDV02` | Tài khoản hướng dẫn viên |

---

## 7. Xác Thực Và Phân Quyền

Đăng nhập:

```http
POST /api/auth/dang-nhap
Content-Type: application/json
```

```json
{
  "tenDangNhap": "admin",
  "matKhau": "password"
}
```

Gắn token vào các request cần đăng nhập:

```http
Authorization: Bearer <accessToken>
```

Endpoint public:

| Method | Endpoint | Mục đích |
|---|---|---|
| `POST` | `/api/auth/dang-ky` | Đăng ký khách hàng |
| `POST` | `/api/auth/dang-nhap` | Đăng nhập lấy JWT |
| `POST` | `/api/auth/quen-mat-khau` | Tạo reset token dev/test |
| `POST` | `/api/auth/dat-lai-mat-khau` | Đặt lại mật khẩu |
| `GET` | `/api/public/tour` | Danh sách tour đang mở bán |
| `GET` | `/api/public/tour/{maTourThucTe}` | Chi tiết tour công khai |
| `GET` | `/api/public/tour/{maTourThucTe}/danh-gia` | Đánh giá tour |
| `GET` | `/swagger-ui/**`, `/v3/api-docs/**` | Swagger/OpenAPI |

Các prefix có bảo vệ bởi `SecurityConfig`:

| Prefix | Quyền |
|---|---|
| `/api/quan-tri/**` | `ADMIN` |
| `/api/san-pham/**` | `SANPHAM` |
| `/api/kinh-doanh/**` | `KINHDOANH` |
| `/api/dieu-hanh/**` | `DIEUHANH` |
| `/api/huong-dan-vien/**` | `DIEUHANH` hoặc `HDV` theo cấu hình URL, một số method vẫn giới hạn bằng `@PreAuthorize` |
| `/api/ke-toan/**` | `KETOAN` |
| `/api/khach-hang/**` | `KHACHHANG` |
| `/api/thanh-toan/**` | `KHACHHANG` |

---

## 8. API Theo Phân Hệ

### 8.1 Công Khai

| Method | Endpoint | Query/body chính |
|---|---|---|
| `GET` | `/api/public/tour` | `giaTu`, `giaDen`, `thoiLuongMin`, `thoiLuongMax`, `page`, `size` |
| `GET` | `/api/public/tour/{maTourThucTe}` | Ví dụ `TTT001` |
| `GET` | `/api/public/tour/{maTourThucTe}/danh-gia` | `page`, `size` |

### 8.2 Khách Hàng

| Method | Endpoint | Mục đích |
|---|---|---|
| `GET` | `/api/khach-hang/ho-so` | Xem hồ sơ khách hàng hiện tại |
| `PUT` | `/api/khach-hang/ho-so` | Cập nhật dị ứng, ghi chú y tế |
| `POST` | `/api/khach-hang/dat-tour` | Tạo đơn đặt tour |
| `GET` | `/api/khach-hang/dat-tour` | Danh sách đơn của tôi |
| `GET` | `/api/khach-hang/dat-tour/{maDatTour}` | Chi tiết đơn của tôi |
| `DELETE` | `/api/khach-hang/dat-tour/{maDatTour}` | Hủy đơn `CHO_XAC_NHAN` |
| `POST` | `/api/khach-hang/dat-tour/{maDatTour}/huy` | Yêu cầu hủy tour đã xác nhận |
| `GET` | `/api/khach-hang/lich-su-tour` | Lịch sử tour đã tham gia |
| `POST` | `/api/khach-hang/yeu-cau-ho-tro` | Gửi ticket hỗ trợ/khiếu nại |
| `GET` | `/api/khach-hang/yeu-cau-ho-tro` | Ticket của tôi |
| `GET` | `/api/khach-hang/vi-voucher` | Ví voucher, gồm trạng thái `CO_HIEU_LUC`, `DA_SU_DUNG`, `DA_THU_HOI`, `HET_HAN` |
| `POST` | `/api/khach-hang/ap-voucher` | Áp voucher `SAN_SANG` và còn `CO_HIEU_LUC` cho đơn |
| `POST` | `/api/khach-hang/doi-diem` | Đổi điểm xanh lấy voucher `SAN_SANG` |
| `POST` | `/api/khach-hang/danh-gia` | Đánh giá tour đã có lịch sử |
| `POST` | `/api/thanh-toan/khoi-tao` | Tạo giao dịch thanh toán |
| `GET` | `/api/thanh-toan/{maDatTour}/ket-qua` | Kiểm tra kết quả thanh toán |

Ví dụ đặt tour:

```json
{
  "maTourThucTe": "TTT001",
  "danhSachDichVu": [
    { "maDichVuThem": "DV012", "soLuong": 1 },
    { "maDichVuThem": "DV002", "soLuong": 2 }
  ],
  "ghiChu": "Ưu tiên phòng tầng thấp"
}
```

Ví dụ thanh toán mock trong dev/test:

```json
{
  "maDonDatTour": "DDT004",
  "phuongThuc": "MOCK",
  "mock": true
}
```

### 8.3 Sản Phẩm

| Method | Endpoint | Mục đích |
|---|---|---|
| `GET` | `/api/san-pham/tour-mau` | Danh sách tour mẫu |
| `GET` | `/api/san-pham/tour-mau/{id}` | Chi tiết tour mẫu |
| `POST` | `/api/san-pham/tour-mau` | Tạo tour mẫu |
| `PUT` | `/api/san-pham/tour-mau/{id}` | Cập nhật tour mẫu |
| `DELETE` | `/api/san-pham/tour-mau/{id}` | Khóa mềm tour mẫu |
| `POST` | `/api/san-pham/tour-mau/{id}/sao-chep` | Sao chép tour mẫu |
| `POST` | `/api/san-pham/tour-mau/{id}/lich-trinh` | Thêm ngày lịch trình |
| `PUT` | `/api/san-pham/tour-mau/{id}/lich-trinh/{maLichTrinh}` | Sửa ngày lịch trình |
| `DELETE` | `/api/san-pham/tour-mau/{id}/lich-trinh/{maLichTrinh}` | Xóa ngày lịch trình |
| `GET/POST/PUT/DELETE` | `/api/san-pham/dich-vu-them` | Quản lý dịch vụ thêm và phụ thu phòng |
| `GET/POST/PUT/DELETE` | `/api/san-pham/hanh-dong-xanh` | Quản lý hành động xanh |

Ví dụ tạo tour mẫu:

```json
{
  "tieuDe": "Ninh Bình xanh 2N1Đ",
  "moTa": "Tràng An, Hang Múa, Tam Cốc",
  "thoiLuong": 2,
  "giaSan": 1800000,
  "lichTrinh": [
    { "ngayThu": 1, "hoatDong": "Hà Nội - Tràng An", "moTa": "Đi thuyền", "thucDon": "Cơm dê" },
    { "ngayThu": 2, "hoatDong": "Hang Múa - Hà Nội", "moTa": "Leo núi nhẹ", "thucDon": "Buffet sáng" }
  ]
}
```

### 8.4 Điều Hành

| Method | Endpoint | Mục đích |
|---|---|---|
| `GET` | `/api/dieu-hanh/tour-thuc-te` | Danh sách tour thực tế |
| `GET` | `/api/dieu-hanh/tour-thuc-te/{id}` | Chi tiết tour thực tế |
| `POST` | `/api/dieu-hanh/tour-thuc-te` | Tạo đợt khởi hành từ tour mẫu |
| `PUT` | `/api/dieu-hanh/tour-thuc-te/{id}` | Cập nhật giá, sức chứa, trạng thái |
| `DELETE` | `/api/dieu-hanh/tour-thuc-te/{id}` | Hủy tour thực tế |
| `GET` | `/api/dieu-hanh/hdv-kha-dung?maTourThucTe=TTT001` | Tìm HDV khả dụng |
| `POST` | `/api/dieu-hanh/phan-cong` | Phân công HDV |
| `DELETE` | `/api/dieu-hanh/phan-cong/{maPhanCong}` | Hủy phân công |
| `GET` | `/api/dieu-hanh/lich-cong-tac` | Xem lịch công tác của tài khoản điều hành hiện tại |
| `GET` | `/api/dieu-hanh/nhan-vien/{maNhanVien}/nang-luc` | Xem năng lực HDV |
| `PUT` | `/api/dieu-hanh/nhan-vien/{maNhanVien}/nang-luc` | Cập nhật năng lực HDV |
| `GET` | `/api/dieu-hanh/tour/{maTour}/doan` | Xem danh sách đoàn |
| `GET` | `/api/dieu-hanh/tour/{maTour}/su-co?mucDo=CAO` | Xem sự cố tour, có thể lọc mức độ |
| `GET` | `/api/dieu-hanh/tour/{maTour}/chi-phi` | Xem chi phí tour |

Ví dụ tạo tour thực tế:

```json
{
  "maTourMau": "TM001",
  "ngayKhoiHanh": "2026-08-20",
  "soKhachToiDa": 25,
  "soKhachToiThieu": 10,
  "giaHienHanh": 3600000
}
```

### 8.5 Kinh Doanh

| Method | Endpoint | Mục đích |
|---|---|---|
| `GET` | `/api/kinh-doanh/dat-tour` | Danh sách booking theo controller kinh doanh |
| `PUT` | `/api/kinh-doanh/dat-tour/{maDatTour}/xac-nhan` | Xác nhận booking |
| `GET` | `/api/kinh-doanh/don-dat-tour` | Danh sách booking theo controller nhân viên |
| `GET` | `/api/kinh-doanh/khach-hang` | Tìm kiếm khách hàng |
| `GET` | `/api/kinh-doanh/khach-hang/{maKhachHang}` | Chi tiết khách hàng |
| `GET` | `/api/kinh-doanh/yeu-cau-ho-tro` | Danh sách ticket hỗ trợ |
| `PUT` | `/api/kinh-doanh/yeu-cau-ho-tro/{maYeuCau}` | Xử lý ticket |
| `GET` | `/api/kinh-doanh/yeu-cau-huy` | Danh sách yêu cầu hủy tour |
| `POST` | `/api/kinh-doanh/yeu-cau-huy/{maYeuCau}/duyet` | Duyệt yêu cầu hủy |
| `POST` | `/api/kinh-doanh/yeu-cau-huy/{maYeuCau}/tu-choi` | Từ chối yêu cầu hủy |
| `GET` | `/api/kinh-doanh/voucher` | Danh sách voucher |
| `POST` | `/api/kinh-doanh/voucher` | Tạo voucher |
| `PUT` | `/api/kinh-doanh/voucher/{maVoucher}` | Cập nhật voucher |
| `PUT` | `/api/kinh-doanh/voucher/{maVoucher}/vo-hieu-hoa` | Vô hiệu hóa voucher đang chạy |
| `POST` | `/api/kinh-doanh/voucher/{maVoucher}/phat-hanh` | Phát hành voucher cho khách |
| `PUT` | `/api/kinh-doanh/voucher/{maVoucher}/khach-hang/{maKhachHang}/thu-hoi` | Thu hồi voucher còn hiệu lực trong ví khách |
| `GET` | `/api/kinh-doanh/danh-gia` | Xem đánh giá để moderation |

Lưu ý: `KHUYENMAI_KH` dùng khóa chính `(MaKhachHang, MaVoucher)`, nên mỗi khách chỉ có một bản ghi cho một voucher. Voucher đã dùng, đã thu hồi hoặc hết hạn được giữ làm lịch sử và không phát hành lại cùng mã đó cho cùng khách.

Ví dụ tạo voucher:

```json
{
  "maCode": "TET2027",
  "loaiUuDai": "SO_TIEN",
  "giaTriGiam": 500000,
  "dieuKienApDung": "Ưu đãi Tết",
  "soLuotPhatHanh": 50,
  "ngayHieuLuc": "2027-01-01",
  "ngayHetHan": "2027-03-31"
}
```

### 8.6 Hướng Dẫn Viên

| Method | Endpoint | Mục đích |
|---|---|---|
| `GET` | `/api/huong-dan-vien/tour-cua-toi` | Tour được phân công |
| `GET` | `/api/huong-dan-vien/lich-cong-tac` | Lịch công tác |
| `GET` | `/api/huong-dan-vien/nang-luc` | Xem năng lực bản thân |
| `GET` | `/api/huong-dan-vien/tour/{maTour}/doan` | Danh sách đoàn |
| `POST` | `/api/huong-dan-vien/tour/{maTour}/diem-danh` | Điểm danh khách |
| `POST` | `/api/huong-dan-vien/tour/{maTour}/hanh-dong-xanh` | Ghi nhận hành động xanh |
| `GET` | `/api/huong-dan-vien/tour/{maTour}/su-co?mucDo=CAO` | Danh sách sự cố, có thể lọc mức độ |
| `POST` | `/api/huong-dan-vien/tour/{maTour}/su-co` | Báo cáo sự cố, `mucDo`: `THAP`, `TRUNG_BINH`, `CAO` |
| `PUT` | `/api/huong-dan-vien/su-co/{maSuCo}` | Cập nhật sự cố, gồm mô tả/giải pháp/mức độ |

`mucDo` mặc định là `TRUNG_BINH`. Các tình huống cụ thể như tai nạn, y tế, mất giấy tờ hoặc thay đổi lịch trình nên ghi chi tiết trong `moTa`.
| `POST` | `/api/huong-dan-vien/tour/{maTour}/chi-phi` | Khai chi phí phát sinh |
| `GET` | `/api/huong-dan-vien/tour/{maTour}/chi-phi` | Chi phí của tour |

Ví dụ điểm danh:

```json
{
  "maKhachHang": "KH001",
  "diaDiem": "Bến xe Mỹ Đình"
}
```

Ví dụ ghi nhận hành động xanh:

```json
{
  "maKhachHang": "KH001",
  "maHanhDongXanh": "HDX001",
  "minhChung": "Ảnh khách dùng bình nước cá nhân"
}
```

### 8.7 Kế Toán

| Method | Endpoint | Mục đích |
|---|---|---|
| `GET` | `/api/ke-toan/chi-phi` | Danh sách chi phí chờ duyệt/đã xử lý |
| `PUT` | `/api/ke-toan/chi-phi/{maChiPhi}/duyet` | Duyệt chi phí |
| `PUT` | `/api/ke-toan/chi-phi/{maChiPhi}/tu-choi` | Từ chối chi phí |
| `GET` | `/api/ke-toan/tour-can-quyet-toan` | Tour kết thúc chưa quyết toán |
| `GET` | `/api/ke-toan/tinh-toan/{maTour}` | Tính thử quyết toán |
| `POST` | `/api/ke-toan/quyet-toan/{maTour}` | Tạo/cập nhật bản nháp quyết toán |
| `PUT` | `/api/ke-toan/quyet-toan/{maQuyetToan}/chot` | Chốt quyết toán |
| `GET` | `/api/ke-toan/quyet-toan` | Danh sách quyết toán |
| `GET` | `/api/ke-toan/quyet-toan/{maQuyetToan}` | Chi tiết quyết toán |
| `GET` | `/api/ke-toan/giao-dich-hoan` | Giao dịch hoàn tiền chờ xử lý |
| `PUT` | `/api/ke-toan/giao-dich-hoan/{maGiaoDich}/xac-nhan` | Xác nhận hoàn tiền |
| `GET` | `/api/ke-toan/bao-cao/doanh-thu` | Báo cáo doanh thu theo kỳ |

Ví dụ báo cáo doanh thu:

```http
GET /api/ke-toan/bao-cao/doanh-thu?tuNgay=2026-01-01&denNgay=2026-12-31
```

### 8.8 Quản Trị

| Method | Endpoint | Mục đích |
|---|---|---|
| `POST` | `/api/quan-tri/dang-ky-nhan-vien` | Tạo tài khoản nhân viên |
| `GET` | `/api/quan-tri/nhan-vien` | Danh sách nhân viên |
| `GET` | `/api/quan-tri/nhan-vien/{maNhanVien}` | Chi tiết nhân viên |
| `PUT` | `/api/quan-tri/nhan-vien/{maNhanVien}/khoa` | Khóa tài khoản nhân viên |
| `PUT` | `/api/quan-tri/nhan-vien/{maNhanVien}/mo-khoa` | Mở khóa tài khoản nhân viên |
| `PUT` | `/api/quan-tri/nhan-vien/{maNhanVien}/vai-tro` | Gán vai trò nhân viên |
| `GET` | `/api/quan-tri/nhat-ky-he-thong` | Tra cứu nhật ký thêm/sửa/xóa hệ thống |

Vai trò nhân viên có thể gán: `SANPHAM`, `KINHDOANH`, `DIEUHANH`, `KETOAN`, `HDV`.

---

## 9. Luồng Test Gợi Ý

### Luồng khách đặt tour và thanh toán

1. `POST /api/auth/dang-nhap` với `khachhang04/password`.
2. `GET /api/public/tour` để chọn tour đang mở bán.
3. `POST /api/khach-hang/dat-tour` với `maTourThucTe=TTT001`.
4. `POST /api/thanh-toan/khoi-tao` với `mock=true`.
5. `GET /api/khach-hang/dat-tour` để kiểm tra trạng thái đơn.

### Luồng sales xử lý booking và ticket

1. Đăng nhập `sales01/password`.
2. `GET /api/kinh-doanh/don-dat-tour?trangThai=CHO_XAC_NHAN`.
3. `PUT /api/kinh-doanh/dat-tour/DDT004/xac-nhan`.
4. `GET /api/kinh-doanh/yeu-cau-ho-tro`.
5. `PUT /api/kinh-doanh/yeu-cau-ho-tro/YCHT004` với `trangThai=DA_XU_LY` hoặc `TU_CHOI`.

### Luồng điều hành phân công HDV

1. Đăng nhập `manager01/password`.
2. `GET /api/dieu-hanh/tour-thuc-te?trangThai=MO_BAN`.
3. `GET /api/dieu-hanh/hdv-kha-dung?maTourThucTe=TTT001`.
4. `POST /api/dieu-hanh/phan-cong` với `maTourThucTe` và `maNhanVien`.

### Luồng HDV vận hành tour

1. Đăng nhập `hdv01/password`.
2. `GET /api/huong-dan-vien/tour-cua-toi`.
3. `GET /api/huong-dan-vien/tour/TTT099/doan`.
4. `POST /api/huong-dan-vien/tour/TTT099/diem-danh`.
5. `POST /api/huong-dan-vien/tour/TTT099/chi-phi`.

### Luồng kế toán

1. Đăng nhập `ketoan01/password`.
2. `GET /api/ke-toan/chi-phi?trangThai=CHO_DUYET`.
3. `PUT /api/ke-toan/chi-phi/CPTT005/duyet`.
4. `GET /api/ke-toan/giao-dich-hoan`.
5. `PUT /api/ke-toan/giao-dich-hoan/GD_HOA01/xac-nhan`.
6. `GET /api/ke-toan/bao-cao/doanh-thu?tuNgay=2026-01-01&denNgay=2026-12-31`.

---

## 10. Background Jobs

Scheduling được bật tại `DigitalTravelErpApplication` bằng `@EnableScheduling`.

| Job | Tần suất | Chức năng |
|---|---|---|
| `DatTourScheduler` | Mỗi 60 giây | Đổi đơn `CHO_XAC_NHAN` quá `ThoiGianHetHan` sang `HET_HAN_GIU_CHO` |
| `TourThucTeScheduler` | Mỗi đầu giờ | Dynamic pricing: tăng/giảm `GiaHienHanh` theo ngày còn lại và tỷ lệ lấp chỗ |

---

## 11. Lỗi Thường Gặp

| Hiện tượng | Nguyên nhân hay gặp | Cách xử lý |
|---|---|---|
| App không start, lỗi datasource | Thiếu `.env` hoặc sai Oracle service | Kiểm tra `DB_HOST`, `DB_PORT`, `DB_SERVICE`, `DB_USERNAME`, `DB_PASSWORD` hoặc `DB_URL` |
| Lỗi validate schema | Chưa chạy `KhoiTaoBang.sql` hoặc schema cũ | Chạy lại DDL và seed theo đúng thứ tự |
| `401 Unauthorized` | Thiếu hoặc sai JWT | Đăng nhập lại và gắn `Authorization: Bearer <token>` |
| `403 Forbidden` | Token đúng nhưng sai vai trò | Dùng đúng account theo phân hệ |
| API cũ `/api/admin`, `/api/sales`, `/api/hdv` không chạy | Prefix hiện tại đã đổi | Dùng các prefix hyphenated trong tài liệu này |
| Đăng nhập seed không được | Dùng nhầm password cũ | Seed hiện tại dùng mật khẩu `password` |
| `@SpringBootTest` fail ở máy local | Không có Oracle schema thật | Chạy test unit focused hoặc cấu hình DB test |

---

## 12. Ghi Chú Khi Phát Triển

- Không commit `.env`, log, profile local hoặc `target/`.
- Entity map Oracle identifier rõ ràng bằng `@Table` và `@Column`; giữ nguyên casing hiện tại khi sửa mapping.
- Các API trả wrapper `ApiResponse<T>`.
- Khi thêm endpoint mới, cập nhật cả `SecurityConfig` hoặc `@PreAuthorize` nếu cần phân quyền khác mặc định.
- Khi thêm bảng/cột mới, cập nhật DDL trước vì Hibernate chỉ `validate`.
