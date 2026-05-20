package com.auctions.auth_service.service;

import com.auctions.auth_service.dto.AuthResponse;
import com.auctions.auth_service.dto.LoginRequest;
import com.auctions.auth_service.dto.ProfileResponse;
import com.auctions.auth_service.dto.RegisterRequest;
import com.auctions.auth_service.dto.UpdateUserStatus;
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
    
    private enum Role {
		BUYER,
		SELLER,
		BUYER_SELLER
	}

    public String register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        boolean validRole = java.util.Arrays.stream(Role.values())
				.anyMatch(role -> role.toString().equalsIgnoreCase(request.getRole()));
        
        if (!validRole) {
        	throw new RuntimeException(
        	        "Invalid role. Please select one of the following: " +
        	        java.util.Arrays.toString(Role.values())
        	    );
		}

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.valueOf(request.getRole().toUpperCase()).toString())	
                .deposit(0.0)
                .usedDeposit(0.0)
                .build();

        userRepository.save(user);

        return "User registered successfully";
    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(user.isActive() == false) {
        	throw new RuntimeException("User is in-active, Please contact to support.");
        }
        
        boolean matches = passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        );

        if (!matches) {
            throw new RuntimeException("Invalid password");
        }

        String accessToken = jwtUtil.generateAccessToken(
                user.getEmail(),
                user.getRole()
        );

        String refreshToken = jwtUtil.generateRefreshToken(
                user.getEmail()
        );

        return new AuthResponse(
                accessToken,
                refreshToken
        );
    }
    
    public AuthResponse refreshToken(String refreshToken) {

        boolean valid = jwtUtil.validateRefreshToken(refreshToken);

        if (!valid) {
            throw new RuntimeException("Invalid refresh token");
        }

        String email = jwtUtil.extractEmail(refreshToken);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String newAccessToken = jwtUtil.generateAccessToken(
                user.getEmail(),
                user.getRole()
        );

        String newRefreshToken = jwtUtil.generateRefreshToken(
                user.getEmail()
        );

        return new AuthResponse(
                newAccessToken,
                newRefreshToken
        );
    }
    
    public ProfileResponse getProfile(String token) {

        if (token == null || !token.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid token");
        }

        token = token.substring(7);

        boolean valid = jwtUtil.validateAccessToken(token);

        if (!valid) {
            throw new RuntimeException("Invalid access token");
        }

        String email = jwtUtil.extractEmail(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ProfileResponse.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .usedDeposit(user.getUsedDeposit())
                .deposit(user.getDeposit())
                .build();
    }
    
    public String updateUserStatus(UpdateUserStatus request) {
    	User user = userRepository
    					.findByUserId(request.getUserId())
    					.orElseThrow(() -> new RuntimeException("User not found..")
    							);
    	
    	user.setActive(request.getIsActive());
    	userRepository.save(user);
    	
    	return "User status updated successfully";
    }
}