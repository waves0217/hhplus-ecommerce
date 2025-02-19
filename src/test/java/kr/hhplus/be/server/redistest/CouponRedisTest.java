package kr.hhplus.be.server.redistest;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.enums.CouponStatus;
import kr.hhplus.be.server.domain.enums.DiscountType;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.domain.coupon.CouponService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.profiles.active=test")
@Import(TestcontainersConfiguration.class)
public class CouponRedisTest {

    @Autowired
    private CouponService couponService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private RedissonClient redissonClient;

    private static final int THREAD_COUNT = 10; // 10명의 유저가 동시에 요청하는 상황을 시뮬레이션

    @BeforeEach
    void setup() {
        // 테스트용 쿠폰 및 유저 데이터 삽입
        Coupon coupon = Coupon.builder()
                .name("선착순 쿠폰")
                .amount(5000)
                .discountType(DiscountType.FIXED)
                .status(CouponStatus.ACTIVE)
                .quantity(5) // 5개만 발급 가능
                .expiredAt(LocalDateTime.now().plusDays(7))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        couponRepository.save(coupon);

        for (int i = 1; i <= THREAD_COUNT; i++) {
            User user = User.builder()
                    .name("User_" + i)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            userRepository.save(user);
        }
    }

    @Test
    @DisplayName("동시 쿠폰 발급 테스트 - 분산 락 확인")
    void testConcurrentCouponIssuance() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

        Long couponId = couponRepository.findAll().get(0).getCouponId();

        for (int i = 1; i <= THREAD_COUNT; i++) {
            Long userId = (long) i;

            executorService.submit(() -> {
                RLock lock = redissonClient.getLock("coupon-lock:" + couponId);
                long startTime = System.currentTimeMillis();
                boolean acquired = false;

                try {
                    acquired = lock.tryLock(5, 10, TimeUnit.SECONDS);
                    if (acquired) {
                        System.out.println("[🔒LOCK ACQUIRED] User " + userId + " at " + (System.currentTimeMillis() - startTime) + "ms");
                        couponService.issueCouponToUser(couponId, User.builder().userId(userId).build());
                    } else {
                        System.out.println("[🚫LOCK FAILED] User " + userId + " could not acquire lock");
                    }
                } catch (InterruptedException e) {
                    System.out.println("[ERROR] InterruptedException for User " + userId);
                } finally {
                    if (acquired) {
                        lock.unlock();
                        System.out.println("[🔓LOCK RELEASED] User " + userId);
                    }
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // Then
        Coupon updatedCoupon = couponRepository.findById(couponId).orElseThrow();
        System.out.println("남은 쿠폰 수량: " + updatedCoupon.getQuantity());

        // 발급된 쿠폰이 5개를 초과하지 않았는지 확인
        assertEquals(0, updatedCoupon.getQuantity(), "쿠폰이 초과 발급되지 않아야 합니다.");
    }
}
