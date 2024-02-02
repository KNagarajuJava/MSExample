package com.knr.os.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.knr.os.api.entity.Order;

public interface OrderRepository extends JpaRepository<Order,Integer> {

}
