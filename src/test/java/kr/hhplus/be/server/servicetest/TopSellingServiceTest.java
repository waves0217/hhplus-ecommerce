package kr.hhplus.be.server.servicetest;

import kr.hhplus.be.server.domain.orderDetail.OrderDetail;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.dto.TopSellingProductDto;
import kr.hhplus.be.server.domain.orderDetail.OrderDetailRepository;
import kr.hhplus.be.server.domain.product.TopSellingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TopSellingServiceTest {

    @Mock
    private OrderDetailRepository orderDetailRepository;

    @InjectMocks
    private TopSellingService topSellingService;

    @Test
    @DisplayName("상위 판매량 상품 조회 성공")
    void testGetTopSellingProducts_Success() {
        // Given
        LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(3);
        Pageable pageable = PageRequest.of(0, 5);

        // Mock 데이터: Product
        Product productA = Product.builder()
                .productId(1L)
                .name("Product A")
                .price(1000)
                .stock(50)
                .build();

        Product productB = Product.builder()
                .productId(2L)
                .name("Product B")
                .price(800)
                .stock(30)
                .build();

        // Mock 데이터: OrderDetail
        OrderDetail orderDetailA = OrderDetail.builder()
                .orderDetailId(1L)
                .product(productA)
                .quantity(100)
                .price(1000)
                .build();

        OrderDetail orderDetailB = OrderDetail.builder()
                .orderDetailId(2L)
                .product(productB)
                .quantity(80)
                .price(800)
                .build();

        // Mock 데이터:
        List<TopSellingProductDto> mockResult = List.of(
                new TopSellingProductDto(productA.getProductId(), productA.getName(), 100L),
                new TopSellingProductDto(productB.getProductId(), productB.getName(), 80L)
        );

        // Repository Mock 설정
        when(orderDetailRepository.findTopSellingProducts(any(LocalDateTime.class), eq(pageable))).thenReturn(mockResult);
        // When
        List<TopSellingProductDto> result = topSellingService.getTopSellingProducts(5);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Product A", result.get(0).getProductName());
        assertEquals(100L, result.get(0).getTotalQuantity());
        assertEquals("Product B", result.get(1).getProductName());
        assertEquals(80L, result.get(1).getTotalQuantity());
    }

}
