package phillip.stockinfo4j.appconfig;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import phillip.stockinfo4j.service.CacheService;
import phillip.stockinfo4j.service.impl.CacheServiceImpl;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig extends CachingConfigurerSupport {

    @Bean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        return redisTemplate;
    }

    @Bean
    public CacheManager cacheManager(RedisTemplate<String, Object> template) {


        // 構造Redis快取管理器
        RedisCacheManager redisCacheManager =
                RedisCacheManager.RedisCacheManagerBuilder
                        // Redis 连接工厂
                        .fromConnectionFactory(template.getConnectionFactory())
                        // 配置快取基礎設置,過期時間1小時
                        .cacheDefaults(getDefaultCacheConfig(template,Duration.ofHours(1L)))
                        //分別配置快取過期時間
                        .withCacheConfiguration(CacheServiceImpl.DailyTranCache, getDefaultCacheConfig(template, Duration.ZERO))
                        .withCacheConfiguration(CacheServiceImpl.DistributionCache, getDefaultCacheConfig(template, Duration.ZERO))
                        // 配置同步修改或删除 put/evict
                        .transactionAware()
                        .build();

        return redisCacheManager;
    }

    private RedisCacheConfiguration getDefaultCacheConfig(RedisTemplate<String, Object> template, Duration ttl) {
        // 基本配置
        RedisCacheConfiguration defaultCacheConfiguration =
                RedisCacheConfiguration
                        .defaultCacheConfig()
                        // 設置key序列化/反序列化
                        .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(template.getStringSerializer()))
                        // 設置value序列化/反序列化
                        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(template.getValueSerializer()))
                        // 不快取null
//                        .disableCachingNullValues()
                        // 快取保存時數
                        .entryTtl(ttl);
        ;
        return defaultCacheConfiguration;
    }
}
