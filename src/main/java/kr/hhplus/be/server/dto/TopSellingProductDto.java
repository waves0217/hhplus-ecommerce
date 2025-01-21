package kr.hhplus.be.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TopSellingProductDto {
    private Long productId;
    private String productName;
    private Long totalQuantity;
}
