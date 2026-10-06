package com.canteen.canteentokensystem.service;

import com.canteen.canteentokensystem.model.MenuItem;

import java.util.List;

public interface MenuService {
    List<MenuItem> getAvailableMenu();
    List<MenuItem> getAllMenu();
    MenuItem addMenuItem(MenuItem item);
    MenuItem updateMenuItem(Long id, MenuItem item);
    MenuItem toggleAvailability(Long id);
    void deleteMenuItem(Long id);
}
