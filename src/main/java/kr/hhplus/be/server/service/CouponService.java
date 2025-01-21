package kr.hhplus.be.server.service;

import kr.hhplus.be.server.domain.Coupon;
import kr.hhplus.be.server.domain.User;
import kr.hhplus.be.server.domain.UserCoupon;
import kr.hhplus.be.server.domain.enums.UserCouponStatus;
import kr.hhplus.be.server.repository.CouponRepository;
import kr.hhplus.be.server.repository.UserCouponRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CouponService {

    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;

    public CouponService(CouponRepository couponRepository, UserCouponRepository userCouponRepository) {
        this.couponRepository = couponRepository;
        this.userCouponRepository = userCouponRepository;
    }

    @Transactional
    public UserCoupon issueCouponToUser(Long couponId, User user) {
        // 쿠폰 조회
        Coupon coupon = couponRepository.findCouponForUpdate(couponId)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰이 존재하지 않습니다."));

        // 재고 확인
        if (coupon.getQuantity() <= 0) {
            throw new IllegalStateException("쿠폰이 모두 소진되었습니다.");
        }

        // 수량 감소 (1 감소)
        coupon.updateQuantity(1); // 중복 감소 방지
        couponRepository.save(coupon);

        // 유저 쿠폰 생성 및 저장
        UserCoupon userCoupon = UserCoupon.create(user, coupon);
        return userCouponRepository.save(userCoupon);
    }


    @Transactional(readOnly = true)
    public List<UserCoupon> getUserCoupons(Long userId) {
        return userCouponRepository.findByUser_UserId(userId);
    }

    @Transactional
    public void useCoupon(Long userCouponId) {
        UserCoupon userCoupon = userCouponRepository.findById(userCouponId)
                .orElseThrow(() -> new IllegalArgumentException("유저 쿠폰을 찾을 수 없습니다."));

        if (userCoupon.getStatus() == UserCouponStatus.USED) {
            throw new IllegalStateException("이미 사용된 쿠폰입니다.");
        }

        userCoupon.markAsUsed();
        userCouponRepository.save(userCoupon);
    }
}

