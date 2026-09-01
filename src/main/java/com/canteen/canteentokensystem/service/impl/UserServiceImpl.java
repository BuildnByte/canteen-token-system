package com.canteen.canteentokensystem.service.impl;

import com.canteen.canteentokensystem.dto.AuthDtos.RegisterRequest;
import com.canteen.canteentokensystem.model.User;
import com.canteen.canteentokensystem.repository.UserRepository;
import com.canteen.canteentokensystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User register(RegisterRequest request) {
        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(request.role());
        return userRepository.save(user);
    }
}
