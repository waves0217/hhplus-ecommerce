package kr.hhplus.be.server.concurrencytest;

import kr.hhplus.be.server.domain.balance.BalanceService;
import kr.hhplus.be.server.domain.payment.PaymentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ExtendWith(SpringExtension.class)
//@Transactional
class PaymentConcurrencyTest {

    private static final Logger log = LoggerFactory.getLogger(PaymentConcurrencyTest.class);

    @Autowired
    private BalanceService balanceService;

    @Autowired
    private PaymentService paymentService;

    @Test
    @DisplayName("동시 결제 요청 시 잔액 정합성 테스트")
    void concurrentPaymentRequestBalanceConsistencyTest() throws InterruptedException {
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    long requestStart = System.currentTimeMillis();
                    log.info("[START] 요청 시작 시간: {}ms", requestStart - startTime);

                    // 동시 결제 시도
                    paymentService.createPayment(1L, 200);

                    long requestEnd = System.currentTimeMillis();
                    log.info("[END] 요청 종료 시간: {}ms", requestEnd - startTime);
                } catch (Exception e) {
                    log.error("[ERROR] 결제 실패: {}", e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        long endTime = System.currentTimeMillis();
        log.info("[TOTAL TIME] 테스트 총 실행 시간: {}ms", endTime - startTime);

        // 데이터 정합성 검증
        int finalBalance = balanceService.getBalance(1L);
        log.info("[RESULT] 남은 잔액: {}", finalBalance);

        // 예상 총 지불 금액: 200 * threadCount
        int expectedBalance = 10000 - (200 * threadCount); // 초기 10000원에서 차감
        assertEquals(expectedBalance, finalBalance, "잔액이 올바르지 않습니다.");
    }


    @Test
    @DisplayName("동시 중복 결제 테스트")
    void concurrentDuplicatePaymentTest() throws InterruptedException {
        int threadCount = 2; // 동시에 2개의 요청 실행
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    long requestStart = System.currentTimeMillis();
                    log.info("[START] 결제 요청 - 주문 ID: 1, 시작 시간: {}ms", requestStart - startTime);

                    paymentService.completePayment(1L);

                    long requestEnd = System.currentTimeMillis();
                    log.info("[END] 결제 완료 - 주문 ID: 1, 종료 시간: {}ms", requestEnd - startTime);
                } catch (Exception e) {
                    log.error("[ERROR] 중복 결제 발생: {}", e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        long endTime = System.currentTimeMillis();
        log.info("[TOTAL TIME] 테스트 총 실행 시간: {}ms", endTime - startTime);
    }



}
