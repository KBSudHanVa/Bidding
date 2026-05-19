package com.auctions.auth_service.dto;

import lombok.Data;

@Data
public class RefreshTokenRequest {

    private String refreshToken;
}