package com.digitaltravel.erp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.digitaltravel.erp.entity.DonDatTour;
import com.digitaltravel.erp.entity.GiaoDich;
import com.digitaltravel.erp.repository.DonDatTourRepository;
import com.digitaltravel.erp.repository.GiaoDichRepository;

@ExtendWith(MockitoExtension.class)
class DatTourSchedulerTest {

    @Mock
    private DonDatTourRepository donDatTourRepository;

    @Mock
    private GiaoDichRepository giaoDichRepository;

    @InjectMocks
    private DatTourScheduler datTourScheduler;

    @Test
    void huyDonHetHan_chuyenGiaoDichDangChoSangThatBai() {
        DonDatTour don = new DonDatTour();
        don.setMaDatTour("DDT_001");
        don.setTrangThai("CHO_XAC_NHAN");
        GiaoDich giaoDich = new GiaoDich();
        giaoDich.setDonDatTour(don);
        giaoDich.setTrangThai("CHO_THANH_TOAN");
        when(donDatTourRepository.findAllByTrangThaiAndThoiGianHetHanBefore(
                org.mockito.ArgumentMatchers.eq("CHO_XAC_NHAN"), any(LocalDateTime.class)))
                .thenReturn(List.of(don));
        when(giaoDichRepository.findByMaDatTour("DDT_001")).thenReturn(List.of(giaoDich));

        datTourScheduler.huyDonHetHan();

        assertEquals("HET_HAN_GIU_CHO", don.getTrangThai());
        assertEquals("THAT_BAI", giaoDich.getTrangThai());
        verify(giaoDichRepository).saveAll(List.of(giaoDich));
        verify(donDatTourRepository).saveAll(List.of(don));
    }

    @Test
    void danhDauQrHetHan_chuyenDonSangHetHanVaGiaoDichSangThatBai() {
        DonDatTour don = new DonDatTour();
        don.setTrangThai("CHO_XAC_NHAN");
        GiaoDich giaoDich = new GiaoDich();
        giaoDich.setDonDatTour(don);
        giaoDich.setTrangThai("CHO_THANH_TOAN");
        when(giaoDichRepository.findQrChoThanhToanQuaHan(any(LocalDateTime.class)))
                .thenReturn(List.of(giaoDich));

        datTourScheduler.danhDauQrHetHan();

        assertEquals("THAT_BAI", giaoDich.getTrangThai());
        assertEquals("HET_HAN_GIU_CHO", don.getTrangThai());
        verify(giaoDichRepository).saveAll(List.of(giaoDich));
        verify(donDatTourRepository).saveAll(List.of(don));
    }
}
