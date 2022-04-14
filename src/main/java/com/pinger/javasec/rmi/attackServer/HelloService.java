package com.pinger.javasec.rmi.attackServer;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author : p1n93r
 * @date : 2021/8/6 18:31
 * Service接口
 */
public interface HelloService extends Remote {
    void sayHello(String str) throws RemoteException;
}
