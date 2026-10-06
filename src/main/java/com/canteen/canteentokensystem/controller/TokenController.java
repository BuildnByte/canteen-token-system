package com.canteen.canteentokensystem.controller;

import com.canteen.canteentokensystem.dto.TokenDtos.CreateTokenRequest;
import com.canteen.canteentokensystem.dto.TokenDtos.TokenResponse;
import com.canteen.canteentokensystem.dto.TokenDtos.UpdateStatusRequest;
import com.canteen.canteentokensystem.dto.TokenDtos.UpdateTokenRequest;
import com.canteen.canteentokensystem.security.CanteenUserDetails;
import com.canteen.canteentokensystem.service.TokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tokens")
@RequiredArgsConstructor
public class TokenController {

    private final TokenService tokenService;

    // ── Student endpoints ─────────────────────────────────────────────────

    /** POST /api/tokens — Student places a new order */
    @PostMapping
    @PreAuthorize("hasAnyRole('STUDENT','STAFF','ADMIN')")
    public ResponseEntity<TokenResponse> createToken(@Valid @RequestBody CreateTokenRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tokenService.createToken(request));
    }

    /** GET /api/tokens?studentId= — Student's own orders */
    @GetMapping
    @PreAuthorize("hasAnyRole('STUDENT','STAFF','ADMIN')")
    public ResponseEntity<List<TokenResponse>> getTokensForStudent(@RequestParam Long studentId) {
        return ResponseEntity.ok(tokenService.getTokensForStudent(studentId));
    }

    /** GET /api/tokens/{id} */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TokenResponse> getToken(@PathVariable Long id) {
        return ResponseEntity.ok(tokenService.getToken(id));
    }

    // ── Staff endpoints ───────────────────────────────────────────────────

    /** GET /api/tokens/unaccepted — Global queue: orders waiting for any staff */
    @GetMapping("/unaccepted")
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public ResponseEntity<List<TokenResponse>> getUnacceptedQueue() {
        return ResponseEntity.ok(tokenService.getUnacceptedQueue());
    }

    /** POST /api/tokens/{id}/accept — Staff claims an order from the global queue */
    @PostMapping("/{id}/accept")
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public ResponseEntity<TokenResponse> acceptToken(@PathVariable Long id,
                                                      Authentication auth) {
        CanteenUserDetails user = (CanteenUserDetails) auth.getPrincipal();
        return ResponseEntity.ok(tokenService.acceptToken(id, user.getUserId()));
    }

    /** GET /api/tokens/my-queue — Staff's own accepted orders */
    @GetMapping("/my-queue")
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public ResponseEntity<List<TokenResponse>> getMyQueue(Authentication auth) {
        CanteenUserDetails user = (CanteenUserDetails) auth.getPrincipal();
        return ResponseEntity.ok(tokenService.getMyQueue(user.getUserId()));
    }

    /** PUT /api/tokens/{id}/status — Staff updates status of their accepted order */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public ResponseEntity<TokenResponse> updateStatus(@PathVariable Long id,
                                                       @Valid @RequestBody UpdateStatusRequest request) {
        return ResponseEntity.ok(tokenService.updateStatus(id, request.status()));
    }

    // ── Admin endpoints ───────────────────────────────────────────────────

    /** GET /api/tokens/queue — Admin: all active orders (read-only overview) */
    @GetMapping("/queue")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TokenResponse>> getActiveQueue() {
        return ResponseEntity.ok(tokenService.getActiveQueue());
    }

    /** GET /api/tokens/search?query= — Admin: search by token id or student name */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TokenResponse>> search(@RequestParam String query) {
        return ResponseEntity.ok(tokenService.search(query));
    }

    /** PUT /api/tokens/{id} — Admin: edit items of a token */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TokenResponse> updateToken(@PathVariable Long id,
                                                      @Valid @RequestBody UpdateTokenRequest request) {
        return ResponseEntity.ok(tokenService.updateToken(id, request));
    }

    /** DELETE /api/tokens/{id} — Admin: remove a token */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteToken(@PathVariable Long id) {
        tokenService.deleteToken(id);
        return ResponseEntity.noContent().build();
    }
}
