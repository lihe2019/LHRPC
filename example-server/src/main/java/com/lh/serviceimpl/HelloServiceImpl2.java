package com.lh.serviceimpl;

import com.lh.Hello;
import com.lh.HelloService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HelloServiceImpl2 implements HelloService {
    static {
        System.out.println("HelloServiceImpl2 is created ");
    }

    @Override
    public String hello(Hello hello) {
        log.info("HelloServiceImpl2 receives: {}.", hello.getMessage());
        String result = "Hello description is " + hello.getDescription();
        log.info("HelloServiceImpl2 return: {}.", result);
        return result;
    }
}
