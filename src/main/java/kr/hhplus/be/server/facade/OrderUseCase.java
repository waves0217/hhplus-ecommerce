package kr.hhplus.be.server.facade;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.dto.OrderItemRequest;
import kr.hhplus.be.server.dto.OrderResponse;
import kr.hhplus.be.server.domain.order.OrderService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderUseCase {

    private final OrderService orderService;

    public OrderUseCase(OrderService orderService) {
        this.orderService = orderService;
    }

    public OrderResponse createOrder(Long userId, List<OrderItemRequest> items, Long couponId) {
        Order order = orderService.createOrder(userId, items, couponId);
        return OrderResponse.fromEntity(order);
    }

    public OrderResponse getOrderDetails(Long orderId) {
        Order order = orderService.getOrderDetails(orderId);
        return OrderResponse.fromEntity(order);
    }

}