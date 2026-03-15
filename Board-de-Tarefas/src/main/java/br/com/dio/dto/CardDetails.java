package br.com.dio.dto;

import java.time.OffsetDateTime;

public record CardDetails(Long id, boolean blocked, OffsetDateTime blockedAt, String blockedReason, int blocksAmount, Long columnId, String columnName) {
}
