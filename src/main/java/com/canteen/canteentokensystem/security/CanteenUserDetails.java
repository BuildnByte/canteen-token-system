package com.canteen.canteentokensystem.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Lightweight principal stored in the SecurityContext.
 * Controllers can cast Authentication.getPrincipal() to this type.
 */
@Getter
@AllArgsConstructor
public class CanteenUserDetails {
    private final Long userId;
    private final String email;
    private final String role;
}
