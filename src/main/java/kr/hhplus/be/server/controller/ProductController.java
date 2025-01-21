package kr.hhplus.be.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.dto.ProductResponse;
import kr.hhplus.be.server.dto.TopSellingProductResponse;
import kr.hhplus.be.server.facade.ProductUseCase;
import kr.hhplus.be.server.facade.TopSellingProductsUseCase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Product API", description = "상품 관련 API를 제공합니다.")
public class ProductController {

    private final ProductUseCase productUseCase;
    private final TopSellingProductsUseCase topSellingProductsUseCase;

    public ProductController(ProductUseCase productUseCase, TopSellingProductsUseCase topSellingProductsUseCase) {
        this.productUseCase = productUseCase;
        this.topSellingProductsUseCase = topSellingProductsUseCase;
    }

    /**
     * 상품 목록 조회
     */
    @GetMapping
    @Operation(summary = "상품 목록 조회", description = "모든 상품 목록을 페이징 형태로 조회합니다.")
    public ResponseEntity<Page<ProductResponse>> getProducts(Pageable pageable) {
        Page<ProductResponse> response = productUseCase.getProducts(pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * 상품 상세 조회
     */
    @GetMapping("/{productId}")
    @Operation(summary = "상품 상세 조회", description = "특정 상품의 상세 정보를 조회합니다.")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long productId) {
        ProductResponse response = productUseCase.getProductById(productId);
        return ResponseEntity.ok(response);
    }

    /**
     * 상위 판매량 상품 조회
     */
    @GetMapping("/top-selling")
    @Operation(summary = "인기 상품 조회", description = "최근 3일간 가장 많이 팔린 상품을 조회합니다.")
    public ResponseEntity<List<TopSellingProductResponse>> getTopSellingProducts(@RequestParam(defaultValue = "5") int limit) {
        List<TopSellingProductResponse> response = topSellingProductsUseCase.getTopSellingProducts(limit);
        return ResponseEntity.ok(response);
    }

}
