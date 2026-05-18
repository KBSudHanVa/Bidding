package com.auction.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.auction.demo.entity.BidHistory;

public interface BidHistoryRepository extends JpaRepository<BidHistory, Integer> {

    List<BidHistory> findByLotNo(Integer lotNo);
}
