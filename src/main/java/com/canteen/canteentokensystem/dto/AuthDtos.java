package com.canteen.canteentokensystem.dto;

import com.canteen.canteentokensystem.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AuthDtos {

    public record RegisterRequest(
            @NotBlank String name,
            @Email @NotBlank String email,
            @NotBlank String password,
            @NotNull Role role
    ) {}

    public record LoginRequest(
            @Email @NotBlank String email,
            @NotBlank String password
    ) {}

    public record LoginResponse(
            String token,
            String email,
            Role role,
            Long userId,
            String name
    ) {}

    public record UserDto(
            Long id,
            String name,
            String email,
            Role role
    ) {}

    public record UpdateRoleRequest(
            @NotNull Role role
    ) {}
}
