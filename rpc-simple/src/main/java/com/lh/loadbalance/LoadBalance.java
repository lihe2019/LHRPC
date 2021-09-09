package com.lh.loadbalance;

import com.lh.extension.SPI;

import java.util.List;

@SPI
public interface LoadBalance {
    String selectServiceAddress(List<String> serviceAddress, String rpcServiceName);
}
