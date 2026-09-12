package com.example.order.application.service;

import com.example.order.application.integration.ProductClient;
import com.example.order.domain.exceptions.CartItemsNotFoundException;
import com.example.order.domain.model.CartItem;
import com.example.order.domain.model.Order;
import com.example.order.domain.model.OrderItem;
import com.example.order.domain.model.OrderStatus;
import com.example.order.domain.repository.CartItemRepository;
import com.example.order.domain.repository.OrderItemRepository;
import com.example.order.domain.repository.OrderRepository;
import com.example.order.domain.service.contracts.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductClient productClient;

    @Override
    @Transactional
    public Order createOrder(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Authenticated user is required to create an order.");
        }

        List<CartItem> cartItems = cartItemRepository.findAllByCreatedBy(username);
        if (cartItems.isEmpty()) {
            throw new CartItemsNotFoundException("Cannot create order: Cart is empty for user " + username);
        }

        List<OrderItem> orderItems = cartItems.stream()
                .map(item -> {
                    productClient.decrementStock(item.getProductId(), item.getQuantity());
                    return OrderItem.createNew(item.getProductId(), item.getQuantity());
                })
                .toList();

        orderItems = orderItemRepository.saveAll(orderItems);
        Order order = Order.createNew(orderItems, username);
        Order savedOrder = orderRepository.save(order);

        cartItemRepository.deleteAllByCreatedBy(username);

        return savedOrder;
    }

    @Override
    public Order findOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow();
    }

    @Override
    public List<Order> getOrdersForUser(String username) {
        if (username == null || username.isBlank()) {
            return Collections.emptyList();
        }
        return orderRepository.findAllByCreatedBy(username);
    }

    @Override
    public OrderItem updateOrderStatus(Long orderItemId, OrderStatus orderStatus) {
        return orderItemRepository.findById(orderItemId)
                .map(item -> {
                    item.updateStatus(orderStatus);
                    return orderItemRepository.save(item);
                })
                .orElseThrow();
    }

    @Override
    public OrderItem cancelOrder(Long orderItemId) {
        return orderItemRepository.findById(orderItemId)
                .map(item -> {
                    item.updateStatus(OrderStatus.CANCELLED);
                    return orderItemRepository.save(item);
                })
                .orElseThrow();
    }
}
