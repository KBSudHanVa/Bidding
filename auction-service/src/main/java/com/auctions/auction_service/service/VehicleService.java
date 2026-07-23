package com.auctions.auction_service.service;

import com.auctions.auction_service.dto.*;
import com.auctions.auction_service.entity.User;
import com.auctions.auction_service.entity.Vehicle;
import com.auctions.auction_service.enums.VehicleStatus;
import com.auctions.auction_service.repository.UserRepository;
import com.auctions.auction_service.repository.VehicleRepository;
import com.auctions.auction_service.specification.VehicleSpecification;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VehicleService {

	private final VehicleRepository vehicleRepository;

	private final UserRepository userRepository;

	// =====================================================
	// CREATE VEHICLE
	// =====================================================

	@Transactional
	public VehicleResponse createVehicle(CreateVehicleRequest request, String userId) {

		User loggedInUser = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

		User clientUser;

		// SELLER
		if (loggedInUser.getRole().equalsIgnoreCase("SELLER")) {
			clientUser = loggedInUser;
		} else if (loggedInUser.getRole().equalsIgnoreCase("ADMIN")) {  //ADMIN
			if (request.getClientId() == null || request.getClientId().isBlank()) {
				clientUser = loggedInUser;
			} else {
				clientUser = userRepository.findById(request.getClientId()).orElseThrow(() -> new RuntimeException("Client not found"));
				if (!clientUser.getIsActive()) {
					throw new RuntimeException("Client is inactive");
				}
				if (!clientUser.getRole().equalsIgnoreCase("SELLER")) {
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

		return mapToVehicleResponse(vehicle, loggedInUser);
	}

	// =====================================================
	// VEHICLE LIST
	// =====================================================

	@Transactional
	public PaginationResponse<VehicleResponse> getVehiclesList(VehicleFilterRequest request, Integer page, Integer size, String userId) {

//		System.out.println("-------*->"+userId+"<--------");
		
		User loggedInUser = userRepository.findById(userId)
				.orElse(null);
//				.orElseThrow(() -> new RuntimeException("User not found"));
		
//		if(loggedInUser.getUserId() == null) {
//			System.out.println("79----null");
//		} else {
//			System.out.println("81---:"+loggedInUser.getUserId());
//		}

		Pageable pageable = PageRequest.of((page == null || page < 1) ? 0 : page - 1,
				(size == null || size < 1) ? 10 : size, Sort.by(Sort.Direction.DESC, "createdAt"));

		Page<Vehicle> vehicles = vehicleRepository.findAll(VehicleSpecification.filterVehicles(request), pageable);

		Page<VehicleResponse> responsePage = vehicles.map(vehicle -> mapToVehicleResponse(vehicle, loggedInUser));

		return PaginationResponse.<VehicleResponse>builder().content(responsePage.getContent())
				.page(responsePage.getNumber() + 1).size(responsePage.getSize())
				.totalElements(responsePage.getTotalElements()).last(responsePage.isLast()).build();
	}

	// =====================================================
	// COMMON RESPONSE MAPPER
	// =====================================================

	private VehicleResponse mapToVehicleResponse(Vehicle vehicle, User loggedInUser) {
		
//		if(loggedInUser.getUserId().equalsIgnoreCase("notFound")) {
//			System.out.println("104----empty");
//		}
		
//		System.out.println("-------->"+loggedInUser.getUserId()+"<--------");

		VehicleResponse.VehicleResponseBuilder builder = VehicleResponse.builder()
				.lotNo(vehicle.getLotNo())
				.title(vehicle.getTitle())
				.description(vehicle.getDescription())
				.targetSellingPrice(vehicle.getTargetSellingPrice())
				.status(vehicle.getStatus() != null ? vehicle.getStatus().name() : null)
				.bidCloseDate(vehicle.getBidCloseDate())
				.createdAt(vehicle.getCreatedAt());

		// =================================================
		// ADMIN
		// =================================================

		if (loggedInUser.getRole().equalsIgnoreCase("ADMIN")) {

			builder.minSellingPrice(vehicle.getMinSellingPrice())
					.currentBidAmount(vehicle.getCurrentBidAmount())
					.winningBidAmount(vehicle.getWinningBidAmount())
					.sellerName(vehicle.getClientId() != null ? vehicle.getClientId().getName() : null)
					.sellerEmail(vehicle.getClientId() != null ? vehicle.getClientId().getEmail() : null)
					.winnerName(vehicle.getWonBy() != null ? vehicle.getWonBy().getName() : null)
					.winnerEmail(vehicle.getWonBy() != null ? vehicle.getWonBy().getEmail() : null)
					.currentHighestBidderName(vehicle.getCurrentHighestBidder() != null ? vehicle.getCurrentHighestBidder().getName() : null)
					.currentHighestBidderEmail(vehicle.getCurrentHighestBidder() != null ? vehicle.getCurrentHighestBidder().getEmail() : null);
		}

		// =================================================
		// SELLER
		// =================================================

		else if (loggedInUser.getRole().equalsIgnoreCase("SELLER")) {

			if (vehicle.getClientId() != null && vehicle.getClientId().getUserId().equals(loggedInUser.getUserId())) {
				builder.minSellingPrice(vehicle.getMinSellingPrice())
						.winningBidAmount(vehicle.getWinningBidAmount())
						.sellerName(vehicle.getClientId().getName())
						.sellerEmail(vehicle.getClientId().getEmail());
			}
		}

		// =================================================
		// BUYER
		// =================================================

		else if (loggedInUser.getRole().equalsIgnoreCase("BUYER")) {
			builder.currentBidAmount(vehicle.getCurrentBidAmount());
			if (vehicle.getWonBy() != null && vehicle.getWonBy().getUserId().equals(loggedInUser.getUserId())) {
				builder.winningBidAmount(vehicle.getWinningBidAmount())
						.winnerName(vehicle.getWonBy().getName())
						.winnerEmail(vehicle.getWonBy().getEmail());
			}
		}

		// =================================================
		// ANONYMOUS
		// =================================================

		else {
			builder.currentBidAmount(vehicle.getCurrentBidAmount());
		}

		return builder.build();
	}
}