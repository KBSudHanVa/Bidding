package com.auctions.auction_service.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.auctions.auction_service.entity.Vehicle;
import com.auctions.auction_service.enums.VehicleStatus;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Integer>, JpaSpecificationExecutor<Vehicle>{

	List<Vehicle> findByStatusAndBidCloseDateBefore(
	        VehicleStatus status,
	        LocalDateTime date);
	
}
