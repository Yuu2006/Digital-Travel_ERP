package com.digitaltravel.erp.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.digitaltravel.erp.config.TaiKhoanDetails;
import com.digitaltravel.erp.dto.requests.CapNhatHoChieuSoRequest;
import com.digitaltravel.erp.dto.requests.DatTourRequest;
import com.digitaltravel.erp.dto.requests.BoSungRequest;
import com.digitaltravel.erp.dto.requests.HuyTourRequest;
import com.digitaltravel.erp.dto.requests.YeuCauHoTroRequest;
import com.digitaltravel.erp.dto.responses.ApiResponse;
import com.digitaltravel.erp.dto.responses.DonDatTourResponse;
import com.digitaltravel.erp.dto.responses.HoChieuSoResponse;
import com.digitaltravel.erp.dto.responses.LichSuTourResponse;
import com.digitaltravel.erp.dto.responses.YeuCauHoTroResponse;
import com.digitaltravel.erp.service.DatTourService;
import com.digitaltravel.erp.service.HuyTourService;
import com.digitaltravel.erp.service.KhachHangService;
import com.digitaltravel.erp.service.YeuCauHoTroService;

import com.digitaltravel.erp.service.DichVuThemService;
import com.digitaltravel.erp.service.HanhDongXanhService;
import com.digitaltravel.erp.dto.responses.DichVuThemResponse;
import com.digitaltravel.erp.dto.responses.HanhDongXanhResponse;
import java.util.List;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/khach-hang")
@RequiredArgsConstructor
public class KhachHangController {

    private final KhachHangService khachHangService;
    private final DatTourService datTourService;
    private final HuyTourService huyTourService;
    private final YeuCauHoTroService yeuCauHoTroService;
    private final DichVuThemService dichVuThemService;
    private final HanhDongXanhService hanhDongXanhService;


    @GetMapping("/ho-so")
    @PreAuthorize("hasAnyRole('KHACHHANG', 'ADMIN')")
    // UC21 - Xem thông tin hồ sơ số: Lấy hồ sơ khách hàng
    public ResponseEntity<ApiResponse<HoChieuSoResponse>> layHoSo(
            @AuthenticationPrincipal TaiKhoanDetails user) {
        String maTaiKhoan = user.getTaiKhoan().getMaTaiKhoan();
        return ResponseEntity.ok(ApiResponse.ok(khachHangService.layHoSo(maTaiKhoan)));
    }

    @PutMapping("/ho-so")
    @PreAuthorize("hasAnyRole('KHACHHANG', 'ADMIN')")
    // UC23 - Cập nhật hồ sơ số: Lưu thông tin khách hàng
    public ResponseEntity<ApiResponse<HoChieuSoResponse>> capNhatHoSo(
            @AuthenticationPrincipal TaiKhoanDetails user,
            @Valid @RequestBody CapNhatHoChieuSoRequest request) {
        String maTaiKhoan = user.getTaiKhoan().getMaTaiKhoan();
        return ResponseEntity.ok(ApiResponse.ok("Cập nhật hồ sơ thành công",
                khachHangService.capNhatHoSo(maTaiKhoan, request)));
    }


    @PostMapping("/dat-tour")
    @PreAuthorize("hasAnyRole('KHACHHANG', 'ADMIN')")
    // UC27 - Đặt tour: Tạo đơn và giữ chỗ tạm thời
    public ResponseEntity<ApiResponse<DonDatTourResponse>> datTour(
            @AuthenticationPrincipal TaiKhoanDetails user,
            @Valid @RequestBody DatTourRequest request) {
        String maTaiKhoan = user.getTaiKhoan().getMaTaiKhoan();
        DonDatTourResponse result = datTourService.datTour(maTaiKhoan, request);
        return ResponseEntity.status(201).body(ApiResponse.created(result));
    }

    @GetMapping("/dat-tour")
    @PreAuthorize("hasAnyRole('KHACHHANG', 'ADMIN')")
    // UC22 - Xem chi tiết lịch sử hành trình: Liệt kê đơn đã đặt
    public ResponseEntity<ApiResponse<Page<DonDatTourResponse>>> danhSachDatTour(
            @AuthenticationPrincipal TaiKhoanDetails user,
            @PageableDefault(size = 10, sort = "ngayDat", direction = Sort.Direction.DESC) Pageable pageable) {
        String maTaiKhoan = user.getTaiKhoan().getMaTaiKhoan();
        return ResponseEntity.ok(ApiResponse.ok(datTourService.danhSachCuaToi(maTaiKhoan, pageable)));
    }

    @GetMapping("/dat-tour/{maDatTour}")
    @PreAuthorize("hasAnyRole('KHACHHANG', 'ADMIN')")
    // UC22 - Xem chi tiết lịch sử hành trình: Xem chi tiết đơn
    public ResponseEntity<ApiResponse<DonDatTourResponse>> chiTietDatTour(
            @AuthenticationPrincipal TaiKhoanDetails user,
            @PathVariable String maDatTour) {
        String maTaiKhoan = user.getTaiKhoan().getMaTaiKhoan();
        return ResponseEntity.ok(ApiResponse.ok(datTourService.chiTietCuaToi(maTaiKhoan, maDatTour)));
    }

