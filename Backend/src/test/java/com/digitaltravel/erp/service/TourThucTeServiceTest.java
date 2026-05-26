package com.digitaltravel.erp.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.digitaltravel.erp.dto.requests.CapNhatTourThucTeRequest;
import com.digitaltravel.erp.entity.TourThucTe;
import com.digitaltravel.erp.exception.AppException;
import com.digitaltravel.erp.repository.DichVuThemRepository;
import com.digitaltravel.erp.repository.DichVuTourThucTeRepository;
import com.digitaltravel.erp.repository.DonDatTourRepository;
import com.digitaltravel.erp.repository.HanhDongXanhRepository;
import com.digitaltravel.erp.repository.HdxTourThucTeRepository;
import com.digitaltravel.erp.repository.LichTrinhTourRepository;
import com.digitaltravel.erp.repository.PhanCongTourRepository;
import com.digitaltravel.erp.repository.TourMauRepository;
import com.digitaltravel.erp.repository.TourThucTeRepository;

@ExtendWith(MockitoExtension.class)
class TourThucTeServiceTest {

    @Mock
    private TourThucTeRepository tourThucTeRepository;
    @Mock
    private TourMauRepository tourMauRepository;
    @Mock
    private LichTrinhTourRepository lichTrinhTourRepository;
    @Mock
    private DonDatTourRepository donDatTourRepository;
    @Mock
    private PhanCongTourRepository phanCongTourRepository;
    @Mock
    private DichVuThemRepository dichVuThemRepository;
    @Mock
    private DichVuTourThucTeRepository dichVuTourThucTeRepository;
    @Mock
    private HanhDongXanhRepository hanhDongXanhRepository;
    @Mock
    private HdxTourThucTeRepository hdxTourThucTeRepository;
    @Mock
    private MaTuDongService maTuDongService;

    @InjectMocks
    private TourThucTeService tourThucTeService;

    @Test
    void capNhatTuChoiMoBanKhiChuaCoHdvDaXacNhan() {
        TourThucTe tour = new TourThucTe();
        tour.setMaTourThucTe("TTT_CHO_01");
        tour.setTrangThai("CHO_KICH_HOAT");

        CapNhatTourThucTeRequest request = new CapNhatTourThucTeRequest();
        ReflectionTestUtils.setField(request, "trangThai", "MO_BAN");

        when(tourThucTeRepository.findById("TTT_CHO_01")).thenReturn(Optional.of(tour));
        when(phanCongTourRepository.existsAcceptedByTourThucTe_MaTourThucTe("TTT_CHO_01")).thenReturn(false);

        assertThrows(AppException.class,
                () -> tourThucTeService.capNhat("TTT_CHO_01", request, "manager01"));
    }
}
