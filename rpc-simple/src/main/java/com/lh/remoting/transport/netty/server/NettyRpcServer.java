package com.lh.remoting.transport.netty.server;

import com.lh.config.CustomShutdownHook;
import com.lh.entity.RpcServiceProperties;
import com.lh.factory.SingletonFactory;
import com.lh.provider.ServiceProvider;
import com.lh.provider.ServiceProviderImpl;
import com.lh.remoting.transport.netty.client.NettyRpcClientHandler;
import com.lh.remoting.transport.netty.codec.RpcMessageDecoder;
import com.lh.remoting.transport.netty.codec.RpcMessageEncoder;
import com.lh.utils.RuntimeUtil;
import com.lh.utils.concurrent.threadpool.ThreadPoolFactoryUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class NettyRpcServer {
    public static final int PORT = 9998;
    private final ServiceProvider serviceProvider = SingletonFactory.getInstance(ServiceProviderImpl.class);

    public void registerService(Object service, RpcServiceProperties rpcServiceProperties) {
        serviceProvider.publishService(service, rpcServiceProperties);
    }

    @SneakyThrows
    public void start() {
        CustomShutdownHook.getCustomShutdownHook().clearAll();
        String host = InetAddress.getLocalHost().getHostAddress();
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        DefaultEventExecutorGroup serviceHandlerGroup = new DefaultEventExecutorGroup(
                RuntimeUtil.cpuNum() * 2,
                ThreadPoolFactoryUtil.createThreadFactory("service-handler-group", false)
        );

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new IdleStateHandler(30,0,0, TimeUnit.SECONDS));
                            p.addLast(new RpcMessageDecoder());
                            p.addLast(new RpcMessageEncoder());
                            p.addLast(serviceHandlerGroup, new NettyRpcClientHandler());
                        }
                    });

            ChannelFuture f = b.bind(host, PORT).sync();
            f.channel().closeFuture().sync();
        }finally {
            log.info("shutdown bossGroup and workerGroup");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            serviceHandlerGroup.shutdownGracefully();
        }

    }
}
