package kr.hhplus.be.server.service;

import kr.hhplus.be.server.domain.Coupon;
import kr.hhplus.be.server.domain.User;
import kr.hhplus.be.server.domain.UserCoupon;
import kr.hhplus.be.server.domain.enums.UserCouponStatus;
import kr.hhplus.be.server.repository.CouponRepository;
import kr.hhplus.be.server.repository.UserCouponRepository;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class CouponService {

    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;
    private final RedissonClient redissonClient;

    public CouponService(CouponRepository couponRepository, UserCouponRepository userCouponRepository, RedissonClient redissonClient) {
        this.couponRepository = couponRepository;
        this.userCouponRepository = userCouponRepository;
        this.redissonClient = redissonClient;
    }

    @Transactional
    public UserCoupon issueCouponToUser(Long couponId, User user) {
        RLock lock = redissonClient.getLock("coupon-lock:" + couponId); // 쿠폰 ID별 Lock
        boolean isLocked = false;
        try {
            // 분산 락 획득 (최대 3초 대기, 10초 동안 유지)
            isLocked = lock.tryLock(3, 10, TimeUnit.SECONDS);
            if (!isLocked) {
                throw new IllegalStateException("현재 쿠폰 발급이 너무 많습니다. 다시 시도해주세요.");
            }
            // 쿠폰 조회
            Coupon coupon = couponRepository.findById(couponId)
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

        } catch (InterruptedException e) {
            throw new IllegalStateException("쿠폰 발급 중 오류가 발생했습니다.", e);
        } finally {
            if (isLocked && lock.isHeldByCurrentThread()) {
                lock.unlock(); // 락 해제
            }
        }

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

