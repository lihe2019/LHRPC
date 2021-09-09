package com.lh.provider;

import com.lh.entity.RpcServiceProperties;

public interface ServiceProvider {
    /**
     * @param service
     * @param serviceClass
     * @param rpcServiceProperties
     */
    void addService(Object service, Class<?> serviceClass, RpcServiceProperties rpcServiceProperties);

    Object getService(RpcServiceProperties rpcServiceProperties);

    void publishService(Object service, RpcServiceProperties rpcServiceProperties);

    void publishService(Object service);



}
