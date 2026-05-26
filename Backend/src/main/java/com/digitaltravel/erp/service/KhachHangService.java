package com.digitaltravel.erp.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.digitaltravel.erp.dto.requests.CapNhatHoChieuSoRequest;
import com.digitaltravel.erp.dto.responses.HoChieuSoResponse;
import com.digitaltravel.erp.dto.responses.LichSuTourResponse;
import com.digitaltravel.erp.entity.HoChieuSo;
import com.digitaltravel.erp.entity.LichSuTour;
import com.digitaltravel.erp.entity.DonDatTour;
import com.digitaltravel.erp.exception.AppException;
import com.digitaltravel.erp.repository.DonDatTourRepository;
import com.digitaltravel.erp.repository.HoChieuSoRepository;
import com.digitaltravel.erp.repository.LichSuTourRepository;
import com.digitaltravel.erp.repository.TaiKhoanRepository;
import com.digitaltravel.erp.repository.DanhGiaKhRepository;
import com.digitaltravel.erp.repository.YeuCauHoTroRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KhachHangService {

    private final HoChieuSoRepository hoChieuSoRepository;
    private final LichSuTourRepository lichSuTourRepository;
    private final DonDatTourRepository donDatTourRepository;
    private final TaiKhoanRepository taiKhoanRepository;
    private final DanhGiaKhRepository danhGiaKhRepository;
    private final YeuCauHoTroRepository yeuCauHoTroRepository;

    // ── Xem hồ sơ của bản thân ──────────────────────────────────────────────
    public HoChieuSoResponse layHoSo(String maTaiKhoan) {
        HoChieuSo hcs = hoChieuSoRepository.findByMaTaiKhoan(maTaiKhoan)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy hồ sơ khách hàng"));
        return toResponse(hcs);
    }

    // ── Cập nhật hồ sơ khách hàng ───────────────────────────────────────────
    @Transactional
    public HoChieuSoResponse capNhatHoSo(String maTaiKhoan, CapNhatHoChieuSoRequest request) {
        HoChieuSo hcs = hoChieuSoRepository.findByMaTaiKhoan(maTaiKhoan)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy hồ sơ khách hàng"));

        if (request.getCccd() != null) {
            if (!request.getCccd().isBlank()
                    && taiKhoanRepository.existsByCccdAndMaTaiKhoanNot(request.getCccd(), hcs.getTaiKhoan().getMaTaiKhoan())) {
                throw AppException.badRequest("CCCD đã được sử dụng");
            }
            hcs.getTaiKhoan().setCccd(request.getCccd());
        }
        if (request.getNgaySinh() != null) {
            hcs.getTaiKhoan().setNgaySinh(request.getNgaySinh());
        }
        if (request.getSoDienThoai() != null) {
            hcs.getTaiKhoan().setSoDienThoai(request.getSoDienThoai());
        }
        if (request.getDiUng() != null) {
            hcs.setDiUng(request.getDiUng());
        }
        if (request.getGhiChuYTe() != null) {
            hcs.setGhiChuYTe(request.getGhiChuYTe());
        }
        if (request.getTenDangNhap() != null) {
            hcs.getTaiKhoan().setTenDangNhap(request.getTenDangNhap());
        }
        if (request.getEmail() != null) {
            hcs.getTaiKhoan().setEmail(request.getEmail());
        }
        hoChieuSoRepository.save(hcs);
        return toResponse(hcs);
    }

    // ── UC22: Lịch sử tour đã tham gia ──────────────────────────────────────
    @Transactional(readOnly = true)
    public Page<LichSuTourResponse> lichSuTour(String maTaiKhoan, Pageable pageable) {
        HoChieuSo hcs = hoChieuSoRepository.findByMaTaiKhoan(maTaiKhoan)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy hồ sơ khách hàng"));
        return lichSuTourRepository.findByMaKhachHang(hcs.getMaKhachHang(), pageable)
                .map(this::toLichSuTourResponseTuLichSu);
    }

    // ── Mapper ────────────────────────────────────────────────────────────────
    public HoChieuSoResponse toResponse(HoChieuSo hcs) {
        return HoChieuSoResponse.builder()
                .maKhachHang(hcs.getMaKhachHang())
                .maTaiKhoan(hcs.getTaiKhoan().getMaTaiKhoan())
                .tenDangNhap(hcs.getTaiKhoan().getTenDangNhap())
                .hoTen(hcs.getTaiKhoan().getHoTen())
                .email(hcs.getTaiKhoan().getEmail())
                .cccd(hcs.getTaiKhoan().getCccd())
                .ngaySinh(hcs.getTaiKhoan().getNgaySinh())
                .soDienThoai(hcs.getTaiKhoan().getSoDienThoai())
                .trangThaiTaiKhoan(hcs.getTaiKhoan().getTrangThai())
                .diUng(hcs.getDiUng())
                .ghiChuYTe(hcs.getGhiChuYTe())
                .hangThanhVien(hcs.getHangThanhVien())
                .diemXanh(hcs.getDiemXanh())
                .build();
    }

    private LichSuTourResponse toLichSuTourResponseTuLichSu(LichSuTour lst) {
        DonDatTour don = lst.getChiTietDatTour() != null ? lst.getChiTietDatTour().getDonDatTour() : null;
        String trangThaiKhieuNai = don != null ? yeuCauHoTroRepository
                .findByMaDatTourAndLoaiYeuCau(don.getMaDatTour(), "KHIEU_NAI")
                .stream()
                .findFirst()
                .map(yc -> yc.getTrangThai())
                .orElse(null) : null;

        return LichSuTourResponse.builder()
                .maLichSuTour(lst.getMaLichSuTour())
                .maDatTour(don != null ? don.getMaDatTour() : null)
                .maTourThucTe(lst.getTourThucTe().getMaTourThucTe())
                .tieuDeTour(lst.getTourThucTe().getTourMau().getTieuDe())
                .ngayKhoiHanh(lst.getTourThucTe().getNgayKhoiHanh())
                .thoiLuong(lst.getTourThucTe().getTourMau().getThoiLuong())
                .ngayDat(don != null ? don.getNgayDat() : null)
                .ngayThamGia(lst.getNgayThamGia() != null ? lst.getNgayThamGia() : lst.getTourThucTe().getNgayKhoiHanh())
                .trangThai(don != null ? don.getTrangThai() : "KET_THUC")
                .trangThaiTour(lst.getTourThucTe().getTrangThai())
                .tongTien(don != null ? don.getTongTien() : null)
                .daDanhGia(danhGiaKhRepository.existsByKhachHangAndTour(
                        lst.getKhachHang().getMaKhachHang(),
                        lst.getTourThucTe().getMaTourThucTe()))
                .daKhieuNai(trangThaiKhieuNai != null)
                .trangThaiKhieuNai(trangThaiKhieuNai)
                .build();
    }
}
