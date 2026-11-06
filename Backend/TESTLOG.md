# TESTLOG - Ket qua kiem thu API

Ngay kiem thu: 2026-05-08

## Tong quan

- Ung dung khoi dong thanh cong tren `http://localhost:8080`.
- Kiem thu Maven sau khi goi API: thanh cong, `3 tests`, `0 failures`.
- Dot smoke test API dien rong: `56 passed`, `30 failed`, tong `86` request.
- Dot chay lai co sua payload: `14 passed`, `12 failed`, tong `26` request.

## Cac nhom chuc nang da chay thanh cong

- Dang nhap thanh cong cho cac role seed: `ADMIN`, `DIEUHANH`, `SANPHAM`, `KINHDOANH`, `KETOAN`, `HDV`, `KHACHHANG`.
- Public API: danh sach tour cong khai va danh sach danh gia tour.
- Quan tri: danh sach nhan vien, dang ky tai khoan nhan vien.
- San pham: CRUD loai phong, dich vu them, hanh dong xanh, tour mau, sao chep tour mau, xoa mem tour mau.
- Dieu hanh: danh sach tour thuc te, lich cong tac, xem/cap nhat nang luc HDV.
- Khach hang: xem/cap nhat ho so, dat tour, thanh toan mock, vi voucher, tao/xem yeu cau ho tro, lich su tour.
- Kinh doanh: danh sach khach hang, danh sach yeu cau ho tro, xu ly/dong yeu cau ho tro, danh sach voucher, danh sach danh gia, danh sach yeu cau huy.
- HDV/Dieu hanh: xem danh sach su co va chi phi theo tour.
- Ke toan: danh sach chi phi, danh sach giao dich hoan.

## Cac loi 500 can dieu tra

Nhom nay tra ve `500 INTERNAL_SERVER_ERROR`, nghia la request hop le ve mat HTTP nhung backend gap exception khong duoc xu ly thanh loi nghiep vu cu the. Can xem log server tai thoi diem goi API de lay stack trace chinh xac.

### Public tour detail

- `GET /api/public/tour/{maTourThucTe}` voi `TTT001` tra ve `500`.
- `GET /api/public/tour` van thanh cong va co tra `TTT001`, nen loi co kha nang nam o logic lay chi tiet tour, mapping response, lazy relation, hoac du lieu lien quan cua tour.
- Huong kiem tra: xem service cua `TourCongKhaiController`, dac biet cac truy van/mapper lay lich trinh, danh gia, gia, cho con lai, hoac quan he `TourThucTe -> TourMau`.

### HDV lich cong tac va doan tour

- `GET /api/huong-dan-vien/tour-cua-toi` tra ve `500`.
- `GET /api/huong-dan-vien/lich-cong-tac` tra ve `500`.
- `GET /api/huong-dan-vien/tour/{maTour}/doan` voi `TTT099` tra ve `500`.
- Loi lap lai voi nhieu tai khoan HDV (`hdv01`, `hdv02`, `hdv03`), nen it kha nang do rieng mot user.
- Mot so endpoint ghi nhan van hanh cua HDV tra `404 Khong tim thay ho so nhan vien`, du dang nhap HDV thanh cong. Dieu nay goi y co the service dang tim `NhanVien` bang sai khoa, vi du dung `username`/`maTaiKhoan` thay vi lien ket tai khoan sang nhan vien, hoac seed data thieu lien ket can thiet.
- Huong kiem tra: xem cach lay nhan vien hien tai tu `Authentication`, repository query theo `TaiKhoan`, va du lieu `NHANVIEN` gan voi `TAIKHOAN` cua HDV.

### Ke toan quyet toan va bao cao

- `GET /api/ke-toan/tour-can-quyet-toan` tra ve `500`.
- `GET /api/ke-toan/tinh-toan/{maTour}` voi `TTT099` tra ve `500`.
- `GET /api/ke-toan/quyet-toan` tra ve `500`.
- `GET /api/ke-toan/quyet-toan/{maQuyetToan}` voi `QT001` tra ve `500`.
- `GET /api/ke-toan/bao-cao/doanh-thu` tra ve `500`.
- `GET /api/ke-toan/chi-phi` va `GET /api/ke-toan/giao-dich-hoan` lai thanh cong, nen loi tap trung o module quyet toan/bao cao doanh thu.
- Huong kiem tra: truy van tong hop doanh thu/chi phi/quyet toan, mapping BigDecimal/date, cac quan he voi `TourThucTe`, `DonDatTour`, `GiaoDichThanhToan`, `QuyetToan`.

### Quan tri

- `POST /api/quan-tri/tham-so-he-thong` tra ve `500`.
- `GET /api/quan-tri/nhat-ky-he-thong` tra ve `500`.
- `GET /api/quan-tri/nhan-vien/{maNhanVien}` voi `NV_HDV02` tra ve `500`.
- `GET /api/quan-tri/tham-so-he-thong` thanh cong nhung danh sach rong, nen loi tao tham so co the do constraint DB, ID generator, ten cot, hoac mapping entity.
- Huong kiem tra: entity/repository `ThamSoHeThong`, entity `NhatKyHeThong`, mapper chi tiet nhan vien va cac truong nullable trong seed.

### Kinh doanh

