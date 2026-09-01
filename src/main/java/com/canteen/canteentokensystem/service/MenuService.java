package com.canteen.canteentokensystem.service;

import com.canteen.canteentokensystem.model.MenuItem;

import java.util.List;

public interface MenuService {
    List<MenuItem> getAvailableMenu();
    MenuItem addMenuItem(MenuItem item);
}
