package com.lh.serviceimpl;

import com.lh.Hello;
import com.lh.HelloService;
import com.lh.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RpcService(group = "test1", version = "version1")
public class HelloServiceImpl implements HelloService {
    static {
        System.out.println("HelloServiceImpl is created ");
    }

    @Override
    public String hello(Hello hello) {
        log.info("HelloServiceImpl receives: {}.", hello.getMessage());
        String result = "Hello description is " + hello.getDescription();
        log.info("HelloServiceImpl return: {}.", result);
        return result;
    }
}
