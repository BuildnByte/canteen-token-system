package com.canteen.canteentokensystem.controller;

import com.canteen.canteentokensystem.dto.TokenDtos.CreateTokenRequest;
import com.canteen.canteentokensystem.dto.TokenDtos.TokenResponse;
import com.canteen.canteentokensystem.dto.TokenDtos.UpdateStatusRequest;
import com.canteen.canteentokensystem.service.TokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tokens")
@RequiredArgsConstructor
public class TokenController {

    private final TokenService tokenService;

    // POST /api/tokens (Student)
    @PostMapping
    public ResponseEntity<TokenResponse> createToken(@Valid @RequestBody CreateTokenRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tokenService.createToken(request));
    }

    // GET /api/tokens/{id}
    @GetMapping("/{id}")
    public ResponseEntity<TokenResponse> getToken(@PathVariable Long id) {
        return ResponseEntity.ok(tokenService.getToken(id));
    }

    // GET /api/tokens?studentId=
    @GetMapping
    public ResponseEntity<List<TokenResponse>> getTokensForStudent(@RequestParam Long studentId) {
        return ResponseEntity.ok(tokenService.getTokensForStudent(studentId));
    }

    // GET /api/tokens/queue (Staff)
    @GetMapping("/queue")
    public ResponseEntity<List<TokenResponse>> getQueue() {
        return ResponseEntity.ok(tokenService.getActiveQueue());
    }

    // PUT /api/tokens/{id}/status (Staff)
    @PutMapping("/{id}/status")
    public ResponseEntity<TokenResponse> updateStatus(@PathVariable Long id,
                                                        @Valid @RequestBody UpdateStatusRequest request) {
        return ResponseEntity.ok(tokenService.updateStatus(id, request.status()));
    }

    // GET /api/tokens/search?query= (Staff)
    @GetMapping("/search")
    public ResponseEntity<List<TokenResponse>> search(@RequestParam String query) {
        return ResponseEntity.ok(tokenService.search(query));
    }
}
