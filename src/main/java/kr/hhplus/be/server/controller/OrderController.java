package kr.hhplus.be.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.dto.OrderResponse;
import kr.hhplus.be.server.dto.request.OrderCreateRequest;
import kr.hhplus.be.server.facade.OrderUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Order API", description = "주문 관련 API를 제공합니다.")
public class OrderController {

    private final OrderUseCase orderUseCase;

    public OrderController(OrderUseCase orderUseCase) { this.orderUseCase = orderUseCase; }

    /**
     * 주문 생성
     */
    @PostMapping
    @Operation(summary = "주문 생성", description = "사용자와 상품 목록을 기반으로 주문을 생성합니다.")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderCreateRequest request) {
        OrderResponse response = orderUseCase.createOrder(
                request.getUserId(),
                request.getItems(),
                request.getCouponId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 주문 상세 조회
     */
    @GetMapping("/{orderId}")
    @Operation(summary = "주문 상세 조회", description = "특정 주문의 상세 정보를 조회합니다.")
    public ResponseEntity<OrderResponse> getOrderDetails(@PathVariable Long orderId) {
        OrderResponse response = orderUseCase.getOrderDetails(orderId);
        return ResponseEntity.ok(response);
    }

}
