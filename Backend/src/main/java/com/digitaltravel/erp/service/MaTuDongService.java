package com.digitaltravel.erp.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.digitaltravel.erp.config.VaiTroConst;
import com.digitaltravel.erp.exception.AppException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.MANDATORY)
public class MaTuDongService {

    private final JdbcTemplate jdbcTemplate;

    public String taoMaTaiKhoanTheoVaiTro(String maVaiTro) {
        String prefix = switch (maVaiTro) {
            case VaiTroConst.KHACHHANG -> "TK_KH";
            case VaiTroConst.HDV -> "TK_HDV";
            case VaiTroConst.SANPHAM -> "TK_SP";
            case VaiTroConst.KINHDOANH -> "TK_KD";
            case VaiTroConst.DIEUHANH -> "TK_DH";
            case VaiTroConst.KETOAN -> "TK_KT";
            case VaiTroConst.ADMIN -> "TK_AD";
            default -> throw AppException.badRequest("Vai trò không hợp lệ");
        };

        return taoMa("TAIKHOAN", "MaTaiKhoan", prefix);
    }

    public String taoMaHoChieuSo() {
        return taoMa("HOCHIEUSO", "MaKhachHang", "KH");
    }

    public String taoMaNhanVien() {
        return taoMa("NHANVIEN", "MaNhanVien", "NV");
    }

    public String taoMaDichVuThem() {
        return taoMa("DICHVUTHEM", "MaDichVuThem", "DV");
    }

    public String taoMaTourMau() {
        return taoMa("TOURMAU", "MaTourMau", "TM");
    }

    public String taoMaTourThucTe() {
        return taoMa("TOURTHUCTE", "MaTourThucTe", "TTT");
    }

    public String taoMaLichTrinhTour() {
        return taoMa("LICHTRINHTOUR", "MaLichTrinhTour", "LT");
    }

    public List<String> taoDanhSachMaLichTrinhTour(int soLuong) {
        return taoNhieuMa("LICHTRINHTOUR", "MaLichTrinhTour", "LT", soLuong);
    }

    private String taoMa(String tenBang, String tenCot, String tienTo) {
        return taoNhieuMa(tenBang, tenCot, tienTo, 1).getFirst();
    }

    private List<String> taoNhieuMa(String tenBang, String tenCot, String tienTo, int soLuong) {
        if (soLuong < 1) {
            return List.of();
        }

        jdbcTemplate.execute("LOCK TABLE " + tenBang + " IN EXCLUSIVE MODE");

        String sql = """
                SELECT NVL(MAX(TO_NUMBER(REGEXP_SUBSTR(%s, '[0-9]+$'))), 0)
                FROM %s
                WHERE REGEXP_LIKE(%s, ?)
                """.formatted(tenCot, tenBang, tenCot);

        Long soLonNhat = jdbcTemplate.queryForObject(sql, Long.class, "^" + tienTo + "[0-9]+$");
        long batDau = (soLonNhat == null ? 0 : soLonNhat) + 1;

        List<String> danhSachMa = new ArrayList<>(soLuong);
        for (int i = 0; i < soLuong; i++) {
            danhSachMa.add(tienTo + (batDau + i));
        }
        return danhSachMa;
    }
}
