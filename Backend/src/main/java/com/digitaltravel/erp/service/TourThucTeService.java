package com.digitaltravel.erp.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.digitaltravel.erp.dto.requests.CapNhatTourThucTeRequest;
import com.digitaltravel.erp.dto.requests.TaoTourThucTeRequest;
import com.digitaltravel.erp.dto.responses.DichVuThemResponse;
import com.digitaltravel.erp.dto.responses.HanhDongXanhResponse;
import com.digitaltravel.erp.dto.responses.LichTrinhResponse;
import com.digitaltravel.erp.dto.responses.TourCongKhaiResponse;
import com.digitaltravel.erp.dto.responses.TourThucTeResponse;
import com.digitaltravel.erp.entity.DichVuThem;
import com.digitaltravel.erp.entity.DichVuTourThucTe;
import com.digitaltravel.erp.entity.DichVuTourThucTeId;
import com.digitaltravel.erp.entity.HanhDongXanh;
import com.digitaltravel.erp.entity.HdxTourThucTe;
import com.digitaltravel.erp.entity.HdxTourThucTeId;
import com.digitaltravel.erp.entity.LichTrinhTour;
import com.digitaltravel.erp.entity.TourMau;
import com.digitaltravel.erp.entity.TourThucTe;
import com.digitaltravel.erp.exception.AppException;
import com.digitaltravel.erp.repository.DichVuThemRepository;
import com.digitaltravel.erp.repository.DichVuTourThucTeRepository;
import com.digitaltravel.erp.repository.HanhDongXanhRepository;
import com.digitaltravel.erp.repository.HdxTourThucTeRepository;
import com.digitaltravel.erp.repository.LichTrinhTourRepository;
import com.digitaltravel.erp.repository.DonDatTourRepository;
import com.digitaltravel.erp.repository.PhanCongTourRepository;
import com.digitaltravel.erp.repository.TourMauRepository;
import com.digitaltravel.erp.repository.TourThucTeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TourThucTeService {

    private final TourThucTeRepository tourThucTeRepository;
    private final TourMauRepository tourMauRepository;
    private final LichTrinhTourRepository lichTrinhTourRepository;
    private final DonDatTourRepository donDatTourRepository;
    private final PhanCongTourRepository phanCongTourRepository;
    private final DichVuThemRepository dichVuThemRepository;
    private final DichVuTourThucTeRepository dichVuTourThucTeRepository;
    private final HanhDongXanhRepository hanhDongXanhRepository;
    private final HdxTourThucTeRepository hdxTourThucTeRepository;
    private final MaTuDongService maTuDongService;

    // ── UC14: Danh sách tour thực tế (nội bộ - có filter) ───────────────────
    @Transactional(readOnly = true)
    public Page<TourThucTeResponse> danhSach(String trangThai, String maTourMau,
                                              java.math.BigDecimal giaTu, java.math.BigDecimal giaDen,
                                              Pageable pageable) {
        return tourThucTeRepository.timKiem(trangThai, maTourMau, giaTu, giaDen, pageable)
                .map(this::toResponse);
    }

    // ── UC14: Danh sách tour thực tế (công khai cho KH) ─────────────────────
    @Transactional(readOnly = true)
    public Page<TourThucTeResponse> danhSachCongKhai(java.math.BigDecimal giaTu, java.math.BigDecimal giaDen,
                                                      Integer thoiLuongMin, Integer thoiLuongMax,
                                                      Pageable pageable) {
        return tourThucTeRepository.timKiemCongKhai(giaTu, giaDen, thoiLuongMin, thoiLuongMax, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<TourThucTeResponse> danhSachCanPhanCong(Pageable pageable) {
        return tourThucTeRepository.findChoKichHoatChuaPhanCong(pageable)
                .map(this::toResponse);
    }

    // ── UC14: Chi tiết tour thực tế ─────────────────────────────────────────
    @Transactional(readOnly = true)
    public TourThucTeResponse chiTiet(String id) {
        TourThucTe ttt = tourThucTeRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Khong tim thay tour thuc te: " + id));
        return toResponse(ttt);
    }

    // ── UC26: Chi tiết tour công khai kèm lịch trình ────────────────────────
    @Transactional(readOnly = true)
    public TourCongKhaiResponse chiTietCongKhai(String id) {
        TourThucTe ttt = tourThucTeRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Khong tim thay tour: " + id));
        TourMau tm = ttt.getTourMau();

        java.util.List<LichTrinhTour> lichTrinh = lichTrinhTourRepository.findByMaTourMau(tm.getMaTourMau());
        java.util.List<LichTrinhResponse> lichTrinhResponses = lichTrinh.stream()
                .map(lt -> LichTrinhResponse.builder()
                        .maLichTrinhTour(lt.getMaLichTrinhTour())
                        .ngayThu(lt.getNgayThu())
                        .hoatDong(lt.getHoatDong())
                        .moTa(lt.getMoTa())
                        .thucDon(lt.getThucDon())
                        .build())
                .toList();

        return TourCongKhaiResponse.builder()
                .maTourThucTe(ttt.getMaTourThucTe())
                .maTourMau(tm.getMaTourMau())
                .tieuDeTour(tm.getTieuDe())
                .moTa(tm.getMoTa())
                .ngayKhoiHanh(ttt.getNgayKhoiHanh())
                .thoiLuong(tm.getThoiLuong())
                .giaHienHanh(ttt.getGiaHienHanh())
                .soKhachToiDa(ttt.getSoKhachToiDa())
                .choConLai(ttt.getChoConLai())
                .trangThai(ttt.getTrangThai())
                .diemDanhGia(tm.getDanhGia())
                .soDanhGia(tm.getSoDanhGia())
                .lichTrinh(lichTrinhResponses)
                .dichVu(layDichVuTheoTour(ttt))
                .hanhDongXanh(layHanhDongXanhTheoTour(ttt))
                .build();
    }

    // ── UC11: Khởi tạo tour thực tế từ tour mẫu ────────────────────────────
    @Transactional
    public TourThucTeResponse taoMoi(TaoTourThucTeRequest request, String nguoiTao) {
        TourMau tourMau = tourMauRepository.findById(request.getMaTourMau())
                .orElseThrow(() -> AppException.notFound("Khong tim thay tour mau: " + request.getMaTourMau()));

        Integer soKhachToiThieu = request.getSoKhachToiThieu() != null
                ? request.getSoKhachToiThieu() : 1;

        if (soKhachToiThieu > request.getSoKhachToiDa()) {
            throw AppException.badRequest("Số khách tối thiểu không được lớn hơn số khách tối đa");
        }

        if (request.getGiaHienHanh().compareTo(tourMau.getGiaSan()) < 0) {
            throw AppException.badRequest("Gia hien hanh khong duoc thap hon gia san cua tour mau ("
                    + tourMau.getGiaSan() + ")");
        }

        TourThucTe ttt = new TourThucTe();
        ttt.setMaTourThucTe(maTuDongService.taoMaTourThucTe());
        ttt.setTourMau(tourMau);
        ttt.setNgayKhoiHanh(request.getNgayKhoiHanh());
        ttt.setGiaHienHanh(request.getGiaHienHanh());
        ttt.setSoKhachToiDa(request.getSoKhachToiDa());
        ttt.setSoKhachToiThieu(soKhachToiThieu);
        ttt.setChoConLai(request.getSoKhachToiDa());
        String trangThai = request.getTrangThai() != null && !request.getTrangThai().isBlank()
                ? request.getTrangThai()
                : "CHO_KICH_HOAT";
        validateTrangThaiTourThucTe(trangThai);
        if ("MO_BAN".equals(trangThai)) {
            throw AppException.badRequest("Tour mới phải ở trạng thái CHO_KICH_HOAT để phân công và xác nhận HDV trước khi mở bán.");
        }
        ttt.setTrangThai(trangThai);
        tourThucTeRepository.save(ttt);
        capNhatDichVuTour(ttt, request.getMaDichVuThem());
        capNhatHanhDongXanhTour(ttt, request.getMaHanhDongXanh());

        return toResponse(ttt);
    }

    // ── UC13: Sửa tour thực tế ──────────────────────────────────────────────
    @Transactional
    public TourThucTeResponse capNhat(String id, CapNhatTourThucTeRequest request, String nguoiCapNhat) {
        TourThucTe ttt = tourThucTeRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Khong tim thay tour thuc te: " + id));

        if (request.getGiaHienHanh() != null) {
            ttt.setGiaHienHanh(request.getGiaHienHanh());
        }
        if (request.getSoKhachToiDa() != null) {
            if (request.getSoKhachToiDa() < ttt.getSoKhachToiThieu()) {
                throw AppException.badRequest("Số khách tối đa không được nhỏ hơn số khách tối thiểu");
            }
            ttt.setSoKhachToiDa(request.getSoKhachToiDa());
        }
        if (request.getSoKhachToiThieu() != null) {
            if (request.getSoKhachToiThieu() > ttt.getSoKhachToiDa()) {
                throw AppException.badRequest("Số khách tối thiểu không được lớn hơn số khách tối đa");
            }
            ttt.setSoKhachToiThieu(request.getSoKhachToiThieu());
        }
        if (request.getTrangThai() != null) {
            validateTrangThaiTourThucTe(request.getTrangThai());
            if ("MO_BAN".equals(request.getTrangThai())
                    && !"MO_BAN".equals(ttt.getTrangThai())
                    && !phanCongTourRepository.existsAcceptedByTourThucTe_MaTourThucTe(id)) {
                throw AppException.badRequest("Tour chỉ được mở bán khi có HDV đã xác nhận phân công.");
            }
            ttt.setTrangThai(request.getTrangThai());
        }
        tourThucTeRepository.save(ttt);
        if (request.getMaDichVuThem() != null) {
            capNhatDichVuTour(ttt, request.getMaDichVuThem());
        }
        if (request.getMaHanhDongXanh() != null) {
            capNhatHanhDongXanhTour(ttt, request.getMaHanhDongXanh());
        }

        return toResponse(ttt);
    }

    // ── UC12: Xóa tour thực tế ──────────────────────────────────────────────
    @Transactional
    public void xoa(String id) {
        TourThucTe ttt = tourThucTeRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Khong tim thay tour thuc te: " + id));

        // Chỉ cho xóa khi ở trạng thái CHO_KICH_HOAT hoặc MO_BAN (chưa có đơn xác nhận)
        if (!"CHO_KICH_HOAT".equals(ttt.getTrangThai()) && !"MO_BAN".equals(ttt.getTrangThai())) {
            throw AppException.badRequest("Chỉ có thể xóa tour thực tế ở trạng thái CHO_KICH_HOAT hoặc MO_BAN");
        }
        if (donDatTourRepository.countBlockingBookingsByTourThucTe(id) > 0) {
            throw AppException.badRequest("Không thể xóa tour thực tế đã phát sinh đơn đặt tour");
        }

        dichVuTourThucTeRepository.deleteByTourThucTe_MaTourThucTe(id);
        hdxTourThucTeRepository.deleteByTourThucTe_MaTourThucTe(id);
        ttt.setTrangThai("HUY");
        tourThucTeRepository.save(ttt);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private void validateTrangThaiTourThucTe(String trangThai) {
        if (!java.util.Set.of("CHO_KICH_HOAT", "MO_BAN",
                "DANG_DIEN_RA", "KET_THUC", "HUY", "DA_QUYET_TOAN").contains(trangThai)) {
            throw AppException.badRequest("Trang thai khong hop le: " + trangThai);
        }
    }

    private void capNhatDichVuTour(TourThucTe ttt, java.util.List<String> maDichVuThem) {
        dichVuTourThucTeRepository.deleteByTourThucTe_MaTourThucTe(ttt.getMaTourThucTe());
        if (maDichVuThem == null) {
            return;
        }
        maDichVuThem.stream()
                .filter(ma -> ma != null && !ma.isBlank())
                .distinct()
                .forEach(ma -> {
                    DichVuThem dichVu = dichVuThemRepository.findById(ma)
                            .orElseThrow(() -> AppException.notFound("Khong tim thay dich vu them: " + ma));
                    DichVuTourThucTe link = new DichVuTourThucTe();
                    link.setId(new DichVuTourThucTeId(ttt.getMaTourThucTe(), dichVu.getMaDichVuThem()));
                    link.setTourThucTe(ttt);
                    link.setDichVuThem(dichVu);
                    dichVuTourThucTeRepository.save(link);
                });
    }

    private void capNhatHanhDongXanhTour(TourThucTe ttt, java.util.List<String> maHanhDongXanh) {
        hdxTourThucTeRepository.deleteByTourThucTe_MaTourThucTe(ttt.getMaTourThucTe());
        if (maHanhDongXanh == null) {
            return;
        }
        maHanhDongXanh.stream()
                .filter(ma -> ma != null && !ma.isBlank())
                .distinct()
                .forEach(ma -> {
                    HanhDongXanh hanhDong = hanhDongXanhRepository.findById(ma)
                            .orElseThrow(() -> AppException.notFound("Khong tim thay hanh dong xanh: " + ma));
                    HdxTourThucTe link = new HdxTourThucTe();
                    link.setId(new HdxTourThucTeId(ttt.getMaTourThucTe(), hanhDong.getMaHanhDongXanh()));
                    link.setTourThucTe(ttt);
                    link.setHanhDongXanh(hanhDong);
                    hdxTourThucTeRepository.save(link);
                });
    }

    private TourThucTeResponse toResponse(TourThucTe ttt) {
        TourMau tm = ttt.getTourMau();
        java.time.LocalDate ngayKetThuc = ttt.getNgayKhoiHanh().plusDays(tm.getThoiLuong() - 1);
        java.util.List<DichVuThemResponse> dichVu = layDichVuTheoTour(ttt);
        java.util.List<HanhDongXanhResponse> hanhDongXanh = layHanhDongXanhTheoTour(ttt);

        return TourThucTeResponse.builder()
                .maTourThucTe(ttt.getMaTourThucTe())
                .maTourMau(tm.getMaTourMau())
                .tieuDeTour(tm.getTieuDe())
                .ngayKhoiHanh(ttt.getNgayKhoiHanh())
                .ngayKetThuc(ngayKetThuc)
                .giaHienHanh(ttt.getGiaHienHanh())
                .soKhachToiDa(ttt.getSoKhachToiDa())
                .soKhachToiThieu(ttt.getSoKhachToiThieu())
                .choConLai(ttt.getChoConLai())
                .trangThai(ttt.getTrangThai())
                .thoiLuong(tm.getThoiLuong())
                .diemDanhGia(tm.getDanhGia())
                .soDanhGia(tm.getSoDanhGia())
                .dichVu(dichVu)
                .hanhDongXanh(hanhDongXanh)
                .build();
    }

    private java.util.List<DichVuThemResponse> layDichVuTheoTour(TourThucTe ttt) {
        return dichVuTourThucTeRepository.findByMaTourThucTe(ttt.getMaTourThucTe()).stream()
                .map(link -> {
                    DichVuThem dv = link.getDichVuThem();
                    return DichVuThemResponse.builder()
                            .maDichVuThem(dv.getMaDichVuThem())
                            .ten(dv.getTen())
                            .donViTinh(dv.getDonViTinh())
                            .donGia(dv.getDonGia())
                            .build();
                })
                .toList();
    }

    private java.util.List<HanhDongXanhResponse> layHanhDongXanhTheoTour(TourThucTe ttt) {
        return hdxTourThucTeRepository.findByMaTourThucTe(ttt.getMaTourThucTe()).stream()
                .map(link -> {
                    HanhDongXanh hdx = link.getHanhDongXanh();
                    return HanhDongXanhResponse.builder()
                            .maHanhDongXanh(hdx.getMaHanhDongXanh())
                            .maTourThucTe(ttt.getMaTourThucTe())
                            .tenHanhDong(hdx.getTenHanhDong())
                            .diemCong(hdx.getDiemCong())
                            .build();
                })
                .toList();
    }
}
