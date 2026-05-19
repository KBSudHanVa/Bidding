package com.auctions.auction_service.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auction")
public class AuctionsContrller {
	
	@GetMapping("/admin")
	@PreAuthorize("hasRole('ADMIN')")
	public String adminApi() {
	    return "Admin API";
	}
	
	@GetMapping("/test")
//	@PreAuthorize("hasRole('ADMIN')")
    public String test(HttpServletRequest request) {
        return ("Working-->" + request.getHeader("X-User-Role")).toString()+"<--";
    }

}
