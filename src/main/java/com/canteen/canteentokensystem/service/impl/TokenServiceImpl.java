package com.canteen.canteentokensystem.service.impl;

import com.canteen.canteentokensystem.dto.DashboardSummaryResponse;
import com.canteen.canteentokensystem.dto.TokenDtos.CreateTokenRequest;
import com.canteen.canteentokensystem.dto.TokenDtos.StaffPerformance;
import com.canteen.canteentokensystem.dto.TokenDtos.TokenResponse;
import com.canteen.canteentokensystem.dto.TokenDtos.UpdateTokenRequest;
import com.canteen.canteentokensystem.model.Token;
import com.canteen.canteentokensystem.model.TokenStatus;
import com.canteen.canteentokensystem.model.User;
import com.canteen.canteentokensystem.repository.TokenRepository;
import com.canteen.canteentokensystem.repository.UserRepository;
import com.canteen.canteentokensystem.service.TokenService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final TokenRepository tokenRepository;
    private final UserRepository  userRepository;
    private final com.canteen.canteentokensystem.repository.MenuItemRepository menuItemRepository;

    // ── Student ────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public TokenResponse createToken(CreateTokenRequest request) {
        User student = userRepository.findById(request.studentId())
                .orElseThrow(() -> new EntityNotFoundException("Student not found: " + request.studentId()));
        if (request.items() == null || request.items().isBlank()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }
        if (request.items().length() > 4000) {
            throw new IllegalArgumentException("Order payload too large");
        }
        try {
            com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
            List<Map<String, Object>> itemsList = objectMapper.readValue(request.items(), new com.fasterxml.jackson.core.type.TypeReference<>() {});
            for (Map<String, Object> itemMap : itemsList) {
                Long menuItemId = Long.valueOf(itemMap.get("menuItemId").toString());
                int qty = Integer.parseInt(itemMap.get("qty").toString());

                com.canteen.canteentokensystem.model.MenuItem menuItem = menuItemRepository.findById(menuItemId)
                        .orElseThrow(() -> new IllegalArgumentException("Menu item not found: " + menuItemId));

                if (menuItem.getQuantity() != null) {
                    if (menuItem.getQuantity() < qty) {
                        throw new IllegalArgumentException("Not enough stock for item: " + menuItem.getName());
                    }
                    menuItem.setQuantity(menuItem.getQuantity() - qty);
                    if (menuItem.getQuantity() == 0) {
                        menuItem.setAvailable(false);
                    }
                    menuItemRepository.save(menuItem);
                }
            }
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid items JSON payload");
        }

        Token token = new Token();
        token.setStudent(student);
        token.setItems(request.items());
        token.setStatus(TokenStatus.PENDING);
        token.setAcceptedBy(null); // starts in global unaccepted queue
        return toResponse(tokenRepository.save(token));
    }

    @Override
    public TokenResponse getToken(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    public List<TokenResponse> getTokensForStudent(Long studentId) {
        return tokenRepository.findByStudentIdOrderByCreatedAtDesc(studentId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ── Staff ──────────────────────────────────────────────────────────────

    @Override
    public List<TokenResponse> getUnacceptedQueue() {
        return tokenRepository.findUnacceptedOrders()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TokenResponse acceptToken(Long tokenId, Long staffId) {
        Token token = findOrThrow(tokenId);
        if (token.getAcceptedBy() != null) {
            throw new IllegalArgumentException(
                    "Token #" + tokenId + " is already accepted by another staff member");
        }
        User staff = userRepository.findById(staffId)
                .orElseThrow(() -> new EntityNotFoundException("Staff not found: " + staffId));
        token.setAcceptedBy(staff);
        token.setStatus(TokenStatus.PENDING); // still PENDING until they start cooking
        return toResponse(tokenRepository.save(token));
    }

    @Override
    public List<TokenResponse> getMyQueue(Long staffId) {
        return tokenRepository.findByAcceptedByIdAndNotCollected(staffId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TokenResponse updateStatus(Long id, TokenStatus status) {
        Token token = findOrThrow(id);
        token.setStatus(status);
        return toResponse(tokenRepository.save(token));
    }

    // ── Admin ──────────────────────────────────────────────────────────────

    @Override
    public List<TokenResponse> getActiveQueue() {
        return tokenRepository.findAllActiveOrders()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<TokenResponse> search(String query) {
        return tokenRepository.searchByIdOrStudentName(query)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public DashboardSummaryResponse getDashboardSummary() {
        List<Token> allTokens = tokenRepository.findAll();

        // Today's count
        LocalDate today = LocalDate.now();
        long todayCount = allTokens.stream()
                .filter(t -> t.getCreatedAt() != null && t.getCreatedAt().toLocalDate().equals(today))
                .count();

        // Status breakdown
        Map<String, Long> breakdown = Stream.of(TokenStatus.values())
                .collect(Collectors.toMap(Enum::name,
                        s -> allTokens.stream().filter(t -> t.getStatus() == s).count()));

        // Unaccepted count
        long unacceptedCount = allTokens.stream().filter(t -> t.getAcceptedBy() == null
                && t.getStatus() == TokenStatus.PENDING).count();

        // Staff performance
        List<Object[]> acceptedRows  = tokenRepository.countByStaff();
        List<Object[]> completedRows = tokenRepository.countCompletedByStaff();

        Map<Long, Long> completedMap = new HashMap<>();
        for (Object[] row : completedRows) {
            completedMap.put((Long) row[0], (Long) row[2]);
        }

        List<StaffPerformance> staffStats = acceptedRows.stream().map(row -> {
            Long staffId      = (Long)   row[0];
            String staffName  = (String) row[1];
            long   total      = (Long)   row[2];
            long   completed  = completedMap.getOrDefault(staffId, 0L);
            return new StaffPerformance(staffId, staffName, total, completed);
        }).collect(Collectors.toList());

        return new DashboardSummaryResponse(todayCount, breakdown, unacceptedCount, staffStats);
    }

    // ── Legacy / Admin helpers ─────────────────────────────────────────────

    @Override
    @Transactional
    public TokenResponse updateToken(Long id, UpdateTokenRequest request) {
        Token token = findOrThrow(id);
        if (request.items() == null || request.items().isBlank()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }
        token.setItems(request.items());
        return toResponse(tokenRepository.save(token));
    }

    @Override
    @Transactional
    public void deleteToken(Long id) {
        tokenRepository.delete(findOrThrow(id));
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    private Token findOrThrow(Long id) {
        return tokenRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Token not found: " + id));
    }

    private TokenResponse toResponse(Token t) {
        Long   staffId   = t.getAcceptedBy() != null ? t.getAcceptedBy().getId()   : null;
        String staffName = t.getAcceptedBy() != null ? t.getAcceptedBy().getName() : null;
        return new TokenResponse(
                t.getId(),
                t.getStudent().getId(),
                t.getStudent().getName(),
                staffId,
                staffName,
                t.getItems(),
                t.getStatus(),
                t.getCreatedAt(),
                t.getUpdatedAt()
        );
    }
}
