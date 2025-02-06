package kr.hhplus.be.server.redistest;

import kr.hhplus.be.server.dto.TopSellingProductResponse;
import kr.hhplus.be.server.facade.TopSellingProductsUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.testcontainers.utility.TestcontainersConfiguration;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
public class TopSellingCacheTest {

    @Autowired
    private TopSellingProductsUseCase topSellingProductsUseCase;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    public void testTopSellingProductsWithCache() {
        // 1. 첫 번째 조회 (DB 조회 및 캐싱)
        List<TopSellingProductResponse> firstResult = topSellingProductsUseCase.getTopSellingProducts(5);
        System.out.println("첫 번째 조회 결과: " + firstResult);

        // 2. Redis에 캐시된 데이터 확인
        Object cachedValue = redisTemplate.opsForValue().get("topSellingProducts:5");
        System.out.println("Redis 캐시 값: " + cachedValue);

        // 3. 두 번째 조회 (캐시에서 가져오기)
        List<TopSellingProductResponse> secondResult = topSellingProductsUseCase.getTopSellingProducts(5);
        System.out.println("두 번째 조회 결과: " + secondResult);

        assertEquals(firstResult, secondResult);
    }
}
