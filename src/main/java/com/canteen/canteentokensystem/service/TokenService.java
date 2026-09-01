package com.canteen.canteentokensystem.service;

import com.canteen.canteentokensystem.dto.DashboardSummaryResponse;
import com.canteen.canteentokensystem.dto.TokenDtos.CreateTokenRequest;
import com.canteen.canteentokensystem.dto.TokenDtos.TokenResponse;
import com.canteen.canteentokensystem.model.TokenStatus;

import java.util.List;

public interface TokenService {
    TokenResponse createToken(CreateTokenRequest request);
    TokenResponse getToken(Long id);
    List<TokenResponse> getTokensForStudent(Long studentId);
    List<TokenResponse> getActiveQueue();
    TokenResponse updateStatus(Long id, TokenStatus status);
    List<TokenResponse> search(String query);
    DashboardSummaryResponse getDashboardSummary();
}
