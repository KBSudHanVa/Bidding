package com.auction.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "bid_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BidHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "lot_no")
    private Integer lotNo;

    @Column(name = "bid_by")
    private String bidBy;

    @Column(name = "bid_amount")
    private Double bidAmount;

    @Column(name = "bid_time")
    private LocalDateTime bidTime;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getLotNo() {
		return lotNo;
	}

	public void setLotNo(Integer lotNo) {
		this.lotNo = lotNo;
	}

	public String getBidBy() {
		return bidBy;
	}

	public void setBidBy(String bidBy) {
		this.bidBy = bidBy;
	}

	public Double getBidAmount() {
		return bidAmount;
	}

	public void setBidAmount(Double bidAmount) {
		this.bidAmount = bidAmount;
	}

	public LocalDateTime getBidTime() {
		return bidTime;
	}

	public void setBidTime(LocalDateTime bidTime) {
		this.bidTime = bidTime;
	}

	public BidHistory(Integer id, Integer lotNo, String bidBy, Double bidAmount, LocalDateTime bidTime) {
		super();
		this.id = id;
		this.lotNo = lotNo;
		this.bidBy = bidBy;
		this.bidAmount = bidAmount;
		this.bidTime = bidTime;
	}

	public BidHistory() {
		super();
		// TODO Auto-generated constructor stub
	}
    
    
}
