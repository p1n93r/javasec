package com.pinger.javasec.rmi;

import java.rmi.Naming;
import java.rmi.Remote;
import java.util.Arrays;

/**
 * @author : p1n93r
 * @date : 2021/3/30 15:46
 * RMI客户端
 */
public class RmiClient {

    public static void main(String[] args)  throws Exception {
        String[] list = Naming.list("rmi://127.0.0.1");
        System.out.println("RMI服务端所有的对象如下：");
        for (String s : list) {
            System.out.println(s);
        }

        //测试调用远程方法
        System.out.println("=================================分隔符=====================================");
        // 调用RemoteHelloWorld和hello()
        Remote obj = Naming.lookup("rmi://127.0.0.1/Hello");
        Class<? extends Remote> clazz = obj.getClass();
        Object hello = clazz.getMethod("hello").invoke(obj);
        System.out.println(hello);
    }
}
