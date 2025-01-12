package kr.hhplus.be.server.dto;

import kr.hhplus.be.server.domain.UserCoupon;
import kr.hhplus.be.server.domain.enums.UserCouponStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserCouponResponse {
    private final Long userCouponId;
    private final Long couponId;
    private final String couponName;
    private final UserCouponStatus status;

    public static UserCouponResponse fromEntity(UserCoupon userCoupon) {
        return new UserCouponResponse(
                userCoupon.getUserCouponId(),
                userCoupon.getCoupon().getCouponId(),
                userCoupon.getCoupon().getName(),
                userCoupon.getStatus()
        );
    }
}