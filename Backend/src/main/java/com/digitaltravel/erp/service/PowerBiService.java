package com.digitaltravel.erp.service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.digitaltravel.erp.config.PowerBiProperties;
import com.digitaltravel.erp.config.TaiKhoanDetails;
import com.digitaltravel.erp.dto.requests.XuatDuLieuRequest;
import com.digitaltravel.erp.dto.responses.PowerBiKetNoiResponse;
import com.digitaltravel.erp.dto.responses.PowerBiKhoDuLieuResponse;
import com.digitaltravel.erp.entity.ChiPhiThucTe;
import com.digitaltravel.erp.entity.DonDatTour;
import com.digitaltravel.erp.entity.GiaoDich;
import com.digitaltravel.erp.entity.QuyetToan;
import com.digitaltravel.erp.entity.TaiKhoan;
import com.digitaltravel.erp.entity.TourThucTe;
import com.digitaltravel.erp.exception.AppException;
import com.digitaltravel.erp.repository.ChiPhiThucTeRepository;
import com.digitaltravel.erp.repository.DonDatTourRepository;
import com.digitaltravel.erp.repository.GiaoDichRepository;
import com.digitaltravel.erp.repository.QuyetToanRepository;
import com.digitaltravel.erp.repository.TaiKhoanRepository;
import com.digitaltravel.erp.repository.TourThucTeRepository;
import com.opencsv.CSVWriter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PowerBiService {

    private final PowerBiProperties powerBiProperties;
    private final NhatKyHeThongService nhatKyHeThongService;
    private final TaiKhoanRepository taiKhoanRepository;
    private final QuyetToanRepository quyetToanRepository;
    private final DonDatTourRepository donDatTourRepository;
    private final ChiPhiThucTeRepository chiPhiThucTeRepository;
    private final TourThucTeRepository tourThucTeRepository;
    private final GiaoDichRepository giaoDichRepository;

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public List<PowerBiKhoDuLieuResponse> danhSachKhoDuLieu() {
        return List.of(
                PowerBiKhoDuLieuResponse.builder()
                        .maKho("DOANH_THU")
                        .tenKho("Doanh thu & Quyết toán")
                        .moTa("Dữ liệu quyết toán tour: tổng doanh thu, chi phí, lợi nhuận theo tour")
                        .build(),
                PowerBiKhoDuLieuResponse.builder()
                        .maKho("DON_DAT_TOUR")
                        .tenKho("Đơn đặt tour")
                        .moTa("Chi tiết đơn đặt tour: mã đặt, tour, ngày đặt, tổng tiền, trạng thái")
                        .build(),
                PowerBiKhoDuLieuResponse.builder()
                        .maKho("CHI_PHI")
                        .tenKho("Chi phí thực tế")
                        .moTa("Chi phí phát sinh theo tour: danh mục, thành tiền, trạng thái duyệt")
                        .build(),
                PowerBiKhoDuLieuResponse.builder()
                        .maKho("TOUR")
                        .tenKho("Tour thực tế")
                        .moTa("Danh sách tour: ngày khởi hành, giá, số khách, trạng thái")
                        .build(),
                PowerBiKhoDuLieuResponse.builder()
                        .maKho("GIAO_DICH")
                        .tenKho("Giao dịch thanh toán")
                        .moTa("Giao dịch thanh toán: loại, phương thức, số tiền, trạng thái")
                        .build()
        );
    }

    @Transactional
    public PowerBiKetNoiResponse layThongTinKetNoi(String maKho, TaiKhoanDetails userDetails) {
        validateMaKho(maKho);
        // Lấy thông tin từ config (tài khoản read-only cố định)
        String host = powerBiProperties.getDbHost();
        int port = powerBiProperties.getDbPort();
        String service = powerBiProperties.getDbService();
        String username = powerBiProperties.getDbUsername();
        String password = powerBiProperties.getDbPassword();
        String jdbcUrl = "jdbc:oracle:thin:@//" + host + ":" + port + "/" + service;

        // Ghi nhật ký
        nhatKyHeThongService.ghiNhan(
                userDetails.getTaiKhoan().getMaTaiKhoan(),
                "XUAT_DU_LIEU_POWERBI",
                "POWER_BI_KET_NOI",
                maKho);

        return PowerBiKetNoiResponse.builder()
                .host(host)
                .port(port)
                .serviceName(service)
                .username(username)
                .password(password)
                .jdbcUrl(jdbcUrl)
                .hetHan("Tài khoản read-only cố định, không tự động hết hạn")
                .huongDan("1. Mở Power BI Desktop → Get Data → Oracle Database\n"
                        + "2. Server: " + host + ":" + port + "/" + service + "\n"
                        + "3. Chọn 'Database' authentication, nhập Username và Password ở trên\n"
                        + "4. Chọn bảng cần phân tích → Load")
                .build();
    }

    @Transactional(readOnly = true)
    public byte[] xuatDuLieu(XuatDuLieuRequest request, TaiKhoanDetails userDetails) {
        String maKho = request.getMaKho();
        validateMaKho(maKho);

        LocalDateTime tuNgay = request.getTuNgay() != null ? request.getTuNgay().atStartOfDay() : null;
        LocalDateTime denNgay = request.getDenNgay() != null ? request.getDenNgay().plusDays(1).atStartOfDay() : null;

        nhatKyHeThongService.ghiNhan(
                userDetails.getTaiKhoan().getMaTaiKhoan(),
                "XUAT_DU_LIEU_POWERBI",
                "POWER_BI_XUAT_FILE",
                maKho + "_" + request.getDinhDang());

        boolean isExcel = "EXCEL".equalsIgnoreCase(request.getDinhDang());

        return switch (maKho) {
            case "DOANH_THU" -> xuatDoanhThu(tuNgay, denNgay, isExcel);
            case "DON_DAT_TOUR" -> xuatDonDatTour(tuNgay, denNgay, isExcel);
            case "CHI_PHI" -> xuatChiPhi(tuNgay, denNgay, isExcel);
            case "TOUR" -> xuatTour(isExcel);
            case "GIAO_DICH" -> xuatGiaoDich(tuNgay, denNgay, isExcel);
            default -> throw AppException.badRequest("Mã kho dữ liệu không hợp lệ: " + maKho);
        };
    }

    // Các phương thức xuất dữ liệu giữ nguyên
    private byte[] xuatDoanhThu(LocalDateTime tuNgay, LocalDateTime denNgay, boolean isExcel) {
        List<QuyetToan> data = quyetToanRepository.xuatDuLieuDoanhThu(tuNgay, denNgay);
        String[] headers = {"Mã QT", "Mã Tour TT", "Tiêu đề Tour", "Tổng Doanh Thu",
                "Tổng Chi Phí", "Lợi Nhuận", "Ngày QT", "Trạng Thái"};
        return isExcel ? writeExcel("DoanhThu", headers, data, this::doanhThuToRow)
                       : writeCsv(headers, data, this::doanhThuToRow);
    }

    private String[] doanhThuToRow(QuyetToan qt) {
        return new String[]{
                qt.getMaQuyetToan(),
                qt.getTourThucTe().getMaTourThucTe(),
                qt.getTourThucTe().getTourMau().getTieuDe(),
                qt.getTongDoanhThu().toPlainString(),
                qt.getTongChiPhi().toPlainString(),
                qt.getLoiNhuan().toPlainString(),
                qt.getNgayQuyetToan() != null ? qt.getNgayQuyetToan().format(DT_FMT) : "",
                qt.getTrangThai()
        };
    }

    private byte[] xuatDonDatTour(LocalDateTime tuNgay, LocalDateTime denNgay, boolean isExcel) {
        List<DonDatTour> data = donDatTourRepository.xuatDuLieu(tuNgay, denNgay);
        String[] headers = {"Mã Đặt Tour", "Mã Tour TT", "Tiêu đề Tour",
                "Ngày Đặt", "Tổng Tiền", "Trạng Thái"};
        return isExcel ? writeExcel("DonDatTour", headers, data, this::donDatTourToRow)
                       : writeCsv(headers, data, this::donDatTourToRow);
    }

    private String[] donDatTourToRow(DonDatTour d) {
        return new String[]{
                d.getMaDatTour(),
                d.getTourThucTe().getMaTourThucTe(),
                d.getTourThucTe().getTourMau().getTieuDe(),
                d.getNgayDat() != null ? d.getNgayDat().format(DT_FMT) : "",
                d.getTongTien().toPlainString(),
                d.getTrangThai()
        };
    }

    private byte[] xuatChiPhi(LocalDateTime tuNgay, LocalDateTime denNgay, boolean isExcel) {
        List<ChiPhiThucTe> data = chiPhiThucTeRepository.xuatDuLieu(tuNgay, denNgay);
        String[] headers = {"Mã Chi Phí", "Mã Tour TT", "Danh Mục",
                "Thành Tiền", "Trạng Thái Duyệt", "Ngày Khai"};
        return isExcel ? writeExcel("ChiPhi", headers, data, this::chiPhiToRow)
                       : writeCsv(headers, data, this::chiPhiToRow);
    }

    private String[] chiPhiToRow(ChiPhiThucTe c) {
        return new String[]{
                c.getMaChiPhiThucTe(),
                c.getTourThucTe().getMaTourThucTe(),
                c.getDanhMuc(),
                c.getThanhTien().toPlainString(),
                c.getTrangThaiDuyet(),
                c.getNgayKhai() != null ? c.getNgayKhai().format(DT_FMT) : ""
        };
    }

    private byte[] xuatTour(boolean isExcel) {
        List<TourThucTe> data = tourThucTeRepository.xuatDuLieuTour();
        String[] headers = {"Mã Tour TT", "Tiêu đề", "Ngày Khởi Hành",
                "Giá Hiện Hành", "Số Khách Tối Đa", "Chỗ Còn Lại", "Trạng Thái"};
        return isExcel ? writeExcel("Tour", headers, data, this::tourToRow)
                       : writeCsv(headers, data, this::tourToRow);
    }

    private String[] tourToRow(TourThucTe t) {
        return new String[]{
                t.getMaTourThucTe(),
                t.getTourMau().getTieuDe(),
                t.getNgayKhoiHanh() != null ? t.getNgayKhoiHanh().toString() : "",
                t.getGiaHienHanh().toPlainString(),
                String.valueOf(t.getSoKhachToiDa()),
                String.valueOf(t.getChoConLai()),
                t.getTrangThai()
        };
    }

    private byte[] xuatGiaoDich(LocalDateTime tuNgay, LocalDateTime denNgay, boolean isExcel) {
        List<GiaoDich> data = giaoDichRepository.xuatDuLieu(tuNgay, denNgay);
        String[] headers = {"Mã Giao Dịch", "Mã Đặt Tour", "Loại GD",
                "Phương Thức", "Số Tiền", "Trạng Thái", "Ngày Thanh Toán"};
        return isExcel ? writeExcel("GiaoDich", headers, data, this::giaoDichToRow)
                       : writeCsv(headers, data, this::giaoDichToRow);
    }

    private String[] giaoDichToRow(GiaoDich g) {
        return new String[]{
                g.getMaGiaoDich(),
                g.getDonDatTour().getMaDatTour(),
                g.getLoaiGiaoDich(),
                g.getPhuongThuc(),
                g.getSoTien().toPlainString(),
                g.getTrangThai(),
                g.getNgayThanhToan() != null ? g.getNgayThanhToan().format(DT_FMT) : ""
        };
    }

    private <T> byte[] writeExcel(String sheetName, String[] headers, List<T> data,
                                  java.util.function.Function<T, String[]> rowMapper) {
        try (SXSSFWorkbook workbook = new SXSSFWorkbook(100)) {
            Sheet sheet = workbook.createSheet(sheetName);
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                var cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (T item : data) {
                Row row = sheet.createRow(rowNum++);
                String[] values = rowMapper.apply(item);
                for (int i = 0; i < values.length; i++) {
                    row.createCell(i).setCellValue(values[i] != null ? values[i] : "");
                }
            }

            if (sheet instanceof SXSSFSheet) {
                ((SXSSFSheet) sheet).trackAllColumnsForAutoSizing();
            }
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            workbook.dispose();
            return bos.toByteArray();
        } catch (Exception e) {
            log.error("Lỗi tạo file Excel: {}", e.getMessage(), e);
            throw new AppException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "EXCEL_EXPORT_ERROR", "Lỗi tạo file Excel: " + e.getMessage());
        }
    }

    private <T> byte[] writeCsv(String[] headers, List<T> data,
                                java.util.function.Function<T, String[]> rowMapper) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bos.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});
            try (CSVWriter writer = new CSVWriter(
                    new OutputStreamWriter(bos, StandardCharsets.UTF_8))) {
                writer.writeNext(headers);
                for (T item : data) {
                    writer.writeNext(rowMapper.apply(item));
                }
            }
            return bos.toByteArray();
        } catch (Exception e) {
            log.error("Lỗi tạo file CSV: {}", e.getMessage(), e);
            throw new AppException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "CSV_EXPORT_ERROR", "Lỗi tạo file CSV: " + e.getMessage());
        }
    }

    private void validateMaKho(String maKho) {
        if (maKho == null || maKho.isBlank()) {
            throw AppException.badRequest("Mã kho dữ liệu không được trống");
        }
        List<String> validKho = List.of("DOANH_THU", "DON_DAT_TOUR", "CHI_PHI", "TOUR", "GIAO_DICH");
        if (!validKho.contains(maKho)) {
            throw AppException.badRequest("Mã kho dữ liệu không hợp lệ: " + maKho
                    + ". Giá trị hợp lệ: " + String.join(", ", validKho));
        }
    }
}
