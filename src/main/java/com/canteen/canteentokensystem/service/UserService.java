package com.canteen.canteentokensystem.service;

import com.canteen.canteentokensystem.dto.AuthDtos.LoginRequest;
import com.canteen.canteentokensystem.dto.AuthDtos.LoginResponse;
import com.canteen.canteentokensystem.dto.AuthDtos.RegisterRequest;
import com.canteen.canteentokensystem.model.User;

public interface UserService {
    User register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
    java.util.List<com.canteen.canteentokensystem.dto.AuthDtos.UserDto> getAllUsers();
    com.canteen.canteentokensystem.dto.AuthDtos.UserDto updateUserRole(Long userId, com.canteen.canteentokensystem.model.Role newRole);
}
