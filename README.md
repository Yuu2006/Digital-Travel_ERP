<p align="center"><img width="454" height="126" alt="image" src="https://github.com/user-attachments/assets/2036c003-62d1-42f1-9817-6cca86de0fc8" /> </p>

# Digital Travel ERP

**Đề tài:** Hệ thống quản lí vận hành du lịch số  
**Kiến trúc:** Oracle Database · Spring Boot Backend · React/Vite Frontend  
**Repository:** Monorepo gồm `Backend` và 3 ứng dụng Frontend: `Admin`, `HDV`, `Khachhang`

**Nguồn source tích hợp:**

| Thành phần | Repository nguồn | Branch |
|---|---|---|
| Backend | [Yuu2006/Do-An-TravelERP](https://github.com/Yuu2006/Do-An-TravelERP) | `main` |
| Frontend Admin | [ThorBietBay001/Frontend-Digital-Travel-ERP](https://github.com/ThorBietBay001/Frontend-Digital-Travel-ERP) | `main` |
| Frontend HDV | [ThorBietBay001/Frontend-Digital-Travel-ERP](https://github.com/ThorBietBay001/Frontend-Digital-Travel-ERP) | `ui-ux/hdv` |
| Frontend Khách hàng | [ThorBietBay001/Frontend-Digital-Travel-ERP](https://github.com/ThorBietBay001/Frontend-Digital-Travel-ERP) | `ui-ux/KH` |

---

## Mục lục

1. [Giới thiệu đồ án](#giới-thiệu-đồ-án)
2. [Cây thư mục](#cây-thư-mục)
3. [Công nghệ và công cụ sử dụng](#công-nghệ-và-công-cụ-sử-dụng)
4. [Yêu cầu môi trường](#yêu-cầu-môi-trường)
5. [Hướng dẫn cài đặt và chạy dự án](#hướng-dẫn-cài-đặt-và-chạy-dự-án)
6. [Tài khoản seed](#tài-khoản-seed)
7. [Lỗi thường gặp](#lỗi-thường-gặp)
8. [Thành viên nhóm](#thành-viên-nhóm)

---

## Giới thiệu đồ án

Digital Travel ERP là hệ thống hỗ trợ quản lí và vận hành nghiệp vụ du lịch theo nhiều phân hệ:

| Phân hệ | Vai trò chính |
|---|---|
| Khách hàng | Tra cứu tour, đặt tour, thanh toán, quản lí hồ sơ số, voucher, khiếu nại |
| Admin / Quản trị | Quản lí tài khoản, phân quyền, nhân sự, nhật ký hệ thống |
| Sản phẩm | Quản lí tour mẫu, dịch vụ bổ sung, hành động xanh |
| Điều hành | Khởi tạo tour thực tế, phân công hướng dẫn viên, quản lí lịch công tác |
| Hướng dẫn viên | Xem tour được phân công, điểm danh, báo cáo sự cố, cập nhật chi phí |
| Kinh doanh | Quản lí đơn đặt tour, khách hàng, voucher, yêu cầu hỗ trợ |
| Kế toán | Duyệt chi phí, xử lí hoàn tiền, quyết toán tour, báo cáo doanh thu |

Backend cung cấp REST API bảo vệ bằng JWT/RBAC. Frontend gồm ba giao diện riêng cho nhóm người dùng khác nhau, cùng gọi API qua prefix `/api`.

---

## Cây thư mục

```text
Digital-Travel_ERP/
├─ Backend/
│  ├─ src/
│  │  ├─ main/
│  │  │  ├─ java/com/digitaltravel/erp/
│  │  │  │  ├─ config/          # Security, JWT, CORS, role constants
│  │  │  │  ├─ controller/      # REST API theo phân hệ
│  │  │  │  ├─ dto/             # Request/response DTO
│  │  │  │  ├─ entity/          # JPA entity mapping Oracle
│  │  │  │  ├─ exception/       # Global exception handler
│  │  │  │  ├─ repository/      # Spring Data JPA repositories
│  │  │  │  ├─ service/         # Business logic và scheduler
│  │  │  │  └─ DigitalTravelErpApplication.java
│  │  │  └─ resources/
│  │  │     ├─ application.yaml
│  │  │     └─ db/
│  │  │        ├─ KhoiTaoBang.sql
│  │  │        ├─ data_v1.sql
│  │  │        └─ data_lien_ket.sql
│  │  └─ test/
│  ├─ mvnw
│  ├─ mvnw.cmd
│  ├─ pom.xml
│  ├─ README.md
│  └─ HUONGDAN.md
├─ Frontend/
│  ├─ Admin/                    # Giao diện quản trị/nhân viên nội bộ
│  │  ├─ src/
│  │  ├─ public/
│  │  ├─ package.json
│  │  └─ vite.config.ts
│  ├─ HDV/                      # Giao diện hướng dẫn viên
│  │  ├─ src/
│  │  ├─ public/
│  │  ├─ package.json
│  │  └─ vite.config.ts
│  └─ Khachhang/                # Giao diện khách hàng
│     ├─ src/
│     ├─ public/
│     ├─ package.json
│     └─ vite.config.ts
├─ .gitignore
└─ README.md
```

---

## Công nghệ và công cụ sử dụng

### Backend

| Nhóm | Công nghệ |
|---|---|
| Ngôn ngữ | Java 21 |
| Framework | Spring Boot 4.0.5 |
| Security | Spring Security, JWT |
| ORM | Spring Data JPA, Hibernate |
| Database | Oracle 19c/21c, JDBC `ojdbc11` |
| Validation | Jakarta Bean Validation |
| API Docs | Springdoc OpenAPI, Swagger UI |
| Build | Maven Wrapper |
| Utility | Lombok, HikariCP |

### Frontend

| Ứng dụng | Công nghệ chính |
|---|---|
| `Frontend/Admin` | React 19, TypeScript, Vite, Tailwind CSS, Axios, React Router, Recharts, Lucide React |
| `Frontend/HDV` | React 19, TypeScript, Vite, Tailwind CSS, Axios, React Router, Lucide React |
| `Frontend/Khachhang` | React 19, TypeScript, Vite, Tailwind CSS, Radix UI, Axios, React Router, Sonner, Lucide React |

### Công cụ phát triển

| Công cụ | Mục đích |
|---|---|
| Git/GitHub | Quản lí mã nguồn |
| SQL Developer / DBeaver / SQL*Plus | Chạy script Oracle |
| Node.js + npm | Cài đặt và chạy frontend |
| Maven Wrapper | Build và chạy backend |
| Swagger UI | Kiểm thử API backend |

---

## Yêu cầu môi trường

| Thành phần | Phiên bản / ghi chú |
|---|---|
| JDK | Java 21 trở lên |
| Node.js | Khuyến nghị Node.js 20 trở lên |
| npm | Đi kèm Node.js |
| Database | Oracle 19c/21c hoặc Oracle XE có service/PDB |
| Port backend | `8080` |
| Port frontend | Vite mặc định `5173`, nếu bận sẽ tự chuyển sang port tiếp theo |

Các frontend đã cấu hình Vite proxy:

```ts
'/api' -> 'http://localhost:8080'
```

Vì vậy khi chạy local, cần chạy frontend bằng `npm run dev` để proxy hoạt động.

---

## Hướng dẫn cài đặt và chạy dự án

Thứ tự chạy khuyến nghị:

1. Khởi tạo Database
2. Chạy Backend
3. Chạy Frontend

### 1. Clone repository

```powershell
git clone https://github.com/Yuu2006/Digital-Travel_ERP.git
cd Digital-Travel_ERP
```

### 2. Khởi tạo Database

Tạo Oracle user/schema phù hợp, ví dụ:

```sql
CREATE USER ERP_APP IDENTIFIED BY ERP123;
GRANT CONNECT, RESOURCE TO ERP_APP;
ALTER USER ERP_APP QUOTA UNLIMITED ON USERS;
```

Đăng nhập vào schema vừa tạo bằng SQL Developer, DBeaver hoặc SQL*Plus, sau đó chạy script theo thứ tự:

```sql
@Backend/src/main/resources/db/KhoiTaoBang.sql
@Backend/src/main/resources/db/data_v1.sql
@Backend/src/main/resources/db/data_lien_ket.sql
```

Ý nghĩa các file:

| File | Nội dung |
|---|---|
| `KhoiTaoBang.sql` | Tạo bảng, khóa chính, khóa ngoại, constraint, index |
| `data_v1.sql` | Seed vai trò, tài khoản nhân viên, dữ liệu nền |
| `data_lien_ket.sql` | Seed dữ liệu liên kết phục vụ demo nghiệp vụ |

### 3. Cấu hình Backend

Tạo file `Backend/.env` trên máy local, không commit file này:

```properties
DB_HOST=localhost
DB_PORT=1521
DB_SERVICE=pdb
DB_USERNAME=ERP_APP
DB_PASSWORD=ERP123

JWT_SECRET=ZGlnaXRhbC10cmF2ZWwtZXJwLXNlY3JldC1rZXktbXVzdC1iZS00Mi1jaGFycy1sb25nLW1pbg==
JWT_EXPIRATION=86400000

PAYMENT_MOCK_ENABLED=true
```

Nếu Oracle dùng service name khác, sửa `DB_SERVICE` tương ứng, ví dụ `XEPDB1`, `orcl`, `pdb`.

### 4. Chạy Backend

```powershell
cd Backend
.\mvnw.cmd spring-boot:run
```

Backend chạy tại:

```text
http://localhost:8080
```

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

Kiểm tra nhanh backend:

```powershell
Invoke-WebRequest -UseBasicParsing http://localhost:8080/api/auth/debug
Invoke-WebRequest -UseBasicParsing http://localhost:8080/api/public/tour
```

### 5. Cài đặt và chạy Frontend

Mở terminal mới từ thư mục root repository.

#### Admin

```powershell
cd Frontend\Admin
npm install
npm run dev
```

#### Hướng dẫn viên

```powershell
cd Frontend\HDV
npm install
npm run dev
```

#### Khách hàng

```powershell
cd Frontend\Khachhang
npm install
npm run dev
```

Sau khi chạy, mở URL Vite in ra trong terminal, ví dụ:

```text
http://localhost:5173
```

Nếu port `5173` đang bận, Vite có thể tự chuyển sang `5174`, `5175`,... Hãy mở đúng URL hiển thị ở terminal.

### 6. Build kiểm tra

Backend:

```powershell
cd Backend
.\mvnw.cmd clean package -DskipTests
```

Frontend:

```powershell
cd Frontend\Admin
npm run build

cd ..\HDV
npm run build

cd ..\Khachhang
npm run build
```

---

## Tài khoản seed

Mật khẩu mặc định cho các tài khoản seed trong `data_v1.sql`: `password`

| Vai trò | Username | Giao diện / phân hệ |
|---|---|---|
| `ADMIN` | `admin` | Admin |
| `SANPHAM` | `sanpham01` | Admin - sản phẩm |
| `DIEUHANH` | `manager01` | Admin - điều hành |
| `KINHDOANH` | `sales01` | Admin - kinh doanh |
| `KETOAN` | `ketoan01` | Admin - kế toán |
| `HDV` | `hdv01` | HDV |
| `HDV` | `hdv02` | HDV |

Tài khoản khách hàng có thể đăng ký từ giao diện khách hàng hoặc dùng dữ liệu seed nếu đã có trong database demo.

---

## Lỗi thường gặp

| Hiện tượng | Nguyên nhân thường gặp | Cách xử lý |
|---|---|---|
| Backend không start | Sai cấu hình Oracle hoặc chưa chạy DB | Kiểm tra `Backend/.env`, port `1521`, service/PDB |
| Lỗi validate schema | Chưa chạy SQL hoặc schema không đúng | Chạy lại `KhoiTaoBang.sql`, `data_v1.sql`, `data_lien_ket.sql` |
| FE gọi API bị `Failed to fetch` | Backend chưa chạy hoặc gọi sai port | Chạy backend ở `8080`, chạy FE bằng `npm run dev` |
| FE mở nhầm giao diện cũ | Vite app cũ còn giữ port hoặc browser cache | Dừng process Node cũ, mở đúng URL Vite in ra, hard refresh `Ctrl + F5` |
| API trả `401 Unauthorized` | Chưa đăng nhập hoặc token hết hạn | Đăng nhập lại |
| API trả `403 Forbidden` | Tài khoản không đúng vai trò | Dùng đúng tài khoản theo phân hệ |
| Admin/HDV/Khachhang gọi `/api` không sang backend | Không chạy bằng Vite dev server | Chạy `npm run dev`, không mở trực tiếp file HTML |

---

## Thành viên nhóm

| STT | MSSV | Họ và Tên | GitHub | Email |
| :--- | :--- | :--- | :--- | :--- |
| 1 | 24521817 | Đoàn Thị Thuỳ Trang | https://github.com/ThorBietBay001 | 24521817@gm.uit.edu.vn |
| 2 | 24521769 | Lê Thị Thanh Tiền | https://github.com/tienlelttt | 24521769@gm.uit.edu.vn |
| 3 | 24521776 | Nguyễn Trần Thủy Tiên | https://github.com/NgKthy | 24521776@gm.uit.edu.vn |
| 4 | 24522039 | Nguyễn Tuấn Vũ | https://github.com/Yuu2006 | 24522039@gm.uit.edu.vn |
