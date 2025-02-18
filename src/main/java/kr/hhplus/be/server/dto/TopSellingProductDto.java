package kr.hhplus.be.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TopSellingProductDto {
    private Long productId;
    private String productName;
    private Long totalQuantity;
}
