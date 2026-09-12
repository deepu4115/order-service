package com.example.order.application.service;

import com.example.order.application.integration.ProductClient;
import com.example.order.domain.exceptions.CartItemsNotFoundException;
import com.example.order.domain.model.CartItem;
import com.example.order.domain.model.Order;
import com.example.order.domain.model.OrderItem;
import com.example.order.domain.repository.CartItemRepository;
import com.example.order.domain.repository.OrderItemRepository;
import com.example.order.domain.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductClient productClient;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void createOrder_Success() {
        String username = "user1";
        CartItem cartItem = CartItem.builder().itemId(1L).productId(10L).quantity(2).createdBy(username).build();
        OrderItem orderItem = OrderItem.builder().itemId(100L).productId(10L).quantity(2).build();
        Order savedOrder = Order.builder().orderId(1000L).createdBy(username).orderItems(List.of(orderItem)).build();

        when(cartItemRepository.findAllByCreatedBy(username)).thenReturn(List.of(cartItem));
        when(orderItemRepository.saveAll(any())).thenReturn(List.of(orderItem));
        when(orderRepository.save(any())).thenReturn(savedOrder);

        Order result = orderService.createOrder(username);

        assertNotNull(result);
        assertEquals(1000L, result.getOrderId());
        verify(productClient).decrementStock(10L, 2);
        verify(cartItemRepository).deleteAllByCreatedBy(username);
    }

    @Test
    void createOrder_EmptyCart_ThrowsException() {
        String username = "user1";
        when(cartItemRepository.findAllByCreatedBy(username)).thenReturn(Collections.emptyList());

        assertThrows(CartItemsNotFoundException.class, () -> orderService.createOrder(username));
    }
}
