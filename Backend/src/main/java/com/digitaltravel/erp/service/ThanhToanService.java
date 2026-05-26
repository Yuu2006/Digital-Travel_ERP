package com.digitaltravel.erp.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.digitaltravel.erp.dto.requests.KhoiTaoThanhToanRequest;
import com.digitaltravel.erp.dto.responses.ThanhToanResponse;
import com.digitaltravel.erp.entity.DonDatTour;
import com.digitaltravel.erp.entity.GiaoDich;
import com.digitaltravel.erp.entity.HoChieuSo;
import com.digitaltravel.erp.entity.LichSuTour;
import com.digitaltravel.erp.entity.TourThucTe;
import com.digitaltravel.erp.exception.AppException;
import com.digitaltravel.erp.repository.ChiTietDatTourRepository;
import com.digitaltravel.erp.repository.DonDatTourRepository;
import com.digitaltravel.erp.repository.GiaoDichRepository;
import com.digitaltravel.erp.repository.LichSuTourRepository;
import com.digitaltravel.erp.repository.TourThucTeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ThanhToanService {

    private static final Logger log = LoggerFactory.getLogger(ThanhToanService.class);
    private static final long THOI_HAN_QR_PHUT = 5;
    private static final String MA_GD_DA_BAO_CHUYEN_KHOAN = "KHXN:";

    private final DonDatTourRepository donDatTourRepository;
    private final GiaoDichRepository giaoDichRepository;
    private final LichSuTourRepository lichSuTourRepository;
    private final TourThucTeRepository tourThucTeRepository;
    private final ChiTietDatTourRepository chiTietDatTourRepository;

    @Value("${app.payment.mock-enabled:false}")
    private boolean mockEnabled;

    // ── Khởi tạo thanh toán ────────────────────────────────────────────────
    @Transactional
    public ThanhToanResponse khoiTaoThanhToan(String maTaiKhoan, KhoiTaoThanhToanRequest request) {
        DonDatTour don = donDatTourRepository.findByIdWithDetails(request.getMaDonDatTour())
                .orElseThrow(() -> AppException.notFound("Khong tim thay don dat tour: " + request.getMaDonDatTour()));

        // Kiểm tra chủ sở hữu đơn
        if (!don.getKhachHang().getTaiKhoan().getMaTaiKhoan().equals(maTaiKhoan)) {
            throw AppException.forbidden("Bạn không có quyền thanh toán đơn này");
        }

        // Chi khoi tao QR cho don chua bao da chuyen khoan va chua het han.
        if (!"CHO_XAC_NHAN".equals(don.getTrangThai())) {
            throw AppException.badRequest(
                    "Don chi co the khoi tao thanh toan o trang thai CHO_XAC_NHAN. Hien tai: " + don.getTrangThai());
        }
        if (don.getThoiGianHetHan() != null && don.getThoiGianHetHan().isBefore(LocalDateTime.now())) {
            throw AppException.badRequest("Đơn đặt tour đã hết hạn giữ chỗ. Vui lòng đặt lại.");
        }

        // Kiểm tra xem đã có giao dịch thành công chưa (idempotency)
        if (giaoDichRepository.findThanhCongByMaDatTour(request.getMaDonDatTour()).isPresent()) {
            throw AppException.badRequest("Đơn này đã được thanh toán thành công trước đó.");
        }

        String phuongThuc = (request.getPhuongThuc() == null || request.getPhuongThuc().isBlank())
                ? "MOMO_WALLET" : request.getPhuongThuc();

        if (request.isMock()) {
            if (!mockEnabled) {
                throw AppException.forbidden("Thanh toán mock đang bị tắt trên môi trường hiện tại");
            }
            return xuLyMock(don, phuongThuc);
        }
        // TODO: Tích hợp MoMo thực — xem README phần UC29
        // Hiện tại trả về lỗi hướng dẫn dùng mock
        return taoGiaoDichChoThanhToan(don, phuongThuc);
    }

    // ── Mock: bypass MoMo, xác nhận thanh toán ngay ──────────────────────
    private ThanhToanResponse taoGiaoDichChoThanhToan(DonDatTour don, String phuongThuc) {
        GiaoDich giaoDich = giaoDichRepository.findByMaDatTour(don.getMaDatTour()).stream()
                .filter(gd -> "CHO_THANH_TOAN".equals(gd.getTrangThai()))
                .findFirst()
                .orElseGet(() -> {
                    GiaoDich gd = new GiaoDich();
                    gd.setMaGiaoDich("GD_QR_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
                    gd.setDonDatTour(don);
                    gd.setLoaiGiaoDich("THANH_TOAN");
                    gd.setPhuongThuc(phuongThuc);
                    gd.setSoTien(don.getTongTien());
                    gd.setMaGDNH("QR_" + don.getMaDatTour());
                    gd.setTrangThai("CHO_THANH_TOAN");
                    gd.setNgayThanhToan(LocalDateTime.now());
                    return giaoDichRepository.save(gd);
                });

        return ThanhToanResponse.builder()
                .maGiaoDich(giaoDich.getMaGiaoDich())
                .maDatTour(don.getMaDatTour())
                .trangThai(giaoDich.getTrangThai())
                .phuongThuc(giaoDich.getPhuongThuc())
                .soTien(giaoDich.getSoTien())
                .ngayThanhToan(giaoDich.getNgayThanhToan())
                .thongBao("Da tao giao dich cho thanh toan QR. Don se duoc xac nhan sau khi doi soat.")
                .build();
    }

    @Transactional
    public void hetHanThanhToanQr(String maTaiKhoan, String maDatTour) {
        DonDatTour don = donDatTourRepository.findByIdWithDetails(maDatTour)
                .orElseThrow(() -> AppException.notFound("Khong tim thay don dat tour: " + maDatTour));

        if (!don.getKhachHang().getTaiKhoan().getMaTaiKhoan().equals(maTaiKhoan)) {
            throw AppException.forbidden("Bạn không có quyền cập nhật đơn này");
        }

        if ("HET_HAN_GIU_CHO".equals(don.getTrangThai())) {
            return;
        }
        if (!"CHO_XAC_NHAN".equals(don.getTrangThai())) {
            throw AppException.badRequest("Đơn không ở trạng thái chờ xác nhận.");
        }

        GiaoDich giaoDich = giaoDichRepository.findByMaDatTour(maDatTour).stream()
                .filter(gd -> "CHO_THANH_TOAN".equals(gd.getTrangThai()))
                .findFirst()
                .orElseThrow(() -> AppException.badRequest("Không có giao dịch QR đang chờ thanh toán."));
        if (giaoDich.getMaGDNH() != null
                && giaoDich.getMaGDNH().startsWith(MA_GD_DA_BAO_CHUYEN_KHOAN)) {
            return;
        }

        if (giaoDich.getNgayThanhToan() == null
                || giaoDich.getNgayThanhToan().plusMinutes(THOI_HAN_QR_PHUT).isAfter(LocalDateTime.now())) {
            throw AppException.badRequest("Mã thanh toán QR vẫn còn hiệu lực.");
        }

        giaoDich.setTrangThai("THAT_BAI");
        giaoDichRepository.save(giaoDich);
        don.setTrangThai("HET_HAN_GIU_CHO");
        donDatTourRepository.save(don);
        log.info("[ThanhToan] Don {} het han giu cho do QR het han", maDatTour);
    }

    @Transactional
    public void xacNhanDaChuyenKhoan(String maTaiKhoan, String maDatTour) {
        DonDatTour don = donDatTourRepository.findByIdWithDetails(maDatTour)
                .orElseThrow(() -> AppException.notFound("Khong tim thay don dat tour: " + maDatTour));

        if (!don.getKhachHang().getTaiKhoan().getMaTaiKhoan().equals(maTaiKhoan)) {
            throw AppException.forbidden("Bạn không có quyền cập nhật đơn này");
        }

        GiaoDich giaoDich = giaoDichRepository.findByMaDatTour(maDatTour).stream()
                .filter(gd -> "CHO_THANH_TOAN".equals(gd.getTrangThai()))
                .findFirst()
                .orElseThrow(() -> AppException.badRequest("Không có giao dịch QR đang chờ thanh toán."));

        if (giaoDich.getMaGDNH() != null
                && giaoDich.getMaGDNH().startsWith(MA_GD_DA_BAO_CHUYEN_KHOAN)) {
            return;
        }
        if (!"CHO_XAC_NHAN".equals(don.getTrangThai())) {
            throw AppException.badRequest("Đơn không ở trạng thái chờ xác nhận.");
        }

        if (giaoDich.getNgayThanhToan() == null
                || giaoDich.getNgayThanhToan().plusMinutes(THOI_HAN_QR_PHUT).isBefore(LocalDateTime.now())) {
            throw AppException.badRequest("Mã thanh toán QR đã hết hiệu lực.");
        }

        giaoDich.setMaGDNH(MA_GD_DA_BAO_CHUYEN_KHOAN + giaoDich.getMaGDNH());
        giaoDichRepository.save(giaoDich);
        log.info("[ThanhToan] Don {} cho xac nhan sau khi khach bao da chuyen khoan", maDatTour);
    }

    @Transactional
    protected ThanhToanResponse xuLyMock(DonDatTour don, String phuongThuc) {
        LocalDateTime now = LocalDateTime.now();
        String maGiaoDich = "GD_MOCK_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        TourThucTe tour = tourThucTeRepository.findByIdForUpdate(don.getTourThucTe().getMaTourThucTe())
                .orElseThrow(() -> AppException.notFound("Không tìm thấy tour thực tế"));
        int soKhach = (int) chiTietDatTourRepository.countByDonDatTour_MaDatTour(don.getMaDatTour());
        if (tour.getChoConLai() < soKhach) {
            throw AppException.badRequest("Tour không còn đủ chỗ cho đơn đặt này");
        }

        // Tạo bản ghi giao dịch
        GiaoDich gd = new GiaoDich();
        gd.setMaGiaoDich(maGiaoDich);
        gd.setDonDatTour(don);
        gd.setLoaiGiaoDich("THANH_TOAN");
        gd.setPhuongThuc(phuongThuc);
        gd.setSoTien(don.getTongTien());
        gd.setMaGDNH("MOCK_" + UUID.randomUUID().toString().substring(0, 12).toUpperCase());
        gd.setTrangThai("THANH_CONG");
        gd.setNgayThanhToan(now);
        giaoDichRepository.save(gd);

        // Cập nhật đơn đặt tour
        don.setTrangThai("DA_XAC_NHAN");
        donDatTourRepository.save(don);

        // Trừ số chỗ còn lại
        int choMoi = tour.getChoConLai() - soKhach;
        tour.setChoConLai(choMoi);
        tourThucTeRepository.save(tour);

        // Tạo LichSuTour (nếu chưa có — UNIQUE constraint)
        HoChieuSo kh = don.getKhachHang();
        boolean daCoLich = lichSuTourRepository
                .findByMaKhachHangAndMaTourThucTe(kh.getMaKhachHang(), tour.getMaTourThucTe())
                .isPresent();
        if (!daCoLich) {
            LichSuTour lst = new LichSuTour();
            lst.setMaLichSuTour("LST_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            lst.setKhachHang(kh);
            lst.setTourThucTe(tour);
            lst.setNgayThamGia(tour.getNgayKhoiHanh() != null ? tour.getNgayKhoiHanh() : LocalDate.now());
            lichSuTourRepository.save(lst);
        }

        log.info("[ThanhToan-Mock] Don {} thanh toan thanh cong. GD: {}", don.getMaDatTour(), maGiaoDich);

        return ThanhToanResponse.builder()
                .maGiaoDich(maGiaoDich)
                .maDatTour(don.getMaDatTour())
                .trangThai("THANH_CONG")
                .phuongThuc(phuongThuc)
                .soTien(don.getTongTien())
                .ngayThanhToan(now)
                .thongBao("[MOCK] Thanh toan thanh cong. Don da duoc xac nhan.")
                .build();
    }

    // ── Kiểm tra kết quả giao dịch ────────────────────────────────────────
    public ThanhToanResponse ketQua(String maTaiKhoan, String maDatTour) {
        DonDatTour don = donDatTourRepository.findByIdWithDetails(maDatTour)
                .orElseThrow(() -> AppException.notFound("Khong tim thay don dat tour: " + maDatTour));

        if (!don.getKhachHang().getTaiKhoan().getMaTaiKhoan().equals(maTaiKhoan)) {
            throw AppException.forbidden("Bạn không có quyền xem đơn này");
        }

        GiaoDich gd = giaoDichRepository.findByMaDatTour(maDatTour).stream()
                .findFirst()
                .orElseThrow(() -> AppException.notFound("Chua co giao dich nao cho don: " + maDatTour));

        return ThanhToanResponse.builder()
                .maGiaoDich(gd.getMaGiaoDich())
                .maDatTour(maDatTour)
                .trangThai(gd.getTrangThai())
                .phuongThuc(gd.getPhuongThuc())
                .soTien(gd.getSoTien())
                .ngayThanhToan(gd.getNgayThanhToan())
                .thongBao("Trang thai giao dich: " + gd.getTrangThai())
                .build();
    }
}
