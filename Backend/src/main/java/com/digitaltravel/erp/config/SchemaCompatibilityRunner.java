package com.digitaltravel.erp.config;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SchemaCompatibilityRunner {

    private final JdbcTemplate jdbcTemplate;

    @PostConstruct
    void updateYeuCauHoTroStatusConstraint() {
        try {
            jdbcTemplate.execute("ALTER TABLE YEUCAUHOTRO DROP CONSTRAINT CK_YCHT_TrangThai");
        } catch (Exception ex) {
            log.debug("CK_YCHT_TrangThai was not dropped, continuing with add/update attempt: {}", ex.getMessage());
        }

        try {
            jdbcTemplate.execute("""
                    ALTER TABLE YEUCAUHOTRO ADD CONSTRAINT CK_YCHT_TrangThai CHECK (
                        TrangThai IN ('CHUA_XU_LY','CHO_BO_SUNG','CHO_GIAI_TRINH','CHO_DUYET','DA_XU_LY','TU_CHOI')
                    )
                    """);
        } catch (Exception ex) {
            log.warn("Could not update CK_YCHT_TrangThai to include CHO_DUYET: {}", ex.getMessage());
        }
    }

    @PostConstruct
    void addQuyetToanHoaDonAnhColumn() {
        try {
            Integer count = jdbcTemplate.queryForObject("""
                    SELECT COUNT(*)
                    FROM USER_TAB_COLUMNS
                    WHERE TABLE_NAME = 'QUYETTOAN'
                      AND COLUMN_NAME = 'HOADONANH'
                    """, Integer.class);
            if (count != null && count == 0) {
                jdbcTemplate.execute("ALTER TABLE QUYETTOAN ADD HoaDonAnh VARCHAR2(1000)");
            }
        } catch (Exception ex) {
            log.warn("Could not ensure QUYETTOAN.HoaDonAnh column exists: {}", ex.getMessage());
        }
    }

    @PostConstruct
    void enforceTourOpeningRequiresAcceptedGuide() {
        try {
            jdbcTemplate.execute("""
                    CREATE OR REPLACE TRIGGER TRG_TTT_OPEN_REQUIRE_HDV
                    BEFORE UPDATE OF TrangThai ON TOURTHUCTE
                    FOR EACH ROW
                    WHEN (NEW.TrangThai = 'MO_BAN' AND OLD.TrangThai <> 'MO_BAN')
                    DECLARE
                        v_so_hdv NUMBER;
                    BEGIN
                        SELECT COUNT(*)
                          INTO v_so_hdv
                          FROM PHANCONGTOUR
                         WHERE MaTourThucTe = :NEW.MaTourThucTe
                           AND TrangThaiChapNhan = 'DA_DONG_Y';

                        IF v_so_hdv = 0 THEN
                            RAISE_APPLICATION_ERROR(-20021,
                                'Tour chỉ được mở bán khi có HDV đã xác nhận phân công.');
                        END IF;
                    END;
                    """);
        } catch (Exception ex) {
            log.warn("Could not enforce TOURTHUCTE opening guide requirement: {}", ex.getMessage());
        }
    }
}
