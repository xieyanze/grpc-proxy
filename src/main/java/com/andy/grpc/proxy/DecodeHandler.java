package com.andy.grpc.proxy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

/**
 * DecodeHandler Created by xieyz on 16-10-20.
 */
@ChannelHandler.Sharable
public class DecodeHandler extends ChannelDuplexHandler {

    private static String headers = ":authority: 127.0.0.1:4000, :path: /Proxy.ProxyTestService/Echo, :method: POST, " +
            ":scheme: http, content-type: application/grpc, te: trailers, user-agent: grpc-java-netty/1.0.1, grpc-accept-encoding: gzip";


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf)msg;
        ByteBuf copy = byteBuf.copy();
        System.out.println(byteBuf.readByte() + "===" + byteBuf.readableBytes());
        ctx.fireChannelRead(copy);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelReadComplete();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
    }

}
