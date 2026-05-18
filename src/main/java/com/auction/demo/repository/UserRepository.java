package com.auction.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.auction.demo.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

}
