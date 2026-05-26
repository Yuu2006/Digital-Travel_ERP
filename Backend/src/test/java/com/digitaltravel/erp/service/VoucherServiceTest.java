package com.digitaltravel.erp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.digitaltravel.erp.dto.requests.VoucherRequest;
import com.digitaltravel.erp.dto.responses.VoucherResponse;
import com.digitaltravel.erp.entity.Voucher;
import com.digitaltravel.erp.exception.AppException;
import com.digitaltravel.erp.repository.DatTourUuDaiRepository;
import com.digitaltravel.erp.repository.DonDatTourRepository;
import com.digitaltravel.erp.repository.HoChieuSoRepository;
import com.digitaltravel.erp.repository.KhuyenMaiKhRepository;
import com.digitaltravel.erp.repository.NhatKyDoiDiemRepository;
import com.digitaltravel.erp.repository.VoucherRepository;

@ExtendWith(MockitoExtension.class)
class VoucherServiceTest {

    @Mock
    private VoucherRepository voucherRepository;

    @Mock
    private KhuyenMaiKhRepository khuyenMaiKhRepository;

    @Mock
    private DatTourUuDaiRepository datTourUuDaiRepository;

    @Mock
    private DonDatTourRepository donDatTourRepository;

    @Mock
    private HoChieuSoRepository hoChieuSoRepository;

    @Mock
    private NhatKyDoiDiemRepository nhatKyDoiDiemRepository;

    @InjectMocks
    private VoucherService voucherService;

    @Test
    void taoVoucher_taoMoiThanhCong() {
        VoucherRequest request = taoRequest("SUMMER10", "PHAN_TRAM", BigDecimal.TEN,
                "Ap dung cho don hang tu 1 trieu", 100,
                LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 30));
        setField(request, "mucGiamToiDa", BigDecimal.valueOf(500_000));
        when(voucherRepository.findByMaCode("SUMMER10")).thenReturn(Optional.empty());
        when(voucherRepository.findMaxVoucherNumber()).thenReturn(0);
        when(voucherRepository.save(any(Voucher.class))).thenAnswer(invocation -> invocation.getArgument(0));

        VoucherResponse response = voucherService.taoVoucher(request, "admin");

        ArgumentCaptor<Voucher> captor = ArgumentCaptor.forClass(Voucher.class);
        verify(voucherRepository).save(captor.capture());
        Voucher saved = captor.getValue();

        assertEquals("VC1", response.getMaVoucher());
        assertEquals("SUMMER10", saved.getMaCode());
        assertEquals("PHAN_TRAM", response.getLoaiUuDai());
        assertEquals(BigDecimal.TEN, response.getGiaTriGiam());
        assertEquals(BigDecimal.valueOf(500_000), response.getMucGiamToiDa());
        assertEquals(100_000L, response.getDiemCanDoi());
        assertEquals(100, response.getSoLuotPhatHanh());
        assertEquals(0, response.getSoLuotDaDung());
        assertEquals("SAN_SANG", response.getTrangThai());
    }

    @Test
    void taoVoucher_tuChoiLoaiUuDaiKhongHopLe() {
        VoucherRequest request = taoRequest("BADTYPE", "GIFT", BigDecimal.TEN,
                null, 10, LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 30));
        when(voucherRepository.findByMaCode("BADTYPE")).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class,
                () -> voucherService.taoVoucher(request, "admin"));

        assertEquals("BAD_REQUEST", ex.getErrorCode());
        assertEquals("Loại ưu đãi chỉ chấp nhận PHAN_TRAM hoặc SO_TIEN", ex.getMessage());
        verify(voucherRepository, never()).save(any());
    }

    @Test
    void capNhatVoucher_tuChoiPhanTramSauCapNhatVuot100() {
        Voucher voucher = taoVoucher("V_01", "SAVE10", "PHAN_TRAM", BigDecimal.TEN,
                100, LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 30));
        VoucherRequest request = taoRequest(null, null, BigDecimal.valueOf(150),
                null, null, null, null);
        when(voucherRepository.findById("V_01")).thenReturn(Optional.of(voucher));

        AppException ex = assertThrows(AppException.class,
                () -> voucherService.capNhatVoucher("V_01", request, "admin"));

        assertEquals("BAD_REQUEST", ex.getErrorCode());
        assertEquals("Giảm PHAN_TRAM không được vượt qua 100%", ex.getMessage());
        verify(voucherRepository, never()).save(any());
    }

    private static VoucherRequest taoRequest(
            String maCode,
            String loaiUuDai,
            BigDecimal giaTriGiam,
            String dieuKienApDung,
            Integer soLuotPhatHanh,
            LocalDate ngayHieuLuc,
            LocalDate ngayHetHan) {
        VoucherRequest request = new VoucherRequest();
        setField(request, "maCode", maCode);
        setField(request, "loaiUuDai", loaiUuDai);
        setField(request, "giaTriGiam", giaTriGiam);
        setField(request, "dieuKienApDung", dieuKienApDung);
        setField(request, "soLuotPhatHanh", soLuotPhatHanh);
        setField(request, "ngayHieuLuc", ngayHieuLuc);
        setField(request, "ngayHetHan", ngayHetHan);
        return request;
    }

    private static void setField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }

    private static Voucher taoVoucher(
            String maVoucher,
            String maCode,
            String loaiUuDai,
            BigDecimal giaTriGiam,
            Integer soLuotPhatHanh,
            LocalDate ngayHieuLuc,
            LocalDate ngayHetHan) {
        Voucher voucher = new Voucher();
        voucher.setMaVoucher(maVoucher);
        voucher.setMaCode(maCode);
        voucher.setLoaiUuDai(loaiUuDai);
        voucher.setGiaTriGiam(giaTriGiam);
        voucher.setSoLuotPhatHanh(soLuotPhatHanh);
        voucher.setSoLuotDaDung(0);
        voucher.setNgayHieuLuc(ngayHieuLuc);
        voucher.setNgayHetHan(ngayHetHan);
        voucher.setTrangThai("SAN_SANG");
        return voucher;
    }
}
