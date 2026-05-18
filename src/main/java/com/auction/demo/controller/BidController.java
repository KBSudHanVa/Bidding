package com.auction.demo.controller;

import org.springframework.web.bind.annotation.*;

import com.auction.demo.entity.BidHistory;
import com.auction.demo.repository.BidHistoryRepository;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/bids")
public class BidController {

    private final BidHistoryRepository bidHistoryRepository;
    
//    test git

    public BidController(BidHistoryRepository bidHistoryRepository) {
        this.bidHistoryRepository = bidHistoryRepository;
    }

    @PostMapping
    public BidHistory placeBid(@RequestBody BidHistory bid) {

        bid.setBidTime(LocalDateTime.now());

        return bidHistoryRepository.save(bid);
    }

    @GetMapping("/{lotNo}")
    public List<BidHistory> getBids(@PathVariable Integer lotNo) {
        return bidHistoryRepository.findByLotNo(lotNo);
    }
}