- `GET /api/kinh-doanh/dat-tour` tra ve `500`.
- `GET /api/kinh-doanh/don-dat-tour` tra ve `500`.
- `GET /api/kinh-doanh/khach-hang/{maKhachHang}` voi `KH001` tra ve `500`.
- `POST /api/kinh-doanh/voucher` tra ve `500`.
- Cac endpoint danh sach khach hang, danh sach voucher, danh gia, yeu cau ho tro van thanh cong.
- Huong kiem tra: mapper chi tiet don dat tour/khach hang, cac relation voi chi tiet khach, dich vu, thanh toan, voucher; voi tao voucher can kiem tra unique code, sequence/ID generator, constraint ngay hieu luc/het han.

## Cac loi nghiep vu hoac du lieu test

Nhom nay khong nhat thiet la bug backend. Nhieu loi la do du lieu seed hien tai khong phu hop voi luong test hoac payload can dung gia tri khac.

### Tao tour thuc te bi tu choi gia

- `POST /api/dieu-hanh/tour-thuc-te` tra ve `400 BAD_REQUEST`.
- Thong bao: `Gia hien hanh khong duoc thap hon gia...`.
- Da thu voi gia `1,500,000` va `3,000,000` cho `TM001` nhung van bi tu choi.
- Co the `giaSan` cua `TM001` trong DB cao hon gia test, hoac rule so sanh dang doc nham gia san/field.
- Huong kiem tra: lay gia san thuc te cua `TM001` trong DB, sau do thu lai voi gia cao hon; neu van fail thi kiem tra logic validate gia trong service tao tour thuc te.

### Ap voucher khong thanh cong

- Lan dau gui sai field `maCode`, trong khi DTO yeu cau `maVoucher`, nen tra `400 VALIDATION_ERROR`.
- Sau khi sua payload thanh `maVoucher='VC001'`, API tra `404 Voucher khong ton tai trong vi`.
- Ket luan: voucher `VC001` co the ton tai trong danh muc voucher, nhung chua duoc phat hanh vao vi cua khach hang dang test.
- Huong kiem tra: phat hanh voucher cho `KH001` truoc khi ap dung, hoac chon voucher dang co trong `GET /api/khach-hang/vi-voucher`.

### Doi diem voucher khong du diem

- `POST /api/khach-hang/doi-diem` tra ve `400 BAD_REQUEST`.
- Thong bao: `Khong du diem xanh. Can: 5000, Hien co...`.
- Day la loi nghiep vu hop le neu khach hang seed khong du diem.
- Huong kiem tra: dung voucher can it diem hon, cap nhat diem xanh test, hoac tao hanh dong xanh/lich su diem truoc khi doi.

### Danh gia tour bi trung

- `POST /api/khach-hang/danh-gia` voi `TTT099` tra ve `400 BAD_REQUEST`.
- Thong bao: `Ban da danh gia tour nay roi`.
- Day la rule nghiep vu hop le neu khach hang da co danh gia cho tour nay.
- Huong kiem tra: dung khach hang khac chua danh gia, hoac tour da hoan thanh khac ma khach hang da tham gia.

### Xac nhan booking sai trang thai

- `PUT /api/kinh-doanh/dat-tour/{maDatTour}/xac-nhan` tra ve `400 BAD_REQUEST`.
- Thong bao: `Chi co the xac nhan don o trang thai...`.
- Booking moi tao trong test co the da duoc thanh toan mock hoac o trang thai khong con phu hop de xac nhan.
- Huong kiem tra: xac nhan don truoc khi thanh toan mock, hoac tao don rieng o dung trang thai cho UC xac nhan.

### Xu ly ho tro sai trang thai o lan dau

- DTO/logic chap nhan `CHUA_XU_LY`, `DA_XU_LY`, `TU_CHOI`.
- Khi chay voi `DA_XU_LY` va `TU_CHOI`, API thanh cong.
- Ket luan: day la loi payload test, khong phai bug backend.

### Them lich trinh vuot thoi luong tour

- `POST /api/san-pham/tour-mau/{id}/lich-trinh` voi `ngayThu=3` tren tour mau `thoiLuong=2` tra ve `400 BAD_REQUEST`.
- Thong bao: `Ngay thu 3 vuot qua thoi luong tour`.
- Day la rule nghiep vu hop le.
- Huong kiem tra: dung `ngayThu` trong khoang `1..thoiLuong`, hoac tao tour mau co thoi luong lon hon.

## Du lieu da bi thay doi trong qua trinh test

Cac API mutation da duoc phep chay va da tao/cap nhat/xoa mem mot so du lieu test.

- Tao tai khoan nhan vien test.
- Tao/cap nhat/xoa loai phong test.
- Tao/cap nhat/xoa dich vu them test.
- Tao/cap nhat/xoa hanh dong xanh test.
- Tao/cap nhat/sao chep/xoa mem tour mau test.
- Tao booking khach hang.
- Tao giao dich thanh toan mock.
- Cap nhat ho so y te khach hang.
- Tao va xu ly yeu cau ho tro.
- Cap nhat nang luc HDV.

## De xuat buoc tiep theo

1. Lay log stack trace cho cac endpoint `500` de xac dinh class/method va dong loi cu the.
2. Viet test tich hop cho cac endpoint dang loi `500`, uu tien public detail, HDV schedule/group, va ke toan quyet toan.
3. Chuan hoa seed data rieng cho API test, gom booking o dung trang thai, voucher da phat hanh vao vi, khach hang chua danh gia, va tour mau co gia/thoi luong phu hop.
4. Neu can test lap lai nhieu lan, nen tao script reset/reseed schema rieng de tranh du lieu test tich luy lam sai ket qua.
