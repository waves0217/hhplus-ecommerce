package kr.hhplus.be.server.servicetest;

import kr.hhplus.be.server.domain.balance.Balance;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.orderDetail.OrderDetail;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.dto.OrderItemRequest;
import kr.hhplus.be.server.domain.balance.BalanceRepository;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.userCoupon.UserCouponRepository;
import kr.hhplus.be.server.domain.order.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private BalanceRepository balanceRepository;

    @Mock
    private UserCouponRepository userCouponRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    @DisplayName("주문 생성 성공 - 쿠폰 없음")
    void testCreateOrder_Success_NoCoupon() {
        // Given
        Long userId = 1L;

        Balance balance = Balance.builder()
                .userId(userId)
                .amount(5000)
                .build();

        Product product = Product.builder()
                .productId(1L)
                .name("Test Product")
                .price(1000)
                .stock(10)
                .build();

        OrderItemRequest orderItem = new OrderItemRequest();
        orderItem.setProductId(1L);
        orderItem.setQuantity(2);

        when(balanceRepository.findById(userId)).thenReturn(Optional.of(balance));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Order order = orderService.createOrder(userId, List.of(orderItem), null);

        // Then
        assertNotNull(order);
        assertEquals(2000, order.getChargedAmount());
        assertEquals(8, product.getStock()); // 재고 감소 확인
        assertEquals(3000, balance.getAmount()); // 잔액 차감 확인
    }

    @Test
    @DisplayName("주문 생성 실패 - 재고 부족")
    void testCreateOrder_Fail_InsufficientStock() {
        // Given
        Long userId = 1L;

        Balance balance = Balance.builder()
                .userId(userId)
                .amount(5000)
                .build();

        Product product = Product.builder()
                .productId(1L)
                .name("Test Product")
                .price(1000)
                .stock(1)
                .build();

        OrderItemRequest orderItem = new OrderItemRequest();
        orderItem.setProductId(1L);
        orderItem.setQuantity(2);

        when(balanceRepository.findById(userId)).thenReturn(Optional.of(balance));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                orderService.createOrder(userId, List.of(orderItem), null));
        assertEquals("재고가 부족합니다: Test Product", exception.getMessage());
    }

    @Test
    @DisplayName("주문 생성 실패 - 잔액 부족")
    void testCreateOrder_Fail_InsufficientBalance() {
        // Given
        Long userId = 1L;

        Balance balance = Balance.builder()
                .userId(userId)
                .amount(500)
                .build();

        Product product = Product.builder()
                .productId(1L)
                .name("Test Product")
                .price(1000)
                .stock(10)
                .build();

        OrderItemRequest orderItem = new OrderItemRequest();
        orderItem.setProductId(1L);
        orderItem.setQuantity(2);

        when(balanceRepository.findById(userId)).thenReturn(Optional.of(balance));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                orderService.createOrder(userId, List.of(orderItem), null));
        assertEquals("잔액이 부족합니다.", exception.getMessage());
    }

    @Test
    @DisplayName("주문 상세 조회 성공")
    void testGetOrderDetails_Success() {
        // Given
        Long orderId = 1L;

        Product product = Product.builder()
                .productId(1L)
                .name("Test Product")
                .price(1000)
                .stock(10)
                .build();

        OrderDetail orderDetail = OrderDetail.builder()
                .orderDetailId(1L)
                .product(product)
                .quantity(2)
                .price(1000)
                .build();

        Order order = Order.builder()
                .orderId(orderId)
                .totalPrice(2000)
                .chargedAmount(2000)
                .orderDetails(List.of(orderDetail))
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // When
        Order result = orderService.getOrderDetails(orderId);

        // Then
        assertNotNull(result);
        assertEquals(2000, result.getTotalPrice());
        assertEquals(1, result.getOrderDetails().size());
        assertEquals("Test Product", result.getOrderDetails().get(0).getProduct().getName());
    }
}
