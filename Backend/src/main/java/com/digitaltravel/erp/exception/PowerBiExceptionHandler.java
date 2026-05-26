package com.digitaltravel.erp.exception;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.digitaltravel.erp.dto.responses.ApiResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * Exception handler riêng cho Power BI controller.
 * Bắt các lỗi đặc thù: lỗi DB (tạo user tạm), lỗi quyền, lỗi định dạng.
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.digitaltravel.erp.controller")
public class PowerBiExceptionHandler {

    /**
     * Lỗi truy cập DB (CREATE USER thất bại, GRANT lỗi, kết nối Oracle lỗi...).
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataAccessException(DataAccessException ex) {
        log.error("PowerBI — Lỗi truy cập CSDL: {}", ex.getMostSpecificCause().getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.<Void>builder()
                        .status(500)
                        .success(false)
                        .message("Lỗi truy cập cơ sở dữ liệu khi xử lý yêu cầu Power BI. "
                                + "Vui lòng liên hệ quản trị viên.")
                        .error("DB_ACCESS_ERROR")
                        .build());
    }

    /**
     * Lỗi tạo user DB tạm — RuntimeException từ TemporaryDbUserManager.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException ex) {
        // Chỉ xử lý nếu liên quan đến Power BI (tạo user tạm)
        if (ex.getMessage() != null && ex.getMessage().contains("tài khoản DB tạm thời")) {
            log.error("PowerBI — Lỗi tạo user DB tạm: {}", ex.getMessage(), ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Void>builder()
                            .status(500)
                            .success(false)
                            .message("Không thể tạo tài khoản truy cập tạm thời. "
                                    + "Tài khoản CSDL chính có thể không đủ quyền CREATE USER. "
                                    + "Vui lòng liên hệ quản trị viên.")
                            .error("TEMP_USER_CREATION_ERROR")
                            .build());
        }
        // Delegate lên GlobalExceptionHandler cho các RuntimeException khác
        throw ex;
    }

    /**
     * Lỗi định dạng file (IOException khi tạo Excel/CSV).
     */
    @ExceptionHandler(java.io.IOException.class)
    public ResponseEntity<ApiResponse<Void>> handleIOException(java.io.IOException ex) {
        log.error("PowerBI — Lỗi I/O khi xuất file: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.<Void>builder()
                        .status(500)
                        .success(false)
                        .message("Lỗi tạo file xuất dữ liệu. Vui lòng thử lại sau.")
                        .error("FILE_EXPORT_ERROR")
                        .build());
    }

    /**
     * Lỗi định dạng số / parse — khi dữ liệu DB không đúng format mong đợi.
     */
    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<ApiResponse<Void>> handleNumberFormatException(NumberFormatException ex) {
        log.error("PowerBI — Lỗi định dạng dữ liệu: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Void>builder()
                        .status(400)
                        .success(false)
                        .message("Dữ liệu trong CSDL có định dạng không hợp lệ. "
                                + "Vui lòng liên hệ quản trị viên.")
                        .error("DATA_FORMAT_ERROR")
                        .build());
    }
}
