package kr.hhplus.be.server.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import kr.hhplus.be.server.dto.TopSellingProductDto;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig {
    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + redisHost + ":" + redisPort)
                .setConnectionMinimumIdleSize(10) // 기본 연결 설정
                .setConnectionPoolSize(64);      // 최대 연결 설정
        return Redisson.create(config);
    }

   /* @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    @Bean(name = "cacheManager")
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(10)) // 캐시 만료 시간 10분
                        .disableCachingNullValues()) // null 값 캐싱 방지
                .build();
    }*/
   @Bean
   public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
       RedisTemplate<String, Object> template = new RedisTemplate<>();
       template.setConnectionFactory(connectionFactory);
       template.setKeySerializer(new StringRedisSerializer());
       //template.setValueSerializer(new GenericJackson2JsonRedisSerializer()); // ✅ JSON 직렬화 적용
       template.setValueSerializer(new Jackson2JsonRedisSerializer<>(TopSellingProductDto.class)); // ✅ DTO 클래스 명시
       return template;
   }

    @Bean(name = "cacheManager")
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())  // LocalDateTime 직렬화 지원
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        RedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);

        RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10)) // 캐시 만료 시간 10분
                .disableCachingNullValues() // null 값 캐싱 방지
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())) // Key 직렬화
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer)); // Value 직렬화

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(cacheConfig)
                .build();
    }

}

