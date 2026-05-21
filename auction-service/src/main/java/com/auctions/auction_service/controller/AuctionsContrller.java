package com.auctions.auction_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auctions.auction_service.dto.ApiResponse;
import com.auctions.auction_service.dto.CreateVehicleRequest;
import com.auctions.auction_service.dto.VehicleResponse;
import com.auctions.auction_service.service.VehicleService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auction")
@RequiredArgsConstructor
public class AuctionsContrller {
	
	private final VehicleService vehicleService;
	
	@GetMapping("/admin")
	@PreAuthorize("hasRole('ADMIN')")
	public String adminApi() {
	    return "Admin API";
	}
	
	@GetMapping("/test")
//	@PreAuthorize("hasRole('ADMIN')")
    public String test(HttpServletRequest request) {
        return ("Working-->" + request.getHeader("X-User-Role")).toString()+"<--";
    }
	
	@PostMapping("/vehicle")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    public ResponseEntity<ApiResponse<?>> createVehicle(@RequestBody CreateVehicleRequest request,
    		@RequestHeader("X-User-Id") String userId) {

        VehicleResponse response = vehicleService.createVehicle(request, userId);
        return ResponseEntity.ok(new ApiResponse<>("Vehicle created successfully", response));
    }
	
	

}
