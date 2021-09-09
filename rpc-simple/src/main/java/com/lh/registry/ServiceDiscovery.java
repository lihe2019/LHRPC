package com.lh.registry;

import com.lh.extension.SPI;

import java.net.InetSocketAddress;

@SPI
public interface ServiceDiscovery {
    InetSocketAddress lookupService(String rpcServiceName);
}
