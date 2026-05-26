package com.digitaltravel.erp.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.digitaltravel.erp.config.TaiKhoanDetails;
import com.digitaltravel.erp.dto.requests.BaoCaoSuCoRequest;
import com.digitaltravel.erp.dto.requests.DiemDanhRequest;
import com.digitaltravel.erp.dto.requests.GhiNhanHanhDongRequest;
import com.digitaltravel.erp.dto.requests.KhaiChiPhiRequest;
import com.digitaltravel.erp.dto.responses.ApiResponse;
import com.digitaltravel.erp.dto.responses.CanhBaoChiPhiResponse;
import com.digitaltravel.erp.dto.responses.ChiPhiThucTeResponse;
import com.digitaltravel.erp.dto.responses.DiemDanhResponse;
import com.digitaltravel.erp.dto.responses.HanhDongResponse;
import com.digitaltravel.erp.dto.responses.LichTrinhResponse;
import com.digitaltravel.erp.dto.responses.NhatKySuCoResponse;
import com.digitaltravel.erp.dto.responses.ThanhVienDoanResponse;
import com.digitaltravel.erp.service.VanHanhService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class VanHanhController {

    private final VanHanhService vanHanhService;

    @GetMapping("/api/huong-dan-vien/tour/{maTour}/lich-trinh")
    @PreAuthorize("hasAnyRole('HDV', 'DIEUHANH', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<LichTrinhResponse>>> lichTrinhTour(
            @PathVariable String maTour,
            @AuthenticationPrincipal TaiKhoanDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(vanHanhService.lichTrinhTour(
                maTour,
                user.getTaiKhoan().getMaTaiKhoan(),
                user.getTaiKhoan().getVaiTro().getMaVaiTro())));
    }

    // ── UC42: Xem danh sách đoàn ─────────────────────────────────────────
    @GetMapping({"/api/huong-dan-vien/tour/{maTour}/doan", "/api/dieu-hanh/tour/{maTour}/doan"})
    @PreAuthorize("hasAnyRole('HDV', 'DIEUHANH', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<ThanhVienDoanResponse>>> danhSachDoan(
            @PathVariable String maTour,
            @AuthenticationPrincipal TaiKhoanDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(vanHanhService.danhSachDoan(
                maTour,
                user.getTaiKhoan().getMaTaiKhoan(),
                user.getTaiKhoan().getVaiTro().getMaVaiTro())));
    }

    // ── UC43: Điểm danh khách ─────────────────────────────────────────────
    @PostMapping("/api/huong-dan-vien/tour/{maTour}/diem-danh")
    @PreAuthorize("hasAnyRole('HDV', 'DIEUHANH', 'ADMIN')")
    public ResponseEntity<ApiResponse<DiemDanhResponse>> diemDanh(
            @PathVariable String maTour,
            @Valid @RequestBody DiemDanhRequest request,
            @AuthenticationPrincipal TaiKhoanDetails user) {
        DiemDanhResponse resp = vanHanhService.diemDanh(
                maTour,
                request,
                user.getTaiKhoan().getMaTaiKhoan(),
                user.getTaiKhoan().getVaiTro().getMaVaiTro());
        return ResponseEntity.status(201).body(ApiResponse.created(resp));
    }

    // ── UC44: Ghi nhận hành động xanh ────────────────────────────────────
    @PostMapping("/api/huong-dan-vien/tour/{maTour}/hanh-dong-xanh")
    @PreAuthorize("hasAnyRole('HDV', 'DIEUHANH', 'ADMIN')")
    public ResponseEntity<ApiResponse<HanhDongResponse>> ghiNhanHanhDong(
            @PathVariable String maTour,
            @Valid @RequestBody GhiNhanHanhDongRequest request,
            @AuthenticationPrincipal TaiKhoanDetails user) {
        HanhDongResponse resp = vanHanhService.ghiNhanHanhDong(
                maTour,
                request,
                user.getTaiKhoan().getMaTaiKhoan(),
                user.getTaiKhoan().getVaiTro().getMaVaiTro());
        return ResponseEntity.status(201).body(ApiResponse.created(resp));
    }

    // ── UC45: Sự cố ───────────────────────────────────────────────────────
    @GetMapping({"/api/huong-dan-vien/tour/{maTour}/su-co", "/api/dieu-hanh/tour/{maTour}/su-co"})
    @PreAuthorize("hasAnyRole('HDV', 'DIEUHANH', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<NhatKySuCoResponse>>> danhSachSuCo(
            @PathVariable String maTour,
            @RequestParam(required = false) String mucDo,
            @AuthenticationPrincipal TaiKhoanDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(vanHanhService.danhSachSuCo(
                maTour,
                mucDo,
                user.getTaiKhoan().getMaTaiKhoan(),
                user.getTaiKhoan().getVaiTro().getMaVaiTro())));
    }

    @GetMapping("/api/huong-dan-vien/su-co")
    @PreAuthorize("hasAnyRole('ADMIN', 'SANPHAM', 'KINHDOANH', 'DIEUHANH', 'KETOAN', 'HDV')")
    public ResponseEntity<ApiResponse<List<NhatKySuCoResponse>>> lichSuSuCoCuaHdv(
            @RequestParam(required = false) String mucDo,
            @AuthenticationPrincipal TaiKhoanDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(vanHanhService.lichSuSuCoCuaHdv(
                mucDo,
                user.getTaiKhoan().getMaTaiKhoan(),
                user.getTaiKhoan().getVaiTro().getMaVaiTro())));
    }

    @PostMapping("/api/huong-dan-vien/tour/{maTour}/su-co")
    @PreAuthorize("hasAnyRole('HDV', 'DIEUHANH', 'ADMIN')")
    public ResponseEntity<ApiResponse<NhatKySuCoResponse>> baoCaoSuCo(
            @PathVariable String maTour,
            @Valid @RequestBody BaoCaoSuCoRequest request,
            @AuthenticationPrincipal TaiKhoanDetails user) {
        NhatKySuCoResponse resp = vanHanhService.baoCaoSuCo(
                maTour,
                request,
                user.getTaiKhoan().getMaTaiKhoan(),
                user.getTaiKhoan().getVaiTro().getMaVaiTro());
        return ResponseEntity.status(201).body(ApiResponse.created(resp));
    }

    @PutMapping("/api/huong-dan-vien/su-co/{maSuCo}")
    @PreAuthorize("hasAnyRole('HDV', 'DIEUHANH', 'ADMIN')")
    public ResponseEntity<ApiResponse<NhatKySuCoResponse>> capNhatSuCo(
            @PathVariable String maSuCo,
            @Valid @RequestBody BaoCaoSuCoRequest request,
            @AuthenticationPrincipal TaiKhoanDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(vanHanhService.capNhatSuCo(
                maSuCo,
                request,
                user.getTaiKhoan().getMaTaiKhoan(),
                user.getTaiKhoan().getVaiTro().getMaVaiTro())));
    }

    // ── UC46: Chi phí phát sinh ───────────────────────────────────────────
    @PostMapping("/api/huong-dan-vien/tour/{maTour}/chi-phi")
    @PreAuthorize("hasAnyRole('HDV', 'DIEUHANH', 'ADMIN')")
    public ResponseEntity<ApiResponse<ChiPhiThucTeResponse>> khaiChiPhi(
            @PathVariable String maTour,
            @Valid @RequestBody KhaiChiPhiRequest request,
            @AuthenticationPrincipal TaiKhoanDetails user) {
        ChiPhiThucTeResponse resp = vanHanhService.khaiChiPhi(
                maTour,
                request,
                user.getTaiKhoan().getMaTaiKhoan(),
                user.getTaiKhoan().getVaiTro().getMaVaiTro());
        return ResponseEntity.status(201).body(ApiResponse.created(resp));
    }

    @GetMapping({"/api/huong-dan-vien/tour/{maTour}/chi-phi", "/api/dieu-hanh/tour/{maTour}/chi-phi"})
    @PreAuthorize("hasAnyRole('HDV', 'DIEUHANH', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<ChiPhiThucTeResponse>>> chiPhiCuaTour(
            @PathVariable String maTour,
            @AuthenticationPrincipal TaiKhoanDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(vanHanhService.chiPhiCuaTour(
                maTour,
                user.getTaiKhoan().getMaTaiKhoan(),
                user.getTaiKhoan().getVaiTro().getMaVaiTro())));
    }

    @GetMapping("/api/huong-dan-vien/chi-phi")
    @PreAuthorize("hasAnyRole('HDV', 'DIEUHANH', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<ChiPhiThucTeResponse>>> lichSuChiPhiCuaHdv(
            @AuthenticationPrincipal TaiKhoanDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(vanHanhService.lichSuChiPhiCuaHdv(
                user.getTaiKhoan().getMaTaiKhoan(),
                user.getTaiKhoan().getVaiTro().getMaVaiTro())));
    }

    @PutMapping("/api/huong-dan-vien/chi-phi/{maChiPhi}/bo-sung")
    @PreAuthorize("hasAnyRole('HDV', 'DIEUHANH', 'ADMIN')")
    public ResponseEntity<ApiResponse<ChiPhiThucTeResponse>> boSungChiPhi(
            @PathVariable String maChiPhi,
            @Valid @RequestBody KhaiChiPhiRequest request,
            @AuthenticationPrincipal TaiKhoanDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(vanHanhService.boSungChiPhi(
                maChiPhi,
                request,
                user.getTaiKhoan().getMaTaiKhoan())));
    }

    // ── Kế toán: DS chi phí chờ duyệt / duyệt / từ chối ─────────────────
    @GetMapping("/api/ke-toan/chi-phi")
    @PreAuthorize("hasAnyRole('KETOAN', 'ADMIN')")
    public ResponseEntity<ApiResponse<Page<ChiPhiThucTeResponse>>> danhSachChiPhi(
            @RequestParam(required = false) String trangThai,
            @RequestParam(required = false) String maTour,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(vanHanhService.danhSachChiPhiChoXuLy(trangThai, maTour, pageable)));
    }

    @PutMapping("/api/ke-toan/chi-phi/{maChiPhi}/duyet")
    @PreAuthorize("hasAnyRole('KETOAN', 'ADMIN')")
    public ResponseEntity<ApiResponse<ChiPhiThucTeResponse>> duyetChiPhi(
            @PathVariable String maChiPhi) {
        return ResponseEntity.ok(ApiResponse.ok(vanHanhService.duyetChiPhi(maChiPhi)));
    }

    @PutMapping("/api/ke-toan/chi-phi/{maChiPhi}/tu-choi")
    @PreAuthorize("hasAnyRole('KETOAN', 'ADMIN')")
    public ResponseEntity<ApiResponse<ChiPhiThucTeResponse>> tuChoiChiPhi(
            @PathVariable String maChiPhi) {
        return ResponseEntity.ok(ApiResponse.ok(vanHanhService.tuChoiChiPhi(maChiPhi)));
    }

    @PutMapping("/api/ke-toan/chi-phi/{maChiPhi}/yeu-cau-bo-sung")
    @PreAuthorize("hasAnyRole('KETOAN', 'ADMIN')")
    public ResponseEntity<ApiResponse<ChiPhiThucTeResponse>> yeuCauBoSungChiPhi(
            @PathVariable String maChiPhi) {
        return ResponseEntity.ok(ApiResponse.ok(vanHanhService.yeuCauBoSungChiPhi(maChiPhi)));
    }

    @GetMapping("/api/ke-toan/canh-bao-chi-phi")
    @PreAuthorize("hasAnyRole('KETOAN', 'ADMIN')")
    public ResponseEntity<ApiResponse<Page<CanhBaoChiPhiResponse>>> danhSachCanhBaoChiPhi(
            @RequestParam(required = false) String trangThai,
            @RequestParam(required = false) String loaiCanhBao,
            @RequestParam(required = false) String mucDo,
            @RequestParam(required = false) String maTour,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(
                vanHanhService.danhSachCanhBaoChiPhi(trangThai, loaiCanhBao, mucDo, maTour, pageable)));
    }

    @GetMapping("/api/ke-toan/canh-bao-chi-phi/{maCanhBao}")
    @PreAuthorize("hasAnyRole('KETOAN', 'ADMIN')")
    public ResponseEntity<ApiResponse<CanhBaoChiPhiResponse>> chiTietCanhBaoChiPhi(
            @PathVariable String maCanhBao) {
        return ResponseEntity.ok(ApiResponse.ok(vanHanhService.chiTietCanhBaoChiPhi(maCanhBao)));
    }
}
