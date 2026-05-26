package com.digitaltravel.erp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.digitaltravel.erp.dto.requests.GanVaiTroRequest;
import com.digitaltravel.erp.dto.responses.NhanVienResponse;
import com.digitaltravel.erp.entity.NhanVien;
import com.digitaltravel.erp.entity.TaiKhoan;
import com.digitaltravel.erp.entity.VaiTro;
import com.digitaltravel.erp.exception.AppException;
import com.digitaltravel.erp.repository.HoChieuSoRepository;
import com.digitaltravel.erp.repository.NhanVienRepository;
import com.digitaltravel.erp.repository.TaiKhoanRepository;
import com.digitaltravel.erp.repository.VaiTroRepository;

@ExtendWith(MockitoExtension.class)
class NhanVienServiceTest {

    @Mock
    private NhanVienRepository nhanVienRepository;

    @Mock
    private TaiKhoanRepository taiKhoanRepository;

    @Mock
    private HoChieuSoRepository hoChieuSoRepository;

    @Mock
    private VaiTroRepository vaiTroRepository;

    @InjectMocks
    private NhanVienService nhanVienService;

    @Test
    void ganVaiTro_capNhatDongBoVaiTroTaiKhoanVaLoaiNhanVien() {
        NhanVien nv = taoNhanVien("NV_01", "TK_01", "SANPHAM");
        VaiTro vaiTroMoi = taoVaiTro("HDV");
        GanVaiTroRequest request = taoRequest(" hdv ");

        when(nhanVienRepository.findById("NV_01")).thenReturn(Optional.of(nv));
        when(vaiTroRepository.findById("HDV")).thenReturn(Optional.of(vaiTroMoi));

        NhanVienResponse response = nhanVienService.ganVaiTro("NV_01", request);

        assertEquals("HDV", nv.getTaiKhoan().getVaiTro().getMaVaiTro());
        assertEquals("HDV", nv.getLoaiNhanVien());
        assertEquals("HDV", response.getMaVaiTro());
        assertEquals("HDV", response.getLoaiNhanVien());
        verify(taiKhoanRepository).save(nv.getTaiKhoan());
        verify(nhanVienRepository).save(nv);
    }

    @Test
    void ganVaiTro_tuChoiVaiTroKhongHopLeChoNhanVien() {
        NhanVien nv = taoNhanVien("NV_01", "TK_01", "SANPHAM");
        GanVaiTroRequest request = taoRequest("KHACHHANG");

        when(nhanVienRepository.findById("NV_01")).thenReturn(Optional.of(nv));

        AppException ex = assertThrows(AppException.class,
                () -> nhanVienService.ganVaiTro("NV_01", request));

        assertEquals("BAD_REQUEST", ex.getErrorCode());
        verify(vaiTroRepository, never()).findById(org.mockito.ArgumentMatchers.anyString());
        verify(taiKhoanRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    private static GanVaiTroRequest taoRequest(String maVaiTro) {
        GanVaiTroRequest request = new GanVaiTroRequest();
        try {
            var field = GanVaiTroRequest.class.getDeclaredField("maVaiTro");
            field.setAccessible(true);
            field.set(request, maVaiTro);
            return request;
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }

    private static NhanVien taoNhanVien(String maNhanVien, String maTaiKhoan, String maVaiTro) {
        TaiKhoan taiKhoan = new TaiKhoan();
        taiKhoan.setMaTaiKhoan(maTaiKhoan);
        taiKhoan.setVaiTro(taoVaiTro(maVaiTro));

        NhanVien nhanVien = new NhanVien();
        nhanVien.setMaNhanVien(maNhanVien);
        nhanVien.setTaiKhoan(taiKhoan);
        nhanVien.setLoaiNhanVien(maVaiTro);
        return nhanVien;
    }

    private static VaiTro taoVaiTro(String maVaiTro) {
        VaiTro vaiTro = new VaiTro();
        vaiTro.setMaVaiTro(maVaiTro);
        return vaiTro;
    }
}
