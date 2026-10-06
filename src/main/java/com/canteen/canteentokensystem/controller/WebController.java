package com.canteen.canteentokensystem.controller;

import com.canteen.canteentokensystem.security.CanteenUserDetails;
import com.canteen.canteentokensystem.service.MenuService;
import com.canteen.canteentokensystem.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Serves Thymeleaf HTML page shells.
 * NOTE: These routes are ALL permitted at the Spring Security level.
 * Role enforcement is handled CLIENT-SIDE by AUTH.requireRole() in nav.js.
 * The API endpoints that fetch data are what's actually JWT-secured.
 */
@Controller
@RequiredArgsConstructor
public class WebController {

    private final MenuService  menuService;
    private final TokenService tokenService;

    // ── Public ──────────────────────────────────────────────────────────────

    @GetMapping("/")
    public String home(Authentication auth, Model model) {
        addUserContext(auth, model);
        return "home";
    }

    @GetMapping("/login")
    public String loginPage(Authentication auth) {
        // If already authenticated via cookie, redirect to the right place
        if (isLoggedIn(auth)) return redirectByRole(auth);
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Authentication auth) {
        if (isLoggedIn(auth)) return redirectByRole(auth);
        return "register";
    }

    // ── Menu — viewable by everyone ──────────────────────────────────────────

    @GetMapping("/menu")
    public String menuPage(Authentication auth, Model model) {
        addUserContext(auth, model);
        model.addAttribute("menuItems", menuService.getAvailableMenu());
        return "menu";
    }

    // ── Student pages (HTML only — auth handled by JS) ───────────────────────

    @GetMapping("/student/orders")
    public String myOrdersPage(Authentication auth, Model model) {
        addUserContext(auth, model);
        return "student/orders";
    }

    // ── Staff pages (HTML only — auth handled by JS) ─────────────────────────

    @GetMapping("/staff/queue")
    public String globalQueuePage(Authentication auth, Model model) {
        addUserContext(auth, model);
        return "staff/global-queue";
    }

    @GetMapping("/staff/my-queue")
    public String myQueuePage(Authentication auth, Model model) {
        addUserContext(auth, model);
        return "staff/my-queue";
    }

    // ── Admin pages (HTML only — auth handled by JS) ─────────────────────────

    @GetMapping("/dashboard")
    public String dashboardPage(Authentication auth, Model model) {
        addUserContext(auth, model);
        return "admin/dashboard";
    }

    @GetMapping("/admin/menu")
    public String adminMenuPage(Authentication auth, Model model) {
        addUserContext(auth, model);
        model.addAttribute("menuItems", menuService.getAllMenu());
        return "admin/menu";
    }

    @GetMapping("/admin/orders")
    public String adminOrdersPage(Authentication auth, Model model) {
        addUserContext(auth, model);
        return "admin/orders";
    }

    @GetMapping("/admin/users")
    public String adminUsersPage(Authentication auth, Model model) {
        addUserContext(auth, model);
        return "admin/users";
    }

    // ── Legacy redirects ──────────────────────────────────────────────────────

    @GetMapping("/queue")
    public String queueRedirect() {
        return "redirect:/staff/queue";
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void addUserContext(Authentication auth, Model model) {
        if (isLoggedIn(auth) && auth.getPrincipal() instanceof CanteenUserDetails u) {
            model.addAttribute("currentUser", u);
            model.addAttribute("userRole",    u.getRole());
            model.addAttribute("userId",      u.getUserId());
            model.addAttribute("userName",    u.getEmail());
        }
    }

    private boolean isLoggedIn(Authentication auth) {
        return auth != null && auth.isAuthenticated()
                && !(auth.getPrincipal() instanceof String s && s.equals("anonymousUser"));
    }

    private String redirectByRole(Authentication auth) {
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")))
            return "redirect:/dashboard";
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_STAFF")))
            return "redirect:/staff/queue";
        return "redirect:/menu";
    }
}
