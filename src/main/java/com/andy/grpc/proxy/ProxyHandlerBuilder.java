package com.andy.grpc.proxy;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http2.AbstractHttp2ConnectionHandlerBuilder;
import io.netty.handler.codec.http2.Http2ConnectionDecoder;
import io.netty.handler.codec.http2.Http2ConnectionEncoder;
import io.netty.handler.codec.http2.Http2Exception;
import io.netty.handler.codec.http2.Http2FrameAdapter;
import io.netty.handler.codec.http2.Http2FrameLogger;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.handler.codec.http2.Http2Settings;
import io.netty.handler.logging.LogLevel;

/**
 * Proxy handler builder
 */
public final class ProxyHandlerBuilder extends AbstractHttp2ConnectionHandlerBuilder<ProxyHandler, ProxyHandlerBuilder> {

    private static final Http2FrameLogger logger = new Http2FrameLogger(LogLevel.INFO, ProxyHandler.class);

    public ProxyHandlerBuilder() {
        frameLogger(logger);
    }

    @Override
    public ProxyHandler build() {
        return super.build();
    }

    @Override
    protected ProxyHandler build(Http2ConnectionDecoder decoder, Http2ConnectionEncoder encoder,
                                 Http2Settings initialSettings) throws Exception {
        ProxyHandler handler = new ProxyHandler(decoder, encoder, initialSettings);
        frameListener(new FrameAdapter());
        return handler;
    }

    private class FrameAdapter extends Http2FrameAdapter {
        @Override
        public int onDataRead(ChannelHandlerContext ctx, int streamId, ByteBuf data, int padding,
                              boolean endOfStream) throws Http2Exception {
            ctx.fireChannelRead(data);
            return data.readableBytes() + padding;
        }

        @Override
        public void onRstStreamRead(ChannelHandlerContext ctx, int streamId, long errorCode) throws Http2Exception {
            super.onRstStreamRead(ctx, streamId, errorCode);
        }

        @Override
        public void onHeadersRead(ChannelHandlerContext ctx, int streamId, Http2Headers headers, int streamDependency,
                                  short weight, boolean exclusive, int padding,
                                  boolean endStream) throws Http2Exception {
            ctx.fireChannelRead(headers);
            ctx.fireChannelReadComplete();
        }
    }
}
