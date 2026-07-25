package com.auctions.auction_service.dto;

import java.time.LocalDateTime;

import com.auctions.auction_service.enums.VehicleStatus;

public record UpdateVehicleStatusRequest(
		VehicleStatus status,
		LocalDateTime bidCloseDate
		) {

}
