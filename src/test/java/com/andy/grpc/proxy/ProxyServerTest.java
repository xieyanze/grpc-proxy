package com.andy.grpc.proxy;

/**
 * ProxyServerTest
 * Created by xieyz on 16-10-8.
 */
public class ProxyServerTest {
    public static void main(String[] args) {
        ProxyServer proxyServer = new ProxyServer(4000);
        proxyServer.start();
    }
}
