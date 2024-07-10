package com.kapturecx.employeelogin.configuration;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        // Example Redis configuration, replace with your Redis server details
        config.useSingleServer()
                .setAddress("redis://127.0.0.1:6379"); // Replace with your Redis server address
        return Redisson.create(config);
    }

}
