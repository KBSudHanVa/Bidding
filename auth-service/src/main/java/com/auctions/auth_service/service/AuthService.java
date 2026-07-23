package com.auctions.auth_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.auctions.auth_service.dto.AuthResponse;
import com.auctions.auth_service.dto.LoginRequest;
import com.auctions.auth_service.dto.PaginationResponse;
import com.auctions.auth_service.dto.ProfileResponse;
import com.auctions.auth_service.dto.RegisterRequest;
import com.auctions.auth_service.dto.UpdateUserStatus;
import com.auctions.auth_service.dto.UserFilterRequest;
import com.auctions.auth_service.dto.UserResponse;
import com.auctions.auth_service.entity.User;
import com.auctions.auth_service.repository.UserRepository;
import com.auctions.auth_service.specification.UserSpecification;
import com.auctions.auth_service.util.JwtUtil;
import com.auctions.auth_service.dto.ResetPasswordRequest;
import com.auctions.auth_service.dto.UpdateUserRole;

import lombok.RequiredArgsConstructor;

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
                user.getRole(),
                user.getUserId()
        );

        String refreshToken = jwtUtil.generateRefreshToken(
        		user.getUserId(),
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
                user.getRole(),
                user.getUserId()
        );

        String newRefreshToken = jwtUtil.generateRefreshToken(
        		user.getUserId(),
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
    
    public String updateUserRole(String userId, UpdateUserRole request) {
    	User user = userRepository
    					.findByUserId(userId)
    					.orElseThrow(() -> new RuntimeException("User not found.."));
    	
    	try {
            Role role = Role.valueOf(request.role().toUpperCase());
            user.setRole(role.name());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role. Allowed roles: BUYER, SELLER, BUYER_SELLER");
        }
    	
    	userRepository.save(user);
    	return "User role updated successfully";
    }
    
    public PaginationResponse<UserResponse> getUsersList(UserFilterRequest request, Integer page, Integer size) {

        Pageable pageable = PageRequest.of(
        		(page == null || page < 1) ? 0 : page - 1,
        		(size == null || size < 1) ? 10 : size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<User> users = userRepository.findAll(
                UserSpecification.filterUsers(request),
                pageable
        );

        Page<UserResponse> responsePage =
                users.map(user -> UserResponse.builder()
                        .userId(user.getUserId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .role(user.getRole())
                        .usedDeposit(user.getUsedDeposit())
                        .deposit(user.getDeposit())
                        .createdAt(user.getCreatedAt())
                        .isActive(user.isActive())
                        .build()
                );

        return PaginationResponse.<UserResponse>builder()
                .content(responsePage.getContent())
                .page(responsePage.getNumber()+1)
                .size(responsePage.getSize())
                .totalElements(responsePage.getTotalElements())
                .totalPages(responsePage.getTotalPages())
                .last(responsePage.isLast())
                .build();
    }
    public String resetPassword(ResetPasswordRequest request) {

        User user = userRepository
                .findByUserId(request.getUserId())
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return "Force password reset successfully";
    }
}