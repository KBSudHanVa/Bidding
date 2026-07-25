package com.auctions.auction_service.config;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.auctions.auction_service.entity.Vehicle;
import com.auctions.auction_service.enums.VehicleStatus;
import com.auctions.auction_service.repository.VehicleRepository;

import jakarta.transaction.Transactional;

@EnableScheduling
@Configuration
public class SchedulerConfig {
	
	@Autowired
	VehicleRepository vehicleRepository;
	
	@Scheduled(cron = "0 * * * * *")
	@Transactional
	public void closeExpiredAuctions() {

	    List<Vehicle> vehicles =
	            vehicleRepository.findByStatusAndBidCloseDateBefore(
	                    VehicleStatus.LIVE,
	                    LocalDateTime.now());

	    for (Vehicle vehicle : vehicles) {

//	        vehicle.setStatus(VehicleStatus.CLOSED);
//	        if (vehicle.getCurrentHighestBidder() != null) {
//
//	            vehicle.setWonBy(vehicle.getCurrentHighestBidder());
//	            vehicle.setWinningBidAmount(vehicle.getCurrentBidAmount());
//
//	        }
	        
	        if (vehicle.getCurrentHighestBidder() == null) {
	            vehicle.setStatus(VehicleStatus.CANCELLED);
	            vehicle.setModifiedOn(LocalDateTime.now());
	        }
	        else {
	            vehicle.setStatus(VehicleStatus.CLOSED);
	            vehicle.setModifiedOn(LocalDateTime.now());
	            vehicle.setWonBy(vehicle.getCurrentHighestBidder());
	            vehicle.setWinningBidAmount(vehicle.getCurrentBidAmount());
	        }

	        vehicleRepository.save(vehicle);
	    }
	}

}
