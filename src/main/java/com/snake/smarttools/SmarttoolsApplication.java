package com.snake.smarttools;

//import org.mybatis.spring.annotation.MapperScan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class}) // 不连接数据库
@ComponentScan("com.snake.smarttools")
//@MapperScan(basePackages = "com.snake.smarttools.model.mapper")
@EnableRetry // RestTemplate 重试机制
public class SmarttoolsApplication {
    public static void main(String[] args) {
        SpringApplication.run(SmarttoolsApplication.class, args);
    }
}
