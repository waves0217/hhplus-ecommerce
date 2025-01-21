package kr.hhplus.be.server.dto.request;

import kr.hhplus.be.server.dto.OrderItemRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateRequest {
    private Long userId;
    private List<OrderItemRequest> items;
    private Long couponId;
}
