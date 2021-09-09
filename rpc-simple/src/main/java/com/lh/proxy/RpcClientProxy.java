package com.lh.proxy;

import com.lh.entity.RpcServiceProperties;
import com.lh.enums.RpcErrorMessageEnum;
import com.lh.enums.RpcResponseCodeEnum;
import com.lh.exception.RpcException;
import com.lh.remoting.dto.RpcRequest;
import com.lh.remoting.dto.RpcResponse;
import com.lh.remoting.transport.RpcRequestTransport;
import com.lh.remoting.transport.netty.client.NettyRpcClient;
import com.lh.remoting.transport.socket.SocketRpcClient;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class RpcClientProxy implements InvocationHandler {
    private static final String INTERFACE_NAME = "interfaceName";

    private final RpcRequestTransport rpcRequestTransport;
    private final RpcServiceProperties rpcServiceProperties;

    public RpcClientProxy(RpcRequestTransport requestTransport, RpcServiceProperties rpcServiceProperties) {
        this.rpcRequestTransport = requestTransport;
        if (rpcServiceProperties.getGroup() == null){
            rpcServiceProperties.setGroup("");
        }
        if (rpcServiceProperties.getVersion()==null){
            rpcServiceProperties.setVersion("");
        }

        this.rpcServiceProperties = rpcServiceProperties;
    }

    public RpcClientProxy(RpcRequestTransport rpcRequestTransport){
        this.rpcRequestTransport = rpcRequestTransport;
        this.rpcServiceProperties = RpcServiceProperties.builder().group("").version("").build();

    }

    public <T> T getProxy(Class<T> clazz){
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")

    @Override
    public Object invoke(Object proxy, Method method, Object[] args)  {log.info("invoked method: [{}]", method.getName());
        RpcRequest rpcRequest = RpcRequest.builder().methodName(method.getName())
                .parameters(args)
                .interfaceName(method.getDeclaringClass().getName())
                .paramTypes(method.getParameterTypes())
                .requestId(UUID.randomUUID().toString())
                .group(rpcServiceProperties.getGroup())
                .version(rpcServiceProperties.getVersion())
                .build();
        RpcResponse<Object> rpcResponse = null;
        if (rpcRequestTransport instanceof NettyRpcClient) {
            CompletableFuture<RpcResponse<Object>> completableFuture = (CompletableFuture<RpcResponse<Object>>)
                    rpcRequestTransport.sendRpcRequest(rpcRequest);
            rpcResponse = completableFuture.get();
        }
        if (rpcRequestTransport instanceof SocketRpcClient) {
            rpcResponse = (RpcResponse<Object>) rpcRequestTransport.sendRpcRequest(rpcRequest);
        }
        this.check(rpcResponse, rpcRequest);
        return rpcResponse.getData();
    }

    private void check(RpcResponse<Object> rpcResponse, RpcRequest rpcRequest) {
        if (rpcResponse == null) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" +
                    rpcRequest.getInterfaceName());
        }

        if (!rpcRequest.getRequestId().equals(rpcResponse.getRequestId())) {
            throw new RpcException(RpcErrorMessageEnum.REQUEST_NOT_MATCH_RESPONSE, INTERFACE_NAME + ":" +
                    rpcRequest.getInterfaceName());
        }

        if (rpcResponse.getCode() == null || !rpcResponse.getCode().equals(RpcResponseCodeEnum.SUCCESS.getCode())) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" +
                    rpcRequest.getInterfaceName());
        }
    }
}
