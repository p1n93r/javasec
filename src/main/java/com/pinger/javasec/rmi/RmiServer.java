package com.pinger.javasec.rmi;

import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

/**
 * @author : p1n93r
 * @date : 2021/3/30 15:37
 * rmi服务端
 */
public class RmiServer {

    /**
     * 继承了Remote类才能进行远程方法调用
     */
    public interface IRemoteHelloWorld extends Remote {
        public String hello() throws RemoteException;
    }

    /**
     * 远程对象，需要继承UnicastRemoteObject类
     */
    public class RemoteHelloWorld extends UnicastRemoteObject implements IRemoteHelloWorld{
        protected RemoteHelloWorld() throws RemoteException {}
        @Override
        public String hello() throws RemoteException {
            System.out.println("我是远程RMI服务端....我正在执行hello()方法...");
            return "Hello,I'm p1n93r.";
        }
    }

    /**
     * 开启RMI监听
     */
    public void startRmiBinding() throws Exception{
        RemoteHelloWorld obj = new RemoteHelloWorld();
        LocateRegistry.createRegistry(1099);
        Naming.bind("rmi://127.0.0.1/Hello",obj);
    }

    public static void main(String[] args) throws Exception {
        new RmiServer().startRmiBinding();
    }
}
