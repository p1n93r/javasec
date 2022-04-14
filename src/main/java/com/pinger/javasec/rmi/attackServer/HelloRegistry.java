package com.pinger.javasec.rmi.attackServer;

import java.rmi.registry.LocateRegistry;
import java.util.concurrent.CountDownLatch;

/**
 * @author : p1n93r
 * @date : 2021/8/7 18:14
 * 模拟RMI注册中心
 */
public class HelloRegistry {
    public static void main(String[] args)throws Exception {
        LocateRegistry.createRegistry(1099);
        new CountDownLatch(1).await();
    }
}
