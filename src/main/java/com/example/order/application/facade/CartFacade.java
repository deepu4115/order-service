package com.example.order.application.facade;

import com.example.order.application.dto.CartItemDetailResponse;
import com.example.order.application.dto.CartItemResponse;
import com.example.order.application.dto.CartResponse;
import com.example.order.application.integration.ProductClient;
import com.example.order.application.integration.dto.Product;
import com.example.order.application.mapping.CartMapper;
import com.example.order.domain.service.contracts.CartService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class CartFacade {

    private final CartService cartService;
    private final CartMapper cartMapper;
    private final ProductClient productClient;

    public CartItemResponse createCartItem(Long productId, int quantity) {
        var cartItem = cartService.createCartItem(productId, quantity);
        return cartMapper.mapToCartItemResponse(cartItem);
    }

    public CartItemResponse createCartItemForUser(Long productId, int quantity, String username) {
        var cartItem = cartService.createCartItemForUser(productId, quantity, username);
        return cartMapper.mapToCartItemResponse(cartItem);
    }

    public CartResponse getCart(String username) {
        var cartItems = cartService.getCartItemsForUser(username);
        double total = 0.0;
        List<CartItemDetailResponse> detailResponses = new ArrayList<>();
        for (var item : cartItems) {
            Product product = null;
            try {
                product = productClient.getProductById(item.getProductId());
            } catch (Exception e) {
                // ignore
            }
            double price = product != null ? product.getPrice() : 0.0;
            String title = product != null ? product.getTitle() : "Unknown Product";
            double itemTotal = price * item.getQuantity();
            total += itemTotal;

            detailResponses.add(new CartItemDetailResponse(
                    item.getItemId(),
                    item.getProductId(),
                    title,
                    price,
                    item.getQuantity(),
                    itemTotal,
                    item.getCreatedAt() != null ? item.getCreatedAt().toEpochMilli() : 0L,
                    item.getUpdatedAt() != null ? item.getUpdatedAt().toEpochMilli() : 0L
            ));
        }
        return new CartResponse(detailResponses, total);
    }

    public CartItemResponse updateCartItem(long cartItemId, int quantity) {
        var cartItem = cartService.updateCartItem(cartItemId, quantity);
        return cartMapper.mapToCartItemResponse(cartItem);
    }

    public void deleteCartItem(Long id) {
        cartService.deleteCartItem(id);
    }
}
