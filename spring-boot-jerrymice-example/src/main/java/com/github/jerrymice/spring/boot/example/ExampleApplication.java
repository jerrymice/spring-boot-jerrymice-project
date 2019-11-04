package com.github.jerrymice.spring.boot.example;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * @author tumingjian
 * 创建时间: 2019-10-08 11:08
 * 功能说明:
 */
@SpringBootApplication
@EnableRedisHttpSession
public class ExampleApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExampleApplication.class,args);
    }
}
