package kr.hhplus.be.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TopSellingProductResponse {
    private Long productId;
    private String productName;
    private Long totalQuantity;

    public static TopSellingProductResponse fromDto(TopSellingProductDto dto) {
        return new TopSellingProductResponse(dto.getProductId(), dto.getProductName(), dto.getTotalQuantity());
    }
}
