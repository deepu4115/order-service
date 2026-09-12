package com.example.order.api;

import com.example.order.application.dto.OrderItemResponse;
import com.example.order.application.dto.OrderResponse;
import com.example.order.application.facade.OrderFacade;
import com.example.order.domain.model.OrderStatus;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("${app.base-url}")
public class OrderController {

    private final OrderFacade orderFacade;

    @PostMapping("/orders")
    public ResponseEntity<OrderResponse> createOrder(@RequestParam(required = false) String username, Principal principal) {
        String authUser = getUsername(principal, username);
        OrderResponse response = orderFacade.createOrder(authUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> getUserOrders(Principal principal) {
        String authUser = getUsername(principal, null);
        List<OrderResponse> orders = orderFacade.getOrdersForUser(authUser);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long orderId) {
        OrderResponse response = orderFacade.findOrderById(orderId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/orders/order-items/{itemId}")
    public ResponseEntity<OrderItemResponse> updateOrderStatus(@PathVariable Long itemId, @RequestParam OrderStatus orderStatus) {
        OrderItemResponse response = orderFacade.updateOrderStatus(itemId, orderStatus);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/orders/order-items/{itemId}")
    public ResponseEntity<OrderItemResponse> cancelOrder(@PathVariable Long itemId) {
        OrderItemResponse response = orderFacade.cancelOrder(itemId);
        return ResponseEntity.ok(response);
    }

    private String getUsername(Principal principal, String fallbackUsername) {
        if (principal != null && principal.getName() != null && !principal.getName().isBlank()) {
            return principal.getName();
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getName() != null && !auth.getName().isBlank()) {
            return auth.getName();
        }
        return fallbackUsername;
    }
}
