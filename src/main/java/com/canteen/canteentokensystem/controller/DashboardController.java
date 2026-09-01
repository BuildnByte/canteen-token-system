package com.canteen.canteentokensystem.controller;

import com.canteen.canteentokensystem.dto.DashboardSummaryResponse;
import com.canteen.canteentokensystem.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final TokenService tokenService;

    // GET /api/dashboard/summary (Admin)
    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryResponse> getSummary() {
        return ResponseEntity.ok(tokenService.getDashboardSummary());
    }
}
