package kr.hhplus.be.server.domain;


import jakarta.persistence.*;
import kr.hhplus.be.server.domain.enums.UserCouponStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_coupon")
public class UserCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userCouponId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;

    @OneToMany(mappedBy = "userCoupon")
    private List<Order> orders = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserCouponStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public static UserCoupon create(User user, Coupon coupon) {
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.user = user;
        userCoupon.coupon = coupon;
        userCoupon.status = UserCouponStatus.UNUSED;
        userCoupon.createdAt = LocalDateTime.now();
        userCoupon.updatedAt = LocalDateTime.now();
        return userCoupon;
    }

    public void markAsUsed() {
        if (this.status == UserCouponStatus.USED) {
            throw new IllegalStateException("이미 사용된 쿠폰입니다.");
        }
        this.status = UserCouponStatus.USED;
        this.updatedAt = LocalDateTime.now();
    }

}