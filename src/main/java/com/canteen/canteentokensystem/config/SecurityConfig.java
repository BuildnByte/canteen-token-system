package com.canteen.canteentokensystem.config;

import com.canteen.canteentokensystem.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security Configuration.
 *
 * KEY DESIGN DECISION:
 * All Thymeleaf PAGE routes (/dashboard, /staff/**, /admin/**, etc.) are
 * intentionally PERMITTED at the Spring Security level — they only render
 * an HTML shell. Role-based access is enforced CLIENT-SIDE by AUTH.requireRole()
 * in nav.js (which redirects to /login if JWT is absent or role is wrong).
 *
 * Actual DATA is only accessible through the /api/** endpoints, which ARE
 * fully secured by the JwtAuthFilter and @PreAuthorize annotations.
 * So even if someone navigates to /dashboard without a token, every API call
 * on that page will return 401/403 — no data leaks.
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth

                // ── Public: auth, static assets, ALL page routes ──────────────
                // Page routes serve HTML shells only — auth is JS/client-side.
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                .requestMatchers(HttpMethod.GET,
                        "/", "/login", "/register", "/menu",
                        "/dashboard",
                        "/student/**",
                        "/staff/**",
                        "/admin/**",
                        "/queue"         // legacy redirect
                ).permitAll()

                // ── Public API: menu is readable without login ────────────────
                .requestMatchers(HttpMethod.GET, "/api/menu", "/api/menu/**").permitAll()

                // ── Secured API: Student ──────────────────────────────────────
                .requestMatchers(HttpMethod.POST, "/api/tokens").hasAnyRole("STUDENT", "STAFF", "ADMIN")
                .requestMatchers(HttpMethod.GET,  "/api/tokens").hasAnyRole("STUDENT", "STAFF", "ADMIN")

                // ── Secured API: Staff ────────────────────────────────────────
                .requestMatchers(HttpMethod.GET,  "/api/tokens/unaccepted").hasAnyRole("STAFF", "ADMIN")
                .requestMatchers(HttpMethod.GET,  "/api/tokens/my-queue").hasAnyRole("STAFF", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/tokens/*/accept").hasAnyRole("STAFF", "ADMIN")
                .requestMatchers(HttpMethod.PUT,  "/api/tokens/*/status").hasAnyRole("STAFF", "ADMIN")

                // ── Secured API: Admin only ───────────────────────────────────
                .requestMatchers("/api/menu/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET,  "/api/tokens/queue").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET,  "/api/tokens/search").hasRole("ADMIN")
                .requestMatchers("/api/tokens/**").hasAnyRole("STAFF", "ADMIN")
                .requestMatchers("/api/dashboard/**").hasRole("ADMIN")

                // ── Everything else (other /api/** calls) ─────────────────────
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
