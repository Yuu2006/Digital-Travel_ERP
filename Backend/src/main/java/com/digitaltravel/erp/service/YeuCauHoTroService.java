package com.digitaltravel.erp.service;

import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.digitaltravel.erp.dto.requests.XuLyHoTroRequest;
import com.digitaltravel.erp.dto.requests.YeuCauHoTroRequest;
import com.digitaltravel.erp.dto.responses.YeuCauHoTroResponse;
import com.digitaltravel.erp.entity.DonDatTour;
import com.digitaltravel.erp.entity.HoChieuSo;
import com.digitaltravel.erp.entity.NhanVien;
import com.digitaltravel.erp.entity.PhanCongTour;
import com.digitaltravel.erp.entity.YeuCauHoTro;
import com.digitaltravel.erp.exception.AppException;
import com.digitaltravel.erp.repository.DonDatTourRepository;
import com.digitaltravel.erp.repository.HoChieuSoRepository;
import com.digitaltravel.erp.repository.NhanVienRepository;
import com.digitaltravel.erp.repository.PhanCongTourRepository;
import com.digitaltravel.erp.repository.YeuCauHoTroRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class YeuCauHoTroService {

    private static final DateTimeFormatter SUPPORT_NOTE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final YeuCauHoTroRepository yeuCauHoTroRepository;
    private final HoChieuSoRepository hoChieuSoRepository;
    private final DonDatTourRepository donDatTourRepository;
    private final NhanVienRepository nhanVienRepository;
    private final PhanCongTourRepository phanCongTourRepository;

    // ── UC36: KH tạo yêu cầu hỗ trợ / khiếu nại ────────────────────────────
    @Transactional
    public YeuCauHoTroResponse taoYeuCau(String maTaiKhoan, YeuCauHoTroRequest request) {
        HoChieuSo hcs = hoChieuSoRepository.findByMaTaiKhoan(maTaiKhoan)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy hồ sơ khách hàng"));

        YeuCauHoTro yc = new YeuCauHoTro();
        yc.setMaYeuCauHoTro("YCHTR_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        yc.setKhachHang(hcs);
        yc.setLoaiYeuCau(request.getLoaiYeuCau());
        yc.setNoiDung(request.getNoiDung());
        yc.setTrangThai("CHUA_XU_LY");

        // Gắn với đơn đặt tour nếu có
        if (request.getMaDatTour() != null && !request.getMaDatTour().isBlank()) {
            DonDatTour don = donDatTourRepository.findByIdAndMaKhachHang(request.getMaDatTour(), hcs.getMaKhachHang())
                    .orElseThrow(() -> AppException.notFound("Không tìm thấy đơn đặt tour: " + request.getMaDatTour()));
            if ("KHIEU_NAI".equals(request.getLoaiYeuCau())) {
                boolean hasActive = yeuCauHoTroRepository.findByMaDatTourAndLoaiYeuCau(request.getMaDatTour(), "KHIEU_NAI")
                        .stream()
                        .anyMatch(k -> !"DA_XU_LY".equals(k.getTrangThai()) && !"TU_CHOI".equals(k.getTrangThai()));
                if (hasActive) {
                    throw AppException.badRequest("Đơn đặt tour này đã có khiếu nại đang chờ xử lý, không thể gửi trùng lặp");
                }
            }
            yc.setDonDatTour(don);
        }

        yeuCauHoTroRepository.save(yc);
        return toResponse(yc);
    }

    // ── UC36: KH xem danh sách yêu cầu của mình ─────────────────────────────
    public Page<YeuCauHoTroResponse> danhSachCuaKhachHang(String maTaiKhoan, String loaiYeuCau, Pageable pageable) {
        HoChieuSo hcs = hoChieuSoRepository.findByMaTaiKhoan(maTaiKhoan)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy hồ sơ khách hàng"));
        // Dùng lại query có sẵn, filter thêm theo khách hàng
        return yeuCauHoTroRepository.timKiemTheoKhachHang(hcs.getMaKhachHang(), loaiYeuCau, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public List<YeuCauHoTroResponse> danhSachCanKhachHangBoSung(String maTaiKhoan) {
        HoChieuSo hcs = hoChieuSoRepository.findByMaTaiKhoan(maTaiKhoan)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy hồ sơ khách hàng"));
        return yeuCauHoTroRepository.findYeuCauCanKhachHangBoSung(hcs.getMaKhachHang())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ── Bổ sung bằng chứng ───────────────────────────────────────────────────
    @Transactional
    public YeuCauHoTroResponse boSung(String maTaiKhoan, String maYeuCau, String noiDungBoSung) {
        HoChieuSo hcs = hoChieuSoRepository.findByMaTaiKhoan(maTaiKhoan)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy hồ sơ khách hàng"));

        YeuCauHoTro yc = yeuCauHoTroRepository.findById(maYeuCau)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy yêu cầu: " + maYeuCau));

        if (!yc.getKhachHang().getMaKhachHang().equals(hcs.getMaKhachHang())) {
            throw AppException.forbidden("Không có quyền truy cập yêu cầu này");
        }

        if (!"CHO_BO_SUNG".equals(yc.getTrangThai()) && !"CHUA_XU_LY".equals(yc.getTrangThai())) {
            throw AppException.badRequest("Yêu cầu này đang ở trạng thái " + yc.getTrangThai() + ", không thể bổ sung");
        }

        String ngayBoSung = java.time.LocalDate.now().toString();
        yc.setNoiDung(yc.getNoiDung() + "\n\n[Bổ sung ngày " + ngayBoSung + "]:\n" + noiDungBoSung);
        yc.setTrangThai("CHUA_XU_LY"); // Đưa lại hàng chờ

        yeuCauHoTroRepository.save(yc);
        return toResponse(yc);
    }

    // ── UC41: SALES xem tất cả yêu cầu ──────────────────────────────────────
    public Page<YeuCauHoTroResponse> danhSachTatCa(String loaiYeuCau, String trangThai, Pageable pageable) {
        return yeuCauHoTroRepository.timKiem(loaiYeuCau, trangThai, pageable)
                .map(this::toResponse);
    }

    // ── UC41: SALES tiếp nhận xử lý ──────────────────────────────────────────
    @Transactional
    public YeuCauHoTroResponse xuLy(String maYeuCau, XuLyHoTroRequest request, String maTaiKhoanXuLy) {
        YeuCauHoTro yc = yeuCauHoTroRepository.findById(maYeuCau)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy yêu cầu: " + maYeuCau));

        if ("DA_XU_LY".equals(yc.getTrangThai()) || "TU_CHOI".equals(yc.getTrangThai())) {
            throw AppException.badRequest("Yêu cầu này đã kết thúc, không thể cập nhật");
        }

        String trangThaiMoi = request.getTrangThai();
        if (!"CHUA_XU_LY".equals(trangThaiMoi)
                && !"CHO_BO_SUNG".equals(trangThaiMoi)
                && !"CHO_GIAI_TRINH".equals(trangThaiMoi)
                && !"CHO_DUYET".equals(trangThaiMoi)
                && !"DA_XU_LY".equals(trangThaiMoi)
                && !"TU_CHOI".equals(trangThaiMoi)) {
            throw AppException.badRequest(
                    "Trạng thái không hợp lệ. Chỉ chấp nhận: CHUA_XU_LY, CHO_BO_SUNG, CHO_GIAI_TRINH, CHO_DUYET, DA_XU_LY, TU_CHOI");
        }
        yc.setTrangThai(trangThaiMoi);

        // Gán nhân viên xử lý nếu có
        if (request.getMaNhanVienXuLy() != null && !request.getMaNhanVienXuLy().isBlank()) {
            NhanVien nv = nhanVienRepository.findById(request.getMaNhanVienXuLy())
                    .orElseThrow(() -> AppException.notFound("Không tìm thấy nhân viên: " + request.getMaNhanVienXuLy()));
            yc.setNhanVienXuLy(nv);
        }

        yeuCauHoTroRepository.save(yc);
        return toResponse(yc);
    }

    @Transactional
    public YeuCauHoTroResponse yeuCauHdvGiaiTrinh(String maYeuCau, String noiDungYeuCau) {
        YeuCauHoTro yc = yeuCauHoTroRepository.findById(maYeuCau)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy yêu cầu: " + maYeuCau));

        if (yc.getDonDatTour() == null || yc.getDonDatTour().getTourThucTe() == null) {
            throw AppException.badRequest("Yêu cầu này chưa gắn với đơn đặt tour nên không xác định được HDV");
        }

        if ("DA_XU_LY".equals(yc.getTrangThai()) || "TU_CHOI".equals(yc.getTrangThai())) {
            throw AppException.badRequest("Yêu cầu này đã kết thúc, không thể yêu cầu HDV giải trình");
        }

        String maTour = yc.getDonDatTour().getTourThucTe().getMaTourThucTe();
        List<PhanCongTour> phanCongList = phanCongTourRepository.findActiveByMaTour(maTour);
        if (phanCongList.isEmpty()) {
            throw AppException.badRequest("Tour " + maTour + " chưa có HDV đã nhận phân công");
        }

        NhanVien hdv = phanCongList.get(0).getNhanVien();
        String timestamp = LocalDateTime.now().format(SUPPORT_NOTE_TIME_FORMAT);
        yc.setNoiDung(yc.getNoiDung()
                + "\n\n[Yêu cầu HDV giải trình lúc " + timestamp + "]: \n" + noiDungYeuCau);
        yc.setNhanVienXuLy(hdv);
        yc.setTrangThai("CHO_BO_SUNG");

        yeuCauHoTroRepository.save(yc);
        return toResponse(yc);
    }

    @Transactional
    public YeuCauHoTroResponse yeuCauKhachHangBoSung(String maYeuCau, String noiDungYeuCau) {
        YeuCauHoTro yc = yeuCauHoTroRepository.findById(maYeuCau)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy yêu cầu: " + maYeuCau));

        if ("DA_XU_LY".equals(yc.getTrangThai()) || "TU_CHOI".equals(yc.getTrangThai())) {
            throw AppException.badRequest("Yêu cầu này đã kết thúc, không thể yêu cầu khách hàng bổ sung");
        }

        String timestamp = LocalDateTime.now().format(SUPPORT_NOTE_TIME_FORMAT);
        yc.setNoiDung(yc.getNoiDung()
                + "\n\n[Yêu cầu KH bổ sung lúc " + timestamp + "]: \n" + noiDungYeuCau);
        yc.setTrangThai("CHO_BO_SUNG");

        yeuCauHoTroRepository.save(yc);
        return toResponse(yc);
    }

    @Transactional(readOnly = true)
    public List<YeuCauHoTroResponse> danhSachGiaiTrinhCuaHdv(String maTaiKhoan) {
        NhanVien hdv = nhanVienRepository.findByMaTaiKhoan(maTaiKhoan)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy hồ sơ nhân viên"));
        return yeuCauHoTroRepository.findYeuCauGiaiTrinhCuaHdv(hdv.getMaNhanVien())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public YeuCauHoTroResponse hdvCapNhatGiaiTrinh(String maTaiKhoan, String maYeuCau, String noiDungGiaiTrinh) {
        NhanVien hdv = nhanVienRepository.findByMaTaiKhoan(maTaiKhoan)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy hồ sơ nhân viên"));
        YeuCauHoTro yc = yeuCauHoTroRepository.findById(maYeuCau)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy yêu cầu: " + maYeuCau));

        if (yc.getNhanVienXuLy() == null || !hdv.getMaNhanVien().equals(yc.getNhanVienXuLy().getMaNhanVien())) {
            throw AppException.forbidden("Không có quyền cập nhật giải trình cho yêu cầu này");
        }

        if (!"CHO_BO_SUNG".equals(yc.getTrangThai())) {
            throw AppException.badRequest("Yêu cầu này đang ở trạng thái " + yc.getTrangThai() + ", không thể gửi giải trình");
        }

        String timestamp = LocalDateTime.now().format(SUPPORT_NOTE_TIME_FORMAT);
        yc.setNoiDung(yc.getNoiDung()
                + "\n\n[HDV giải trình lúc " + timestamp + "]: \n" + noiDungGiaiTrinh);
        yc.setTrangThai("CHO_DUYET");

        yeuCauHoTroRepository.save(yc);
        return toResponse(yc);
    }

    // ── Chi tiết yêu cầu ─────────────────────────────────────────────────────
    public YeuCauHoTroResponse chiTiet(String maYeuCau) {
        YeuCauHoTro yc = yeuCauHoTroRepository.findById(maYeuCau)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy yêu cầu: " + maYeuCau));
        return toResponse(yc);
    }

    // ── Mapper ───────────────────────────────────────────────────────────────
    public YeuCauHoTroResponse toResponse(YeuCauHoTro yc) {
        String trangThai = yc.getTrangThai();
        if ("HUY_TOUR".equals(yc.getLoaiYeuCau()) && yc.getDonDatTour() != null) {
            String trangThaiDon = yc.getDonDatTour().getTrangThai();
            if ("DA_HUY".equals(trangThaiDon)) {
                trangThai = "DA_XU_LY";
            } else if ("TU_CHOI_HOAN_TIEN".equals(trangThaiDon)) {
                trangThai = "TU_CHOI";
            }
        }

        return YeuCauHoTroResponse.builder()
                .maYeuCau(yc.getMaYeuCauHoTro())
                .maDatTour(yc.getDonDatTour() != null ? yc.getDonDatTour().getMaDatTour() : null)
                .loaiYeuCau(yc.getLoaiYeuCau())
                .noiDung(yc.getNoiDung())
                .trangThai(trangThai)
                .maNhanVienXuLy(yc.getNhanVienXuLy() != null ? yc.getNhanVienXuLy().getMaNhanVien() : null)
                .build();
    }
}
