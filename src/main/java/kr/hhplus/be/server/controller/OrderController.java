package kr.hhplus.be.server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody Map<String, Object> request) {
        // Mock 데이터
        Map<String, Object> response = new HashMap<>();
        response.put("result", "SUCCESS");
        response.put("data", Map.of(
                "orderId", 123,
                "totalPrice", 45000,
                "discountedPrice", 40000,
                "remainingBalance", 10000,
                "products", List.of(
                        Map.of("productId", 1, "productName", "Product A", "quantity", 2, "price", 20000),
                        Map.of("productId", 3, "productName", "Product C", "quantity", 1, "price", 5000)
                ),
                "coupon", Map.of(
                        "couponId", 10,
                        "name", "10% Discount",
                        "discountAmount", 5000,
                        "discountType", "PERCENT"
                )
        ));
        return ResponseEntity.ok(response);
    }
}
