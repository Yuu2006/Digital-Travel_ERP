package com.digitaltravel.erp.dto.responses;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final int status;
    private final boolean success;
    private final String message;
    private final T data;
    private final String error;

    private ApiResponse(int status, boolean success, String message, T data, String error) {
        this.status = status;
        this.success = success;
        this.message = message;
        this.data = data;
        this.error = error;
    }

    // ── Static factory ────────────────────────────────────────────────────────

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(200, true, "Thành công", data, null);
    }

    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(200, true, message, data, null);
    }

    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(201, true, "Tạo mới thành công", data, null);
    }

    public static ApiResponse<Void> noContent(String message) {
        return new ApiResponse<>(200, true, message, null, null);
    }

    public static ApiResponse<Void> badRequest(String error, String message) {
        return new ApiResponse<>(400, false, message, null, error);
    }

    public static ApiResponse<Void> unauthorized(String error, String message) {
        return new ApiResponse<>(401, false, message, null, error);
    }

    public static ApiResponse<Void> forbidden(String message) {
        return new ApiResponse<>(403, false, message, null, "FORBIDDEN");
    }

    public static ApiResponse<Void> notFound(String message) {
        return new ApiResponse<>(404, false, message, null, "NOT_FOUND");
    }

    public static ApiResponse<Void> internalError(String message) {
        return new ApiResponse<>(500, false, message, null, "INTERNAL_SERVER_ERROR");
    }

    // ── Builder (dùng trong GlobalExceptionHandler) ───────────────────────────

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static class Builder<T> {
        private int status;
        private boolean success;
        private String message;
        private T data;
        private String error;

        public Builder<T> status(int status)       { this.status = status;   return this; }
        public Builder<T> success(boolean success)  { this.success = success; return this; }
        public Builder<T> message(String message)   { this.message = message; return this; }
        public Builder<T> data(T data)              { this.data = data;       return this; }
        public Builder<T> error(String error)       { this.error = error;     return this; }

        public ApiResponse<T> build() {
            return new ApiResponse<>(status, success, message, data, error);
        }
    }
}
