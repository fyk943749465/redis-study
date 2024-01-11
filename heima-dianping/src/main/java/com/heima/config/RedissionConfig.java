package com.heima.config;

import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.Redisson;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissionConfig {
    @Bean
    public RedissonClient redissonClient()
    {
        Config config=new Config();
        config.useSingleServer().setAddress("redis://192.168.10.17:6379").setPassword("thinker");
        return Redisson.create(config);
    }
}
