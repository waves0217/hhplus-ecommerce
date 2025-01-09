package kr.hhplus.be.server.domain;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.enums.ProductStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private Integer stock;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "product")
    private List<OrderDetail> orderDetails = new ArrayList<>();

    public static Product create(String name, Integer price, Integer stock, ProductStatus status) {
        if (price <= 0) {
            throw new IllegalArgumentException("상품 가격은 0보다 커야 합니다.");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("재고가 부족합니다.");
        }
        Product product = new Product();
        product.name = name;
        product.price = price;
        product.stock = stock;
        product.status = status;
        product.createdAt = LocalDateTime.now();
        product.updatedAt = LocalDateTime.now();
        return product;
    }

    public void reduceStock(int quantity) {
        if (quantity > this.stock) {
            throw new IllegalStateException("감소할 재고 수량은 0보다 커야 합니다.");
        }
        this.stock -= quantity;
        this.updatedAt = LocalDateTime.now();
    }

    public void increaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("증가할 재고 수량은 0보다 커야 합니다.");
        }
        this.stock += quantity;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateStatus(ProductStatus newStatus) {
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }

}
