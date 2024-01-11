package com.heima;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@MapperScan("com.heima.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)
@SpringBootApplication
public class HeimaDianpingApplication {

    public static void main(String[] args) {
        SpringApplication.run(HeimaDianpingApplication.class, args);
    }

}
