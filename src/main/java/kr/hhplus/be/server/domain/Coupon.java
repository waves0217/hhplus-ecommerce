package kr.hhplus.be.server.domain;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.enums.CouponStatus;
import kr.hhplus.be.server.domain.enums.DiscountType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "coupon")
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long couponId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountType discountType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponStatus status;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private LocalDateTime expiredAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder.Default
    @OneToMany(mappedBy = "coupon", orphanRemoval = true)
    private List<UserCoupon> userCoupons = new ArrayList<>();

    public static Coupon create(String name, Integer amount, DiscountType discountType, Integer quantity, LocalDateTime expiredAt) {
        Coupon coupon = new Coupon();
        coupon.name = name;
        coupon.amount = amount;
        coupon.discountType = discountType;
        coupon.status = CouponStatus.ACTIVE;
        coupon.quantity = quantity;
        coupon.expiredAt = expiredAt;
        coupon.createdAt = LocalDateTime.now();
        coupon.updatedAt = LocalDateTime.now();
        return coupon;
    }

    public void deactivate() {
        this.status = CouponStatus.EXPIRED;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateQuantity(int quantityToReduce) {
        if (this.quantity < quantityToReduce) {
            throw new IllegalArgumentException("사용 가능한 쿠폰이 충분하지 않습니다.");
        }
        this.quantity -= quantityToReduce;
        this.updatedAt = LocalDateTime.now();
    }
}
