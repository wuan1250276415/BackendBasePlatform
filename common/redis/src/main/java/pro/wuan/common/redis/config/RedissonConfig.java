package pro.wuan.common.redis.config;

import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RedissonConfig {

    @Autowired
    private RedisConfig redisConfig;

    /**
     * 创建redission实列--用于获取或释放redis分布锁
     *
     * @return
     */
    @Bean
    public Redisson redisson() {
        Config config = new Config();
        if (redisConfig.getRedisPassword() == null) {
            config.useSingleServer().setAddress("redis://" + redisConfig.getRedisHost() + ":" + redisConfig.getRedisPort())
                    .setTimeout(redisConfig.getTimeout());
        } else {
            config.useSingleServer().setAddress("redis://" + redisConfig.getRedisHost() + ":" + redisConfig.getRedisPort())
                    .setPassword(redisConfig.getRedisPassword())
                    .setTimeout(redisConfig.getTimeout());
        }
        return (Redisson) Redisson.create(config);
    }
}
