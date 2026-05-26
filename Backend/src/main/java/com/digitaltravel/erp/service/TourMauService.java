package com.digitaltravel.erp.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.digitaltravel.erp.dto.requests.CapNhatTourMauRequest;
import com.digitaltravel.erp.dto.requests.LichTrinhRequest;
import com.digitaltravel.erp.dto.requests.TaoTourMauRequest;
import com.digitaltravel.erp.dto.responses.LichTrinhResponse;
import com.digitaltravel.erp.dto.responses.TourMauChiTietResponse;
import com.digitaltravel.erp.dto.responses.TourMauResponse;
import com.digitaltravel.erp.entity.LichTrinhTour;
import com.digitaltravel.erp.entity.TourMau;
import com.digitaltravel.erp.exception.AppException;
import com.digitaltravel.erp.repository.LichTrinhTourRepository;
import com.digitaltravel.erp.repository.TourMauRepository;
import com.digitaltravel.erp.repository.TourThucTeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TourMauService {

    private final TourMauRepository tourMauRepository;
    private final LichTrinhTourRepository lichTrinhTourRepository;
    private final TourThucTeRepository tourThucTeRepository;
    private final MaTuDongService maTuDongService;

    // ── UC06: Danh sách tour mẫu (filter + phân trang) ──────────────────────
    public Page<TourMauResponse> danhSach(String tieuDe, Integer thoiLuongMin, Integer thoiLuongMax,
                                           Pageable pageable) {
        return tourMauRepository.timKiem(tieuDe, thoiLuongMin, thoiLuongMax, pageable)
                .map(this::toResponse);
    }

    // ── UC06: Chi tiết tour mẫu + lịch trình ────────────────────────────────
    public TourMauChiTietResponse chiTiet(String id) {
        TourMau tm = tourMauRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Khong tim thay tour mau: " + id));
        List<LichTrinhTour> lichTrinh = lichTrinhTourRepository.findByMaTourMau(id);
        return toChiTietResponse(tm, lichTrinh);
    }

    // ── UC02: Thêm tour mẫu ─────────────────────────────────────────────────
    @Transactional
    public TourMauChiTietResponse taoMoi(TaoTourMauRequest request, String nguoiTao) {
        TourMau tm = new TourMau();
        tm.setMaTourMau(maTuDongService.taoMaTourMau());
        tm.setTieuDe(request.getTieuDe());
        tm.setMoTa(request.getMoTa());
        tm.setThoiLuong(request.getThoiLuong());
        tm.setGiaSan(request.getGiaSan());
        tm.setDanhGia(null);
        tm.setSoDanhGia(0);
        tourMauRepository.save(tm);

        // Tạo lịch trình nếu có
        if (request.getLichTrinh() != null) {
            List<String> maLichTrinh = maTuDongService.taoDanhSachMaLichTrinhTour(request.getLichTrinh().size());
            for (int i = 0; i < request.getLichTrinh().size(); i++) {
                LichTrinhRequest ltReq = request.getLichTrinh().get(i);
                validateLichTrinh(tm, ltReq);
                LichTrinhTour lt = new LichTrinhTour();
                lt.setMaLichTrinhTour(maLichTrinh.get(i));
                lt.setTourMau(tm);
                lt.setNgayThu(ltReq.getNgayThu());
                lt.setHoatDong(ltReq.getHoatDong());
                lt.setMoTa(ltReq.getMoTa());
                lt.setThucDon(ltReq.getThucDon());
                lichTrinhTourRepository.save(lt);
            }
        }

        List<LichTrinhTour> lichTrinh = lichTrinhTourRepository.findByMaTourMau(tm.getMaTourMau());
        return toChiTietResponse(tm, lichTrinh);
    }

    // ── UC04: Sửa tour mẫu ──────────────────────────────────────────────────
    @Transactional
    public TourMauResponse capNhat(String id, CapNhatTourMauRequest request, String nguoiCapNhat) {
        TourMau tm = tourMauRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Khong tim thay tour mau: " + id));

        if (request.getTieuDe() != null) tm.setTieuDe(request.getTieuDe());
        if (request.getMoTa() != null) tm.setMoTa(request.getMoTa());
        if (request.getThoiLuong() != null) tm.setThoiLuong(request.getThoiLuong());
        if (request.getGiaSan() != null) tm.setGiaSan(request.getGiaSan());
        tourMauRepository.save(tm);
        return toResponse(tm);
    }

    // ── UC05: Xóa mềm tour mẫu ─────────────────────────────────────────────
    @Transactional
    public void xoaMem(String id) {
        TourMau tm = tourMauRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Khong tim thay tour mau: " + id));

        if (tourThucTeRepository.existsByMaTourMau(id)) {
            throw AppException.badRequest("Không thể xóa tour mẫu vì đã có tour thực tế liên kết");
        }

        lichTrinhTourRepository.deleteAll(lichTrinhTourRepository.findByMaTourMau(id));
        tourMauRepository.delete(tm);
    }

    // ── UC03: Sao chép tour mẫu ─────────────────────────────────────────────
    @Transactional
    public TourMauChiTietResponse saoChep(String id, String nguoiTao) {
        TourMau goc = tourMauRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Khong tim thay tour mau: " + id));

        TourMau banSao = new TourMau();
        banSao.setMaTourMau(maTuDongService.taoMaTourMau());
        banSao.setTieuDe("[Sao chep] " + goc.getTieuDe());
        banSao.setMoTa(goc.getMoTa());
        banSao.setThoiLuong(goc.getThoiLuong());
        banSao.setGiaSan(goc.getGiaSan());
        banSao.setDanhGia(null);
        banSao.setSoDanhGia(0);
        tourMauRepository.save(banSao);

        // Deep copy lịch trình
        List<LichTrinhTour> lichTrinhGoc = lichTrinhTourRepository.findByMaTourMau(id);
        List<String> maLichTrinh = maTuDongService.taoDanhSachMaLichTrinhTour(lichTrinhGoc.size());
        for (int i = 0; i < lichTrinhGoc.size(); i++) {
            LichTrinhTour ltGoc = lichTrinhGoc.get(i);
            LichTrinhTour ltMoi = new LichTrinhTour();
            ltMoi.setMaLichTrinhTour(maLichTrinh.get(i));
            ltMoi.setTourMau(banSao);
            ltMoi.setNgayThu(ltGoc.getNgayThu());
            ltMoi.setHoatDong(ltGoc.getHoatDong());
            ltMoi.setMoTa(ltGoc.getMoTa());
            ltMoi.setThucDon(ltGoc.getThucDon());
            lichTrinhTourRepository.save(ltMoi);
        }

        List<LichTrinhTour> lichTrinhMoi = lichTrinhTourRepository.findByMaTourMau(banSao.getMaTourMau());
        return toChiTietResponse(banSao, lichTrinhMoi);
    }

    // ── UC08: Thêm ngày lịch trình ──────────────────────────────────────────
    @Transactional
    public LichTrinhResponse themLichTrinh(String maTourMau, LichTrinhRequest request) {
        TourMau tm = tourMauRepository.findById(maTourMau)
                .orElseThrow(() -> AppException.notFound("Khong tim thay tour mau: " + maTourMau));

        validateLichTrinh(tm, request);

        LichTrinhTour lt = new LichTrinhTour();
        lt.setMaLichTrinhTour(maTuDongService.taoMaLichTrinhTour());
        lt.setTourMau(tm);
        lt.setNgayThu(request.getNgayThu());
        lt.setHoatDong(request.getHoatDong());
        lt.setMoTa(request.getMoTa());
        lt.setThucDon(request.getThucDon());
        lichTrinhTourRepository.save(lt);

        return toLichTrinhResponse(lt);
    }

    // ── UC09: Sửa lịch trình ────────────────────────────────────────────────
    @Transactional
    public LichTrinhResponse suaLichTrinh(String maTourMau, String maLichTrinh, LichTrinhRequest request) {
        tourMauRepository.findById(maTourMau)
                .orElseThrow(() -> AppException.notFound("Khong tim thay tour mau: " + maTourMau));

        LichTrinhTour lt = lichTrinhTourRepository.findById(maLichTrinh)
                .orElseThrow(() -> AppException.notFound("Khong tim thay lich trinh: " + maLichTrinh));

        if (!lt.getTourMau().getMaTourMau().equals(maTourMau)) {
            throw AppException.badRequest("Lịch trình không thuộc tour mẫu này");
        }

        // Kiểm tra ngày thứ không trùng (trừ chính nó)
        if (request.getNgayThu() != null && !request.getNgayThu().equals(lt.getNgayThu())) {
            if (lichTrinhTourRepository.existsByTourMauAndNgayThuExcluding(maTourMau, request.getNgayThu(), maLichTrinh)) {
                throw AppException.badRequest("Ngay thu " + request.getNgayThu() + " da ton tai trong tour nay");
            }
            TourMau tm = lt.getTourMau();
            if (request.getNgayThu() > tm.getThoiLuong()) {
                throw AppException.badRequest("Ngay thu khong duoc vuot qua thoi luong tour (" + tm.getThoiLuong() + " ngay)");
            }
            lt.setNgayThu(request.getNgayThu());
        }

        if (request.getHoatDong() != null) lt.setHoatDong(request.getHoatDong());
        if (request.getMoTa() != null) lt.setMoTa(request.getMoTa());
        if (request.getThucDon() != null) lt.setThucDon(request.getThucDon());
        lichTrinhTourRepository.save(lt);

        return toLichTrinhResponse(lt);
    }

    // ── UC09: Xóa ngày lịch trình ───────────────────────────────────────────
    @Transactional
    public void xoaLichTrinh(String maTourMau, String maLichTrinh) {
        LichTrinhTour lt = lichTrinhTourRepository.findById(maLichTrinh)
                .orElseThrow(() -> AppException.notFound("Khong tim thay lich trinh: " + maLichTrinh));

        if (!lt.getTourMau().getMaTourMau().equals(maTourMau)) {
            throw AppException.badRequest("Lịch trình không thuộc tour mẫu này");
        }

        lichTrinhTourRepository.delete(lt);
    }

    // ── Validation helpers ───────────────────────────────────────────────────

    private void validateLichTrinh(TourMau tm, LichTrinhRequest request) {
        if (request.getNgayThu() > tm.getThoiLuong()) {
            throw AppException.badRequest("Ngay thu " + request.getNgayThu()
                    + " vuot qua thoi luong tour (" + tm.getThoiLuong() + " ngay)");
        }
        if (lichTrinhTourRepository.existsByTourMauAndNgayThu(tm.getMaTourMau(), request.getNgayThu())) {
            throw AppException.badRequest("Ngay thu " + request.getNgayThu() + " da ton tai trong tour nay");
        }
    }

    // ── Mapping helpers ──────────────────────────────────────────────────────

    private TourMauResponse toResponse(TourMau tm) {
        return TourMauResponse.builder()
                .maTourMau(tm.getMaTourMau())
                .tieuDe(tm.getTieuDe())
                .moTa(tm.getMoTa())
                .thoiLuong(tm.getThoiLuong())
                .giaSan(tm.getGiaSan())
                .danhGia(tm.getDanhGia())
                .soDanhGia(tm.getSoDanhGia())
                .build();
    }

    private TourMauChiTietResponse toChiTietResponse(TourMau tm, List<LichTrinhTour> lichTrinh) {
        List<LichTrinhResponse> lichTrinhRes = lichTrinh.stream()
                .map(this::toLichTrinhResponse)
                .toList();

        return TourMauChiTietResponse.builder()
                .maTourMau(tm.getMaTourMau())
                .tieuDe(tm.getTieuDe())
                .moTa(tm.getMoTa())
                .thoiLuong(tm.getThoiLuong())
                .giaSan(tm.getGiaSan())
                .danhGia(tm.getDanhGia())
                .soDanhGia(tm.getSoDanhGia())
                .lichTrinh(lichTrinhRes)
                .build();
    }

    private LichTrinhResponse toLichTrinhResponse(LichTrinhTour lt) {
        return LichTrinhResponse.builder()
                .maLichTrinhTour(lt.getMaLichTrinhTour())
                .ngayThu(lt.getNgayThu())
                .hoatDong(lt.getHoatDong())
                .moTa(lt.getMoTa())
                .thucDon(lt.getThucDon())
                .build();
    }
}
