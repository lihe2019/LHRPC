package com.lh;

import com.lh.entity.RpcServiceProperties;
import com.lh.proxy.RpcClientProxy;
import com.lh.remoting.transport.RpcRequestTransport;
import com.lh.remoting.transport.socket.SocketRpcClient;

public class SocketClientMain {
    public static void main(String[] args) {
        RpcRequestTransport rpcRequestTransport = new SocketRpcClient();
        RpcServiceProperties rpcServiceProperties = RpcServiceProperties.builder().group("test2").version("version2").build();
        RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcRequestTransport, rpcServiceProperties);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        String hello = helloService.hello(new Hello("111", "222"));
        System.out.println(hello);
    }
}
