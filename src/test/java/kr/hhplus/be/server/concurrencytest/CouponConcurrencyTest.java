package kr.hhplus.be.server.concurrencytest;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.domain.coupon.CouponService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class CouponConcurrencyTest {

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserRepository userRepository;

    private static final Logger log = LoggerFactory.getLogger(CouponConcurrencyTest.class);

    private final Long couponId = 1L; // 10% 할인 쿠폰

    @Test
    @DisplayName("쿠폰 동시 발급 초과 방지 테스트")
    void CouponIssueConcurrencyTest() throws InterruptedException {
        int threadCount = 200; // 동시 실행할 사용자 개수 (각각 1번씩 발급)
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        long startTime = System.currentTimeMillis();

        for (int i = 1; i <= threadCount; i++) {
            final Long userId = (long) i; // 각 요청에 대해 다른 사용자 ID 할당

            executorService.execute(() -> {
                try {
                    long requestStart = System.currentTimeMillis();
                    log.info("[START] 쿠폰 발급 요청 - 사용자 ID: {}, 쿠폰 ID: {}, 시작 시간: {}ms", userId, couponId, requestStart - startTime);

                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

                    couponService.issueCouponToUser(couponId, user);

                    long requestEnd = System.currentTimeMillis();
                    log.info("[END] 쿠폰 발급 완료 - 사용자 ID: {}, 종료 시간: {}ms", userId, requestEnd - startTime);
                } catch (Exception e) {
                    log.error("[ERROR] 쿠폰 발급 실패: {}", e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 모든 스레드가 종료될 때까지 대기
        executorService.shutdown();

        long endTime = System.currentTimeMillis();
        log.info("[TOTAL TIME] 테스트 총 실행 시간: {}ms", endTime - startTime);

        // 최종 쿠폰 재고 검증
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다."));
        log.info("[CHECK] 남은 쿠폰 수량: {} (기대값: 90)", coupon.getQuantity());

        assertEquals(90, coupon.getQuantity(), "쿠폰이 초과 발급되었습니다!");
    }

}
