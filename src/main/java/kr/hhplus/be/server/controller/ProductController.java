package kr.hhplus.be.server.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Product API", description = "상품 관련 API를 제공합니다.")
public class ProductController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {

        // Mock 데이터
        Map<String, Object> response = new HashMap<>();
        response.put("result", "SUCCESS");
        Map<String, Object> data = new HashMap<>();
        data.put("page", Map.of("number", page, "size", size, "totalPages", 5, "totalElements", 50));
        data.put("items", List.of(
                Map.of("productId", 1, "productName", "Product A", "price", 10000, "stock", 50),
                Map.of("productId", 2, "productName", "Product B", "price", 20000, "stock", 30)
        ));
        response.put("data", data);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/top")
    public ResponseEntity<Map<String, Object>> getTopProducts() {
        // Mock 데이터
        Map<String, Object> response = new HashMap<>();
        response.put("result", "SUCCESS");
        response.put("data", Map.of(
                "period", Map.of("startDate", "2025-01-01", "endDate", "2025-01-03"),
                "topProducts", List.of(
                        Map.of("productId", 1, "productName", "Product A", "totalSold", 50, "price", 10000),
                        Map.of("productId", 2, "productName", "Product B", "totalSold", 40, "price", 20000)
                )
        ));

        return ResponseEntity.ok(response);
    }
}
