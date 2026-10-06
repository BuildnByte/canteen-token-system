package com.canteen.canteentokensystem.controller;

import com.canteen.canteentokensystem.dto.AuthDtos.LoginRequest;
import com.canteen.canteentokensystem.dto.AuthDtos.LoginResponse;
import com.canteen.canteentokensystem.dto.AuthDtos.RegisterRequest;
import com.canteen.canteentokensystem.model.User;
import com.canteen.canteentokensystem.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    // POST /api/auth/register
    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody RegisterRequest request) {
        User created = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }
}
