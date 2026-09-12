package com.example.order.infrastructure.database.repositories;

import com.example.order.infrastructure.database.entities.CartItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {

    Optional<CartItemEntity> findByProductId(Long productId);

    Optional<CartItemEntity> findByProductIdAndCreatedBy(Long productId, String username);

    Collection<CartItemEntity> findAllByCreatedBy(String username);

    void deleteAllByCreatedBy(String username);
}
