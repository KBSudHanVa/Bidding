package com.auctions.auth_service.controller;

import com.auctions.auth_service.dto.ApiResponse;
import com.auctions.auth_service.dto.AuthResponse;
import com.auctions.auth_service.dto.LoginRequest;
import com.auctions.auth_service.dto.RegisterRequest;
import com.auctions.auth_service.service.AuthService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    
    @GetMapping("/hello")
    public String hello() {

		return "Hello from Auth Service";
	}

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> register(
            @RequestBody RegisterRequest request
    ) {

        try {

            String response = authService.register(request);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(
                            new ApiResponse<>(
                                    true,
                                    response,
                                    null
                            )
                    );

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(
                            new ApiResponse<>(
                                    false,
                                    e.getMessage(),
                                    null
                            )
                    );
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(
            @RequestBody LoginRequest request
    ) {

        try {

            String token = authService.login(request);

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "Login successful",
                            new AuthResponse(token)
                    )
            );

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(
                            new ApiResponse<>(
                                    false,
                                    e.getMessage(),
                                    null
                            )
                    );
        }
    }
}