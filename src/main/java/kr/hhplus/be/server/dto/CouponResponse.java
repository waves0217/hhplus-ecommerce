package kr.hhplus.be.server.dto;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.enums.DiscountType;
import lombok.Getter;

@Getter
public class CouponResponse {
    private final Long id;
    private final String name;
    private final Integer amount;
    private final DiscountType discountType;
    private final Integer quantity;

    private CouponResponse(Long id, String name, Integer amount, DiscountType discountType, Integer quantity) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.discountType = discountType;
        this.quantity = quantity;
    }

    public static CouponResponse fromEntity(Coupon coupon) {
        return new CouponResponse(
                coupon.getCouponId(),
                coupon.getName(),
                coupon.getAmount(),
                coupon.getDiscountType(),
                coupon.getQuantity()
        );
    }
}