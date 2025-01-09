package kr.hhplus.be.server.dto;

import kr.hhplus.be.server.domain.OrderDetail;
import lombok.Getter;

@Getter
public class OrderDetailResponse {
    private final Long productId;
    private final String productName;
    private final int price;
    private final int quantity;

    private OrderDetailResponse(Long productId, String productName, int price, int quantity) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
    }

    public static OrderDetailResponse fromEntity(OrderDetail detail) {
        return new OrderDetailResponse(detail.getProduct().getProductId(),
                detail.getProduct().getName(), detail.getPrice(), detail.getQuantity());
    }
}
