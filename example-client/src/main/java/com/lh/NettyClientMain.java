package com.lh;

import com.lh.annotation.RpcScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@RpcScan(basePackage = {"com.lh"})
public class NettyClientMain {
    public static void main(String[] args) throws InterruptedException{
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(NettyClientMain.class);
        HelloController helloController = (HelloController) applicationContext.getBean("helloController");
        helloController.test();
    }

}
