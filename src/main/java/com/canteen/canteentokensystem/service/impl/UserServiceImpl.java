package com.canteen.canteentokensystem.service.impl;

import com.canteen.canteentokensystem.dto.AuthDtos.LoginRequest;
import com.canteen.canteentokensystem.dto.AuthDtos.LoginResponse;
import com.canteen.canteentokensystem.dto.AuthDtos.RegisterRequest;
import com.canteen.canteentokensystem.model.User;
import com.canteen.canteentokensystem.repository.UserRepository;
import com.canteen.canteentokensystem.security.JwtUtils;
import com.canteen.canteentokensystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    public User register(RegisterRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Email already registered: " + request.email());
        }
        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(com.canteen.canteentokensystem.model.Role.STUDENT);
        return userRepository.save(user);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String token = jwtUtils.generateToken(user.getEmail(), user.getRole().name(), user.getId());
        return new LoginResponse(token, user.getEmail(), user.getRole(), user.getId(), user.getName());
    }

    @Override
    public java.util.List<com.canteen.canteentokensystem.dto.AuthDtos.UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(u -> new com.canteen.canteentokensystem.dto.AuthDtos.UserDto(u.getId(), u.getName(), u.getEmail(), u.getRole()))
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public com.canteen.canteentokensystem.dto.AuthDtos.UserDto updateUserRole(Long userId, com.canteen.canteentokensystem.model.Role newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setRole(newRole);
        userRepository.save(user);
        return new com.canteen.canteentokensystem.dto.AuthDtos.UserDto(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }
}
