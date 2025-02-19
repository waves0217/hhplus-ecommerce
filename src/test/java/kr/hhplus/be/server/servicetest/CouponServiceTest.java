package kr.hhplus.be.server.servicetest;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.userCoupon.UserCoupon;
import kr.hhplus.be.server.domain.enums.CouponStatus;
import kr.hhplus.be.server.domain.enums.DiscountType;
import kr.hhplus.be.server.domain.enums.UserCouponStatus;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.userCoupon.UserCouponRepository;
import kr.hhplus.be.server.domain.coupon.CouponService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private UserCouponRepository userCouponRepository;

    @InjectMocks
    private CouponService couponService;

    @Test
    @DisplayName("쿠폰 발급 성공")
    void testIssueCoupon_Success() {
        // Given
        User user = User.builder()
                .userId(1L)
                .name("Test User")
                .build();

        Coupon coupon = Coupon.builder()
                .couponId(1L)
                .name("Discount")
                .amount(1000)
                .discountType(DiscountType.FIXED)
                .status(CouponStatus.ACTIVE)
                .quantity(10)
                .build();

        // Mock 동작 설정
        when(couponRepository.findById(1L)).thenReturn(Optional.of(coupon));
        when(userCouponRepository.save(any(UserCoupon.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        UserCoupon result = couponService.issueCouponToUser(1L, user);

        // Then
        assertNotNull(result);
        assertEquals(UserCouponStatus.UNUSED, result.getStatus());
        assertEquals(9, coupon.getQuantity()); // 쿠폰 수량 감소 확인
    }

    @Test
    @DisplayName("쿠폰 발급 실패 - 재고 부족")
    void testIssueCoupon_Fail_NoStock() {
        // Given
        User user = User.builder()
                .userId(1L)
                .name("Test User")
                .build();

        Coupon coupon = Coupon.builder()
                .couponId(1L)
                .name("Discount")
                .amount(1000)
                .discountType(DiscountType.FIXED)
                .status(CouponStatus.ACTIVE)
                .quantity(0)
                .build();

        when(couponRepository.findById(1L)).thenReturn(Optional.of(coupon));

        // When & Then
        assertThrows(IllegalStateException.class, () -> couponService.issueCouponToUser(1L, user));
    }

    @Test
    @DisplayName("쿠폰 사용 성공")
    void testUseCoupon_Success() {
        // Given
        UserCoupon userCoupon = UserCoupon.builder()
                .userCouponId(1L)
                .status(UserCouponStatus.UNUSED)
                .build();

        when(userCouponRepository.findById(1L)).thenReturn(Optional.of(userCoupon));

        // When
        couponService.useCoupon(1L);

        // Then
        assertEquals(UserCouponStatus.USED, userCoupon.getStatus());
    }

    @Test
    @DisplayName("쿠폰 사용 실패 - 이미 사용된 쿠폰")
    void testUseCoupon_Fail_AlreadyUsed() {
        // Given
        UserCoupon userCoupon = UserCoupon.builder()
                .userCouponId(1L)
                .status(UserCouponStatus.USED)
                .build();

        when(userCouponRepository.findById(1L)).thenReturn(Optional.of(userCoupon));

        // When & Then
        assertThrows(IllegalStateException.class, () -> couponService.useCoupon(1L));
    }
}
