package pro.wuan.common.redis.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.lang.NonNull;

import java.time.Duration;

/**
 * Configuration class for Redis caching.
 */
@Configuration
@EnableCaching
@Slf4j
public class CacheConfig {

    private final RedisConfig redisConfig;

    /**
     * Constructor for CacheConfig.
     *
     * @param redisConfig The configuration for Redis.
     */
    public CacheConfig(RedisConfig redisConfig) {
        this.redisConfig = redisConfig;
    }

    /**
     * Bean for LettuceConnectionFactory.
     *
     * @return LettuceConnectionFactory instance.
     */
    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(redisConfig.getRedisHost());
        redisStandaloneConfiguration.setPort(redisConfig.getRedisPort());
        redisStandaloneConfiguration.setDatabase(redisConfig.getDatabase());
        redisStandaloneConfiguration.setPassword(redisConfig.getRedisPassword());
        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }

    /**
     * Bean for RedisCacheConfiguration.
     *
     * @return RedisCacheConfiguration instance.
     */
    @Bean
    public RedisCacheConfiguration redisCacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig().serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())).serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer())).entryTtl(Duration.ofHours(2)); //设置缓存时间默认2小时
    }

    /**
     * Bean for RedisCacheManager.
     *
     * @param lettuceConnectionFactory The LettuceConnectionFactory instance.
     * @param configuration            The RedisCacheConfiguration instance.
     * @return RedisCacheManager instance.
     */
    @Bean
    @Primary
    public RedisCacheManager cacheManager(@Autowired LettuceConnectionFactory lettuceConnectionFactory, @Autowired RedisCacheConfiguration configuration) {
        return RedisCacheManager.builder(lettuceConnectionFactory).cacheDefaults(configuration).transactionAware().build();
    }

    /**
     * Bean for RedisTemplate.
     *
     * @param lettuceConnectionFactory The LettuceConnectionFactory instance.
     * @return RedisTemplate instance.
     */
    @Bean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer()); // 使用 Jackson2JsonRedisSerializer 作为值的序列化器
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer()); // 使用 Jackson2JsonRedisSerializer 作为哈希值的序列化器
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    /**
     * Bean for CacheErrorHandler.
     *
     * @return CacheErrorHandler instance.
     */
    @Bean
    public CacheErrorHandler errorHandler() {
        // 异常处理，当Redis发生异常时，打印日志，但是程序正常走
        log.info("初始化 -> [{}]", "Redis CacheErrorHandler");
        return new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(
                    @NonNull RuntimeException e,
                    @NonNull Cache cache,
                    @NonNull Object key) {
                log.error("Redis occur handleCacheGetError：key -> [{}]", key, e);
            }

            @Override
            public void handleCachePutError(
                    @NonNull RuntimeException e,
                    @NonNull Cache cache,
                    @NonNull Object key, Object value) {
                log.error("Redis occur handleCachePutError：key -> [{}]；value -> [{}]", key, value, e);
            }

            @Override
            public void handleCacheEvictError(
                    @NonNull RuntimeException e,
                    @NonNull Cache cache,
                    @NonNull Object key) {
                log.error("Redis occur handleCacheEvictError：key -> [{}]", key, e);
            }

            @Override
            public void handleCacheClearError(
                    @NonNull RuntimeException e,
                    @NonNull Cache cache) {
                log.error("Redis occur handleCacheClearError：", e);
            }
        };
    }
}