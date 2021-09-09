package com.lh;

import com.lh.annotation.RpcScan;
import com.lh.entity.RpcServiceProperties;
import com.lh.remoting.transport.netty.server.NettyRpcServer;
import com.lh.serviceimpl.HelloServiceImpl2;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@RpcScan(basePackage = {"com.lh"})
public class NettyServerMain {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(NettyServerMain.class);
        NettyRpcServer nettyRpcServer = (NettyRpcServer) applicationContext.getBean("nettyRpcServer");

        HelloService helloServiceImpl2 = new HelloServiceImpl2();
        RpcServiceProperties rpcServiceProperties = RpcServiceProperties.builder().group("test2").version("versions").build();
        nettyRpcServer.registerService(helloServiceImpl2, rpcServiceProperties);
        nettyRpcServer.start();
    }
}
