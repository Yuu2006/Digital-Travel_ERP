package com.digitaltravel.erp.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.digitaltravel.erp.dto.requests.HuyTourRequest;
import com.digitaltravel.erp.dto.requests.XuLyYeuCauRequest;
import com.digitaltravel.erp.dto.responses.YeuCauHoTroResponse;
import com.digitaltravel.erp.entity.DonDatTour;
import com.digitaltravel.erp.entity.GiaoDich;
import com.digitaltravel.erp.entity.HoChieuSo;
import com.digitaltravel.erp.entity.YeuCauHoTro;
import com.digitaltravel.erp.exception.AppException;
import com.digitaltravel.erp.repository.DonDatTourRepository;
import com.digitaltravel.erp.repository.GiaoDichRepository;
import com.digitaltravel.erp.repository.HoChieuSoRepository;
import com.digitaltravel.erp.repository.YeuCauHoTroRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HuyTourService {

    private final DonDatTourRepository donDatTourRepository;
    private final HoChieuSoRepository hoChieuSoRepository;
    private final YeuCauHoTroRepository yeuCauHoTroRepository;
    private final GiaoDichRepository giaoDichRepository;

    // ── UC32: Khách hàng gửi yêu cầu hủy tour ─────────────────────────────
    @Transactional
    public YeuCauHoTroResponse yeuCauHuyTour(String maTaiKhoan, String maDatTour, HuyTourRequest request) {
        HoChieuSo kh = hoChieuSoRepository.findByMaTaiKhoan(maTaiKhoan)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy hồ sơ khách hàng"));

        DonDatTour don = donDatTourRepository.findByIdAndMaKhachHang(maDatTour, kh.getMaKhachHang())
                .orElseThrow(() -> AppException.notFound("Khong tim thay don dat tour: " + maDatTour));

        // Cho phép hủy đơn ở trạng thái DA_XAC_NHAN hoặc CHO_XAC_NHAN
        if (!"DA_XAC_NHAN".equals(don.getTrangThai()) && !"CHO_XAC_NHAN".equals(don.getTrangThai())) {
            throw AppException.badRequest(
                    "Chi co the yeu cau huy don o trang thai DA_XAC_NHAN hoac CHO_XAC_NHAN. Hien tai: " + don.getTrangThai());
        }

        // Kiểm tra chưa có yêu cầu hủy đang chờ
        boolean daCoYeuCau = !yeuCauHoTroRepository
                .findByMaDatTourAndLoaiYeuCau(maDatTour, "HUY_TOUR").isEmpty();
        if (daCoYeuCau) {
            throw AppException.badRequest("Đã có yêu cầu hủy tour đang chờ xử lý cho đơn này.");
        }

        // Tính tỉ lệ hoàn tiền
        LocalDate ngayKhoiHanh = don.getTourThucTe().getNgayKhoiHanh();
        if (ngayKhoiHanh == null) {
            throw AppException.badRequest("Tour chưa có ngày khởi hành, không thể yêu cầu hoàn tiền");
        }
        long soNgayConLai = ChronoUnit.DAYS.between(LocalDate.now(), ngayKhoiHanh);
        if (soNgayConLai <= 2) {
            throw AppException.badRequest("Không thể yêu cầu hoàn tiền trong vòng 2 ngày trước ngày khởi hành tour");
        }
        int tiLeHoan = tinhTiLeHoan((int) soNgayConLai);
        BigDecimal soTienHoan = don.getTongTien()
                .multiply(BigDecimal.valueOf(tiLeHoan))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        /*
            > 15 ngày: hoàn 90%
            7 - 15 ngày: hoàn 70%
            3 - 6 ngày: hoàn 50%
            < 3 ngày: Không cho yêu cầu hoàn tiền
        */
        // Tạo yêu cầu hỗ trợ
        YeuCauHoTro yc = new YeuCauHoTro();
        yc.setMaYeuCauHoTro("YCHT_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        yc.setDonDatTour(don);
        yc.setKhachHang(kh);
        yc.setLoaiYeuCau("HUY_TOUR");
        yc.setNoiDung(String.format(
                "Ly do: %s | Ngay con lai den khoi hanh: %d | Ti le hoan: %d%% | So tien hoan: %s",
                request.getLyDo(), soNgayConLai, tiLeHoan, soTienHoan.toPlainString()));
        yc.setTrangThai("CHUA_XU_LY");
        yeuCauHoTroRepository.save(yc);

        taoGiaoDichHoanTienNeuChuaCo(don, soTienHoan);

        // Đổi trạng thái đơn sang CHO_HUY
        don.setTrangThai("CHO_HUY");
        donDatTourRepository.save(don);

        return toResponse(yc, soTienHoan, tiLeHoan);
    }

    // ── UC33: SALES duyệt yêu cầu hủy ────────────────────────────────────
    @Transactional
    public YeuCauHoTroResponse duyetHuyTour(String maYeuCau, String maNhanVien, XuLyYeuCauRequest request) {
        YeuCauHoTro yc = yeuCauHoTroRepository.findById(maYeuCau)
                .orElseThrow(() -> AppException.notFound("Khong tim thay yeu cau: " + maYeuCau));

        if (!"CHUA_XU_LY".equals(yc.getTrangThai())) {
            throw AppException.badRequest("Yêu cầu không ở trạng thái có thể duyệt.");
        }

        DonDatTour don = yc.getDonDatTour();

        // Tạo giao dịch hoàn tiền chờ kế toán xác nhận; đơn vẫn giữ chỗ ở CHO_HUY.
        BigDecimal soTienHoan = tinhSoTienHoanTuNoiDung(yc.getNoiDung(), don.getTongTien());
        taoGiaoDichHoanTienNeuChuaCo(don, soTienHoan);

        // Cập nhật yêu cầu; đơn chỉ thành DA_HUY khi kế toán xác nhận hoàn tiền.
        don.setTrangThai("CHO_HUY");
        donDatTourRepository.save(don);

        yc.setTrangThai("DA_XU_LY");
        yeuCauHoTroRepository.save(yc);

        return toResponse(yc, soTienHoan, 0);
    }

    // ── UC33: SALES từ chối yêu cầu hủy ─────────────────────────────────
    @Transactional
    public YeuCauHoTroResponse tuChoiHuyTour(String maYeuCau, String maNhanVien, XuLyYeuCauRequest request) {
        YeuCauHoTro yc = yeuCauHoTroRepository.findById(maYeuCau)
                .orElseThrow(() -> AppException.notFound("Khong tim thay yeu cau: " + maYeuCau));

        if (!"CHUA_XU_LY".equals(yc.getTrangThai())) {
            throw AppException.badRequest("Yêu cầu không ở trạng thái có thể từ chối.");
        }

        // Kinh doanh từ chối yêu cầu hủy: đơn quay lại trạng thái đã xác nhận.
        DonDatTour don = yc.getDonDatTour();
        don.setTrangThai("DA_XAC_NHAN");
        donDatTourRepository.save(don);

        String noiDung = yc.getNoiDung() + " | TU_CHOI: " + (request.getGhiChu() != null ? request.getGhiChu() : "");
        yc.setNoiDung(noiDung);
        yc.setTrangThai("TU_CHOI");
        yeuCauHoTroRepository.save(yc);

        return toResponse(yc, BigDecimal.ZERO, 0);
    }

    // ── Danh sách yêu cầu (cho SALES xem) ───────────────────────────────
    public Page<YeuCauHoTroResponse> danhSachYeuCau(String loaiYeuCau, String trangThai, Pageable pageable) {
        return yeuCauHoTroRepository.timKiem(loaiYeuCau, trangThai, pageable)
                .map(yc -> toResponse(yc, BigDecimal.ZERO, 0));
    }

    // ── Helper: tính tỉ lệ hoàn ──────────────────────────────────────────
    private int tinhTiLeHoan(int soNgayConLai) {
        if (soNgayConLai > 15)
            return 90;
        if (soNgayConLai >= 7)
            return 70;
        if (soNgayConLai >= 3)
            return 50;
        return 0;
    }

    private BigDecimal tinhSoTienHoanTuNoiDung(String noiDung, BigDecimal tongTien) {
        // Parse tỉ lệ từ NoiDung (pattern: "Ti le hoan: XX%")
        try {
            int idx = noiDung.indexOf("Ti le hoan: ");
            if (idx >= 0) {
                String sub = noiDung.substring(idx + "Ti le hoan: ".length());
                int pct = Integer.parseInt(sub.substring(0, sub.indexOf('%')).trim());
                return tongTien.multiply(BigDecimal.valueOf(pct))
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            }
        } catch (Exception ignored) {
            /* fallback */ }
        return BigDecimal.ZERO;
    }

    private void taoGiaoDichHoanTienNeuChuaCo(DonDatTour don, BigDecimal soTienHoan) {
        if (giaoDichRepository.findPendingRefundByMaDatTour(don.getMaDatTour()).isPresent()) {
            return;
        }

        GiaoDich gdHoan = new GiaoDich();
        gdHoan.setMaGiaoDich("GD_HOAN_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        gdHoan.setDonDatTour(don);
        gdHoan.setLoaiGiaoDich("HOAN_TIEN");
        gdHoan.setPhuongThuc("CHUYEN_KHOAN");
        gdHoan.setSoTien(soTienHoan);
        gdHoan.setTrangThai("CHO_THANH_TOAN");
        gdHoan.setNgayThanhToan(LocalDateTime.now());
        giaoDichRepository.save(gdHoan);
    }

    private YeuCauHoTroResponse toResponse(YeuCauHoTro yc, BigDecimal soTienHoan, int tiLeHoan) {
        return YeuCauHoTroResponse.builder()
                .maYeuCau(yc.getMaYeuCauHoTro())
                .maDatTour(yc.getDonDatTour() != null ? yc.getDonDatTour().getMaDatTour() : null)
                .loaiYeuCau(yc.getLoaiYeuCau())
                .noiDung(yc.getNoiDung())
                .trangThai(yc.getTrangThai())
                .maNhanVienXuLy(yc.getNhanVienXuLy() != null ? yc.getNhanVienXuLy().getMaNhanVien() : null)
                .soTienHoan(soTienHoan)
                .tiLeHoan(tiLeHoan)
                .build();
    }
}
