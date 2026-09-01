package com.canteen.canteentokensystem.dto;

import java.util.Map;

public record DashboardSummaryResponse(
        long totalOrdersToday,
        Map<String, Long> statusBreakdown
) {}
