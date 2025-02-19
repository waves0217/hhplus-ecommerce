package kr.hhplus.be.server.concurrencytest;


import kr.hhplus.be.server.domain.balance.BalanceRepository;
import kr.hhplus.be.server.domain.balance.BalanceService;
import org.junit.jupiter.api.BeforeEach;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class BalanceConcurrencyTest {

    @Autowired
    private BalanceService balanceService;

    @Autowired
    private BalanceRepository balanceRepository;

    private static final Logger log = LoggerFactory.getLogger(BalanceConcurrencyTest.class);

    private final Long userId = 1L;

    @BeforeEach
    void setUp() {
        // 테스트 시작 전에 사용자의 잔액을 초기화
        balanceRepository.findById(userId).ifPresent(balance -> {
            balance.setAmount(0);  // 잔액을 0으로 업데이트
            balanceRepository.save(balance);
            log.info("[SETUP] 사용자 ID: {}의 잔액을 0원으로 초기화", userId);
        });
    }

    @Test
    @DisplayName("동시 잔액 충전 테스트")
    void concurrent_balance_recharge_test() throws InterruptedException {
        int threadCount = 10; // 동시 실행할 스레드 개수
        int chargeAmount = 1000; // 각 요청마다 충전할 금액
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        long startTime = System.currentTimeMillis(); // 테스트 시작 시간 기록

        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    long requestStart = System.currentTimeMillis();
                    log.info("[START] 잔액 충전 요청 - 사용자 ID: {}, 충전 금액: {}원, 시작 시간: {}ms", userId, chargeAmount, requestStart - startTime);

                    balanceService.chargeBalance(userId, chargeAmount); // 사용자 ID 1번에게 1000원 충전

                    long requestEnd = System.currentTimeMillis();
                    log.info("[END] 잔액 충전 완료 - 사용자 ID: {}, 종료 시간: {}ms", userId, requestEnd - startTime);
                } catch (Exception e) {
                    log.error("[ERROR] 잔액 충전 실패: {}", e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 모든 스레드가 종료될 때까지 대기
        executorService.shutdown();

        long endTime = System.currentTimeMillis(); // 테스트 종료 시간 기록
        log.info("[TOTAL TIME] 테스트 총 실행 시간: {}ms", endTime - startTime);

        // 최종 잔액 검증
        int finalBalance = balanceService.getBalance(userId);
        log.info("[CHECK] 최종 잔액: {}원 (기대값: {})", finalBalance, chargeAmount * threadCount);

        assertEquals(chargeAmount * threadCount, finalBalance, "잔액이 정확히 반영되지 않았습니다!");
    }

}
