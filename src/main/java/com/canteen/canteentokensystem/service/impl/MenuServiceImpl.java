package com.canteen.canteentokensystem.service.impl;

import com.canteen.canteentokensystem.model.MenuItem;
import com.canteen.canteentokensystem.repository.MenuItemRepository;
import com.canteen.canteentokensystem.service.MenuService;
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
    public MenuItem addMenuItem(MenuItem item) {
        return menuItemRepository.save(item);
    }
}
