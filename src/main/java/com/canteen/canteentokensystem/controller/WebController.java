package com.canteen.canteentokensystem.controller;

import com.canteen.canteentokensystem.service.MenuService;
import com.canteen.canteentokensystem.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final MenuService menuService;
    private final TokenService tokenService;

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/menu")
    public String menuPage(Model model) {
        model.addAttribute("menuItems", menuService.getAvailableMenu());
        return "menu";
    }

    @GetMapping("/queue")
    public String queuePage(Model model) {
        model.addAttribute("tokens", tokenService.getActiveQueue());
        return "queue";
    }

    @GetMapping("/dashboard")
    public String dashboardPage(Model model) {
        model.addAttribute("summary", tokenService.getDashboardSummary());
        return "dashboard";
    }
}
