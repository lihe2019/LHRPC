package com.lh.remoting.transport;

import com.lh.extension.SPI;
import com.lh.remoting.dto.RpcRequest;

@SPI
public interface RpcRequestTransport {
    Object sendRpcRequest(RpcRequest rpcRequest);
}
