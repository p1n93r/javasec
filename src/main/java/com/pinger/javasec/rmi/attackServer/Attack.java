package com.pinger.javasec.rmi.attackServer;

import com.pinger.javasec.common.CC6;

import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * @author : p1n93r
 * @date : 2021/8/6 18:45
 */
public class Attack {
    public static void main(String[] args)throws Exception {
        Registry registry = LocateRegistry.getRegistry("127.0.0.1", 1099);
        HelloService hello = (HelloService)registry.lookup("hello");
        hello.sayHello("p1n93r");
    }
}
