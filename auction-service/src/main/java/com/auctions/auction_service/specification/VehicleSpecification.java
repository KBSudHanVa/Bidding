package com.auctions.auction_service.specification;

import com.auctions.auction_service.dto.VehicleFilterRequest;
import com.auctions.auction_service.entity.Vehicle;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class VehicleSpecification {

	public static Specification<Vehicle> filterVehicles(VehicleFilterRequest request) {

		return (root, query, cb) -> {

			List<Predicate> predicates = new ArrayList<>();

			if (request.getLotNo() != null) {
				predicates.add(cb.equal(root.get("lotNo"), request.getLotNo()));
			}

			if (request.getTitle() != null && !request.getTitle().trim().isEmpty()) {
				predicates.add(cb.like(cb.lower(root.get("title")), "%" + request.getTitle().toLowerCase() + "%"));
			}

			if (request.getDescription() != null && !request.getDescription().trim().isEmpty()) {
				predicates.add(cb.like(cb.lower(root.get("description")), "%" + request.getDescription().toLowerCase() + "%"));
			}

//			if (request.getAddedBy() != null && !request.getAddedBy().trim().isEmpty()) {
//				predicates.add(cb.like(cb.lower(root.get("addedBy")), "%" + request.getAddedBy().toLowerCase() + "%"));
//			}

//			if (request.getWonBy() != null && !request.getWonBy().trim().isEmpty()) {
//				predicates.add(cb.like(cb.lower(root.get("wonBy")), "%" + request.getWonBy().toLowerCase() + "%"));
//			}
			
			if (request.getWonBy() != null && !request.getWonBy().trim().isEmpty()) {
				Predicate winnerName = cb.like(cb.lower(root.get("wonBy").get("name")), "%" + request.getWonBy().toLowerCase() + "%");
				Predicate winnerEmail = cb.like(cb.lower(root.get("wonBy").get("email")), "%" + request.getWonBy().toLowerCase() + "%");
				predicates.add(cb.or(winnerName, winnerEmail));
			}

//			if (request.getClientId() != null && !request.getClientId().trim().isEmpty()) {
//				predicates.add(cb.like(cb.lower(root.get("clientId")), "%" + request.getClientId().toLowerCase() + "%"));
//			}
			
			if (request.getClientId() != null && !request.getClientId().trim().isEmpty()) {
				Predicate sellerName = cb.like(cb.lower(root.get("clientId").get("name")), "%" + request.getClientId().toLowerCase() + "%");
				Predicate sellerEmail = cb.like(cb.lower(root.get("clientId").get("email")), "%" + request.getClientId().toLowerCase() + "%");
				predicates.add(cb.or(sellerName, sellerEmail));
			}

//			if (request.getCurrentHighestBidder() != null && !request.getCurrentHighestBidder().trim().isEmpty()) {
//				predicates.add(cb.like(cb.lower(root.get("currentHighestBidder")), "%" + request.getCurrentHighestBidder().toLowerCase() + "%"));
//			}
			
			if (request.getCurrentHighestBidder() != null && !request.getCurrentHighestBidder().trim().isEmpty()) {
				Predicate bidderName = cb.like(cb.lower(root.get("currentHighestBidder").get("name")), "%" + request.getCurrentHighestBidder().toLowerCase() + "%");
				Predicate bidderEmail = cb.like(cb.lower(root.get("currentHighestBidder").get("email")), "%" + request.getCurrentHighestBidder().toLowerCase() + "%");
				predicates.add(cb.or(bidderName, bidderEmail));
			}

			if (request.getTargetSellingPrice() != null) {
				predicates.add(cb.equal(root.get("targetSellingPrice"), request.getTargetSellingPrice()));
			}

			if (request.getMinSellingPrice() != null) {
				predicates.add(cb.equal(root.get("minSellingPrice"), request.getMinSellingPrice()));
			}

			if (request.getWinningBidAmount() != null) {
				predicates.add(cb.equal(root.get("winningBidAmount"), request.getWinningBidAmount()));
			}

			if (request.getCurrentBidAmount() != null) {
				predicates.add(cb.equal(root.get("currentBidAmount"), request.getCurrentBidAmount()));
			}

			if (request.getStatus() != null) {
				predicates.add(cb.equal(root.get("status"), request.getStatus()));
			}

			if (request.getCreatedAt() != null) {
				predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), request.getCreatedAt()));
			}

			if (request.getBidCloseDate() != null) {
				predicates.add(cb.greaterThanOrEqualTo(root.get("bidCloseDate"), request.getBidCloseDate()));
			}

			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}
}