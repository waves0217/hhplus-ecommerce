package kr.hhplus.be.server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/coupon")
public class CouponController {

    @GetMapping("/users/{userId}")
    public ResponseEntity<Map<String, Object>> getUserCoupons(@PathVariable int userId) {
        // Mock 데이터
        Map<String, Object> response = new HashMap<>();
        response.put("result", "SUCCESS");
        response.put("data", Map.of(
                "userId", userId,
                "coupon", List.of(
                        Map.of("userCouponId", 123, "couponName", "10% 할인", "discountRate", 10,
                                "discountType", "PERCENT", "couponId", 22, "expiredAt", "2026-01-01T00:00:00", "useYn", "N")
                )
        ));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/issue")
    public ResponseEntity<Map<String, Object>> issueCoupon(@RequestBody Map<String, Object> request) {
        // Mock 데이터
        Map<String, Object> response = new HashMap<>();
        response.put("result", "SUCCESS");
        response.put("data", Map.of(
                "userCouponId", 123,
                "userId", request.get("userId"),
                "couponId", request.get("couponId"),
                "couponName", "New Year Discount",
                "issuedAt", "2024-12-31T12:00:00",
                "expiredAt", "2025-01-31T12:00:00",
                "status", "ISSUED"
        ));
        return ResponseEntity.ok(response);
    }
}
