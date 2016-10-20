package com.andy.grpc.proxy;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpServerUpgradeHandler;
import io.netty.handler.codec.http2.DefaultHttp2Headers;
import io.netty.handler.codec.http2.Http2ConnectionDecoder;
import io.netty.handler.codec.http2.Http2ConnectionEncoder;
import io.netty.handler.codec.http2.Http2ConnectionHandler;
import io.netty.handler.codec.http2.Http2FrameAdapter;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.handler.codec.http2.Http2Settings;
import io.netty.util.AsciiString;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;

/**
 * proxy handler
 */
public class ProxyHandler extends Http2ConnectionHandler {

    /**
     * Handles the cleartext HTTP upgrade event. If an upgrade occurred, sends a simple response via HTTP/2 on stream 1
     * (the stream specifically reserved for cleartext HTTP upgrade).
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        System.out.println("userEventTriggered");
        if (evt instanceof HttpServerUpgradeHandler.UpgradeEvent) {
            // Write an HTTP/2 response to the upgrade request
            Http2Headers headers =
                    new DefaultHttp2Headers().status(OK.codeAsText())
                            .set(new AsciiString("http-to-http2-upgrade"), new AsciiString("true"));
            encoder().writeHeaders(ctx, 1, headers, 0, true, ctx.newPromise());
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
        ctx.close();
    }

    ProxyHandler(Http2ConnectionDecoder decoder,
                 Http2ConnectionEncoder encoder,
                 Http2Settings initialSettings) {
        super(decoder, encoder, initialSettings);
    }
}
