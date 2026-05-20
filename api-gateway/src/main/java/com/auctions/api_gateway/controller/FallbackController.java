package com.auctions.api_gateway.controller;

import com.auctions.api_gateway.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class FallbackController {

    @RequestMapping("/fallback/auth")
    public ResponseEntity<ApiResponse<?>> authFallback() {

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(
                        new ApiResponse<>(
                                "Auth service is temporarily unavailable. Please try again later.",
                                503
                        )
                );
    }

    @RequestMapping("/fallback/auction")
    public ResponseEntity<ApiResponse<?>> auctionFallback() {

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(
                        new ApiResponse<>(
                                "Auction service is temporarily unavailable. Please try again later.",
                                503
                        )
                );
    }
}