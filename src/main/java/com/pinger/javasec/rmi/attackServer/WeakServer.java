package com.pinger.javasec.rmi.attackServer;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * @author : p1n93r
 * @date : 2021/8/6 18:29
 * 模拟脆弱的服务端
 */
public class WeakServer {
    public static void main(String[] args) throws Exception{
        Registry registry = LocateRegistry.getRegistry("127.0.0.1", 1099);
        HelloServiceImpl helloService = new HelloServiceImpl();
        // Bind a service to the registry
        registry.bind("hello",helloService);
        System.out.println("RMI server is ready");
    }
}
