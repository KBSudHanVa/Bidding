package com.auctions.auction_service.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateVehicleRequest {

    private String title;

    private String description;

    private String clientId;

    private Double targetSellingPrice;

    private Double minSellingPrice;
}