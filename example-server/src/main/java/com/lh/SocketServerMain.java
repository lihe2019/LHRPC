package com.lh;

import com.lh.entity.RpcServiceProperties;
import com.lh.remoting.transport.socket.SocketRpcServer;
import com.lh.serviceimpl.HelloServiceImpl;

public class SocketServerMain {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        SocketRpcServer socketRpcServer = new SocketRpcServer();
        RpcServiceProperties rpcServiceProperties = RpcServiceProperties.builder().group("test2").version("version2").build();
        socketRpcServer.registerService(helloService, rpcServiceProperties);
        socketRpcServer.start();
    }
}
