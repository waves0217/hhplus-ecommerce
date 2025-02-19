package kr.hhplus.be.server.servicetest;

import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.product.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("상품 목록 조회 성공 - 페이징")
    void testGetProducts() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Product> productList = List.of(
                new Product(1L, "Product 1", 1000, 10, ProductStatus.AVAILABLE, null, null, null),
                new Product(2L, "Product 2", 2000, 5, ProductStatus.OUT_OF_STOCK, null, null, null)
        );
        Page<Product> productPage = new PageImpl<>(productList, pageable, productList.size());
        when(productRepository.findAll(pageable)).thenReturn(productPage);

        // When
        Page<Product> result = productService.getProducts(pageable);

        // Then
        assertEquals(2, result.getContent().size());
        assertEquals("Product 1", result.getContent().get(0).getName());
    }

    @Test
    @DisplayName("상품 상세 조회 성공")
    void testGetProductById_Success() {
        // Given
        Product product = new Product(1L, "Product 1", 1000, 10, ProductStatus.AVAILABLE, null, null, null);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // When
        Product result = productService.getProductById(1L);

        // Then
        assertNotNull(result);
        assertEquals("Product 1", result.getName());
    }
}
