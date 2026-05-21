package com.auctions.auction_service.service;

import com.auctions.auction_service.dto.*;
import com.auctions.auction_service.entity.User;
import com.auctions.auction_service.entity.Vehicle;
import com.auctions.auction_service.enums.VehicleStatus;
import com.auctions.auction_service.repository.UserRepository;
import com.auctions.auction_service.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    
    private final UserRepository userRepository;

    public VehicleResponse createVehicle(CreateVehicleRequest request, String userId) {
    	
    	User loggedInUser = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
    	
    	if(loggedInUser.getRole().equalsIgnoreCase("SELLER")) {
    		request.setClientId(userId);
    	} else if (loggedInUser.getRole().equalsIgnoreCase("ADMIN")) {
    		if(request.getClientId() == null || request.getClientId().isEmpty()) {
        		request.setClientId(userId);
        	} else if (!request.getClientId().equalsIgnoreCase(userId)) {
        		User seller = userRepository.findById(request.getClientId()).orElseThrow(() -> new RuntimeException("Client not found"));
        		
        		if (!seller.getIsActive()) {
                    throw new RuntimeException("Client is inactive");
                }
        		
        		// seller role validation
                if (!seller.getRole().equalsIgnoreCase("SELLER")) {
                    throw new RuntimeException(
                            "Client has no access to add vehicle"
                    );
                }
        	}
    	}else {
    		throw new RuntimeException("You don't have access to add vehicle");
    	}
    		
    	
        Vehicle vehicle = Vehicle.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .clientId(request.getClientId())
                .targetSellingPrice(request.getTargetSellingPrice())
                .minSellingPrice(request.getMinSellingPrice())
                .addedBy(userId)
                .status(VehicleStatus.DRAFT)
                .currentBidAmount(0.0)
                .build();

        vehicle = vehicleRepository.save(vehicle);

        return VehicleResponse.builder()
                .lotNo(vehicle.getLotNo())
                .title(vehicle.getTitle())
                .description(vehicle.getDescription())
                .targetSellingPrice(vehicle.getTargetSellingPrice())
                .currentBidAmount(vehicle.getCurrentBidAmount())
                .status(vehicle.getStatus().name())
                .build();
    }
}