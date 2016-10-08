package com.andy.grpc.proxy;

import java.io.IOException;

import io.grpc.internal.ServerImpl;
import io.grpc.netty.NettyServerBuilder;
import io.grpc.stub.StreamObserver;

public class TestServer {
    public static void main(String[] args) throws InterruptedException, IOException {
        int port = 8080;
        ServerImpl server = NettyServerBuilder.forPort(port).addService(new ProxyTestServiceImpl()).build();
        server.start();
        System.out.println("Proxy Test Server start listener on port : " + port);
        server.awaitTermination();
    }

    private static class ProxyTestServiceImpl extends ProxyTestServiceGrpc.ProxyTestServiceImplBase {
        @Override
        public void echo(EchoRequest request, StreamObserver<EchoResponse> responseObserver) {
            System.out.println("request:" + request);
            EchoResponse.Builder response = EchoResponse.newBuilder().setContent(request.getContent());
            responseObserver.onNext(response.build());
            responseObserver.onCompleted();
        }
    }
}
