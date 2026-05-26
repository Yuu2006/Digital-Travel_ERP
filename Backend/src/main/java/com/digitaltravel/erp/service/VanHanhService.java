package com.digitaltravel.erp.service;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.digitaltravel.erp.config.VaiTroConst;
import com.digitaltravel.erp.dto.requests.BaoCaoSuCoRequest;
import com.digitaltravel.erp.dto.requests.DiemDanhRequest;
import com.digitaltravel.erp.dto.requests.GhiNhanHanhDongRequest;
import com.digitaltravel.erp.dto.requests.KhaiChiPhiRequest;
import com.digitaltravel.erp.dto.responses.CanhBaoChiPhiResponse;
import com.digitaltravel.erp.dto.responses.ChiPhiThucTeResponse;
import com.digitaltravel.erp.dto.responses.DiemDanhResponse;
import com.digitaltravel.erp.dto.responses.HanhDongResponse;
import com.digitaltravel.erp.dto.responses.LichTrinhResponse;
import com.digitaltravel.erp.dto.responses.NhatKySuCoResponse;
import com.digitaltravel.erp.dto.responses.ThanhVienDoanResponse;
import com.digitaltravel.erp.entity.ChiPhiThucTe;
import com.digitaltravel.erp.entity.DiemDanh;
import com.digitaltravel.erp.entity.DsNguoiDongHanh;
import com.digitaltravel.erp.entity.HanhDong;
import com.digitaltravel.erp.entity.HanhDongXanh;
import com.digitaltravel.erp.entity.HoChieuSo;
import com.digitaltravel.erp.entity.NhanVien;
import com.digitaltravel.erp.entity.NhatKySuCo;
import com.digitaltravel.erp.entity.TourThucTe;
import com.digitaltravel.erp.exception.AppException;
import com.digitaltravel.erp.repository.ChiPhiThucTeRepository;
import com.digitaltravel.erp.repository.DiemDanhRepository;
import com.digitaltravel.erp.repository.DonDatTourRepository;
import com.digitaltravel.erp.repository.DsNguoiDongHanhRepository;
import com.digitaltravel.erp.repository.HanhDongRepository;
import com.digitaltravel.erp.repository.HanhDongXanhRepository;
import com.digitaltravel.erp.repository.HoChieuSoRepository;
import com.digitaltravel.erp.repository.LichTrinhTourRepository;
import com.digitaltravel.erp.repository.NhanVienRepository;
import com.digitaltravel.erp.repository.NhatKySuCoRepository;
import com.digitaltravel.erp.repository.PhanCongTourRepository;
import com.digitaltravel.erp.repository.TourThucTeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VanHanhService {

    private final TourThucTeRepository tourThucTeRepository;
    private final NhanVienRepository nhanVienRepository;
    private final HoChieuSoRepository hoChieuSoRepository;
    private final DiemDanhRepository diemDanhRepository;
    private final DsNguoiDongHanhRepository dsNguoiDongHanhRepository;
    private final HanhDongRepository hanhDongRepository;
    private final HanhDongXanhRepository hanhDongXanhRepository;
    private final NhatKySuCoRepository nhatKySuCoRepository;
    private final ChiPhiThucTeRepository chiPhiThucTeRepository;
    private final PhanCongTourRepository phanCongTourRepository;
    private final DonDatTourRepository donDatTourRepository;
    private final LichTrinhTourRepository lichTrinhTourRepository;

    @Transactional(readOnly = true)
    public List<LichTrinhResponse> lichTrinhTour(String maTour, String maTaiKhoan, String maVaiTro) {
        kiemTraQuyenXemLichTrinhTour(maTour, maTaiKhoan, maVaiTro);
        TourThucTe tour = tourThucTeRepository.findById(maTour)
                .orElseThrow(() -> AppException.notFound("Khong tim thay tour: " + maTour));

        return lichTrinhTourRepository.findByMaTourMau(tour.getTourMau().getMaTourMau()).stream()
                .map(item -> LichTrinhResponse.builder()
                        .maLichTrinhTour(item.getMaLichTrinhTour())
                        .ngayThu(item.getNgayThu())
                        .hoatDong(item.getHoatDong())
                        .moTa(item.getMoTa())
                        .thucDon(item.getThucDon())
                        .build())
                .toList();
    }

    // ── UC42: Xem danh sách đoàn ─────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<ThanhVienDoanResponse> danhSachDoan(String maTour, String maTaiKhoan, String maVaiTro) {
        kiemTraQuyenVanHanhTour(maTour, maTaiKhoan, maVaiTro);

        List<DiemDanh> ddList = diemDanhRepository.findByMaTour(maTour);
        java.util.Map<String, String> khStatus = new java.util.HashMap<>();
        java.util.Map<String, String> ndhStatus = new java.util.HashMap<>();
        for (DiemDanh dd : ddList) {
            if (dd.getKhachHang() != null && !khStatus.containsKey(dd.getKhachHang().getMaKhachHang())) {
                khStatus.put(dd.getKhachHang().getMaKhachHang(), dd.getTrangThai());
            }
            if (dd.getNguoiDongHanh() != null && !ndhStatus.containsKey(dd.getNguoiDongHanh().getMaNguoiDongHanh())) {
                ndhStatus.put(dd.getNguoiDongHanh().getMaNguoiDongHanh(), dd.getTrangThai());
            }
        }

        List<ThanhVienDoanResponse> result = new ArrayList<>();
        donDatTourRepository.timKiem("DA_XAC_NHAN", maTour, Pageable.unpaged()).forEach(don -> {
            HoChieuSo kh = don.getKhachHang();
            String hoTen = kh.getTaiKhoan() != null ? kh.getTaiKhoan().getHoTen() : "";
            result.add(ThanhVienDoanResponse.builder()
                    .maDatTour(don.getMaDatTour())
                    .loaiKhach("NGUOI_DAT")
                    .maKhachHang(kh.getMaKhachHang())
                    .hoTenKhachHang(hoTen)
                    .soDienThoai(kh.getTaiKhoan() != null ? kh.getTaiKhoan().getSoDienThoai() : null)
                    .hangThanhVien(kh.getHangThanhVien())
                    .ghiChuYTe(kh.getGhiChuYTe())
                    .diUng(kh.getDiUng())
                    .ghiChuDatTour(don.getGhiChu())
                    .hanhDongXanh(don.getHanhDongXanh())
                    .diemXanh(kh.getDiemXanh())
                    .trangThai(khStatus.getOrDefault(kh.getMaKhachHang(), "CHUA_DIEM_DANH"))
                    .build());

            dsNguoiDongHanhRepository.findByDonDatTour_MaDatTour(don.getMaDatTour()).forEach(ndh -> {
                result.add(ThanhVienDoanResponse.builder()
                        .maDatTour(don.getMaDatTour())
                        .loaiKhach("NGUOI_DONG_HANH")
                        .maNguoiDongHanh(ndh.getMaNguoiDongHanh())
                        .hoTenKhachHang(ndh.getHoTen())
                        .soDienThoai(ndh.getSoDienThoai())
                        .hangThanhVien("THANH_VIEN")
                        .ghiChuYTe(ndh.getGhiChu())
                        .ghiChuDatTour(don.getGhiChu())
                        .hanhDongXanh(don.getHanhDongXanh())
                        .diemXanh(0L)
                        .trangThai(ndhStatus.getOrDefault(ndh.getMaNguoiDongHanh(), "CHUA_DIEM_DANH"))
                        .build());
            });
        });
        return result;
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

    // ── UC43: Điểm danh khách ─────────────────────────────────────────────
    @Transactional
    public DiemDanhResponse diemDanh(String maTour, DiemDanhRequest req, String maTaiKhoan, String maVaiTro) {
        kiemTraQuyenVanHanhTour(maTour, maTaiKhoan, maVaiTro);
        TourThucTe tour = getActiveTour(maTour);
        HoChieuSo kh = null;
        DsNguoiDongHanh nguoiDongHanh = null;
        boolean coKhachHang = req.getMaKhachHang() != null && !req.getMaKhachHang().isBlank();
        boolean coNguoiDongHanh = req.getMaNguoiDongHanh() != null && !req.getMaNguoiDongHanh().isBlank();

        if (coKhachHang == coNguoiDongHanh) {
            throw AppException.badRequest("Chỉ được gửi một trong hai trường: mã khách hàng hoặc mã người đồng hành");
        }

        if (coKhachHang) {
            kh = hoChieuSoRepository.findById(req.getMaKhachHang())
                    .orElseThrow(() -> AppException.notFound("Khong tim thay khach hang: " + req.getMaKhachHang()));
            kiemTraKhachThuocTourDaXacNhan(maTour, kh.getMaKhachHang());
        } else {
            nguoiDongHanh = dsNguoiDongHanhRepository.findById(req.getMaNguoiDongHanh())
                    .orElseThrow(
                            () -> AppException.notFound("Khong tim thay nguoi dong hanh: " + req.getMaNguoiDongHanh()));
            if (!nguoiDongHanh.getDonDatTour().getTourThucTe().getMaTourThucTe().equals(maTour)) {
                throw AppException.badRequest("Người đồng hành không thuộc tour này");
            }
        }
        NhanVien hdv = getHdv(maTaiKhoan);

        DiemDanh dd = new DiemDanh();
        dd.setMaDiemDanh("DD_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        dd.setTourThucTe(tour);
        dd.setKhachHang(kh);
        dd.setNguoiDongHanh(nguoiDongHanh);
        dd.setLoaiKhach(coKhachHang ? "NGUOI_DAT" : "NGUOI_DONG_HANH");
        dd.setNhanVien(hdv);
        dd.setThoiGian(LocalDateTime.now());
        dd.setDiaDiem(req.getDiaDiem());
        dd.setTrangThai(chuanHoaTrangThaiDiemDanh(req.getTrangThai()));
        diemDanhRepository.save(dd);

        return toDiemDanhResponse(dd);
    }

    // ── UC44: Ghi nhận hành động xanh ────────────────────────────────────
    @Transactional
    public HanhDongResponse ghiNhanHanhDong(String maTour, GhiNhanHanhDongRequest req, String maTaiKhoan,
            String maVaiTro) {
        kiemTraQuyenVanHanhTour(maTour, maTaiKhoan, maVaiTro);
        TourThucTe tour = getActiveTour(maTour);
        HoChieuSo kh = hoChieuSoRepository.findById(req.getMaKhachHang())
                .orElseThrow(() -> AppException.notFound("Khong tim thay khach hang: " + req.getMaKhachHang()));
        kiemTraKhachThuocTourDaXacNhan(maTour, kh.getMaKhachHang());
        HanhDongXanh hdx = hanhDongXanhRepository.findById(req.getMaHanhDongXanh())
                .orElseThrow(() -> AppException.notFound("Khong tim thay hanh dong xanh: " + req.getMaHanhDongXanh()));
        if (!hanhDongXanhRepository.existsAvailableForTour(req.getMaHanhDongXanh(), maTour)) {
            throw AppException.badRequest("Hanh dong xanh khong thuoc tour thuc te: " + req.getMaHanhDongXanh());
        }
        if (!hanhDongRepository.findByTourAndKhachAndHanhDong(
                maTour, kh.getMaKhachHang(), req.getMaHanhDongXanh()).isEmpty()) {
            throw AppException.badRequest("Hành động xanh này đã được ghi nhận cho khách hàng trong tour.");
        }
        NhanVien hdv = getHdv(maTaiKhoan);

        HanhDong hd = new HanhDong();
        hd.setMaGhiNhanHanhDong("HD_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        hd.setTourThucTe(tour);
        hd.setKhachHang(kh);
        hd.setHanhDongXanh(hdx);
        hd.setNhanVienXacMinh(hdv);
        hd.setThoiGian(LocalDateTime.now());
        hd.setMinhChung(req.getMinhChung());
        hanhDongRepository.save(hd);

        // Cộng điểm xanh cho KH
        kh.setDiemXanh(kh.getDiemXanh() + hdx.getDiemCong());
        kiemTraNangHang(kh);
        hoChieuSoRepository.save(kh);

        return toHanhDongResponse(hd, hdx);
    }

    // ── UC45: Báo cáo sự cố ──────────────────────────────────────────────
    @Transactional
    public NhatKySuCoResponse baoCaoSuCo(String maTour, BaoCaoSuCoRequest req, String maTaiKhoan, String maVaiTro) {
        kiemTraQuyenVanHanhTour(maTour, maTaiKhoan, maVaiTro);
        TourThucTe tour = kiemTraHopLeDuLieuSuCo(maTour, req);
        NhanVien hdv = getHdv(maTaiKhoan);

        NhatKySuCo sc = xuLyTaoSuCoMoi(tour, hdv, req);
        guiCanhBaoDenQuanLyNeuCan(sc);

        return toSuCoResponse(sc);
    }

    private TourThucTe kiemTraHopLeDuLieuSuCo(String maTour, BaoCaoSuCoRequest req) {
        if (req.getMoTa() == null || req.getMoTa().isBlank()) {
            throw AppException.badRequest("Mô tả sự cố không được để trống");
        }
        chuanHoaMucDoSuCo(req.getMucDo());
        chuanHoaLoaiSuCo(req.getLoaiSuCo());
        return tourThucTeRepository.findById(maTour)
                .orElseThrow(() -> AppException.notFound("Khong tim thay tour: " + maTour));
    }

    private NhatKySuCo xuLyTaoSuCoMoi(TourThucTe tour, NhanVien hdv, BaoCaoSuCoRequest req) {
        NhatKySuCo sc = new NhatKySuCo();
        sc.setMaNhatKySuCo("SC_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        sc.setTourThucTe(tour);
        sc.setNhanVienBaoCao(hdv);
        sc.setMoTa(req.getMoTa());
        sc.setGiaiPhap(req.getGiaiPhap());
        sc.setMucDo(chuanHoaMucDoSuCo(req.getMucDo()));
        sc.setLoaiSuCo(chuanHoaLoaiSuCo(req.getLoaiSuCo()));
        ganNguoiLienQuanSuCo(sc, tour, req);
        sc.setThoiGianBaoCao(LocalDateTime.now());
        return nhatKySuCoRepository.save(sc);
    }

    private void ganNguoiLienQuanSuCo(NhatKySuCo sc, TourThucTe tour, BaoCaoSuCoRequest req) {
        boolean coKhachHang = req.getMaKhachHang() != null && !req.getMaKhachHang().isBlank();
        boolean coNguoiDongHanh = req.getMaNguoiDongHanh() != null && !req.getMaNguoiDongHanh().isBlank();

        if (coKhachHang && coNguoiDongHanh) {
            throw AppException.badRequest("Chỉ được gửi một trong hai trường: mã khách hàng hoặc mã người đồng hành");
        }
        if (coKhachHang) {
            HoChieuSo kh = hoChieuSoRepository.findById(req.getMaKhachHang())
                    .orElseThrow(() -> AppException.notFound("Khong tim thay khach hang: " + req.getMaKhachHang()));
            kiemTraKhachThuocTourDaXacNhan(tour.getMaTourThucTe(), kh.getMaKhachHang());
            sc.setKhachHang(kh);
            return;
        }
        if (coNguoiDongHanh) {
            DsNguoiDongHanh ndh = dsNguoiDongHanhRepository.findById(req.getMaNguoiDongHanh())
                    .orElseThrow(
                            () -> AppException.notFound("Khong tim thay nguoi dong hanh: " + req.getMaNguoiDongHanh()));
            if (!ndh.getDonDatTour().getTourThucTe().getMaTourThucTe().equals(tour.getMaTourThucTe())) {
                throw AppException.badRequest("Người đồng hành không thuộc tour này");
            }
            sc.setNguoiDongHanh(ndh);
        }
    }

    private void guiCanhBaoDenQuanLyNeuCan(NhatKySuCo sc) {
        // Do an hien chua co kenh thong bao rieng; tach method de the hien buoc thiet
        // ke.
        if ("SOS".equals(sc.getMucDo())) {
            // TODO: gui canh bao den Dieu hanh/Quan ly khi bo sung module thong bao.
        }
    }

    // Cập nhật mô tả / giải pháp sự cố
    @Transactional
    public NhatKySuCoResponse capNhatSuCo(
            String maSuCo,
            BaoCaoSuCoRequest req,
            String maTaiKhoan,
            String maVaiTro) {
        NhatKySuCo sc = nhatKySuCoRepository.findById(maSuCo)
                .orElseThrow(() -> AppException.notFound("Khong tim thay su co: " + maSuCo));
        kiemTraQuyenVanHanhTour(sc.getTourThucTe().getMaTourThucTe(), maTaiKhoan, maVaiTro);
        if (req.getGiaiPhap() != null)
            sc.setGiaiPhap(req.getGiaiPhap());
        if (req.getMoTa() != null)
            sc.setMoTa(req.getMoTa());
        if (req.getMucDo() != null)
            sc.setMucDo(chuanHoaMucDoSuCo(req.getMucDo()));
        if (req.getLoaiSuCo() != null)
            sc.setLoaiSuCo(chuanHoaLoaiSuCo(req.getLoaiSuCo()));
        nhatKySuCoRepository.save(sc);
        return toSuCoResponse(sc);
    }

    public List<NhatKySuCoResponse> danhSachSuCo(
            String maTour,
            String mucDo,
            String maTaiKhoan,
            String maVaiTro) {
        kiemTraQuyenVanHanhTour(maTour, maTaiKhoan, maVaiTro);
        return nhatKySuCoRepository.findByMaTour(maTour, chuanHoaMucDoSuCoFilter(mucDo)).stream()
                .map(this::toSuCoResponse).toList();
    }

    public List<NhatKySuCoResponse> lichSuSuCoCuaHdv(String mucDo, String maTaiKhoan, String maVaiTro) {
        if (VaiTroConst.HDV.equals(maVaiTro)) {
            return nhatKySuCoRepository.findByHdvTaiKhoan(maTaiKhoan, chuanHoaMucDoSuCoFilter(mucDo)).stream()
                    .map(this::toSuCoResponse).toList();
        }
        return nhatKySuCoRepository.findAllWithRelations(chuanHoaMucDoSuCoFilter(mucDo)).stream()
                .map(this::toSuCoResponse)
                .toList();
    }

    // ── UC46: Khai báo chi phí phát sinh ─────────────────────────────────
    @Transactional
    public ChiPhiThucTeResponse khaiChiPhi(String maTour, KhaiChiPhiRequest req, String maTaiKhoan, String maVaiTro) {
        kiemTraQuyenVanHanhTour(maTour, maTaiKhoan, maVaiTro);
        TourThucTe tour = tourThucTeRepository.findById(maTour)
                .orElseThrow(() -> AppException.notFound("Khong tim thay tour: " + maTour));
        NhanVien hdv = getHdv(maTaiKhoan);

        ChiPhiThucTe cp = new ChiPhiThucTe();
        cp.setMaChiPhiThucTe("CP_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        cp.setTourThucTe(tour);
        cp.setNhanVien(hdv);
        cp.setDanhMuc(req.getDanhMuc());
        cp.setThanhTien(req.getThanhTien());
        cp.setHoaDonAnh(req.getHoaDonAnh());
        cp.setTrangThaiDuyet("CHO_DUYET");
        cp.setNgayKhai(LocalDateTime.now());
        chiPhiThucTeRepository.save(cp);

        return toChiPhiResponse(cp);
    }

    public List<ChiPhiThucTeResponse> chiPhiCuaTour(String maTour, String maTaiKhoan, String maVaiTro) {
        kiemTraQuyenVanHanhTour(maTour, maTaiKhoan, maVaiTro);
        return chiPhiThucTeRepository.findByMaTour(maTour).stream()
                .map(this::toChiPhiResponse).toList();
    }

    public List<ChiPhiThucTeResponse> lichSuChiPhiCuaHdv(String maTaiKhoan, String maVaiTro) {
        if (VaiTroConst.HDV.equals(maVaiTro)) {
            return chiPhiThucTeRepository.findByHdvTaiKhoan(maTaiKhoan).stream()
                    .map(this::toChiPhiResponse).toList();
        }
        return chiPhiThucTeRepository.findAllWithRelations().stream()
                .map(this::toChiPhiResponse).toList();
    }

    @Transactional
    public ChiPhiThucTeResponse boSungChiPhi(String maChiPhi, KhaiChiPhiRequest req, String maTaiKhoan) {
        ChiPhiThucTe cp = chiPhiThucTeRepository.findById(maChiPhi)
                .orElseThrow(() -> AppException.notFound("Khong tim thay chi phi: " + maChiPhi));
        if (!"YEU_CAU_BO_SUNG".equals(cp.getTrangThaiDuyet())) {
            throw AppException.badRequest("Chi phí không ở trạng thái YEU_CAU_BO_SUNG.");
        }
        if (!cp.getNhanVien().getTaiKhoan().getMaTaiKhoan().equals(maTaiKhoan)) {
            throw AppException.forbidden("Không có quyền bổ sung chi phí này");
        }

        cp.setDanhMuc(req.getDanhMuc());
        cp.setThanhTien(req.getThanhTien());
        cp.setHoaDonAnh(req.getHoaDonAnh());
        cp.setTrangThaiDuyet("CHO_DUYET");
        chiPhiThucTeRepository.save(cp);
        return toChiPhiResponse(cp);
    }

    // ── Kế toán: DS chi phí chờ duyệt ────────────────────────────────────
    public Page<ChiPhiThucTeResponse> danhSachChiPhiChoXuLy(String trangThai, String maTour, Pageable pageable) {
        return chiPhiThucTeRepository.findByTrangThai(trangThai, maTour, pageable)
                .map(this::toChiPhiResponse);
    }

    @Transactional
    public ChiPhiThucTeResponse duyetChiPhi(String maChiPhi) {
        return setTrangThaiChiPhi(maChiPhi, "DA_DUYET");
    }

    @Transactional
    public ChiPhiThucTeResponse tuChoiChiPhi(String maChiPhi) {
        return setTrangThaiChiPhi(maChiPhi, "TU_CHOI");
    }

    @Transactional
    public ChiPhiThucTeResponse yeuCauBoSungChiPhi(String maChiPhi) {
        return setTrangThaiChiPhi(maChiPhi, "YEU_CAU_BO_SUNG");
    }

    public Page<CanhBaoChiPhiResponse> danhSachCanhBaoChiPhi(String trangThai, String loaiCanhBao,
            String mucDo, String maTour, Pageable pageable) {
        List<CanhBaoChiPhiResponse> canhBao = chiPhiThucTeRepository.findAllWithRelations().stream()
                .filter(cp -> maTour == null || cp.getTourThucTe().getMaTourThucTe().equals(maTour))
                .flatMap(cp -> taoCanhBaoDong(cp).stream())
                .filter(cb -> trangThai == null || trangThai.equals(cb.getTrangThai()))
                .filter(cb -> loaiCanhBao == null || loaiCanhBao.equals(cb.getLoaiCanhBao()))
                .filter(cb -> mucDo == null || mucDo.equals(cb.getMucDo()))
                .toList();

        int start = Math.min((int) pageable.getOffset(), canhBao.size());
        int end = Math.min(start + pageable.getPageSize(), canhBao.size());
        return new PageImpl<>(canhBao.subList(start, end), pageable, canhBao.size());
    }

    public CanhBaoChiPhiResponse chiTietCanhBaoChiPhi(String maCanhBao) {
        return chiPhiThucTeRepository.findAllWithRelations().stream()
                .flatMap(cp -> taoCanhBaoDong(cp).stream())
                .filter(cb -> cb.getMaCanhBao().equals(maCanhBao))
                .findFirst()
                .orElseThrow(() -> AppException.notFound("Khong tim thay canh bao chi phi: " + maCanhBao));
    }

    private ChiPhiThucTeResponse setTrangThaiChiPhi(String maChiPhi, String trangThai) {
        ChiPhiThucTe cp = chiPhiThucTeRepository.findById(maChiPhi)
                .orElseThrow(() -> AppException.notFound("Khong tim thay chi phi: " + maChiPhi));
        if (!"CHO_DUYET".equals(cp.getTrangThaiDuyet())) {
            throw AppException.badRequest("Chi phí đã được xử lý rồi.");
        }
        cp.setTrangThaiDuyet(trangThai);
        chiPhiThucTeRepository.save(cp);
        return toChiPhiResponse(cp);
    }

    private List<CanhBaoChiPhiResponse> taoCanhBaoDong(ChiPhiThucTe cp) {
        List<CanhBaoChiPhiResponse> canhBao = new ArrayList<>();
        if (cp.getHoaDonAnh() == null || cp.getHoaDonAnh().isBlank()) {
            canhBao.add(toCanhBaoChiPhiResponse(cp, "THIEU_CHUNG_TU", "CAO",
                    "Chi phi chua co anh/chung tu hoa don dinh kem"));
        }

        BigDecimal dinhMuc = dinhMucTheoDanhMuc(cp.getDanhMuc());
        if (dinhMuc != null && cp.getThanhTien().compareTo(dinhMuc) > 0) {
            canhBao.add(toCanhBaoChiPhiResponse(cp, "VUOT_DINH_MUC", mucDoVuotNguong(cp.getThanhTien(), dinhMuc),
                    "Chi phi " + cp.getDanhMuc() + " vuot dinh muc " + dinhMuc));
        }

        BigDecimal giaThiTruong = giaThiTruongTheoDanhMuc(cp.getDanhMuc());
        if (giaThiTruong != null && cp.getThanhTien().compareTo(giaThiTruong.multiply(new BigDecimal("1.5"))) > 0) {
            canhBao.add(toCanhBaoChiPhiResponse(cp, "BAT_THUONG_THI_TRUONG",
                    mucDoVuotNguong(cp.getThanhTien(), giaThiTruong),
                    "Chi phi " + cp.getDanhMuc() + " cao bat thuong so voi gia thi truong " + giaThiTruong));
        }
        return canhBao;
    }

    private BigDecimal dinhMucTheoDanhMuc(String danhMuc) {
        String key = chuanHoaDanhMuc(danhMuc);
        if (key.contains("khach san"))
            return new BigDecimal("4000000");
        if (key.contains("ve") || key.contains("tham quan"))
            return new BigDecimal("1500000");
        if (key.contains("xe") || key.contains("dua don"))
            return new BigDecimal("3000000");
        if (key.contains("an uong"))
            return new BigDecimal("2000000");
        return null;
    }

    private BigDecimal giaThiTruongTheoDanhMuc(String danhMuc) {
        String key = chuanHoaDanhMuc(danhMuc);
        if (key.contains("khach san"))
            return new BigDecimal("4200000");
        if (key.contains("ve") || key.contains("tham quan"))
            return new BigDecimal("1600000");
        if (key.contains("xe") || key.contains("dua don"))
            return new BigDecimal("2800000");
        if (key.contains("an uong"))
            return new BigDecimal("2200000");
        return null;
    }

    private String mucDoVuotNguong(BigDecimal thanhTien, BigDecimal nguong) {
        if (thanhTien.compareTo(nguong.multiply(new BigDecimal("1.5"))) > 0)
            return "NGHIEM_TRONG";
        if (thanhTien.compareTo(nguong.multiply(new BigDecimal("1.3"))) > 0)
            return "CAO";
        if (thanhTien.compareTo(nguong.multiply(new BigDecimal("1.1"))) > 0)
            return "TRUNG_BINH";
        return "THAP";
    }

    private String chuanHoaDanhMuc(String danhMuc) {
        if (danhMuc == null)
            return "";
        return Normalizer.normalize(danhMuc, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace('đ', 'd')
                .replace('Đ', 'D')
                .toLowerCase();
    }

    // ── Helpers ───────────────────────────────────────────────────────────
    private TourThucTe getActiveTour(String maTour) {
        return tourThucTeRepository.findById(maTour)
                .orElseThrow(() -> AppException.notFound("Khong tim thay tour: " + maTour));
    }

    private NhanVien getHdv(String maTaiKhoan) {
        return nhanVienRepository.findByMaTaiKhoan(maTaiKhoan)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy hồ sơ nhân viên"));
    }

    private void kiemTraQuyenXemLichTrinhTour(String maTour, String maTaiKhoan, String maVaiTro) {
        if (!VaiTroConst.HDV.equals(maVaiTro)
                || phanCongTourRepository.existsViewableByMaTourAndMaTaiKhoan(maTour, maTaiKhoan)) {
            return;
        }
        throw AppException.forbidden("Ban khong co quyen xem lich trinh tour nay");
    }

    private void kiemTraQuyenVanHanhTour(String maTour, String maTaiKhoan, String maVaiTro) {
        if (VaiTroConst.ADMIN.equals(maVaiTro) || VaiTroConst.DIEUHANH.equals(maVaiTro)) {
            return;
        }
        if (VaiTroConst.HDV.equals(maVaiTro)
                && phanCongTourRepository.existsByMaTourAndMaTaiKhoan(maTour, maTaiKhoan)) {
            return;
        }
        throw AppException.forbidden("Bạn không có quyền vận hành tour này");
    }

    private void kiemTraKhachThuocTourDaXacNhan(String maTour, String maKhachHang) {
        if (!donDatTourRepository.existsConfirmedKhachHangInTour(maTour, maKhachHang)) {
            throw AppException.badRequest("Khách hàng không thuộc tour đã xác nhận này");
        }
    }

    // Nâng hạng thành viên tự động theo ngưỡng điểm xanh
    private void kiemTraNangHang(HoChieuSo hcs) {
        long diem = hcs.getDiemXanh();
        String hangHienTai = hcs.getHangThanhVien();
        String hangMoi = hangHienTai;
        if (diem >= 5000) {
            hangMoi = "KIM_CUONG";
        } else if (diem >= 2000) {
            hangMoi = "VANG";
        } else if (diem >= 1000) {
            hangMoi = "BAC";
        } else if (diem >= 500) {
            hangMoi = "DONG";
        } else {
            hangMoi = "THANH_VIEN";
        }
        if (thuTuHang(hangMoi) > thuTuHang(hangHienTai)) {
            hcs.setHangThanhVien(hangMoi);
        }
    }

    private int thuTuHang(String hangThanhVien) {
        return switch (hangThanhVien) {
            case "DONG" -> 1;
            case "BAC" -> 2;
            case "VANG" -> 3;
            case "KIM_CUONG" -> 4;
            default -> 0;
        };
    }

    private String chuanHoaTrangThaiDiemDanh(String trangThai) {
        if (trangThai == null || trangThai.isBlank()) {
            return "DA_DIEM_DANH";
        }
        if (!java.util.Set.of("DA_DIEM_DANH", "CHUA_DIEM_DANH", "VANG").contains(trangThai)) {
            throw AppException.badRequest("TrangThai diem danh khong hop le: " + trangThai);
        }
        return trangThai;
    }

    // ── Mappers ───────────────────────────────────────────────────────────
    private DiemDanhResponse toDiemDanhResponse(DiemDanh dd) {
        HoChieuSo kh = dd.getKhachHang();
        DsNguoiDongHanh nguoiDongHanh = dd.getNguoiDongHanh();
        String hoTen = kh != null && kh.getTaiKhoan() != null
                ? kh.getTaiKhoan().getHoTen()
                : nguoiDongHanh != null ? nguoiDongHanh.getHoTen() : "";
        return DiemDanhResponse.builder()
                .maDiemDanh(dd.getMaDiemDanh())
                .loaiKhach(dd.getLoaiKhach())
                .maKhachHang(kh != null ? kh.getMaKhachHang() : null)
                .maNguoiDongHanh(nguoiDongHanh != null ? nguoiDongHanh.getMaNguoiDongHanh() : null)
                .hoTenKhachHang(hoTen)
                .diaDiem(dd.getDiaDiem())
                .trangThai(dd.getTrangThai())
                .thoiGian(dd.getThoiGian())
                .soDienThoai(kh != null ? kh.getTaiKhoan().getSoDienThoai()
                        : (nguoiDongHanh != null ? nguoiDongHanh.getSoDienThoai() : null))
                .hangThanhVien(kh != null ? kh.getHangThanhVien() : "THANH_VIEN")
                .ghiChuYTe(kh != null ? kh.getGhiChuYTe() : (nguoiDongHanh != null ? nguoiDongHanh.getGhiChu() : null))
                .diemXanh(kh != null ? kh.getDiemXanh() : 0L)
                .build();
    }

    private HanhDongResponse toHanhDongResponse(HanhDong hd, HanhDongXanh hdx) {
        String hoTen = hd.getKhachHang().getTaiKhoan() != null
                ? hd.getKhachHang().getTaiKhoan().getHoTen()
                : "";
        return HanhDongResponse.builder()
                .maGhiNhan(hd.getMaGhiNhanHanhDong())
                .maKhachHang(hd.getKhachHang().getMaKhachHang())
                .hoTenKhachHang(hoTen)
                .maHanhDongXanh(hdx.getMaHanhDongXanh())
                .tenHanhDong(hdx.getTenHanhDong())
                .diemCong(hdx.getDiemCong())
                .minhChung(hd.getMinhChung())
                .thoiGian(hd.getThoiGian())
                .build();
    }

    private NhatKySuCoResponse toSuCoResponse(NhatKySuCo sc) {
        HoChieuSo kh = sc.getKhachHang();
        DsNguoiDongHanh nguoiDongHanh = sc.getNguoiDongHanh();
        String hoTenKhachHang = kh != null && kh.getTaiKhoan() != null
                ? kh.getTaiKhoan().getHoTen()
                : nguoiDongHanh != null ? nguoiDongHanh.getHoTen() : null;
        return NhatKySuCoResponse.builder()
                .maNhatKySuCo(sc.getMaNhatKySuCo())
                .maTour(sc.getTourThucTe().getMaTourThucTe())
                .moTa(sc.getMoTa())
                .giaiPhap(sc.getGiaiPhap())
                .mucDo(sc.getMucDo())
                .loaiSuCo(sc.getLoaiSuCo())
                .maHdvBaoCao(sc.getNhanVienBaoCao() != null ? sc.getNhanVienBaoCao().getMaNhanVien() : null)
                .maKhachHang(kh != null ? kh.getMaKhachHang() : null)
                .maNguoiDongHanh(nguoiDongHanh != null ? nguoiDongHanh.getMaNguoiDongHanh() : null)
                .hoTenKhachHang(hoTenKhachHang)
                .ghiChuYTe(kh != null ? kh.getGhiChuYTe() : (nguoiDongHanh != null ? nguoiDongHanh.getGhiChu() : null))
                .diUng(kh != null ? kh.getDiUng() : null)
                .thoiGianBaoCao(sc.getThoiGianBaoCao())
                .build();
    }

    private String chuanHoaMucDoSuCo(String mucDo) {
        String normalized = mucDo == null || mucDo.isBlank() ? "THAP" : mucDo.trim().toUpperCase();
        if (!java.util.Set.of("THAP", "SOS").contains(normalized)) {
            throw AppException.badRequest("Mức độ sự cố không hợp lệ. Chỉ chấp nhận: THAP, SOS");
        }
        return normalized;
    }

    private String chuanHoaMucDoSuCoFilter(String mucDo) {
        if (mucDo == null || mucDo.isBlank())
            return null;
        return chuanHoaMucDoSuCo(mucDo);
    }

    private String chuanHoaLoaiSuCo(String loaiSuCo) {
        String normalized = "KHAC";
        if (loaiSuCo != null && !loaiSuCo.isBlank()) {
            normalized = Normalizer.normalize(loaiSuCo.trim().toUpperCase(), Normalizer.Form.NFD)
                    .replaceAll("\\p{M}", "")
                    .replace('Đ', 'D')
                    .replace(' ', '_');
        }
        if (!java.util.Set.of("Y_TE", "THOI_TIET", "PHUONG_TIEN", "AN_UONG", "KHAC").contains(normalized)) {
            throw AppException
                    .badRequest("Loại sự cố không hợp lệ. Chỉ chấp nhận: Y_TE, THOI_TIET, PHUONG_TIEN, AN_UONG, KHAC");
        }
        return normalized;
    }

    private ChiPhiThucTeResponse toChiPhiResponse(ChiPhiThucTe cp) {
        NhanVien nv = cp.getNhanVien();
        String tenNv = nv.getTaiKhoan() != null ? nv.getTaiKhoan().getHoTen() : "";
        return ChiPhiThucTeResponse.builder()
                .maChiPhi(cp.getMaChiPhiThucTe())
                .maTour(cp.getTourThucTe().getMaTourThucTe())
                .maNhanVien(nv.getMaNhanVien())
                .tenNhanVien(tenNv)
                .danhMuc(cp.getDanhMuc())
                .thanhTien(cp.getThanhTien())
                .hoaDonAnh(cp.getHoaDonAnh())
                .trangThaiDuyet(cp.getTrangThaiDuyet())
                .ngayKhai(cp.getNgayKhai())
                .build();
    }

    private CanhBaoChiPhiResponse toCanhBaoChiPhiResponse(ChiPhiThucTe cp, String loaiCanhBao,
            String mucDo, String noiDung) {
        NhanVien nv = cp.getNhanVien();
        String tenNv = nv.getTaiKhoan() != null ? nv.getTaiKhoan().getHoTen() : "";
        return CanhBaoChiPhiResponse.builder()
                .maCanhBao(cp.getMaChiPhiThucTe() + "_" + loaiCanhBao)
                .maChiPhi(cp.getMaChiPhiThucTe())
                .maTour(cp.getTourThucTe().getMaTourThucTe())
                .maNhanVien(nv.getMaNhanVien())
                .tenNhanVien(tenNv)
                .danhMuc(cp.getDanhMuc())
                .thanhTien(cp.getThanhTien())
                .loaiCanhBao(loaiCanhBao)
                .mucDo(mucDo)
                .noiDung(noiDung)
                .trangThai(trangThaiCanhBao(cp.getTrangThaiDuyet()))
                .ngayTao(cp.getNgayKhai())
                .build();
    }

    private String trangThaiCanhBao(String trangThaiDuyet) {
        return switch (trangThaiDuyet) {
            case "YEU_CAU_BO_SUNG" -> "DANG_XU_LY";
            case "DA_DUYET", "TU_CHOI" -> "DA_XU_LY";
            default -> "MOI";
        };
    }
}
