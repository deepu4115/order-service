package com.example.order.api;

import com.example.order.application.dto.CartItemResponse;
import com.example.order.application.dto.CartResponse;
import com.example.order.application.facade.CartFacade;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("${app.base-url}")
@AllArgsConstructor
public class CartController {

    private final CartFacade cartFacade;

    @GetMapping("/cart")
    public ResponseEntity<CartResponse> getCart(Principal principal) {
        String username = getUsername(principal);
        CartResponse cart = cartFacade.getCart(username);
        return ResponseEntity.ok(cart);
    }

    @GetMapping("/cart-items")
    public ResponseEntity<CartResponse> getCartItems(Principal principal) {
        String username = getUsername(principal);
        CartResponse cart = cartFacade.getCart(username);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/cart-items/products/{productId}")
    public ResponseEntity<CartItemResponse> createCartItem(@PathVariable Long productId, @RequestParam int quantity, Principal principal) {
        String username = getUsername(principal);
        CartItemResponse response = cartFacade.createCartItemForUser(productId, quantity, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/cart-items/{cartItemId}")
    public ResponseEntity<CartItemResponse> updateCartItem(@PathVariable long cartItemId, @RequestParam int quantity) {
        CartItemResponse updatedCartItem = cartFacade.updateCartItem(cartItemId, quantity);
        return ResponseEntity.ok(updatedCartItem);
    }

    @DeleteMapping("/cart-items/{id}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable Long id) {
        cartFacade.deleteCartItem(id);
        return ResponseEntity.noContent().build();
    }

    private String getUsername(Principal principal) {
        if (principal != null) {
            return principal.getName();
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null && auth.isAuthenticated()) ? auth.getName() : null;
    }
}
