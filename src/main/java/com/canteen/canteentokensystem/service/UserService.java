package com.canteen.canteentokensystem.service;

import com.canteen.canteentokensystem.dto.AuthDtos.RegisterRequest;
import com.canteen.canteentokensystem.model.User;

public interface UserService {
    User register(RegisterRequest request);
}
