package com.digitaltravel.erp.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.digitaltravel.erp.dto.requests.DatTourRequest;
import com.digitaltravel.erp.dto.requests.DichVuThemDatRequest;
import com.digitaltravel.erp.dto.requests.HanhDongXanhDatRequest;
import com.digitaltravel.erp.dto.requests.NguoiDongHanhRequest;
import com.digitaltravel.erp.dto.requests.XacNhanThanhToanOfflineRequest;
import com.digitaltravel.erp.dto.responses.ChiTietDatTourResponse;
import com.digitaltravel.erp.dto.responses.ChiTietDichVuResponse;
import com.digitaltravel.erp.dto.responses.DonDatTourResponse;
import com.digitaltravel.erp.entity.ChiTietDatTour;
import com.digitaltravel.erp.entity.ChiTietDichVu;
import com.digitaltravel.erp.entity.DatTourUuDai;
import com.digitaltravel.erp.entity.DichVuThem;
import com.digitaltravel.erp.entity.DonDatTour;
import com.digitaltravel.erp.entity.DsNguoiDongHanh;
import com.digitaltravel.erp.entity.GiaoDich;
import com.digitaltravel.erp.entity.HanhDongXanh;
import com.digitaltravel.erp.entity.HoChieuSo;
import com.digitaltravel.erp.entity.LichSuTour;
import com.digitaltravel.erp.entity.NangLucNhanVien;
import com.digitaltravel.erp.entity.PhanCongTour;
import com.digitaltravel.erp.entity.TourThucTe;
import com.digitaltravel.erp.exception.AppException;
import com.digitaltravel.erp.repository.ChiTietDatTourRepository;
import com.digitaltravel.erp.repository.ChiTietDichVuRepository;
import com.digitaltravel.erp.repository.DanhGiaKhRepository;
import com.digitaltravel.erp.repository.DatTourUuDaiRepository;
import com.digitaltravel.erp.repository.DichVuThemRepository;
import com.digitaltravel.erp.repository.DichVuTourThucTeRepository;
import com.digitaltravel.erp.repository.DonDatTourRepository;
import com.digitaltravel.erp.repository.DsNguoiDongHanhRepository;
import com.digitaltravel.erp.repository.GiaoDichRepository;
import com.digitaltravel.erp.repository.HanhDongXanhRepository;
import com.digitaltravel.erp.repository.HoChieuSoRepository;
import com.digitaltravel.erp.repository.LichSuTourRepository;
import com.digitaltravel.erp.repository.NangLucNhanVienRepository;
import com.digitaltravel.erp.repository.PhanCongTourRepository;
import com.digitaltravel.erp.repository.TourThucTeRepository;
import com.digitaltravel.erp.repository.YeuCauHoTroRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DatTourService {

    private static final String MA_GD_DA_BAO_CHUYEN_KHOAN = "KHXN:";

    private static final int TUOI_TOI_DA_TRE_EM = 11;

    private final DonDatTourRepository donDatTourRepository;
    private final ChiTietDatTourRepository chiTietDatTourRepository;
    private final ChiTietDichVuRepository chiTietDichVuRepository;
    private final DatTourUuDaiRepository datTourUuDaiRepository;
    private final TourThucTeRepository tourThucTeRepository;
    private final HoChieuSoRepository hoChieuSoRepository;
    private final DichVuThemRepository dichVuThemRepository;
    private final DichVuTourThucTeRepository dichVuTourThucTeRepository;
    private final DsNguoiDongHanhRepository dsNguoiDongHanhRepository;
    private final HanhDongXanhRepository hanhDongXanhRepository;
    private final GiaoDichRepository giaoDichRepository;
    private final LichSuTourRepository lichSuTourRepository;
    private final PhanCongTourRepository phanCongTourRepository;
    private final NangLucNhanVienRepository nangLucNhanVienRepository;
    private final YeuCauHoTroRepository yeuCauHoTroRepository;
    private final DanhGiaKhRepository danhGiaKhRepository;

    // ── Đặt tour ────────────────────────────────────────────────────────────
    @Transactional
    public DonDatTourResponse datTour(String maTaiKhoan, DatTourRequest request) {
        HoChieuSo khachHang = hoChieuSoRepository.findByMaTaiKhoan(maTaiKhoan)
                .orElseThrow(() -> AppException.notFound("Khách hàng chưa có hồ sơ. Vui lòng liên hệ hỗ trợ."));
        List<NguoiDongHanhRequest> dsNguoiDongHanh = request.getDanhSachNguoiDongHanh() != null
                ? request.getDanhSachNguoiDongHanh()
                : List.of();
        int soKhach = 1 + dsNguoiDongHanh.size();

        TourThucTe tour = kiemTraDieuKienDatTour(request.getMaTourThucTe(), soKhach);
        List<HanhDongXanhDaChon> dsHanhDongXanh = kiemTraHanhDongXanh(tour, request);
        List<ChiTietDichVu> dsDichVu = taoChiTietDichVuTam(tour, request);
        BigDecimal tongTien = tinhTongTienDonHang(tour, khachHang, dsNguoiDongHanh, dsDichVu);
        DonDatTour don = taoDonDatTour(khachHang, tour, tongTien, request.getGhiChu(), dsHanhDongXanh);
        List<ChiTietDatTour> dsChiTiet = new ArrayList<>();

        ChiTietDatTour chiTietNguoiDat = new ChiTietDatTour();
        chiTietNguoiDat.setMaChiTietDat("CTDT_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        chiTietNguoiDat.setDonDatTour(don);
        chiTietNguoiDat.setKhachHang(khachHang);
        chiTietNguoiDat.setLoaiKhach("NGUOI_DAT");
        chiTietNguoiDat.setGiaTaiThoiDiemDat(tinhGiaVeTheoNgaySinh(
                tour.getGiaHienHanh(),
                khachHang.getTaiKhoan().getNgaySinh(),
                tour));
        chiTietDatTourRepository.save(chiTietNguoiDat);
        dsChiTiet.add(chiTietNguoiDat);

        for (NguoiDongHanhRequest nguoiReq : dsNguoiDongHanh) {
            DsNguoiDongHanh nguoiDongHanh = new DsNguoiDongHanh();
            nguoiDongHanh.setMaNguoiDongHanh("NDH_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            nguoiDongHanh.setDonDatTour(don);
            nguoiDongHanh.setHoTen(nguoiReq.getHoTen());
            nguoiDongHanh.setCccd(nguoiReq.getCccd());
            nguoiDongHanh.setSoDienThoai(nguoiReq.getSoDienThoai());
            nguoiDongHanh.setNgaySinh(nguoiReq.getNgaySinh());
            nguoiDongHanh.setGioiTinh(nguoiReq.getGioiTinh());
            nguoiDongHanh.setGhiChu(nguoiReq.getGhiChu());
            dsNguoiDongHanhRepository.save(nguoiDongHanh);

            ChiTietDatTour chiTietNguoiDongHanh = new ChiTietDatTour();
            chiTietNguoiDongHanh.setMaChiTietDat("CTDT_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            chiTietNguoiDongHanh.setDonDatTour(don);
            chiTietNguoiDongHanh.setNguoiDongHanh(nguoiDongHanh);
            chiTietNguoiDongHanh.setLoaiKhach("NGUOI_DONG_HANH");
            chiTietNguoiDongHanh
                    .setGiaTaiThoiDiemDat(tinhGiaVeTheoNgaySinh(tour.getGiaHienHanh(), nguoiReq.getNgaySinh(), tour));
            chiTietDatTourRepository.save(chiTietNguoiDongHanh);
            dsChiTiet.add(chiTietNguoiDongHanh);
        }

        luuChiTietDichVu(don, dsDichVu);

        return toResponse(don, dsChiTiet, dsDichVu);
    }

    // ── Danh sách đơn đặt tour của tôi ─────────────────────────────────────
    @Transactional(readOnly = true)
    public Page<DonDatTourResponse> danhSachCuaToi(String maTaiKhoan, Pageable pageable) {
        HoChieuSo khachHang = hoChieuSoRepository.findByMaTaiKhoan(maTaiKhoan)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy hồ sơ khách hàng"));

        return donDatTourRepository.findByMaKhachHang(khachHang.getMaKhachHang(), pageable)
                .map(don -> {
                    List<ChiTietDatTour> dsChiTiet = chiTietDatTourRepository.findByMaDatTour(don.getMaDatTour());
                    List<ChiTietDichVu> dsDichVu = chiTietDichVuRepository.findByMaDatTour(don.getMaDatTour());
                    return toResponse(don, dsChiTiet, dsDichVu);
                });
    }

    // ── Chi tiết đơn đặt tour của tôi ──────────────────────────────────────
    @Transactional(readOnly = true)
    public DonDatTourResponse chiTietCuaToi(String maTaiKhoan, String maDatTour) {
        HoChieuSo khachHang = hoChieuSoRepository.findByMaTaiKhoan(maTaiKhoan)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy hồ sơ khách hàng"));

        DonDatTour don = donDatTourRepository.findByIdAndMaKhachHang(maDatTour, khachHang.getMaKhachHang())
                .orElseThrow(() -> AppException.notFound("Khong tim thay don dat tour: " + maDatTour));

        List<ChiTietDatTour> dsChiTiet = chiTietDatTourRepository.findByMaDatTour(maDatTour);
        List<ChiTietDichVu> dsDichVu = chiTietDichVuRepository.findByMaDatTour(maDatTour);
        return toResponse(don, dsChiTiet, dsDichVu);
    }

    // ── Hủy đặt tour ────────────────────────────────────────────────────────
    @Transactional
    public void huyDatTour(String maTaiKhoan, String maDatTour) {
        HoChieuSo khachHang = hoChieuSoRepository.findByMaTaiKhoan(maTaiKhoan)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy hồ sơ khách hàng"));

        DonDatTour don = donDatTourRepository.findByIdAndMaKhachHang(maDatTour, khachHang.getMaKhachHang())
                .orElseThrow(() -> AppException.notFound("Khong tim thay don dat tour: " + maDatTour));

        String trangThai = don.getTrangThai();
        if (!"CHO_XAC_NHAN".equals(trangThai)) {
            throw AppException.badRequest(
                    "Chi co the huy don o trang thai CHO_XAC_NHAN. Trang thai hien tai: " + trangThai);
        }
        boolean daBaoChuyenKhoan = giaoDichRepository.findByMaDatTour(maDatTour).stream()
                .anyMatch(gd -> "CHO_THANH_TOAN".equals(gd.getTrangThai())
                        && gd.getMaGDNH() != null
                        && gd.getMaGDNH().startsWith(MA_GD_DA_BAO_CHUYEN_KHOAN));
        if (daBaoChuyenKhoan) {
            throw AppException.badRequest("Don da bao chuyen khoan, khong the huy truc tiep.");
        }

        don.setTrangThai("DA_HUY");
        donDatTourRepository.save(don);
        // Không cần hoàn ChoConLai vì chưa trừ (chưa thanh toán)
    }

    // ── Nhân viên Sales: xem tất cả đơn ────────────────────────────────────
    @Transactional(readOnly = true)
    public Page<DonDatTourResponse> danhSachTatCa(String trangThai, String maTourThucTe, Pageable pageable) {
        return donDatTourRepository.timKiem(trangThai, maTourThucTe, pageable)
                .map(don -> {
                    List<ChiTietDatTour> dsChiTiet = chiTietDatTourRepository.findByMaDatTour(don.getMaDatTour());
                    List<ChiTietDichVu> dsDichVu = chiTietDichVuRepository.findByMaDatTour(don.getMaDatTour());
                    return toResponse(don, dsChiTiet, dsDichVu);
                });
    }

    // ── Nhân viên Sales: xác nhận đơn ──────────────────────────────────────
    @Transactional
    public DonDatTourResponse xacNhanDon(
            String maDatTour,
            String nguoiXacNhan,
            XacNhanThanhToanOfflineRequest request) {
        DonDatTour don = donDatTourRepository.findByIdWithDetails(maDatTour)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy đơn đặt tour: " + maDatTour));

        if (!"CHO_XAC_NHAN".equals(don.getTrangThai())) {
            throw AppException.badRequest(
                    "Chỉ có thể xác nhận đơn ở trạng thái 'Chờ xác nhận'. Trạng thái hiện tại: " + don.getTrangThai());
        }
        // Nhan vien co the xac nhan sau thoi han giu cho khi da doi soat duoc tien.
        boolean daThanhToan = giaoDichRepository.findThanhCongByMaDatTour(maDatTour).isPresent();
        GiaoDich giaoDichChoXacNhan = giaoDichRepository.findByMaDatTour(maDatTour).stream()
                .filter(gd -> "CHO_THANH_TOAN".equals(gd.getTrangThai()))
                .findFirst()
                .orElse(null);
        if (!daThanhToan && giaoDichChoXacNhan != null
                && (giaoDichChoXacNhan.getMaGDNH() == null
                        || !giaoDichChoXacNhan.getMaGDNH().startsWith(MA_GD_DA_BAO_CHUYEN_KHOAN))) {
            throw AppException.badRequest("Khach hang chua xac nhan da chuyen khoan.");
        }

        TourThucTe tour = tourThucTeRepository.findByIdForUpdate(don.getTourThucTe().getMaTourThucTe())
                .orElseThrow(() -> AppException.notFound("Không tìm thấy tour thực tế"));
        List<ChiTietDatTour> dsChiTiet = chiTietDatTourRepository.findByMaDatTour(maDatTour);
        int soKhach = dsChiTiet.size();
        if (!daThanhToan && tour.getChoConLai() < soKhach) {
            throw AppException.badRequest("Tour không còn đủ chỗ cho đơn đặt này");
        }

        List<ChiTietDichVu> dsDichVu = chiTietDichVuRepository.findByMaDatTour(maDatTour);
        don.setTongTien(tinhTongTienTuChiTiet(dsChiTiet, dsDichVu));

        // Neu giao dich QR dang cho doi soat thi xac nhan chinh giao dich do, tranh tao trung giao dich.
        if (!daThanhToan) {
            if (giaoDichChoXacNhan != null) {
                giaoDichChoXacNhan.setTrangThai("THANH_CONG");
                giaoDichChoXacNhan.setNgayThanhToan(LocalDateTime.now());
                giaoDichRepository.save(giaoDichChoXacNhan);
            } else {
                GiaoDich giaoDich = taoGiaoDichOffline(don, nguoiXacNhan, request);
                giaoDichRepository.save(giaoDich);
            }
        }

        don.setTrangThai("DA_XAC_NHAN");
        donDatTourRepository.save(don);

        tour.setChoConLai(tour.getChoConLai() - soKhach);
        tourThucTeRepository.save(tour);

        taoLichSuTourNeuChuaCo(don, tour, dsChiTiet);

        return toResponse(don, dsChiTiet, dsDichVu);
    }

    public DonDatTourResponse xacNhanDon(String maDatTour, String nguoiXacNhan) {
        return xacNhanDon(maDatTour, nguoiXacNhan, null);
    }

    @Transactional
    public DonDatTourResponse tuChoiThanhToan(String maDatTour) {
        DonDatTour don = donDatTourRepository.findByIdWithDetails(maDatTour)
                .orElseThrow(() -> AppException.notFound("Khong tim thay don dat tour: " + maDatTour));

        if (!"CHO_XAC_NHAN".equals(don.getTrangThai())) {
            throw AppException.badRequest(
                    "Chi co the tu choi thanh toan don o trang thai CHO_XAC_NHAN. Hien tai: " + don.getTrangThai());
        }

        GiaoDich giaoDich = giaoDichRepository.findByMaDatTour(maDatTour).stream()
                .filter(gd -> "CHO_THANH_TOAN".equals(gd.getTrangThai())
                        && gd.getMaGDNH() != null
                        && gd.getMaGDNH().startsWith(MA_GD_DA_BAO_CHUYEN_KHOAN))
                .findFirst()
                .orElseThrow(() -> AppException.badRequest("Khong co giao dich dang cho doi soat."));
        giaoDich.setTrangThai("THAT_BAI");
        giaoDichRepository.save(giaoDich);

        don.setTrangThai("THANH_TOAN_THAT_BAI");
        donDatTourRepository.save(don);

        List<ChiTietDatTour> dsChiTiet = chiTietDatTourRepository.findByMaDatTour(maDatTour);
        List<ChiTietDichVu> dsDichVu = chiTietDichVuRepository.findByMaDatTour(maDatTour);
        return toResponse(don, dsChiTiet, dsDichVu);
    }

    private GiaoDich taoGiaoDichOffline(
            DonDatTour don,
            String nguoiXacNhan,
            XacNhanThanhToanOfflineRequest request) {
        String maGdNganHang = request != null ? request.getMaGiaoDichNganHang() : null;
        if (maGdNganHang != null && !maGdNganHang.isBlank()
                && giaoDichRepository.findByMaGDNH(maGdNganHang).isPresent()) {
            throw AppException.badRequest("Mã giao dịch ngân hàng đã tồn tại: " + maGdNganHang);
        }

        String phuongThuc = request != null ? request.getPhuongThuc() : null;
        if (phuongThuc == null || phuongThuc.isBlank()) {
            phuongThuc = "CHUYEN_KHOAN";
        }
        if (maGdNganHang == null || maGdNganHang.isBlank()) {
            maGdNganHang = "OFFLINE_" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
        }

        GiaoDich giaoDich = new GiaoDich();
        giaoDich.setMaGiaoDich("GD_OFFLINE_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        giaoDich.setDonDatTour(don);
        giaoDich.setLoaiGiaoDich("THANH_TOAN");
        giaoDich.setPhuongThuc(phuongThuc);
        giaoDich.setSoTien(don.getTongTien());
        giaoDich.setMaGDNH(maGdNganHang);
        giaoDich.setTrangThai("THANH_CONG");
        giaoDich.setNgayThanhToan(LocalDateTime.now());
        return giaoDich;
    }

    private void taoLichSuTourNeuChuaCo(DonDatTour don, TourThucTe tour, List<ChiTietDatTour> dsChiTiet) {
        // 1. Map cho người đặt
        HoChieuSo kh = don.getKhachHang();
        taoChoMotKhachHang(kh, tour, dsChiTiet.stream()
                .filter(ct -> ct.getKhachHang() != null
                        && kh.getMaKhachHang().equals(ct.getKhachHang().getMaKhachHang()))
                .findFirst()
                .orElse(null));

        // 2. Map cho những người đồng hành (nếu họ có Hộ chiếu số)
        for (ChiTietDatTour ct : dsChiTiet) {
            if ("NGUOI_DONG_HANH".equals(ct.getLoaiKhach()) && ct.getNguoiDongHanh() != null) {
                String cccd = ct.getNguoiDongHanh().getCccd();
                String sdt = ct.getNguoiDongHanh().getSoDienThoai();
                if ((cccd != null && !cccd.isBlank()) || (sdt != null && !sdt.isBlank())) {
                    List<HoChieuSo> dsHcs = hoChieuSoRepository.findByCccdOrSoDienThoai(cccd, sdt);
                    for (HoChieuSo hcsDongHanh : dsHcs) {
                        if (!hcsDongHanh.getMaKhachHang().equals(kh.getMaKhachHang())) {
                            taoChoMotKhachHang(hcsDongHanh, tour, ct);
                        }
                    }
                }
            }
        }
    }

    private void taoChoMotKhachHang(HoChieuSo kh, TourThucTe tour, ChiTietDatTour ctDat) {
        boolean daCoLich = lichSuTourRepository
                .findByMaKhachHangAndMaTourThucTe(kh.getMaKhachHang(), tour.getMaTourThucTe())
                .isPresent();
        if (daCoLich) {
            return;
        }

        LichSuTour lichSu = new LichSuTour();
        lichSu.setMaLichSuTour("LST_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        lichSu.setKhachHang(kh);
        lichSu.setTourThucTe(tour);
        if (ctDat != null) {
            lichSu.setChiTietDatTour(ctDat);
        }
        lichSu.setNgayThamGia(tour.getNgayKhoiHanh() != null ? tour.getNgayKhoiHanh() : LocalDate.now());
        lichSuTourRepository.save(lichSu);
    }

    private TourThucTe kiemTraDieuKienDatTour(String maTourThucTe, int soKhach) {
        TourThucTe tour = tourThucTeRepository.findById(maTourThucTe)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy tour thực tế: " + maTourThucTe));
        if (!"MO_BAN".equals(tour.getTrangThai())) {
            throw AppException.badRequest("Tour không ở trạng thái \"Mở bán\", không thể đặt");
        }
        if (tour.getChoConLai() < soKhach) {
            throw AppException.badRequest("Tour đã hết chỗ");
        }
        return tour;
    }

    private List<HanhDongXanhDaChon> kiemTraHanhDongXanh(TourThucTe tour, DatTourRequest request) {
        Map<String, Long> soLuongTheoMa = new LinkedHashMap<>();

        if (request.getDanhSachHanhDongXanhChiTiet() != null) {
            for (HanhDongXanhDatRequest hdxReq : request.getDanhSachHanhDongXanhChiTiet()) {
                if (hdxReq.getMaHanhDongXanh() == null || hdxReq.getMaHanhDongXanh().isBlank()) {
                    continue;
                }
                long soLuong = hdxReq.getSoLuong() != null ? hdxReq.getSoLuong() : 1L;
                String ma = hdxReq.getMaHanhDongXanh().trim();
                soLuongTheoMa.merge(ma, soLuong, Long::sum);
            }
        }

        if (soLuongTheoMa.isEmpty() && request.getDanhSachHanhDongXanh() != null) {
            request.getDanhSachHanhDongXanh().stream()
                    .filter(ma -> ma != null && !ma.isBlank())
                    .map(String::trim)
                    .forEach(ma -> soLuongTheoMa.merge(ma, 1L, Long::sum));
        }

        if (soLuongTheoMa.isEmpty()) {
            return List.of();
        }
        List<HanhDongXanhDaChon> dsHopLe = new ArrayList<>();
        for (Map.Entry<String, Long> entry : soLuongTheoMa.entrySet()) {
            String maHanhDongXanh = entry.getKey();
            HanhDongXanh hdx = hanhDongXanhRepository.findById(maHanhDongXanh)
                    .orElseThrow(() -> AppException.notFound("Không tìm thấy hành động xanh: " + maHanhDongXanh));
            if (!hanhDongXanhRepository.existsAvailableForTour(maHanhDongXanh, tour.getMaTourThucTe())) {
                throw AppException.badRequest("Hành động xanh không thuộc tour thực tế: " + maHanhDongXanh);
            }
            dsHopLe.add(new HanhDongXanhDaChon(maHanhDongXanh, entry.getValue(),
                    hdx.getDiemCong() != null ? hdx.getDiemCong() : 0L));
        }
        return dsHopLe;
    }

    private List<ChiTietDichVu> taoChiTietDichVuTam(TourThucTe tour, DatTourRequest request) {
        List<ChiTietDichVu> dsDichVu = new ArrayList<>();
        if (request.getDanhSachDichVu() == null) {
            return dsDichVu;
        }
        for (DichVuThemDatRequest dvReq : request.getDanhSachDichVu()) {
            DichVuThem dv = dichVuThemRepository.findById(dvReq.getMaDichVuThem())
                    .orElseThrow(
                            () -> AppException.notFound("Không tìm thấy dịch vụ thêm: " + dvReq.getMaDichVuThem()));
            if (!dichVuTourThucTeRepository.existsByDichVuThem_MaDichVuThemAndTourThucTe_MaTourThucTe(
                    dv.getMaDichVuThem(), tour.getMaTourThucTe())) {
                throw AppException.badRequest("Dịch vụ thêm không thuộc tour thực tế: " + dv.getMaDichVuThem());
            }
            // Dịch vụ: ThanhTien = DonGia * SoLuong
            // TongTien = Sum(GiaVeKhach) + ThanhTien (dịch vụ)
            // Voucher: Giảm thẳng: TongTien = TongTien - SoTienGiam
            //          Giảm phần trăm: TongTien = TongTien * GiaTriGiam / 100
            BigDecimal thanhTien = dv.getDonGia().multiply(BigDecimal.valueOf(dvReq.getSoLuong()));

            ChiTietDichVu ct = new ChiTietDichVu();
            ct.setMaChiTietDichVu("CTDV_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            ct.setDichVuThem(dv);
            ct.setSoLuong(dvReq.getSoLuong());
            ct.setDonGia(dv.getDonGia());
            ct.setThanhTien(thanhTien);
            dsDichVu.add(ct);
        }
        return dsDichVu;
    }

    private BigDecimal tinhTongTienDonHang(TourThucTe tour,
            HoChieuSo khachHang,
            List<NguoiDongHanhRequest> dsNguoiDongHanh,
            List<ChiTietDichVu> dsDichVu) {
        BigDecimal tongTienTour = tinhGiaVeTheoNgaySinh(
                tour.getGiaHienHanh(),
                khachHang.getTaiKhoan().getNgaySinh(),
                tour);
        for (NguoiDongHanhRequest nguoiDongHanh : dsNguoiDongHanh) {
            tongTienTour = tongTienTour
                    .add(tinhGiaVeTheoNgaySinh(tour.getGiaHienHanh(), nguoiDongHanh.getNgaySinh(), tour));
        }
        BigDecimal tongTienDichVu = dsDichVu.stream()
                .map(ChiTietDichVu::getThanhTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return tongTienTour.add(tongTienDichVu);
    }

    private BigDecimal tinhGiaVeTheoNgaySinh(BigDecimal giaNguoiLon, LocalDate ngaySinh, TourThucTe tour) {
        if (laTreEm(ngaySinh, ngayTinhTuoi(tour))) {
            return giaNguoiLon.divide(BigDecimal.valueOf(2));
        }
        return giaNguoiLon;
    }

    private boolean laTreEm(LocalDate ngaySinh, LocalDate ngayTinhTuoi) {
        if (ngaySinh == null) {
            return false;
        }
        return Period.between(ngaySinh, ngayTinhTuoi).getYears() <= TUOI_TOI_DA_TRE_EM;
    }

    private Integer tinhTuoi(LocalDate ngaySinh, LocalDate ngayTinhTuoi) {
        return ngaySinh == null ? null : Period.between(ngaySinh, ngayTinhTuoi).getYears();
    }

    private LocalDate ngayTinhTuoi(TourThucTe tour) {
        return tour.getNgayKhoiHanh() != null ? tour.getNgayKhoiHanh() : LocalDate.now();
    }

    private DonDatTour taoDonDatTour(HoChieuSo khachHang,
            TourThucTe tour,
            BigDecimal tongTien,
            String ghiChu,
            List<HanhDongXanhDaChon> dsHanhDongXanh) {
        DonDatTour don = new DonDatTour();
        don.setMaDatTour("DDT_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        don.setTourThucTe(tour);
        don.setKhachHang(khachHang);
        don.setNgayDat(LocalDateTime.now());
        don.setTongTien(tongTien);
        don.setTrangThai("CHO_XAC_NHAN");
        don.setThoiGianHetHan(LocalDateTime.now().plusDays(2));
        don.setGhiChu(ghiChu);
        don.setHanhDongXanh(maHoaHanhDongXanh(dsHanhDongXanh));
        return donDatTourRepository.save(don);
    }

    private String maHoaHanhDongXanh(List<HanhDongXanhDaChon> dsHanhDongXanh) {
        return String.join(",", dsHanhDongXanh.stream()
                .map(item -> item.maHanhDongXanh() + ":" + item.soLuong() + ":" + item.diemCongTaiThoiDiemDat())
                .toList());
    }

    private void luuChiTietDichVu(DonDatTour don, List<ChiTietDichVu> dsDichVu) {
        for (ChiTietDichVu ctdv : dsDichVu) {
            ctdv.setDonDatTour(don);
            chiTietDichVuRepository.save(ctdv);
        }
    }

    // ── Mapper ────────────────────────────────────────────────────────────────
    private DonDatTourResponse toResponse(DonDatTour don,
            List<ChiTietDatTour> dsChiTiet,
            List<ChiTietDichVu> dsDichVu) {
        TourThucTe ttt = don.getTourThucTe();
        LocalDate ngayTinhTuoi = ngayTinhTuoi(ttt);
        int soTreEm = (int) dsChiTiet.stream()
                .filter(ct -> laTreEm(layNgaySinh(ct), ngayTinhTuoi))
                .count();
        int soNguoiLon = dsChiTiet.size() - soTreEm;
        PhanCongTour phanCong = phanCongTourRepository.findActiveByMaTour(ttt.getMaTourThucTe()).stream()
                .findFirst()
                .orElse(null);
        NangLucNhanVien nangLuc = phanCong != null
                ? nangLucNhanVienRepository.findByMaNhanVien(phanCong.getNhanVien().getMaNhanVien()).orElse(null)
                : null;
        String trangThaiKhieuNai = yeuCauHoTroRepository
                .findByMaDatTourAndLoaiYeuCau(don.getMaDatTour(), "KHIEU_NAI")
                .stream()
                .findFirst()
                .map(yc -> yc.getTrangThai())
                .orElse(null);
        List<DatTourUuDai> dsUuDai = datTourUuDaiRepository.findByMaDatTour(don.getMaDatTour());
        BigDecimal soTienUuDai = dsUuDai.stream()
                .map(DatTourUuDai::getSoTienUuDai)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        DatTourUuDai uuDaiDauTien = dsUuDai.stream().findFirst().orElse(null);
        List<GiaoDich> dsGiaoDich = giaoDichRepository.findByMaDatTour(don.getMaDatTour());
        boolean daBaoChuyenKhoan = dsGiaoDich.stream()
                .findFirst()
                .map(gd -> gd.getMaGDNH() != null
                        && gd.getMaGDNH().startsWith(MA_GD_DA_BAO_CHUYEN_KHOAN))
                .orElse(false);
        String maGiaoDich = dsGiaoDich.isEmpty() ? null : dsGiaoDich.getFirst().getMaGiaoDich();
        String phuongThuc = dsGiaoDich.isEmpty() ? null : dsGiaoDich.getFirst().getPhuongThuc();
        return DonDatTourResponse.builder()
                .maDatTour(don.getMaDatTour())
                .maTourThucTe(ttt.getMaTourThucTe())
                .tieuDeTour(ttt.getTourMau().getTieuDe())
                .ngayKhoiHanh(ttt.getNgayKhoiHanh())
                .giaHienHanh(ttt.getGiaHienHanh())
                .thoiLuong(ttt.getTourMau().getThoiLuong())
                .maKhachHang(don.getKhachHang().getMaKhachHang())
                .tenKhachHang(don.getKhachHang().getTaiKhoan().getHoTen())
                .ngayDat(don.getNgayDat())
                .tongTien(don.getTongTien())
                .tongTienGoc(don.getTongTien().add(soTienUuDai))
                .soTienUuDai(soTienUuDai)
                .maVoucher(uuDaiDauTien != null ? uuDaiDauTien.getVoucher().getMaVoucher() : null)
                .maCodeVoucher(uuDaiDauTien != null ? uuDaiDauTien.getVoucher().getMaCode() : null)
                .diemXanhDuKien(tinhDiemXanhDuKien(don.getHanhDongXanh()))
                .trangThai(don.getTrangThai())
                .daBaoChuyenKhoan(daBaoChuyenKhoan)
                .maGiaoDich(maGiaoDich)
                .phuongThuc(phuongThuc)
                .trangThaiTour(ttt.getTrangThai())
                .thoiGianHetHan(don.getThoiGianHetHan())
                .ghiChu(don.getGhiChu())
                .danhSachHanhDongXanh(parseHanhDongXanh(don.getHanhDongXanh()))
                .soNguoiLon(soNguoiLon)
                .soTreEm(soTreEm)
                .tenHuongDanVien(phanCong != null ? phanCong.getNhanVien().getTaiKhoan().getHoTen() : null)
                .soDienThoaiHuongDanVien(
                        phanCong != null ? phanCong.getNhanVien().getTaiKhoan().getSoDienThoai() : null)
                .danhGiaHuongDanVien(nangLuc != null ? nangLuc.getDanhGia() : null)
                .soDanhGiaHuongDanVien(nangLuc != null ? nangLuc.getSoDanhGia() : null)
                .daDanhGia(danhGiaKhRepository.existsByKhachHangAndTour(
                        don.getKhachHang().getMaKhachHang(),
                        don.getTourThucTe().getMaTourThucTe()))
                .daKhieuNai(trangThaiKhieuNai != null)
                .trangThaiKhieuNai(trangThaiKhieuNai)
                .chiTietKhach(dsChiTiet.stream().map(this::toChiTietResponse).toList())
                .chiTietDichVu(dsDichVu.stream().map(this::toDichVuResponse).toList())
                .build();
    }

    private BigDecimal tinhTongTienTuChiTiet(List<ChiTietDatTour> dsChiTiet, List<ChiTietDichVu> dsDichVu) {
        BigDecimal tongTienKhach = dsChiTiet.stream()
                .map(ChiTietDatTour::getGiaTaiThoiDiemDat)
                .filter(gia -> gia != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal tongTienDichVu = dsDichVu.stream()
                .map(ChiTietDichVu::getThanhTien)
                .filter(thanhTien -> thanhTien != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return tongTienKhach.add(tongTienDichVu);
    }

    private List<String> parseHanhDongXanh(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        return List.of(value.split(","));
    }

    private Long tinhDiemXanhDuKien(String value) {
        if (value == null || value.isBlank()) {
            return 0L;
        }
        long tongDiem = 0L;
        for (String item : value.split(",")) {
            if (item == null || item.isBlank()) {
                continue;
            }
            String[] parts = item.split(":");
            long soLuong = 1L;
            if (parts.length > 1) {
                try {
                    soLuong = Long.parseLong(parts[1].trim());
                } catch (NumberFormatException ignored) {
                    soLuong = 1L;
                }
            }
            if (parts.length > 2) {
                try {
                    tongDiem += Long.parseLong(parts[2].trim()) * soLuong;
                    continue;
                } catch (NumberFormatException ignored) {
                    // Fallback for legacy malformed data below.
                }
            }
            String maHanhDongXanh = parts[0].trim();
            final long finalSoLuong = soLuong;
            tongDiem += hanhDongXanhRepository.findById(maHanhDongXanh)
                    .map(hdx -> (hdx.getDiemCong() != null ? hdx.getDiemCong() : 0L) * finalSoLuong)
                    .orElse(0L);
        }
        return tongDiem;
    }

    private ChiTietDatTourResponse toChiTietResponse(ChiTietDatTour ct) {
        HoChieuSo khachHang = ct.getKhachHang();
        DsNguoiDongHanh nguoiDongHanh = ct.getNguoiDongHanh();
        LocalDate ngaySinh = layNgaySinh(ct);
        LocalDate ngayTinhTuoi = ngayTinhTuoi(ct.getDonDatTour().getTourThucTe());
        boolean treEm = laTreEm(ngaySinh, ngayTinhTuoi);

        return ChiTietDatTourResponse.builder()
                .maChiTietDat(ct.getMaChiTietDat())
                .loaiKhach(ct.getLoaiKhach())
                .maKhachHang(khachHang != null ? khachHang.getMaKhachHang() : null)
                .maNguoiDongHanh(nguoiDongHanh != null ? nguoiDongHanh.getMaNguoiDongHanh() : null)
                .hoTen(khachHang != null ? khachHang.getTaiKhoan().getHoTen() : nguoiDongHanh.getHoTen())
                .cccd(khachHang != null ? khachHang.getTaiKhoan().getCccd() : nguoiDongHanh.getCccd())
                .soDienThoai(
                        khachHang != null ? khachHang.getTaiKhoan().getSoDienThoai() : nguoiDongHanh.getSoDienThoai())
                .ngaySinh(ngaySinh)
                .tuoi(tinhTuoi(ngaySinh, ngayTinhTuoi))
                .nhomTuoi(treEm ? "TRE_EM" : "NGUOI_LON")
                .gioiTinh(nguoiDongHanh != null ? nguoiDongHanh.getGioiTinh() : null)
                .ghiChu(nguoiDongHanh != null ? nguoiDongHanh.getGhiChu() : null)
                .ghiChuYTe(khachHang != null
                        ? gopGhiChuYTeVaDiUng(khachHang.getGhiChuYTe(), khachHang.getDiUng())
                        : nguoiDongHanh.getGhiChu())
                .giaTaiThoiDiemDat(ct.getGiaTaiThoiDiemDat())
                .build();
    }

    private String gopGhiChuYTeVaDiUng(String ghiChuYTe, String diUng) {
        List<String> parts = new ArrayList<>();
        if (ghiChuYTe != null && !ghiChuYTe.isBlank()) {
            parts.add(ghiChuYTe);
        }
        if (diUng != null && !diUng.isBlank()) {
            String allergy = diUng.trim();
            parts.add("Dị ứng " + Character.toLowerCase(allergy.charAt(0)) + allergy.substring(1));
        }
        return String.join(" | ", parts);
    }

    private LocalDate layNgaySinh(ChiTietDatTour ct) {
        if (ct.getNguoiDongHanh() != null) {
            return ct.getNguoiDongHanh().getNgaySinh();
        }
        if (ct.getKhachHang() != null) {
            return ct.getKhachHang().getTaiKhoan().getNgaySinh();
        }
        return null;
    }

    private ChiTietDichVuResponse toDichVuResponse(ChiTietDichVu cv) {
        return ChiTietDichVuResponse.builder()
                .maChiTietDichVu(cv.getMaChiTietDichVu())
                .maDichVuThem(cv.getDichVuThem().getMaDichVuThem())
                .tenDichVu(cv.getDichVuThem().getTen())
                .donViTinh(cv.getDichVuThem().getDonViTinh())
                .soLuong(cv.getSoLuong())
                .donGia(cv.getDonGia())
                .thanhTien(cv.getThanhTien())
                .build();
    }

    private record HanhDongXanhDaChon(String maHanhDongXanh, long soLuong, long diemCongTaiThoiDiemDat) {
    }
}
