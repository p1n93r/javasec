package com.pinger.javasec.rmi.attackServer;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * @author : p1n93r
 * @date : 2021/8/6 18:32
 * Service实现
 */
public class HelloServiceImpl extends UnicastRemoteObject implements HelloService {
    protected HelloServiceImpl() throws RemoteException { }

    @Override
    public void sayHello(String str) throws RemoteException {
        System.out.println("Server received str:"+str);
    }
}
