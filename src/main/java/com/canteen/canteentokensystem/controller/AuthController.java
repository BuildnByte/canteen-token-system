package com.canteen.canteentokensystem.controller;

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
    // TODO (Week 5+): implement Spring Security authentication + JWT issuance here.
    @PostMapping("/login")
    public ResponseEntity<String> login() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body("Login not yet implemented - see Week 5 feature branch.");
    }
}
