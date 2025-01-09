package kr.hhplus.be.server.dto;

import kr.hhplus.be.server.domain.UserCoupon;
import kr.hhplus.be.server.domain.enums.UserCouponStatus;

public class UserCouponResponse {
    private final Long id;
    private final Long couponId;
    private final String couponName;
    private final UserCouponStatus status;

    private UserCouponResponse(Long id, Long couponId, String couponName, UserCouponStatus status) {
        this.id = id;
        this.couponId = couponId;
        this.couponName = couponName;
        this.status = status;
    }

    public static UserCouponResponse fromEntity(UserCoupon userCoupon) {
        return new UserCouponResponse(
                userCoupon.getUserCouponId(),
                userCoupon.getCoupon().getCouponId(),
                userCoupon.getCoupon().getName(),
                userCoupon.getStatus()
        );
    }
}