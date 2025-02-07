package kr.hhplus.be.server.redistest;

import com.fasterxml.jackson.core.type.TypeReference;
import kr.hhplus.be.server.dto.TopSellingProductResponse;
import kr.hhplus.be.server.facade.TopSellingProductsUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.profiles.active=test")
@Import(TestcontainersConfiguration.class)
public class TopSellingCacheTest {

    @Autowired
    private TopSellingProductsUseCase topSellingProductsUseCase;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final String CACHE_KEY = "topSellingProducts:5";

    @BeforeEach
    public void clearRedisCache() {
        redisTemplate.delete(CACHE_KEY);
        System.out.println("🔄 Redis 캐시 초기화 완료");
    }

    @Test
    public void testTopSellingProductsWithCache() {
        // 1️⃣ 첫 번째 조회 (DB 조회 및 캐싱)
        List<TopSellingProductResponse> firstResult = topSellingProductsUseCase.getTopSellingProducts(5);
        System.out.println("✅ 첫 번째 조회 결과: " + firstResult);

        // 2️⃣ Redis에 캐시된 데이터 확인
        Object cachedValue = redisTemplate.opsForValue().get(CACHE_KEY);
        System.out.println("📌 Redis 캐시 값: " + cachedValue);

        // 3️⃣ 두 번째 조회 (캐시에서 가져오기)
        List<TopSellingProductResponse> secondResult = topSellingProductsUseCase.getTopSellingProducts(5);
        System.out.println("✅ 두 번째 조회 결과: " + secondResult);

        assertEquals(firstResult, secondResult);
    }
}
