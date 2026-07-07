package com.talentflow.presentation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.List;

/**
 * Envelope padronizado para TODAS as respostas da API.
 * 
 * Estrutura consistente:
 * {
 *   "success": true,
 *   "data": { ... },
 *   "meta": { "page": 0, "size": 20, "total": 150, "totalPages": 8 },
 *   "error": null,
 *   "timestamp": "2026-07-02T00:00:00Z"
 * }
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
    boolean success,
    T data,
    Meta meta,
    ErrorDetail error,
    Instant timestamp
) {
    // ─── Success ───────────────────────────────────────

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, null, null, Instant.now());
    }

    public static <T> ApiResponse<T> ok(T data, Meta meta) {
        return new ApiResponse<>(true, data, meta, null, Instant.now());
    }

    public static ApiResponse<Void> created() {
        return new ApiResponse<>(true, null, null, null, Instant.now());
    }

    public static ApiResponse<Void> noContent() {
        return new ApiResponse<>(true, null, null, null, Instant.now());
    }

    // ─── Error ─────────────────────────────────────────

    public static <T> ApiResponse<T> error(int status, String message, List<FieldError> errors) {
        return new ApiResponse<>(false, null, null,
            new ErrorDetail(status, message, errors), Instant.now());
    }

    public static <T> ApiResponse<T> error(int status, String message) {
        return error(status, message, null);
    }

    // ─── Pagination ────────────────────────────────────

    public record Meta(int page, int size, long total, int totalPages) {
        public static Meta of(org.springframework.data.domain.Page<?> page) {
            return new Meta(page.getNumber(), page.getSize(),
                          page.getTotalElements(), page.getTotalPages());
        }
    }

    // ─── Error detail ──────────────────────────────────

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ErrorDetail(int status, String message, List<FieldError> errors) {}

    public record FieldError(String field, String message) {}
}
