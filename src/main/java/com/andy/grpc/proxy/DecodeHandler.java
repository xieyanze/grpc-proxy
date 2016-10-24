package com.andy.grpc.proxy;

import java.util.ArrayList;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http2.DefaultHttp2Connection;
import io.netty.handler.codec.http2.DefaultHttp2ConnectionDecoder;
import io.netty.handler.codec.http2.DefaultHttp2ConnectionEncoder;
import io.netty.handler.codec.http2.DefaultHttp2FrameReader;
import io.netty.handler.codec.http2.DefaultHttp2FrameWriter;
import io.netty.handler.codec.http2.Http2Connection;
import io.netty.handler.codec.http2.Http2ConnectionDecoder;
import io.netty.handler.codec.http2.Http2ConnectionEncoder;
import io.netty.handler.codec.http2.Http2Exception;
import io.netty.handler.codec.http2.Http2FrameAdapter;
import io.netty.handler.codec.http2.Http2FrameLogger;
import io.netty.handler.codec.http2.Http2FrameReader;
import io.netty.handler.codec.http2.Http2FrameWriter;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.handler.codec.http2.Http2InboundFrameLogger;
import io.netty.handler.codec.http2.Http2OutboundFrameLogger;

import static io.netty.buffer.ByteBufUtil.hexDump;
import static io.netty.handler.logging.LogLevel.INFO;
import static java.lang.Math.min;

/**
 * DecodeHandler Created by xieyz on 16-10-20.
 */
@ChannelHandler.Sharable
public class DecodeHandler extends ChannelDuplexHandler {

    private static String headers = ":authority: 127.0.0.1:4000, :path: /Proxy.ProxyTestService/Echo, :method: POST, " +
            ":scheme: http, content-type: application/grpc, te: trailers, user-agent: grpc-java-netty/1.0.1, grpc-accept-encoding: gzip";

    public static ThreadLocal<ByteBuf> threadLocal = new ThreadLocal<ByteBuf>();

    private static final Http2FrameLogger HTTP2_FRAME_LOGGER = new Http2FrameLogger(INFO, Http2FrameCodec.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        threadLocal.set(byteBuf);
        ByteBuf copy = byteBuf.copy();
        if (byteBuf.getByte(3) == 1) {
            Http2FrameReader reader = new Http2InboundFrameLogger(new DefaultHttp2FrameReader(),
                    HTTP2_FRAME_LOGGER);
            reader.readFrame(ctx, byteBuf, new Http2FrameAdapter() {
                @Override
                public void onHeadersRead(ChannelHandlerContext ctx, int streamId, Http2Headers headers,
                                          int streamDependency,
                                          short weight, boolean exclusive, int padding,
                                          boolean endStream) throws Http2Exception {
                    System.out.println(headers);
                }
            });
            byteBuf.release();
        }
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
