package com.example.order.application.dto;

public record CartItemDetailResponse(
        Long itemId,
        Long productId,
        String productTitle,
        double price,
        int quantity,
        double itemTotal,
        long createdAt,
        long updatedAt
) {
}
