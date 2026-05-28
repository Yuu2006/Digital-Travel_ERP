package com.digitaltravel.erp.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import com.digitaltravel.erp.dto.requests.BoSungQuyetToanRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.digitaltravel.erp.dto.requests.QuyetToanRequest;
import com.digitaltravel.erp.dto.responses.QuyetToanResponse;
import com.digitaltravel.erp.dto.responses.ThanhToanResponse;
import com.digitaltravel.erp.entity.ChiPhiThucTe;
import com.digitaltravel.erp.entity.DonDatTour;
import com.digitaltravel.erp.entity.GiaoDich;
import com.digitaltravel.erp.entity.NhanVien;
import com.digitaltravel.erp.entity.QuyetToan;
import com.digitaltravel.erp.entity.TourThucTe;
import com.digitaltravel.erp.entity.YeuCauHoTro;
import com.digitaltravel.erp.exception.AppException;
import com.digitaltravel.erp.repository.ChiPhiThucTeRepository;
import com.digitaltravel.erp.repository.ChiTietDatTourRepository;
import com.digitaltravel.erp.repository.DonDatTourRepository;
import com.digitaltravel.erp.repository.GiaoDichRepository;
import com.digitaltravel.erp.repository.NhanVienRepository;
import com.digitaltravel.erp.repository.QuyetToanRepository;
import com.digitaltravel.erp.repository.TourThucTeRepository;
import com.digitaltravel.erp.repository.YeuCauHoTroRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuyetToanService {

    private static final String YEU_CAU_BO_SUNG_MARKER = "[Yêu cầu bổ sung quyết toán";
    private static final String HDV_BO_SUNG_MARKER = "[HDV bổ sung quyết toán";

    private final QuyetToanRepository quyetToanRepository;
    private final TourThucTeRepository tourThucTeRepository;
    private final NhanVienRepository nhanVienRepository;
    private final DonDatTourRepository donDatTourRepository;
    private final ChiPhiThucTeRepository chiPhiThucTeRepository;
    private final GiaoDichRepository giaoDichRepository;
    private final ChiTietDatTourRepository chiTietDatTourRepository;
    private final YeuCauHoTroRepository yeuCauHoTroRepository;

    // ── UC47: Danh sách tour cần quyết toán (đã kết thúc, chưa quyết toán)
    @Transactional(readOnly = true)
    public Page<QuyetToanResponse> tourCanQuyetToan(Pageable pageable) {
        try {
            return tourThucTeRepository.timKiem("KET_THUC", null, null, null, pageable)
                    .map(tt -> {
                        boolean daqt = quyetToanRepository.existsByTourThucTe_MaTourThucTe(tt.getMaTourThucTe());
                        if (daqt)
                            return null;
                        return toQuyetToanXemTruoc(tt);
                    })
                    .map(r -> r);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    // ── UC48: Tính toán sơ bộ (xem trước, không lưu DB) ──────────────────
    @Transactional(readOnly = true)
    public QuyetToanResponse tinhToan(String maTour) {
        TourThucTe tour = getKetThucTour(maTour);
        BigDecimal doanhThu = tinhDoanhThu(maTour);
        BigDecimal chiPhi = tinhChiPhi(maTour);
        BigDecimal loiNhuan = doanhThu.subtract(chiPhi);

        return QuyetToanResponse.builder()
                .maTour(maTour)
                .tenTour(tour.getTourMau() != null ? tour.getTourMau().getTieuDe() : "")
                .tongDoanhThu(doanhThu)
                .tongChiPhi(chiPhi)
                .loiNhuan(loiNhuan)
                .trangThai("XEM_TRUOC")
                .build();
    }

    // ── UC49: Tạo bản nháp quyết toán ────────────────────────────────────
    @Transactional
    public QuyetToanResponse taoQuyetToan(String maTour, QuyetToanRequest req, String maTaiKhoan) {
        getKetThucTour(maTour);

        if (quyetToanRepository.existsByTourThucTe_MaTourThucTe(maTour)) {
            // Nếu chưa quyết toán thì cập nhật
            QuyetToan existing = quyetToanRepository.findByTourThucTe_MaTourThucTe(maTour).get();
            if ("DA_QUYET_TOAN".equals(existing.getTrangThai())) {
                throw AppException.badRequest("Quyết toán đã bị chốt, không thể sửa.");
            }
            return capNhatQT(existing, maTour, req, maTaiKhoan);
        }

        NhanVien nv = nhanVienRepository.findByMaTaiKhoan(maTaiKhoan)
                .orElseGet(() -> nhanVienRepository.findAll().stream()
                        .filter(n -> "KETOAN".equals(n.getLoaiNhanVien()) || "ADMIN".equals(n.getLoaiNhanVien()))
                        .findFirst()
                        .orElseThrow(() -> AppException.notFound("Không tìm thấy hồ sơ nhân viên")));

        BigDecimal doanhThu = tinhDoanhThu(maTour);
        BigDecimal chiPhi = tinhChiPhi(maTour);

        QuyetToan qt = new QuyetToan();
        qt.setMaQuyetToan("QT_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        qt.setTourThucTe(tourThucTeRepository.findById(maTour).get());
        qt.setNhanVien(nv);
        qt.setTongDoanhThu(doanhThu);
        qt.setTongChiPhi(chiPhi);
        qt.setGiaCamKet(req != null ? req.getGiaCamKet() : null);
        qt.setLoiNhuan(doanhThu.subtract(chiPhi));
        qt.setNgayQuyetToan(LocalDateTime.now());
        qt.setTrangThai("CHUA_QUYET_TOAN");
        qt.setGhiChu(req != null ? req.getGhiChu() : null);
        qt.setHoaDonAnh(req != null ? req.getHoaDonAnh() : null);
        quyetToanRepository.save(qt);

        return toResponse(qt);
    }

    // ── UC50: Chốt quyết toán ─────────────────────────────────────────────
    @Transactional
    public QuyetToanResponse chotQuyetToan(String maQuyetToan) {
        QuyetToan qt = quyetToanRepository.findById(maQuyetToan)
                .orElseThrow(() -> AppException.notFound("Khong tim thay quyet toan: " + maQuyetToan));
        if (!"CHUA_QUYET_TOAN".equals(qt.getTrangThai())) {
            throw AppException.badRequest("Chỉ có thể chốt quyết toán ở trạng thái CHUA_QUYET_TOAN.");
        }
        qt.setTrangThai("DA_QUYET_TOAN");

        // Cập nhật tour sang DA_QUYET_TOAN
        TourThucTe tour = qt.getTourThucTe();
        tour.setTrangThai("DA_QUYET_TOAN");
        tourThucTeRepository.save(tour);
        quyetToanRepository.save(qt);

        return toResponse(qt);
    }

    // ── UC51-53: Xem danh sách / chi tiết ────────────────────────────────
    @Transactional(readOnly = true)
    public Page<QuyetToanResponse> danhSach(String trangThai, Pageable pageable) {
        return quyetToanRepository.findByTrangThai(trangThai, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public QuyetToanResponse chiTiet(String maQuyetToan) {
        QuyetToan qt = quyetToanRepository.findById(maQuyetToan)
                .orElseThrow(() -> AppException.notFound("Khong tim thay quyet toan: " + maQuyetToan));
        return toResponse(qt);
    }

    @Transactional
    public QuyetToanResponse yeuCauBoSung(String maQuyetToan, String noiDung) {
        QuyetToan qt = quyetToanRepository.findById(maQuyetToan)
                .orElseThrow(() -> AppException.notFound("Khong tim thay quyet toan: " + maQuyetToan));
        if ("DA_QUYET_TOAN".equals(qt.getTrangThai())) {
            throw AppException.badRequest("Quyết toán đã bị chốt, không thể yêu cầu bổ sung.");
        }

        qt.setGhiChu(noiGhiChuMoi(qt.getGhiChu(), YEU_CAU_BO_SUNG_MARKER, noiDung));
        qt.setNgayQuyetToan(LocalDateTime.now());
        quyetToanRepository.save(qt);
        return toResponse(qt);
    }

    @Transactional(readOnly = true)
    public List<QuyetToanResponse> danhSachCanBoSungCuaHdv(String maTaiKhoan) {
        return quyetToanRepository.findChuaQuyetToanTheoHdv(maTaiKhoan).stream()
                .filter(qt -> dangChoHdvBoSung(qt.getGhiChu()))
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public QuyetToanResponse hdvBoSung(
            String maTaiKhoan,
            String maQuyetToan,
            String ghiChu,
            String hoaDonAnh) {
        QuyetToan qt = quyetToanRepository.findById(maQuyetToan)
                .orElseThrow(() -> AppException.notFound("Khong tim thay quyet toan: " + maQuyetToan));
        if (!quyetToanRepository.findChuaQuyetToanTheoHdv(maTaiKhoan).stream()
                .anyMatch(item -> item.getMaQuyetToan().equals(maQuyetToan))) {
            throw AppException.forbidden("Không có quyền bổ sung quyết toán này");
        }
        if (!dangChoHdvBoSung(qt.getGhiChu())) {
            throw AppException.badRequest("Quyết toán này không chờ hướng dẫn viên bổ sung.");
        }

        String noiDung = ghiChu;
        if (hoaDonAnh != null && !hoaDonAnh.isBlank()) {
            noiDung += "\nHóa đơn ảnh: " + hoaDonAnh.trim();
        }
        qt.setGhiChu(noiGhiChuMoi(qt.getGhiChu(), HDV_BO_SUNG_MARKER, noiDung));
        qt.setNgayQuyetToan(LocalDateTime.now());
        quyetToanRepository.save(qt);
        return toResponse(qt);
    }

    // ── UC50: KeToan xác nhận đã hoàn tiền cho KH ────────────────────────
    @Transactional
    public ThanhToanResponse xacNhanHoanTien(String maGiaoDich, String nguoiXacNhan) {
        GiaoDich gd = giaoDichRepository.findById(maGiaoDich)
                .orElseThrow(() -> AppException.notFound("Khong tim thay giao dich: " + maGiaoDich));

        if (!"HOAN_TIEN".equals(gd.getLoaiGiaoDich())) {
            throw AppException.badRequest("Giao dịch này không phải hoàn tiền");
        }
        if ("DA_HOAN_TIEN".equals(gd.getTrangThai())) {
            throw AppException.badRequest("Giao dịch này đã được xác nhận hoàn tiền rồi");
        }
        if (!"CHO_THANH_TOAN".equals(gd.getTrangThai())) {
            throw AppException.badRequest("Chỉ có thể xác nhận giao dịch ở trạng thái CHO_THANH_TOAN");
        }

        DonDatTour don = gd.getDonDatTour();
        if (!"CHO_HUY".equals(don.getTrangThai())) {
            throw AppException.badRequest("Chỉ có thể xác nhận hoàn tiền cho đơn ở trạng thái CHO_HUY. Trạng thái hiện tại: " + don.getTrangThai());
        }

        gd.setTrangThai("DA_HOAN_TIEN");
        gd.setNgayThanhToan(LocalDateTime.now());
        giaoDichRepository.save(gd);

        TourThucTe tour = don.getTourThucTe();
        int soKhach = (int) chiTietDatTourRepository.countByDonDatTour_MaDatTour(don.getMaDatTour());
        int newChoConLai = Math.min(tour.getChoConLai() + soKhach, tour.getSoKhachToiDa());
        tour.setChoConLai(newChoConLai);
        tourThucTeRepository.save(tour);

        don.setTrangThai("DA_HUY");
        donDatTourRepository.save(don);
        capNhatYeuCauHuyTour(don, "DA_XU_LY", "Da hoan tien thanh cong");

        return ThanhToanResponse.builder()
                .maGiaoDich(gd.getMaGiaoDich())
                .maDatTour(gd.getDonDatTour().getMaDatTour())
                .soTien(gd.getSoTien())
                .phuongThuc(gd.getPhuongThuc())
                .trangThai(gd.getTrangThai())
                .ngayThanhToan(gd.getNgayThanhToan())
                .thongBao("Xac nhan hoan tien thanh cong")
                .build();
    }

    @Transactional
    public ThanhToanResponse tuChoiHoanTien(String maGiaoDich, String nguoiXacNhan) {
        GiaoDich gd = giaoDichRepository.findById(maGiaoDich)
                .orElseThrow(() -> AppException.notFound("Khong tim thay giao dich: " + maGiaoDich));

        if (!"HOAN_TIEN".equals(gd.getLoaiGiaoDich())) {
            throw AppException.badRequest("Giao dịch này không phải hoàn tiền");
        }
        if (!"CHO_THANH_TOAN".equals(gd.getTrangThai())) {
            throw AppException.badRequest("Chỉ có thể từ chối giao dịch hoàn tiền ở trạng thái CHO_THANH_TOAN");
        }

        DonDatTour don = gd.getDonDatTour();
        if (!"CHO_HUY".equals(don.getTrangThai())) {
            throw AppException.badRequest("Chỉ có thể từ chối hoàn tiền cho đơn ở trạng thái CHO_HUY. Trạng thái hiện tại: " + don.getTrangThai());
        }

        gd.setTrangThai("THAT_BAI");
        gd.setNgayThanhToan(LocalDateTime.now());
        giaoDichRepository.save(gd);

        don.setTrangThai("TU_CHOI_HOAN_TIEN");
        donDatTourRepository.save(don);
        capNhatYeuCauHuyTour(don, "TU_CHOI", "Tu choi hoan tien");

        return ThanhToanResponse.builder()
                .maGiaoDich(gd.getMaGiaoDich())
                .maDatTour(don.getMaDatTour())
                .soTien(gd.getSoTien())
                .phuongThuc(gd.getPhuongThuc())
                .trangThai(gd.getTrangThai())
                .ngayThanhToan(gd.getNgayThanhToan())
                .thongBao("Tu choi hoan tien thanh cong")
                .build();
    }

    // ── UC50: Danh sách giao dịch chờ hoàn tiền ──────────────────────────
    public Page<ThanhToanResponse> danhSachChoHoanTien(Pageable pageable) {
        return giaoDichRepository.findChoHoanTien(pageable)
                .map(gd -> ThanhToanResponse.builder()
                        .maGiaoDich(gd.getMaGiaoDich())
                        .maDatTour(gd.getDonDatTour().getMaDatTour())
                        .soTien(gd.getSoTien())
                        .phuongThuc(gd.getPhuongThuc())
                        .trangThai(gd.getTrangThai())
                        .ngayThanhToan(gd.getNgayThanhToan())
                        .build());
    }

    // ── Helpers ───────────────────────────────────────────────────────────
    private TourThucTe getKetThucTour(String maTour) {
        TourThucTe tour = tourThucTeRepository.findById(maTour)
                .orElseThrow(() -> AppException.notFound("Khong tim thay tour: " + maTour));
        if (!"KET_THUC".equals(tour.getTrangThai()) && !"DA_QUYET_TOAN".equals(tour.getTrangThai())) {
            throw AppException.badRequest("Tour chưa kết thúc, không thể quyết toán.");
        }
        return tour;
    }

    private void capNhatYeuCauHuyTour(DonDatTour don, String trangThai, String ghiChu) {
        List<YeuCauHoTro> yeuCauHuyTour = yeuCauHoTroRepository
                .findByMaDatTourAndLoaiYeuCau(don.getMaDatTour(), "HUY_TOUR");
        yeuCauHuyTour.forEach(yc -> {
            yc.setTrangThai(trangThai);
            yc.setNoiDung(appendNote(yc.getNoiDung(), ghiChu + " luc " + LocalDateTime.now()));
            yeuCauHoTroRepository.save(yc);
        });
    }

    private BigDecimal tinhDoanhThu(String maTour) {
        // Tổng TongTien của các đơn còn hiệu lực của tour
        return donDatTourRepository.findAll().stream()
                .filter(d -> d.getTourThucTe().getMaTourThucTe().equals(maTour))
                .filter(d -> "DA_XAC_NHAN".equals(d.getTrangThai())
                        || "CHO_HUY".equals(d.getTrangThai())
                        || "CHO_HOAN_TIEN".equals(d.getTrangThai())
                        || "TU_CHOI_HOAN_TIEN".equals(d.getTrangThai()))
                .map(DonDatTour::getTongTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal tinhChiPhi(String maTour) {
        List<ChiPhiThucTe> list = chiPhiThucTeRepository.findDaDuyetByMaTour(maTour);
        return list.stream()
                .map(ChiPhiThucTe::getThanhTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String noiGhiChuMoi(String ghiChuHienTai, String marker, String noiDung) {
        String phanTruoc = ghiChuHienTai == null || ghiChuHienTai.isBlank()
                ? ""
                : ghiChuHienTai + "\n\n";
        return phanTruoc + marker + " lúc " + LocalDateTime.now() + "]:\n" + noiDung.trim();
    }

    private boolean dangChoHdvBoSung(String ghiChu) {
        if (ghiChu == null) {
            return false;
        }
        int lanYeuCauCuoi = ghiChu.lastIndexOf(YEU_CAU_BO_SUNG_MARKER);
        int lanBoSungCuoi = ghiChu.lastIndexOf(HDV_BO_SUNG_MARKER);
        return lanYeuCauCuoi >= 0 && lanYeuCauCuoi > lanBoSungCuoi;
    }

    private QuyetToanResponse capNhatQT(QuyetToan qt, String maTour, QuyetToanRequest req, String maTaiKhoan) {
        BigDecimal doanhThu = tinhDoanhThu(maTour);
        BigDecimal chiPhi = tinhChiPhi(maTour);
        qt.setTongDoanhThu(doanhThu);
        qt.setTongChiPhi(chiPhi);
        if (req != null && req.getGiaCamKet() != null)
            qt.setGiaCamKet(req.getGiaCamKet());
        qt.setLoiNhuan(doanhThu.subtract(chiPhi));
        qt.setNgayQuyetToan(LocalDateTime.now());
        if (req != null && req.getGhiChu() != null)
            qt.setGhiChu(req.getGhiChu());
        if (req != null && req.getHoaDonAnh() != null)
            qt.setHoaDonAnh(req.getHoaDonAnh());
        quyetToanRepository.save(qt);
        return toResponse(qt);
    }

    private String appendNote(String current, String note) {
        if (current == null || current.isBlank()) {
            return note;
        }
        return current + "\n\n" + note;
    }

    private QuyetToanResponse toQuyetToanXemTruoc(TourThucTe tt) {
        java.math.BigDecimal doanhThu = tinhDoanhThu(tt.getMaTourThucTe());
        java.math.BigDecimal chiPhi = tinhChiPhi(tt.getMaTourThucTe());
        return QuyetToanResponse.builder()
                .maTour(tt.getMaTourThucTe())
                .tenTour(tt.getTourMau() != null ? tt.getTourMau().getTieuDe() : "")
                .tongDoanhThu(doanhThu)
                .tongChiPhi(chiPhi)
                .loiNhuan(doanhThu.subtract(chiPhi))
                .ngayQuyetToan(tt.getNgayKhoiHanh() != null ? tt.getNgayKhoiHanh().atStartOfDay() : null)
                .trangThai("CHUA_QUYET_TOAN")
                .build();
    }

    private QuyetToanResponse toResponse(QuyetToan qt) {
        TourThucTe tt = qt.getTourThucTe();
        NhanVien nv = qt.getNhanVien();
        return QuyetToanResponse.builder()
                .maQuyetToan(qt.getMaQuyetToan())
                .maTour(tt.getMaTourThucTe())
                .tenTour(tt.getTourMau() != null ? tt.getTourMau().getTieuDe() : "")
                .tongDoanhThu(qt.getTongDoanhThu())
                .tongChiPhi(qt.getTongChiPhi())
                .giaCamKet(qt.getGiaCamKet())
                .loiNhuan(qt.getLoiNhuan())
                .trangThai(qt.getTrangThai())
                .ghiChu(qt.getGhiChu())
                .hoaDonAnh(qt.getHoaDonAnh())
                .ngayQuyetToan(qt.getNgayQuyetToan())
                .maNhanVien(nv.getMaNhanVien())
                .tenNhanVien(nv.getTaiKhoan() != null ? nv.getTaiKhoan().getHoTen() : "")
                .build();
    }
}
