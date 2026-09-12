package com.example.order.domain.service.contracts;

import com.example.order.domain.model.Order;
import com.example.order.domain.model.OrderItem;
import com.example.order.domain.model.OrderStatus;

import java.util.List;

public interface OrderService {

    Order createOrder(String username);

    Order findOrderById(Long orderId);

    List<Order> getOrdersForUser(String username);

    OrderItem updateOrderStatus(Long orderItemId, OrderStatus orderStatus);

    OrderItem cancelOrder(Long orderItemId);
}
