package com.canteen.canteentokensystem.service;

import com.canteen.canteentokensystem.dto.DashboardSummaryResponse;
import com.canteen.canteentokensystem.dto.TokenDtos.CreateTokenRequest;
import com.canteen.canteentokensystem.dto.TokenDtos.TokenResponse;
import com.canteen.canteentokensystem.dto.TokenDtos.UpdateTokenRequest;
import com.canteen.canteentokensystem.model.TokenStatus;

import java.util.List;

public interface TokenService {
    /** Student: place a new order. */
    TokenResponse createToken(CreateTokenRequest request);

    /** Get any single token by id. */
    TokenResponse getToken(Long id);

    /** Student: get all orders for a specific student. */
    List<TokenResponse> getTokensForStudent(Long studentId);

    /** Staff: global queue — orders not yet accepted by any staff. */
    List<TokenResponse> getUnacceptedQueue();

    /** Staff: accept/claim a token from the global queue. */
    TokenResponse acceptToken(Long tokenId, Long staffId);

    /** Staff: get the personal queue of a specific staff member. */
    List<TokenResponse> getMyQueue(Long staffId);

    /** Staff: update status of a token in their queue. */
    TokenResponse updateStatus(Long id, TokenStatus status);

    /** Admin: all active (non-collected) orders. */
    List<TokenResponse> getActiveQueue();

    /** Admin: search by token id or student name. */
    List<TokenResponse> search(String query);

    /** Admin: dashboard summary with staff performance. */
    DashboardSummaryResponse getDashboardSummary();

    // Legacy / admin helpers
    TokenResponse updateToken(Long id, UpdateTokenRequest request);
    void deleteToken(Long id);
}
