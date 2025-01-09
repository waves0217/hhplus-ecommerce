package kr.hhplus.be.server.facade;

import kr.hhplus.be.server.domain.User;
import kr.hhplus.be.server.domain.UserCoupon;
import kr.hhplus.be.server.dto.UserCouponResponse;
import kr.hhplus.be.server.service.CouponService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CouponUseCase {

    private final CouponService couponService;

    public CouponUseCase(CouponService couponService) {
        this.couponService = couponService;
    }

    public UserCouponResponse issueCoupon(Long couponId, User user) {
        UserCoupon userCoupon = couponService.issueCouponToUser(couponId, user);
        return UserCouponResponse.fromEntity(userCoupon);
    }

    public List<UserCouponResponse> getUserCoupons(Long userId) {
        return couponService.getUserCoupons(userId).stream()
                .map(UserCouponResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public void useCoupon(Long userCouponId) {
        couponService.useCoupon(userCouponId);
    }
}

