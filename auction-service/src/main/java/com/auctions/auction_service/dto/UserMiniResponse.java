package com.auctions.auction_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserMiniResponse {

    private String userId;

    private String name;

    private String email;
}
