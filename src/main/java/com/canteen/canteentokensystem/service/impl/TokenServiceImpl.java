package com.canteen.canteentokensystem.service.impl;

import com.canteen.canteentokensystem.dto.DashboardSummaryResponse;
import com.canteen.canteentokensystem.dto.TokenDtos.CreateTokenRequest;
import com.canteen.canteentokensystem.dto.TokenDtos.TokenResponse;
import com.canteen.canteentokensystem.model.Token;
import com.canteen.canteentokensystem.model.TokenStatus;
import com.canteen.canteentokensystem.model.User;
import com.canteen.canteentokensystem.repository.TokenRepository;
import com.canteen.canteentokensystem.repository.UserRepository;
import com.canteen.canteentokensystem.service.TokenService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    @Override
    public TokenResponse createToken(CreateTokenRequest request) {
        User student = userRepository.findById(request.studentId())
                .orElseThrow(() -> new EntityNotFoundException("Student not found: " + request.studentId()));
        if (request.items() == null || request.items().isBlank()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }        
        if (request.items() != null && request.items().length() > 2000) {
    throw new IllegalArgumentException("Order payload too large");
}
        Token token = new Token();
        token.setStudent(student);
        token.setItems(request.items());
        token.setStatus(TokenStatus.PENDING);

        return toResponse(tokenRepository.save(token));
    }

    @Override
    public TokenResponse getToken(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    public List<TokenResponse> getTokensForStudent(Long studentId) {
        return tokenRepository.findByStudentId(studentId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TokenResponse> getActiveQueue() {
        List<TokenStatus> active = Arrays.asList(TokenStatus.PENDING, TokenStatus.PREPARING, TokenStatus.READY);
        return tokenRepository.findByStatusIn(active).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TokenResponse updateStatus(Long id, TokenStatus status) {
        Token token = findOrThrow(id);
        token.setStatus(status);
        return toResponse(tokenRepository.save(token));
    }

    @Override
    public List<TokenResponse> search(String query) {
        return tokenRepository.searchByIdOrStudentName(query).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public DashboardSummaryResponse getDashboardSummary() {
        List<Token> allTokens = tokenRepository.findAll();

        LocalDate today = LocalDate.now();
        long todayCount = allTokens.stream()
                .filter(t -> t.getCreatedAt() != null && t.getCreatedAt().toLocalDate().equals(today))
                .count();

        Map<String, Long> breakdown = Stream.of(TokenStatus.values())
                .collect(Collectors.toMap(
                        Enum::name,
                        s -> allTokens.stream().filter(t -> t.getStatus() == s).count()
                ));

        return new DashboardSummaryResponse(todayCount, breakdown);
    }

    private Token findOrThrow(Long id) {
        return tokenRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Token not found: " + id));
    }

    private TokenResponse toResponse(Token t) {
        return new TokenResponse(
                t.getId(),
                t.getStudent().getId(),
                t.getStudent().getName(),
                t.getItems(),
                t.getStatus(),
                t.getCreatedAt(),
                t.getUpdatedAt()
        );
    }
}
