package com.canteen.canteentokensystem.dto;

import com.canteen.canteentokensystem.model.TokenStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class TokenDtos {

    public record CreateTokenRequest(
            @NotNull Long studentId,
            @NotBlank String items // JSON: [{menuItemId, name, qty, price}]
    ) {}

    public record UpdateStatusRequest(
            @NotNull TokenStatus status
    ) {}

    public record UpdateTokenRequest(
            @NotBlank String items
    ) {}

    public record TokenResponse(
            Long id,
            Long studentId,
            String studentName,
            Long acceptedByStaffId,    // null if not yet accepted
            String acceptedByStaffName,// null if not yet accepted
            String items,
            TokenStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}

    /** Per-staff performance summary for the admin dashboard. */
    public record StaffPerformance(
            Long staffId,
            String staffName,
            long totalAccepted,
            long totalCompleted   // status == COLLECTED
    ) {}

    /** Admin dashboard extended summary. */
    public record DashboardSummaryResponse(
            long totalOrdersToday,
            Map<String, Long> statusBreakdown,
            long unacceptedCount,
            List<StaffPerformance> staffPerformance
    ) {}
}
