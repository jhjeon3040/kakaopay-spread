package com.kakaopay.demo.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Slf4j
@Configuration
public class EmbeddedRedisConfig {

    @Value("${spring.redis.port}")
    private int redisPort;

    private RedisServer redisServer;

    @PostConstruct
    public void init() {
        redisServer = RedisServer.builder()
                .port(redisPort)
                .setting ("maxmemory 128M") // for windows
                .build();
        redisServer.start();
    }

    @PreDestroy
    public void destroy() {
        if(redisServer.isActive())
            redisServer.stop();
    }
}
