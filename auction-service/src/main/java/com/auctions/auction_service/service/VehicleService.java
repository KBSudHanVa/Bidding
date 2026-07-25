package com.auctions.auction_service.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.auctions.auction_service.dto.CreateVehicleRequest;
import com.auctions.auction_service.dto.PaginationResponse;
import com.auctions.auction_service.dto.SellerDecisionRequest;
import com.auctions.auction_service.dto.UpdateVehicleStatusRequest;
import com.auctions.auction_service.dto.VehicleFilterRequest;
import com.auctions.auction_service.dto.VehicleResponse;
import com.auctions.auction_service.entity.User;
import com.auctions.auction_service.entity.Vehicle;
import com.auctions.auction_service.enums.VehicleStatus;
import com.auctions.auction_service.repository.UserRepository;
import com.auctions.auction_service.repository.VehicleRepository;
import com.auctions.auction_service.specification.VehicleSpecification;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VehicleService {

	private final VehicleRepository vehicleRepository;

	private final UserRepository userRepository;
	
//	public enum allowedRole {
//		ADMIN,
//	    SELLER,
//	    BUYER_SELLER
//	}

	// =====================================================
	// CREATE VEHICLE
	// =====================================================

	@Transactional
	public VehicleResponse createVehicle(CreateVehicleRequest request, String userId) {

		User loggedInUser = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

		User clientUser;
		
//		Set<String> allowedRoles = Set.of("SELLER", "BUYER_SELLER");
//		allowedRole role = allowedRole.valueOf(loggedInUser.getRole().toUpperCase());

		// SELLER
		if (loggedInUser.getRole().equalsIgnoreCase("SELLER") || loggedInUser.getRole().equalsIgnoreCase("BUYER_SELLER")) {
//		if( allowedRoles.contains(loggedInUser.getRole()) ) {
//		if (role == allowedRole.SELLER || role == allowedRole.BUYER_SELLER) {
			clientUser = loggedInUser;
		} else if (loggedInUser.getRole().equalsIgnoreCase("ADMIN")) {  //ADMIN
			if (request.getClientId() == null || request.getClientId().isBlank()) {
				clientUser = loggedInUser;
			} else {
				clientUser = userRepository.findById(request.getClientId()).orElseThrow(() -> new RuntimeException("Client not found"));
				if (!clientUser.getIsActive()) {
					throw new RuntimeException("Client is inactive");
				}
				if (!clientUser.getRole().equalsIgnoreCase("SELLER") && !clientUser.getRole().equalsIgnoreCase("BUYER_SELLER")) {
//				if(!allowedRoles.contains(loggedInUser.getRole())) {
//				if (role != allowedRole.SELLER && role != allowedRole.BUYER_SELLER) {
					throw new RuntimeException("Client has no access to add vehicle");
				}
			}
		} else {
			throw new RuntimeException("You don't have access to add vehicle");
		}

		Vehicle vehicle = Vehicle.builder().title(request.getTitle()).description(request.getDescription())
				.clientId(clientUser).addedBy(loggedInUser).targetSellingPrice(request.getTargetSellingPrice())
				.minSellingPrice(request.getMinSellingPrice()).status(VehicleStatus.DRAFT).currentBidAmount(0.0)
				.build();

		vehicle = vehicleRepository.save(vehicle);

		return mapToVehicleResponse(vehicle);
	}
	
	
//	new vehicle list
	@Transactional
	public PaginationResponse<VehicleResponse> getVehiclesListNew(
	        VehicleFilterRequest request,
	        Integer page,
	        Integer size,
	        String userId) {

	    User loggedInUser = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

	    Pageable pageable = PageRequest.of((page == null || page < 1) ? 0 : page - 1, (size == null || size < 1) ? 10 : size, Sort.by(Sort.Direction.DESC, "createdAt"));

	    Page<Vehicle> vehicles;

	    switch (loggedInUser.getRole().toUpperCase()) {

	        case "ADMIN":
	            vehicles = vehicleRepository.findAll(
	                    VehicleSpecification.filterVehicles(request),
	                    pageable);
	            break;

	        case "SELLER":
	            vehicles = vehicleRepository.findAll(
	                    filterVehiclesForSeller(
	                            request,
	                            loggedInUser.getUserId()),
	                    pageable);
	            break;

	        case "BUYER":
	            vehicles = vehicleRepository.findAll(
	                    filterVehiclesForBuyer(
	                            request,
	                            loggedInUser.getUserId()),
	                    pageable);
	            break;

	        case "BUYER_SELLER":
	            vehicles = vehicleRepository.findAll(
	                    filterVehiclesForBuyerSeller(
	                            request,
	                            loggedInUser.getUserId()),
	                    pageable);
	            break;

	        default:
	            throw new RuntimeException("Access denied");
	    }

	    Page<VehicleResponse> response =
	            vehicles.map(v -> mapToVehicleResponse(v));

	    return PaginationResponse.<VehicleResponse>builder()
	            .content(response.getContent())
	            .page(response.getNumber() + 1)
	            .size(response.getSize())
	            .totalElements(response.getTotalElements())
	            .last(response.isLast())
	            .build();
	}
	
	public static Specification<Vehicle> filterVehiclesForSeller(
	        VehicleFilterRequest request,
	        String userId) {

	    return VehicleSpecification.filterVehicles(request)
	            .and((root, query, cb) ->
	                    cb.equal(root.get("clientId").get("userId"), userId));
	}
	
	public static Specification<Vehicle> filterVehiclesForBuyer(
	        VehicleFilterRequest request,
	        String userId) {

	    return VehicleSpecification.filterVehicles(request)
	            .and((root, query, cb) ->
	                    cb.equal(root.get("wonBy").get("userId"), userId));
	}
	
	public static Specification<Vehicle> filterVehiclesForBuyerSeller(
	        VehicleFilterRequest request,
	        String userId) {

	    return VehicleSpecification.filterVehicles(request)
	            .and((root, query, cb) ->
	                    cb.or(
	                        cb.equal(root.get("clientId").get("userId"), userId),
	                        cb.equal(root.get("wonBy").get("userId"), userId)
	                    ));
	}
	
	private VehicleResponse mapToVehicleResponse(Vehicle vehicle) {
	    return VehicleResponse.builder()
	            .lotNo(vehicle.getLotNo())
	            .title(vehicle.getTitle())
	            .description(vehicle.getDescription())
	            .targetSellingPrice(vehicle.getTargetSellingPrice())
	            .minSellingPrice(vehicle.getMinSellingPrice())
	            .currentBidAmount(vehicle.getCurrentBidAmount())
	            .winningBidAmount(vehicle.getWinningBidAmount())
	            .sellerName(vehicle.getClientId() != null ? vehicle.getClientId().getName() : null)
	            .sellerEmail(vehicle.getClientId() != null ? vehicle.getClientId().getEmail() : null)
	            .winnerName(vehicle.getWonBy() != null ? vehicle.getWonBy().getName() : null)
	            .winnerEmail(vehicle.getWonBy() != null ? vehicle.getWonBy().getEmail() : null)
	            .status(vehicle.getStatus() != null ? vehicle.getStatus().name() : null)
	            .bidCloseDate(vehicle.getBidCloseDate() != null ? vehicle.getBidCloseDate() : null)
	            .createdAt(vehicle.getCreatedAt() != null ? vehicle.getCreatedAt() : null)
	            .build();
	}
	

//	// =====================================================
//	// VEHICLE LIST
//	// =====================================================
//
//	@Transactional
//	public PaginationResponse<VehicleResponse> getVehiclesList(VehicleFilterRequest request, Integer page, Integer size, String userId) {
//
////		System.out.println("-------*->"+userId+"<--------");
//		
////		User loggedInUser = userRepository.findById(userId)
////				.orElse(null);
//		
////		System.out.println("Logged User: " + loggedInUser.getUserId());
////		System.out.println("Role: " + loggedInUser.getRole());
//		
//		User loggedInUser = userRepository.findById(userId)
////				.orElse(null)
//				.orElseThrow(() -> new RuntimeException("User not found"));
//		
////		if(loggedInUser.getUserId() == null) {
////			System.out.println("79----null");
////		} else {
////			System.out.println("81---:"+loggedInUser.getUserId());
////		}
//
//		Pageable pageable = PageRequest.of((page == null || page < 1) ? 0 : page - 1,
//				(size == null || size < 1) ? 10 : size, Sort.by(Sort.Direction.DESC, "createdAt"));
//
//		Page<Vehicle> vehicles = vehicleRepository.findAll(VehicleSpecification.filterVehicles(request), pageable);
//
//		Page<VehicleResponse> responsePage = vehicles.map(vehicle -> mapToVehicleResponse(vehicle, loggedInUser));
//
//		return PaginationResponse.<VehicleResponse>builder().content(responsePage.getContent())
//				.page(responsePage.getNumber() + 1).size(responsePage.getSize())
//				.totalElements(responsePage.getTotalElements()).last(responsePage.isLast()).build();
//	}
//
//	// =====================================================
//	// COMMON RESPONSE MAPPER
//	// =====================================================
//
//	private VehicleResponse mapToVehicleResponse(Vehicle vehicle, User loggedInUser) {
//		
////		if(loggedInUser.getUserId().equalsIgnoreCase("notFound")) {
////			System.out.println("104----empty");
////		}
//		
////		System.out.println("-------->"+loggedInUser.getUserId()+"<--------");
//
//		VehicleResponse.VehicleResponseBuilder builder = VehicleResponse.builder()
//				.lotNo(vehicle.getLotNo())
//				.title(vehicle.getTitle())
//				.description(vehicle.getDescription())
//				.targetSellingPrice(vehicle.getTargetSellingPrice())
//				.status(vehicle.getStatus() != null ? vehicle.getStatus().name() : null)
//				.bidCloseDate(vehicle.getBidCloseDate())
//				.createdAt(vehicle.getCreatedAt());
//		
//		// =================================================
//		// ANONYMOUS
//		// =================================================
////		if (loggedInUser == null || loggedInUser.getRole().equalsIgnoreCase("ANONYMOUS")) {
////			builder.currentBidAmount(vehicle.getCurrentBidAmount());
////			return builder.build();
////		}
//
//		// =================================================
//		// ADMIN
//		// =================================================
//
//		if (loggedInUser.getRole().equalsIgnoreCase("ADMIN")) {
//
//			builder.minSellingPrice(vehicle.getMinSellingPrice())
//					.currentBidAmount(vehicle.getCurrentBidAmount())
//					.winningBidAmount(vehicle.getWinningBidAmount())
//					.sellerName(vehicle.getClientId() != null ? vehicle.getClientId().getName() : null)
//					.sellerEmail(vehicle.getClientId() != null ? vehicle.getClientId().getEmail() : null)
//					.winnerName(vehicle.getWonBy() != null ? vehicle.getWonBy().getName() : null)
//					.winnerEmail(vehicle.getWonBy() != null ? vehicle.getWonBy().getEmail() : null)
//					.currentHighestBidderName(vehicle.getCurrentHighestBidder() != null ? vehicle.getCurrentHighestBidder().getName() : null)
//					.currentHighestBidderEmail(vehicle.getCurrentHighestBidder() != null ? vehicle.getCurrentHighestBidder().getEmail() : null);
//		}
//
//		// =================================================
//		// BUYER_SELLER
//		// =================================================
//
//		else if (loggedInUser.getRole().equalsIgnoreCase("BUYER_SELLER")) {
//		    // Seller logic
//		    if (vehicle.getClientId() != null &&
//		        vehicle.getClientId().getUserId().equals(loggedInUser.getUserId())) {
//
//		        builder.minSellingPrice(vehicle.getMinSellingPrice())
//		               .sellerName(vehicle.getClientId().getName())
//		               .sellerEmail(vehicle.getClientId().getEmail());
//		    }
//
//		    // Buyer logic
//		    builder.currentBidAmount(vehicle.getCurrentBidAmount());
//
//		    if (vehicle.getWonBy() != null &&
//		        vehicle.getWonBy().getUserId().equals(loggedInUser.getUserId())) {
//
//		        builder.winningBidAmount(vehicle.getWinningBidAmount())
//		               .winnerName(vehicle.getWonBy().getName())
//		               .winnerEmail(vehicle.getWonBy().getEmail());
//		    }
//		}
//		
//		// =================================================
//		// SELLER
//		// =================================================
//
//		else if (loggedInUser.getRole().equalsIgnoreCase("SELLER")) {
//
//			if (vehicle.getClientId() != null && vehicle.getClientId().getUserId().equals(loggedInUser.getUserId())) {
//				builder.minSellingPrice(vehicle.getMinSellingPrice())
//						.winningBidAmount(vehicle.getWinningBidAmount())
//						.sellerName(vehicle.getClientId().getName())
//						.sellerEmail(vehicle.getClientId().getEmail());
//			}
//		}
//
//		// =================================================
//		// BUYER
//		// =================================================
//
//		else if (loggedInUser.getRole().equalsIgnoreCase("BUYER")) {
//			builder.currentBidAmount(vehicle.getCurrentBidAmount());
//			if (vehicle.getWonBy() != null && vehicle.getWonBy().getUserId().equals(loggedInUser.getUserId())) {
//				builder.winningBidAmount(vehicle.getWinningBidAmount())
//						.winnerName(vehicle.getWonBy().getName())
//						.winnerEmail(vehicle.getWonBy().getEmail());
//			}
//		}
//
////		else {
////			builder.currentBidAmount(vehicle.getCurrentBidAmount());
////		}
//
//		return builder.build();
//	}
	
	
	
	
	
//	workflow
	
	@Transactional
	public String updateStatus(Integer lotNo, UpdateVehicleStatusRequest request) {

	    Vehicle vehicle = vehicleRepository.findById(lotNo)
	            .orElseThrow(() -> new RuntimeException("Vehicle not found"));

	    VehicleStatus current = vehicle.getStatus();
	    VehicleStatus next = request.status();

	    validateTransition(current, next);

	    if (next == VehicleStatus.LIVE) {

	        if (request.bidCloseDate() == null) {
	            throw new RuntimeException("Bid close date is required");
	        }

	        if (request.bidCloseDate().isBefore(LocalDateTime.now())) {
	            throw new RuntimeException("Bid close date must be in the future");
	        }

	        vehicle.setBidCloseDate(request.bidCloseDate());
	    }

	    vehicle.setStatus(next);
        vehicle.setModifiedOn(LocalDateTime.now());

	    vehicleRepository.save(vehicle);

	    return "Vehicle moved to " + next;
	}
	
	
	private void validateTransition(VehicleStatus current,
            VehicleStatus next) {
		
		switch (current) {
		
		case DRAFT -> {
			if (next != VehicleStatus.OPEN)
			throw new RuntimeException("Only OPEN is allowed.");
		}
		case OPEN -> {
			if (next != VehicleStatus.LIVE)
			throw new RuntimeException("Only LIVE is allowed.");
		}
		case LIVE -> {
			throw new RuntimeException("LIVE can only be closed by scheduler.");
		}
		case CLOSED -> {
			if (next != VehicleStatus.SOLD && next != VehicleStatus.CANCELLED)
			throw new RuntimeException("Only SOLD or CANCELLED allowed.");
		}
		case SOLD ->
			throw new RuntimeException("Vehicle already SOLD.");
		case CANCELLED ->
			throw new RuntimeException("Vehicle already CANCELLED.");
		}
	}
	
	@Transactional
	public String sellerDecision(Integer lotNo, SellerDecisionRequest request, String userId) {

	    Vehicle vehicle = vehicleRepository.findById(lotNo)
	            .orElseThrow(() -> new RuntimeException("Vehicle not found"));
	    
//	    
//	    if( !vehicle.getClientId().equals(userId) ) {
//	    	throw new RuntimeException("Only vehicle seller can approve");
//	    }
	    
//	    if(vehicle.getClientId().toString().equalsIgnoreCase(userId)) {
//	    	throw new RuntimeException("Only vehicle seller can approve");
//	    }
	    
	    if(!vehicle.getClientId().getUserId().toString().equalsIgnoreCase(userId)) {
	    	System.out.println(vehicle.getClientId().getUserId().toString());
		    System.out.println(userId);
	    	throw new RuntimeException("Only vehicle seller can approve");
	    }

	    if (vehicle.getStatus().equals(VehicleStatus.SOLD)) {
	        throw new RuntimeException("Vehicle is sold");
	    } else if (vehicle.getStatus().equals(VehicleStatus.CANCELLED)) {
	        throw new RuntimeException("Vehicle is cancelled");
	    } else if (vehicle.getStatus() != VehicleStatus.CLOSED) {
	        throw new RuntimeException("Auction is not closed");
	    }  

	    if (request.approve()) {
	        vehicle.setStatus(VehicleStatus.SOLD);
	    } else {
	        vehicle.setStatus(VehicleStatus.CANCELLED);
	    }

        vehicle.setModifiedOn(LocalDateTime.now());
	    vehicleRepository.save(vehicle);

	    return "Vehicle updated successfully";
	}
	
	
//	workflow
	
	
	
	
	
}