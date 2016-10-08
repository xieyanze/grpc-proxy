package com.andy.grpc.proxy;

import java.net.InetSocketAddress;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http2.Http2Headers;

/**
 * SimpleFeature for read properties
 * Created by xieyz on 16-10-8.
 */
@SuppressWarnings("unused")
public class SimpleFeature implements ProxyFeature {

    @Override
    public InetSocketAddress decide(Http2Headers headers, ByteBuf data) {
        return new InetSocketAddress("127.0.0.1", 8080);
    }
}
