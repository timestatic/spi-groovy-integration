package com.github.sgi.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @FileName: Application.java
 * @Description: Application.java类说明
 * @Author: timestatic
 * @Date: 2020/1/1 17:21
 */
@SpringBootApplication(scanBasePackages = {"com.github.sgi"})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
