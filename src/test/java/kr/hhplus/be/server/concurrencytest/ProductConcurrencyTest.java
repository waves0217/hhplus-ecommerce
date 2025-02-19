package kr.hhplus.be.server.concurrencytest;

import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.product.ProductService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
@Transactional
@RunWith(SpringRunner.class)
class ProductConcurrencyTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    private static final Logger log = LoggerFactory.getLogger(ProductConcurrencyTest.class);
    private final int THREAD_COUNT = 10; // 동시 요청 개수

    @Test
    @DisplayName("상품 재고 감소 - 동시 주문 시 재고 초과 판매 문제 >비관적락<")
    void testConcurrentReduceStock() throws InterruptedException {

        List<Product> products = productRepository.findAll();
        for (Product product : products) {
            log.info("상품명: {}, 재고: {}", product.getName(), product.getStock());
        }

        Long productId = 1L;
        int quantityToReduce = 1;

        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

        // 실행 시간 측정 시작
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < THREAD_COUNT; i++) {
            executorService.submit(() -> {
                try {
                    productService.reduceStock(productId, quantityToReduce);
                } catch (Exception e) {
                    log.error("에러 발생: {}", e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 모든 스레드가 종료될 때까지 대기
        executorService.shutdown();

        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;

        Product product = productRepository.findProductForUpdate(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        // 재고가 음수가 되면 테스트 실패
        Assertions.assertTrue(product.getStock() >= 0, "재고가 음수가 되면 안됩니다.");

        // 실행 시간 출력
        log.info("총 동시 요청 처리 시간: {}ms", elapsedTime);
    }
}
