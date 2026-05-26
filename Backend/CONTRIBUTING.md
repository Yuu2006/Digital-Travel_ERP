# Contributing Guide

Tài liệu này mô tả quy trình đóng góp code cho dự án Digital Travel ERP.
Mục tiêu: đồng bộ cách làm việc, merge nhanh, giảm lỗi khi release.

## 1) Branch strategy

- `main`: nhánh ổn định để demo/release, không commit trực tiếp.
- `dev`: nhánh tích hợp chính, nhận PR từ `feature/*`.
- `feature/*`: nhánh phát triển tính năng, tạo từ `dev`.

Đặt tên branch để nhìn là biết việc gì đang làm:

- `feature/auth-jwt`
- `feature/booking-create`
- `feature/tour-search-filter`
- `fix/payment-callback`

### Flow làm việc

```powershell
git switch -c feature/<ten-ngan-gon>
```

Sau khi code xong:

```powershell
git add .
git commit -m "feat(scope): mo ta ngan gon"
git push -u origin feature/<ten-ngan-gon>
```

Mở Pull Request: `feature/*` -> `dev`.
Chỉ merge `dev` -> `main` khi đã test và chuẩn bị demo/release.

## 2) Commit message convention

Sử dụng mẫu Conventional Commits rút gọn:

`<type>(<scope>): <subject>`

Loại commit khuyến nghị:

- `feat`: thêm tính năng
- `fix`: sửa lỗi
- `refactor`: đổi cấu trúc, không đổi hành vi
- `test`: bổ sung/chỉnh sửa test
- `docs`: cập nhật tài liệu
- `chore`: việc hệ thống/build/tooling

Ví dụ:

- `feat(auth): add jwt login endpoint`
- `fix(booking): prevent overbooking when seats are full`
- `docs(readme): update week-1 progress`

## 3) Pull Request rules

Mỗi PR cần có:

- Tiêu đề rõ ràng, ngắn gọn
- Mô tả "tại sao thay đổi" và "thay đổi gì"
- Link task/use case (nếu có)
- Hướng dẫn test nhanh hoặc bằng chứng test
- Screenshot/Swagger response nếu thay đổi API/UI

Khuyến nghị:

- 1 PR chỉ nên giải quyết 1 vấn đề chính
- Tránh PR quá lớn (> 500 dòng thay đổi nếu không cần thiết)
- Resolve conversation trước khi merge.

## 4) Review checklist

Trước khi request review, tự kiểm tra:

- [ ] Build pass trên máy local
- [ ] Test liên quan pass (hoặc ghi rõ lý do chưa có test)
- [ ] Không commit file rác (log, secrets, file tạm)
- [ ] Đặt tên class/package/biến rõ ràng, đúng convention
- [ ] Không phá vỡ API/hành vi cũ mà không ghi chú
- [ ] Cập nhật `README.md`/tài liệu nếu đổi cách dùng

## 5) Local quality gate (tối thiểu)

Chạy trước khi mở PR:

```powershell
./mvnw.cmd test
```

Nếu thay đổi liên quan compile/build, chạy thêm:

```powershell
./mvnw.cmd clean verify
```

## 6) Không commit thông tin nhạy cảm

Tuyệt đối không commit:

- Mật khẩu DB, API key, token thật
- File env local (`.env`, `application-local.yml`...)
- Dữ liệu cá nhân thật của khách hàng

Nếu lỡ commit thông tin nhạy cảm:

1. Thu hồi/rotate secret ngay lập tức.
2. Tạo commit xóa secret.
3. Thông báo cho team để kiểm tra ảnh hưởng.

## 7) Nguyên tắc merge

- Ưu tiên squash merge vào `dev` để lịch sử gọn gàng.
- Không force-push lên nhánh chung (`main`, `dev`).
- Release branch (nếu cần) tạo từ `dev`, merge ngược về `dev` sau khi hotfix.

---

Cảm ơn bạn đã đóng góp. Làm nhỏ, review nhanh, merge đều.

