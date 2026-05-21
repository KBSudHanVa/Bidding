package com.auctions.auction_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.auctions.auction_service.entity.Vehicle;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Integer>{

}
