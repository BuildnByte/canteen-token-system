package com.canteen.canteentokensystem.controller;

import com.canteen.canteentokensystem.model.MenuItem;
import com.canteen.canteentokensystem.service.MenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    // GET /api/menu
    @GetMapping
    public ResponseEntity<List<MenuItem>> getMenu() {
        return ResponseEntity.ok(menuService.getAvailableMenu());
    }

    // POST /api/menu (Admin only)
    @PostMapping
    public ResponseEntity<MenuItem> addMenuItem(@Valid @RequestBody MenuItem item) {
        return ResponseEntity.status(HttpStatus.CREATED).body(menuService.addMenuItem(item));
    }
}
