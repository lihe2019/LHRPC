package com.lh.spring;

import com.lh.annotation.RpcReference;
import com.lh.annotation.RpcService;
import com.lh.entity.RpcServiceProperties;
import com.lh.extension.ExtensionLoader;
import com.lh.factory.SingletonFactory;
import com.lh.provider.ServiceProvider;
import com.lh.provider.ServiceProviderImpl;
import com.lh.proxy.RpcClientProxy;
import com.lh.remoting.transport.RpcRequestTransport;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Slf4j
@Component
public class SpringBeanPostProcessor implements BeanPostProcessor {
    private final ServiceProvider serviceProvider;
    private final RpcRequestTransport rpcClient;

    public SpringBeanPostProcessor() {
        this.serviceProvider = SingletonFactory.getInstance(ServiceProviderImpl.class);
        this.rpcClient = ExtensionLoader.getExtensionLoader(RpcRequestTransport.class).getExtension("netty");
    }

    @SneakyThrows

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(RpcService.class)){
            log.info("[{}] is annotated with [{}]", bean.getClass().getName(), RpcService.class.getCanonicalName());

            RpcService rpcService = bean.getClass().getAnnotation(RpcService.class);

            RpcServiceProperties rpcServiceProperties = RpcServiceProperties.builder()
                    .group(rpcService.group()).version(rpcService.version()).build();
            serviceProvider.publishService(bean, rpcServiceProperties);
        }
        return bean;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = bean.getClass();
        Field[] declareFields = targetClass.getDeclaredFields();
        for (Field declareField : declareFields) {
            RpcReference rpcReference = declareField.getAnnotation(RpcReference.class);
            if (rpcReference != null){
                RpcServiceProperties rpcServiceProperties = RpcServiceProperties.builder()
                        .group(rpcReference.group()).version(rpcReference.version()).build();
                RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcClient, rpcServiceProperties);
                Object clientProxy = rpcClientProxy.getProxy(declareField.getType());
                declareField.setAccessible(true);
                try {
                    declareField.set(bean, clientProxy);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return bean;
    }
}
