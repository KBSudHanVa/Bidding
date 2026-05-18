package com.auctions.auth_service.service;

import com.auctions.auth_service.dto.LoginRequest;
import com.auctions.auth_service.dto.RegisterRequest;
import com.auctions.auth_service.entity.User;
import com.auctions.auth_service.repository.UserRepository;
import com.auctions.auth_service.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    public String register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {

            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .deposit(0.0)
                .usedDeposit(0.0)
                .build();

        userRepository.save(user);

        return "User registered successfully";
    }

    public String login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean matches = passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        );

        if (!matches) {

            throw new RuntimeException("Invalid password");
        }

        return jwtUtil.generateToken(
                user.getEmail(),
                user.getRole()
        );
    }
}