    @DeleteMapping("/dat-tour/{maDatTour}")
    @PreAuthorize("hasAnyRole('KHACHHANG', 'ADMIN')")
    // UC32 - Hủy tour: Hủy đơn đặt tour chờ xác nhận
    public ResponseEntity<ApiResponse<Void>> huyDatTour(
            @AuthenticationPrincipal TaiKhoanDetails user,
            @PathVariable String maDatTour) {
        String maTaiKhoan = user.getTaiKhoan().getMaTaiKhoan();
        datTourService.huyDatTour(maTaiKhoan, maDatTour);
        return ResponseEntity.ok(ApiResponse.noContent("ủy đơn đặt tour thành công  (chờ nhân viên duyệt)"));
    }

    @PostMapping("/dat-tour/{maDatTour}/huy")
    @PreAuthorize("hasAnyRole('KHACHHANG', 'ADMIN')")
    // UC32 - Hủy tour: Gửi yêu cầu hủy tour
    public ResponseEntity<ApiResponse<YeuCauHoTroResponse>> yeuCauHuyTour(
            @AuthenticationPrincipal TaiKhoanDetails user,
            @PathVariable String maDatTour,
            @Valid @RequestBody HuyTourRequest request) {
        String maTaiKhoan = user.getTaiKhoan().getMaTaiKhoan();
        YeuCauHoTroResponse result = huyTourService.yeuCauHuyTour(maTaiKhoan, maDatTour, request);
        return ResponseEntity.ok(ApiResponse.ok("Gửi yêu cầu hủy tour thành công. Đang chờ nhân viên duyệt.", result));
    }

    @GetMapping("/lich-su-tour")
    @PreAuthorize("hasAnyRole('KHACHHANG', 'ADMIN')")
    // UC22 - Xem chi tiết lịch sử hành trình: Tra cứu lịch sử tour
    public ResponseEntity<ApiResponse<Page<LichSuTourResponse>>> lichSuTour(
            @AuthenticationPrincipal TaiKhoanDetails user,
            @PageableDefault(size = 10, sort = "NgayThamGia", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(
                khachHangService.lichSuTour(user.getTaiKhoan().getMaTaiKhoan(), pageable)));
    }


    @PostMapping("/yeu-cau-ho-tro")
    @PreAuthorize("hasAnyRole('KHACHHANG', 'ADMIN')")
    // UC36 - Khiếu nại: Tạo yêu cầu hỗ trợ
    public ResponseEntity<ApiResponse<YeuCauHoTroResponse>> taoYeuCau(
            @AuthenticationPrincipal TaiKhoanDetails user,
            @Valid @RequestBody YeuCauHoTroRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.created(
                yeuCauHoTroService.taoYeuCau(user.getTaiKhoan().getMaTaiKhoan(), request)));
    }

    @GetMapping("/yeu-cau-ho-tro")
    @PreAuthorize("hasAnyRole('KHACHHANG', 'ADMIN')")
    // UC36 - Khiếu nại: Tra cứu yêu cầu của khách hàng
    public ResponseEntity<ApiResponse<Page<YeuCauHoTroResponse>>> danhSachYeuCauCuaToi(
            @AuthenticationPrincipal TaiKhoanDetails user,
            @RequestParam(required = false) String loaiYeuCau,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(
                yeuCauHoTroService.danhSachCuaKhachHang(user.getTaiKhoan().getMaTaiKhoan(), loaiYeuCau, pageable)));
    }

    @GetMapping("/yeu-cau-ho-tro/can-bo-sung")
    @PreAuthorize("hasAnyRole('KHACHHANG', 'ADMIN')")
    // UC39 - Giải quyết khiếu nại: Lấy yêu cầu cần bổ sung
    public ResponseEntity<ApiResponse<List<YeuCauHoTroResponse>>> danhSachYeuCauCanBoSung(
            @AuthenticationPrincipal TaiKhoanDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(
                yeuCauHoTroService.danhSachCanKhachHangBoSung(user.getTaiKhoan().getMaTaiKhoan())));
    }

    @PutMapping("/yeu-cau-ho-tro/{maYeuCau}/bo-sung")
    @PreAuthorize("hasAnyRole('KHACHHANG', 'ADMIN')")
    // UC39 - Giải quyết khiếu nại: Bổ sung thông tin khiếu nại
    public ResponseEntity<ApiResponse<YeuCauHoTroResponse>> boSungYeuCau(
            @AuthenticationPrincipal TaiKhoanDetails user,
            @PathVariable String maYeuCau,
            @Valid @RequestBody BoSungRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(
                yeuCauHoTroService.boSung(user.getTaiKhoan().getMaTaiKhoan(), maYeuCau, request.getNoiDung())));
    }

    @GetMapping("/dich-vu-them")
    @PreAuthorize("hasAnyRole('KHACHHANG', 'ADMIN')")
    // UC27 - Đặt tour: Lấy dịch vụ đi kèm
    public ResponseEntity<ApiResponse<List<DichVuThemResponse>>> danhSachDichVuThem(
            @RequestParam(required = false) String maTourThucTe) {
        return ResponseEntity.ok(ApiResponse.ok(dichVuThemService.danhSach(maTourThucTe)));
    }

    @GetMapping("/hanh-dong-xanh")
    @PreAuthorize("hasAnyRole('KHACHHANG', 'ADMIN')")
    // UC27 - Đặt tour: Lấy hành động xanh để chọn
    public ResponseEntity<ApiResponse<List<HanhDongXanhResponse>>> danhSachHanhDongXanh(
            @RequestParam(required = false) String maTourThucTe) {
        return ResponseEntity.ok(ApiResponse.ok(hanhDongXanhService.danhSach(maTourThucTe)));
    }
}
