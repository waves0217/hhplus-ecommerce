package kr.hhplus.be.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TopSellingProductResponse {
    private Long productId;
    private String productName;
    private Long totalQuantity;

    public static TopSellingProductResponse fromDto(TopSellingProductDto dto) {
        return new TopSellingProductResponse(dto.getProductId(), dto.getProductName(), dto.getTotalQuantity());
    }

    @Override
    public boolean equals(Object o) { // ✅ equals() 추가
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TopSellingProductResponse that = (TopSellingProductResponse) o;
        return Objects.equals(productId, that.productId) &&
                Objects.equals(productName, that.productName) &&
                Objects.equals(totalQuantity, that.totalQuantity);
    }

    @Override
    public int hashCode() { // ✅ hashCode() 추가
        return Objects.hash(productId, productName, totalQuantity);
    }
}