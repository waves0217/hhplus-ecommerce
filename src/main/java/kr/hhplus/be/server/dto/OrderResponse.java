package kr.hhplus.be.server.dto;

import kr.hhplus.be.server.domain.Order;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class OrderResponse {
    private final Long orderId;
    private final int totalPrice;
    private final int discount;
    private final int finalPrice;
    private final List<OrderDetailResponse> orderDetails;

    private OrderResponse(Long orderId, int totalPrice, int discount, int finalPrice, List<OrderDetailResponse> orderDetails) {
        this.orderId = orderId;
        this.totalPrice = totalPrice;
        this.discount = discount;
        this.finalPrice = finalPrice;
        this.orderDetails = orderDetails;
    }

    public static OrderResponse fromEntity(Order order) {
        List<OrderDetailResponse> details = order.getOrderDetails().stream()
                .map(OrderDetailResponse::fromEntity)
                .collect(Collectors.toList());
        return new OrderResponse(order.getOrderId(), order.getTotalPrice(), order.getDiscountAmount(),
                order.getChargedAmount(), details);
    }
}
