package kr.hhplus.be.server.dto;

import kr.hhplus.be.server.domain.Order;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class OrderResponse {
    private final Long orderId;
    private final int totalPrice;
    private final int discountAmount;
    private final int chargedAmount;
    private final List<OrderDetailResponse> orderDetails;

    private OrderResponse(Long orderId, int totalPrice, int discountAmount, int chargedAmount, List<OrderDetailResponse> orderDetails) {
        this.orderId = orderId;
        this.totalPrice = totalPrice;
        this.discountAmount = discountAmount;
        this.chargedAmount = chargedAmount;
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
