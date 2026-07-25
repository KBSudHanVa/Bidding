package com.auctions.auction_service.controller;

import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.auctions.auction_service.dto.ApiResponse;
import com.auctions.auction_service.dto.CreateVehicleRequest;
import com.auctions.auction_service.dto.PaginationResponse;
import com.auctions.auction_service.dto.SellerDecisionRequest;
import com.auctions.auction_service.dto.UpdateVehicleStatusRequest;
import com.auctions.auction_service.dto.VehicleFilterRequest;
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
		System.out.println(request.getHeader("X-User-Role"));
		System.out.println(request.getHeader("X-User-Id"));
        return ("Working-->" + request.getHeader("X-User-Role")).toString()+"<--";
    }
	
	@PostMapping("/vehicle")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER', 'BUYER_SELLER')")
    public ResponseEntity<ApiResponse<?>> createVehicle(@RequestBody CreateVehicleRequest request,
    		@RequestHeader("X-User-Id") String userId) {

        VehicleResponse response = vehicleService.createVehicle(request, userId);
        return ResponseEntity.ok(new ApiResponse<>("Vehicle created successfully", response));
    }
	
	@PatchMapping("/{lotNo}/status")
	@PreAuthorize("hasAnyRole('ADMIN','SELLER', 'BUYER_SELLER')")
	public ResponseEntity<ApiResponse<?>> updateStatus(@PathVariable Integer lotNo, @RequestBody UpdateVehicleStatusRequest request){
		return ResponseEntity.ok(
	            new ApiResponse<>(vehicleService.updateStatus(lotNo, request), null));
	}
	
	@PatchMapping("/{lotNo}/approve")
	@PreAuthorize("hasAnyRole('ADMIN','SELLER', 'BUYER_SELLER')")
	public ResponseEntity<ApiResponse<?>> sellerDecision(@PathVariable Integer lotNo, 
			@RequestBody SellerDecisionRequest request,
			@RequestHeader("X-User-Id") String userId){
		System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
		return ResponseEntity.ok(
	            new ApiResponse<>(vehicleService.sellerDecision(lotNo, request, userId), null));
	}
	
	
	
//	@GetMapping("/vehicle")
//    public ResponseEntity<ApiResponse<?>> getVehicle(
//            @RequestBody(required = false) VehicleFilterRequest request,
//            @RequestParam(defaultValue = "1") Integer page,
//            @RequestParam(defaultValue = "10") Integer size,
//            @RequestHeader (value = "X-User-Id", required = false) String userId 
//            ) {
//		
//        if (request == null) {
//            request = new VehicleFilterRequest();
//        }
//        System.out.println("User ID: " + userId);
//        String user = Objects.requireNonNullElse(userId, "ANONYMOUS");
//
//        PaginationResponse<VehicleResponse> response = vehicleService.getVehiclesList(request, page, size, user);
//
//        return ResponseEntity.ok(new ApiResponse<>("Vehicles fetched successfully", response));
//    }
	
	@GetMapping("/vehicles")
    public ResponseEntity<ApiResponse<?>> getVehicles(
            @RequestBody(required = false) VehicleFilterRequest request,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size, 
            @RequestHeader (value = "X-User-Id") String userId 
            ) {
		
        if (request == null) {
            request = new VehicleFilterRequest();
        }

        PaginationResponse<VehicleResponse> response = vehicleService.getVehiclesListNew(request, page, size, userId);

        return ResponseEntity.ok(new ApiResponse<>("Vehicles fetched successfully", response));
    }
	
//	@GetMapping("/vehicle")
//	public String name() {
//		return "hello";
//	}

}
