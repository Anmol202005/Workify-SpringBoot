package com.workify.auth.repository;

import com.workify.auth.models.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Orders, Long> {
    public Orders findByOrderId(String orderId);
}
