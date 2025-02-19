package kr.hhplus.be.server.dto;

import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.enums.ProductStatus;
import lombok.Getter;

@Getter
public class ProductResponse {

    private final Long id;
    private final String name;
    private final Integer price;
    private final Integer stock;
    private final ProductStatus status;

    private ProductResponse(Long id, String name, Integer price, Integer stock, ProductStatus status) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.status = status;
    }

    public static ProductResponse fromEntity(Product product) {
        return new ProductResponse(
                product.getProductId(),
                product.getName(),
                product.getPrice(),
                product.getStock(),
                product.getStatus()
        );
    }
}

