package kr.hhplus.be.server.facade;

import kr.hhplus.be.server.domain.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.dto.ProductResponse;
import kr.hhplus.be.server.domain.product.ProductService;
import org.springframework.stereotype.Component;

@Component
public class ProductUseCase {

    private final ProductService productService;

    public ProductUseCase(ProductService productService) {
        this.productService = productService;
    }

    public Page<ProductResponse> getProducts(Pageable pageable) {
        Page<Product> products = productService.getProducts(pageable);
        return products.map(ProductResponse::fromEntity);
    }

    public ProductResponse getProductById(Long productId) {
        Product product = productService.getProductById(productId);
        return ProductResponse.fromEntity(product);
    }

    public void updateProductStatus(Long productId, ProductStatus newStatus) {
        productService.updateProductStatus(productId, newStatus);
    }

    public void increaseStock(Long productId, int quantity) {
        productService.increaseStock(productId, quantity);
    }

    public void reduceStock(Long productId, int quantity) {
        productService.reduceStock(productId, quantity);
    }
}
