package com.canteen.canteentokensystem.dto;

import com.canteen.canteentokensystem.model.TokenStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class TokenDtos {

    public record CreateTokenRequest(
            @NotNull Long studentId,
            @NotBlank String items // JSON string: [{"menuItemId":1,"name":"Samosa","qty":2}]
    ) {}

    public record UpdateStatusRequest(
            @NotNull TokenStatus status
    ) {}

    public record TokenResponse(
            Long id,
            Long studentId,
            String studentName,
            String items,
            TokenStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}
}
