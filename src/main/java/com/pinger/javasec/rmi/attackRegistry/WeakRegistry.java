package com.pinger.javasec.rmi.attackRegistry;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.CountDownLatch;
/**
 * @author : p1n93r
 * @date : 2021/7/29 18:30
 * 模拟脆弱的RMI Registry服务
 */
public class WeakRegistry {
    public static void main(String[] args) throws Exception{
        Registry registry = LocateRegistry.createRegistry(1099);
        new CountDownLatch(1).await();
    }
}
