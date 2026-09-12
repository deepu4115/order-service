package com.example.order.infrastructure.database.repositories;

import com.example.order.infrastructure.database.entities.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findAllByCreatedBy(String username);
}
