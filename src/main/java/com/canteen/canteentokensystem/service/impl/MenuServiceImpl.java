package com.canteen.canteentokensystem.service.impl;

import com.canteen.canteentokensystem.model.MenuItem;
import com.canteen.canteentokensystem.repository.MenuItemRepository;
import com.canteen.canteentokensystem.service.MenuService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuItemRepository menuItemRepository;

    @Override
    public List<MenuItem> getAvailableMenu() {
        return menuItemRepository.findByAvailableTrue();
    }

    @Override
    public List<MenuItem> getAllMenu() {
        return menuItemRepository.findAll();
    }

    @Override
    public MenuItem addMenuItem(MenuItem item) {
        if (item.getPrice() == null || item.getPrice().signum() < 0) {
            throw new IllegalArgumentException("Price must be non-negative");
        }
        if (item.getName() == null || item.getName().isBlank()) {
            throw new IllegalArgumentException("Item name is required");
        }
        return menuItemRepository.save(item);
    }

    @Override
    public MenuItem updateMenuItem(Long id, MenuItem item) {
        MenuItem existing = menuItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Menu item not found: " + id));
        if (item.getName() != null && !item.getName().isBlank()) {
            existing.setName(item.getName());
        }
        if (item.getPrice() != null) {
            if (item.getPrice().signum() < 0) {
                throw new IllegalArgumentException("Price must be non-negative");
            }
            existing.setPrice(item.getPrice());
        }
        existing.setAvailable(item.isAvailable());
        existing.setQuantity(item.getQuantity());
        return menuItemRepository.save(existing);
    }

    @Override
    public MenuItem toggleAvailability(Long id) {
        MenuItem existing = menuItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Menu item not found: " + id));
        existing.setAvailable(!existing.isAvailable());
        return menuItemRepository.save(existing);
    }

    @Override
    public void deleteMenuItem(Long id) {
        MenuItem existing = menuItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Menu item not found: " + id));
        menuItemRepository.delete(existing);
    }
}
