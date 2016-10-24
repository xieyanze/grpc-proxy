package com.andy.grpc.proxy;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * ProxyServerInitializer Created by xieyz on 16-10-8.
 */
class ProxyServerInitializer extends ChannelInitializer<SocketChannel> {


    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(
                new LoggingHandler(LogLevel.INFO),
                new DecodeHandler(),
                new HexDumpProxyFrontendHandler()
        );
    }
}
