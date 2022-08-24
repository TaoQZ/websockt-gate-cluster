package xyz.taoqz.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author taoqingzhou
 * @version 1.0
 * @date 2022/8/23 13:48
 */
@Configuration
public class SystemConfig {

    @Autowired
    private RedisTemplate redisTemplate;


    @Bean
    @Primary
    public RedisTemplate redisTemplateInit() {
        RedisSerializer stringSerializer = new StringRedisSerializer();
        this.redisTemplate.setKeySerializer(stringSerializer);
        this.redisTemplate.setValueSerializer(stringSerializer);
        this.redisTemplate.setHashKeySerializer(stringSerializer);
        this.redisTemplate.setHashValueSerializer(stringSerializer);
        return this.redisTemplate;
    }

}
