package kr.hhplus.be.server;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.enums.PaymentStatus;
import kr.hhplus.be.server.domain.enums.ProductStatus;
import kr.hhplus.be.server.domain.enums.UserCouponStatus;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.userCoupon.UserCoupon;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DomainTest {
    /**
     *  Payment
    */
    @Test
    @DisplayName("Payment 생성 성공 - 정상적인 금액과 주문 정보로 생성")
    void testCreatePayment_Success() {
        // Given
        Order order = new Order();
        int amount = 1000;
        // When
        Payment payment = Payment.create(order, amount);
        // Then
        assertNotNull(payment);
        assertEquals(amount, payment.getAmount());
        assertEquals(PaymentStatus.PENDING, payment.getStatus());
        assertNotNull(payment.getCreatedAt());
        assertNotNull(payment.getUpdatedAt());
    }
    @Test
    @DisplayName("Payment 생성 실패 - 음수 금액으로 생성 시 예외 발생")
    void testCreatePayment_Fail_NegativeAmount() {
        // Given
        Order order = new Order();
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> Payment.create(order, -1000));
    }
    @Test
    @DisplayName("Payment 상태 변경 성공 - PENDING -> COMPLETED")
    void testCompletePayment_Success() {
        // Given
        Payment payment = Payment.create(new Order(), 1000);
        // When
        payment.completePayment();
        // Then
        assertEquals(PaymentStatus.COMPLETED, payment.getStatus());
    }
    @Test
    @DisplayName("Payment 상태 변경 실패 - 이미 COMPLETED 상태일 때 다시 COMPLETED로 변경 시 예외 발생")
    void testCompletePayment_Fail_AlreadyCompleted() {
        // Given
        Payment payment = Payment.create(new Order(), 1000);
        payment.completePayment();
        // When & Then
        assertThrows(IllegalStateException.class, payment::completePayment);
    }

    /**
     *  Product
     */
    @Test
    @DisplayName("재고 감소 성공 - 정상적인 수량 감소")
    void testReduceStock_Success() {
        // Given
        Product product = Product.create("Test Product", 1000, 10, ProductStatus.AVAILABLE);

        // When
        product.reduceStock(3);

        // Then
        assertEquals(7, product.getStock());
    }

    @Test
    @DisplayName("재고 감소 실패 - 재고 부족 시 예외 발생")
    void testReduceStock_Fail_NotEnoughStock() {
        // Given
        Product product = Product.create("Test Product", 1000, 2, ProductStatus.AVAILABLE);

        // When & Then
        assertThrows(IllegalStateException.class, () -> product.reduceStock(3));
    }

    @Test
    @DisplayName("재고 증가 성공 - 정상적인 수량 증가")
    void testIncreaseStock() {
        // Given
        Product product = Product.create("Test Product", 1000, 5, ProductStatus.AVAILABLE);

        // When
        product.increaseStock(3);

        // Then
        assertEquals(8, product.getStock());
    }

    @Test
    @DisplayName("상품 상태 변경 성공 - AVAILABLE -> OUT_OF_STOCK")
    void testUpdateStatus() {
        // Given
        Product product = Product.create("Test Product", 1000, 10, ProductStatus.AVAILABLE);

        // When
        product.updateStatus(ProductStatus.OUT_OF_STOCK);

        // Then
        assertEquals(ProductStatus.OUT_OF_STOCK, product.getStatus());
    }

    /**
     *  UserCoupon
     */
    @Test
    @DisplayName("UserCoupon 생성 성공 - 기본 상태 UNUSED로 생성")
    void testCreateUserCoupon() {
        // Given
        User user = new User();
        Coupon coupon = new Coupon();

        // When
        UserCoupon userCoupon = UserCoupon.create(user, coupon);

        // Then
        assertNotNull(userCoupon);
        assertEquals(UserCouponStatus.UNUSED, userCoupon.getStatus());
        assertNotNull(userCoupon.getCreatedAt());
        assertNotNull(userCoupon.getUpdatedAt());
    }

    @Test
    @DisplayName("UserCoupon 상태 변경 성공 - UNUSED -> USED")
    void testMarkAsUsed_Success() {
        // Given
        UserCoupon userCoupon = UserCoupon.create(new User(), new Coupon());

        // When
        userCoupon.markAsUsed();

        // Then
        assertEquals(UserCouponStatus.USED, userCoupon.getStatus());
    }

    @Test
    @DisplayName("UserCoupon 상태 변경 실패 - 이미 USED 상태에서 다시 변경 시 예외 발생")
    void testMarkAsUsed_Fail_AlreadyUsed() {
        // Given
        UserCoupon userCoupon = UserCoupon.create(new User(), new Coupon());
        userCoupon.markAsUsed();

        // When & Then
        assertThrows(IllegalStateException.class, userCoupon::markAsUsed);
    }
}
