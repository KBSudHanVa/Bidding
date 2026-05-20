package com.auctions.api_gateway.exception;

import com.auctions.api_gateway.dto.ApiResponse;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalGatewayExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleServiceUnavailable(
            NotFoundException ex
    ) {

        String message = ex.getMessage();

        if (message.contains("AUTH-SERVICE")) {

            return ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(
                            new ApiResponse<>(
                                    "Auth service is temporarily unavailable",
                                    503
                            )
                    );
        }

        if (message.contains("AUCTION-SERVICE")) {

            return ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(
                            new ApiResponse<>(
                                    "Auction service is temporarily unavailable",
                                    503
                            )
                    );
        }

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(
                        new ApiResponse<>(
                                "Service temporarily unavailable",
                                503
                        )
                );
    }
}