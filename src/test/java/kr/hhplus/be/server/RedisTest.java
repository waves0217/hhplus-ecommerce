package kr.hhplus.be.server;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
public class RedisTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    public void testRedisConnection() {
        redisTemplate.opsForValue().set("testKey", "레디스 테스트");

        // 데이터 조회
        Object value = redisTemplate.opsForValue().get("testKey");
        System.out.println("Redis에서 가져온 값: " + value);

        assertEquals("레디스 테스트", value.toString());
    }
}
