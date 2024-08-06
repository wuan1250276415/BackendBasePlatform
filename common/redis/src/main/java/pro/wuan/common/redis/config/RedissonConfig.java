package pro.wuan.common.redis.config;

import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description:
 * @author: oldone
 * @date: 2020/7/27 13:58
 */
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
        if (redisConfig.getPassword() == null) {
            config.useSingleServer().setAddress("redis://" + redisConfig.getHost() + ":" + redisConfig.getPort())
                    .setTimeout(redisConfig.getTimeout());
        } else {
            config.useSingleServer().setAddress("redis://" + redisConfig.getHost() + ":" + redisConfig.getPort())
                    .setPassword(redisConfig.getPassword())
                    .setTimeout(redisConfig.getTimeout());
        }
        return (Redisson) Redisson.create(config);
    }
}
