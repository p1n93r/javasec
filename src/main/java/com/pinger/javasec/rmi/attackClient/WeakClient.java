package com.pinger.javasec.rmi.attackClient;

import javax.naming.InitialContext;
import java.lang.Exception;
import java.lang.String;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * @author : p1n93r
 * @date : 2021/8/2 9:50
 * 模拟脆弱的客户端
 */
public class WeakClient {

    /**
     * 使用JNDI方式的RMI
     */
    public static void rmiUnderJndi()throws Exception {
        String jndiUrl="rmi://127.0.0.1:1023/EvalClass";
        InitialContext initialContext = new InitialContext();
        initialContext.lookup(jndiUrl);
    }


    /**
     * 基于传统方式的RMI
     */
    public static void traditionalRmi()throws Exception{
        String name="Hack";
        Registry registry = LocateRegistry.getRegistry("127.0.0.1", 1099);
        registry.lookup(name);
    }



    public static void main(String[] args)throws Exception {
        rmiUnderJndi();
    }

}
