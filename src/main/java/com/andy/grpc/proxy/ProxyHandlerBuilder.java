package com.andy.grpc.proxy;

import io.netty.handler.codec.http2.AbstractHttp2ConnectionHandlerBuilder;
import io.netty.handler.codec.http2.Http2ConnectionDecoder;
import io.netty.handler.codec.http2.Http2ConnectionEncoder;
import io.netty.handler.codec.http2.Http2FrameLogger;
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
        frameListener(handler);
        return handler;
    }
}
