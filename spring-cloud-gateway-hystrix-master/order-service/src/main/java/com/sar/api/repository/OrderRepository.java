package com.sar.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sar.os.api.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer>{

}
