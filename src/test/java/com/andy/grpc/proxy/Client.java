package com.andy.grpc.proxy;

import io.grpc.internal.ManagedChannelImpl;
import io.grpc.netty.NegotiationType;
import io.grpc.netty.NettyChannelBuilder;

/**
 * client Created by xieyz on 16-10-8.
 */
public class Client {
    public static void main(String[] args) {
        ManagedChannelImpl managedChannel = NettyChannelBuilder.forAddress("127.0.0.1", 4000).negotiationType(
                NegotiationType.PLAINTEXT).build();
        ProxyTestServiceGrpc.ProxyTestServiceBlockingStub proxyTestServiceBlockingStub = ProxyTestServiceGrpc.newBlockingStub(
                managedChannel);
        proxyTestServiceBlockingStub.echo(EchoRequest.newBuilder().setContent("testasdfsadfsadf").setPwd("password")
                .build());
    }
}
