package com.canteen.canteentokensystem.dto;

import java.util.List;
import java.util.Map;

/**
 * Dashboard summary returned by GET /api/dashboard/summary.
 * Includes today's order count, status breakdown, unaccepted count,
 * and per-staff performance metrics.
 */
public record DashboardSummaryResponse(
        long totalOrdersToday,
        Map<String, Long> statusBreakdown,
        long unacceptedCount,
        List<TokenDtos.StaffPerformance> staffPerformance
) {}

