package com.example.order.domain.repository;

import com.example.order.domain.model.CartItem;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository {

    CartItem save(CartItem cartItem);
    Optional<CartItem> findByProductId(Long productId);

    Optional<CartItem> findByProductIdAndCreatedBy(Long productId, String username);

    Optional<CartItem> findById(long cartItemId);

    void deleteById(Long id);

    boolean existById(Long cartItemId);

    List<CartItem> findAllByCreatedBy(String username);

    void deleteAllByCreatedBy(String username);
}
