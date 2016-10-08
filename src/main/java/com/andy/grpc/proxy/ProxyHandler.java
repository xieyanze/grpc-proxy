package com.andy.grpc.proxy;

import java.net.InetSocketAddress;
import java.util.ServiceLoader;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.handler.codec.http.HttpServerUpgradeHandler;
import io.netty.handler.codec.http2.DefaultHttp2Headers;
import io.netty.handler.codec.http2.Http2ConnectionDecoder;
import io.netty.handler.codec.http2.Http2ConnectionEncoder;
import io.netty.handler.codec.http2.Http2ConnectionHandler;
import io.netty.handler.codec.http2.Http2Exception;
import io.netty.handler.codec.http2.Http2Flags;
import io.netty.handler.codec.http2.Http2FrameListener;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.handler.codec.http2.Http2Settings;
import io.netty.util.AsciiString;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;

/**
 * proxy handler
 */
public class ProxyHandler extends Http2ConnectionHandler implements Http2FrameListener {

    /**
     * Handles the cleartext HTTP upgrade event. If an upgrade occurred, sends a simple response via HTTP/2
     * on stream 1 (the stream specifically reserved for cleartext HTTP upgrade).
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

    protected ProxyHandler(Http2ConnectionDecoder decoder,
            Http2ConnectionEncoder encoder,
            Http2Settings initialSettings) {
        super(decoder, encoder, initialSettings);
    }

    @Override
    public int onDataRead(ChannelHandlerContext ctx, int streamId, ByteBuf data, int padding,
            boolean endOfStream) throws Http2Exception {
        System.out.println("onDataRead");


        final Channel inboundChannel = ctx.channel();

        // Start the connection attempt.
        Bootstrap b = new Bootstrap();
        b.group(inboundChannel.eventLoop())
                .channel(ctx.channel().getClass())
                .handler(new HexDumpProxyBackendHandler(inboundChannel))
                .option(ChannelOption.AUTO_READ, false);
        ProxyFeature proxyFeature = getProxyFeature();
        if (proxyFeature == null) {
            throw new RuntimeException("can not find proxy feature");
        }
        InetSocketAddress decide = proxyFeature.decide(null, null);
        ChannelFuture f = b.connect(decide.getHostName(), decide.getPort());
        outboundChannel = f.channel();
        f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                System.out.println("operationComplete");
                if (future.isSuccess()) {
                    // connection complete start to read first data
                    inboundChannel.read();
                } else {
                    // Close the connection if the connection attempt has failed.
                    inboundChannel.close();
                }
            }
        });

        encoder().writeData(ctx, streamId, data.retain(), padding, true, ctx.newPromise());
        flush(ctx);
        return data.readableBytes() + padding;
    }


    private ProxyFeature getProxyFeature() {
        ServiceLoader<ProxyFeature> proxyFeatures = ServiceLoader.load(ProxyFeature.class);
        return proxyFeatures.iterator().next();
    }

    @Override
    public void onHeadersRead(ChannelHandlerContext ctx, int streamId, Http2Headers headers, int padding,
            boolean endOfStream) throws Http2Exception {
        System.out.println("onHeadersRead");
    }

    private Channel outboundChannel;

    @Override
    public void onHeadersRead(ChannelHandlerContext ctx, int streamId, Http2Headers headers, int streamDependency,
            short weight, boolean exclusive, int padding, boolean endOfStream) throws Http2Exception {
        System.out.println("onHeadersRead 11");
        encoder().writeHeaders(ctx, streamId, headers, 0, false, ctx.newPromise());
    }

    @Override
    public void onPriorityRead(ChannelHandlerContext ctx, int streamId, int streamDependency, short weight,
            boolean exclusive) throws Http2Exception {
        System.out.println("onPriorityRead");
    }

    @Override
    public void onRstStreamRead(ChannelHandlerContext ctx, int streamId, long errorCode) throws Http2Exception {
        System.out.println("onRstStreamRead");
    }

    @Override
    public void onSettingsAckRead(ChannelHandlerContext ctx) throws Http2Exception {
        System.out.println("onSettingsAckRead");
    }

    @Override
    public void onSettingsRead(ChannelHandlerContext ctx, Http2Settings settings) throws Http2Exception {
        System.out.println("onSettingsRead");
    }

    @Override
    public void onPingRead(ChannelHandlerContext ctx, ByteBuf data) throws Http2Exception {
        System.out.println("onPingRead");
    }

    @Override
    public void onPingAckRead(ChannelHandlerContext ctx, ByteBuf data) throws Http2Exception {
        System.out.println("onPingAckRead");
    }

    @Override
    public void onPushPromiseRead(ChannelHandlerContext ctx, int streamId, int promisedStreamId, Http2Headers headers,
            int padding) throws Http2Exception {
        System.out.println("onPushPromiseRead");
    }

    @Override
    public void onGoAwayRead(ChannelHandlerContext ctx, int lastStreamId, long errorCode,
            ByteBuf debugData) throws Http2Exception {
        System.out.println("onGoAwayRead");
    }

    @Override
    public void onWindowUpdateRead(ChannelHandlerContext ctx, int streamId,
            int windowSizeIncrement) throws Http2Exception {
        System.out.println("onWindowUpdateRead");
    }

    @Override
    public void onUnknownFrame(ChannelHandlerContext ctx, byte frameType, int streamId, Http2Flags flags,
            ByteBuf payload) throws Http2Exception {
        System.out.println("onUnknownFrame");
    }

    static void closeOnFlush(Channel ch) {
        if (ch.isActive()) {
            ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }
}
