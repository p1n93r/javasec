package com.pinger.javasec;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author : p1n93r
 * @date : 2021/11/22 18:44
 */
public class justTest extends ClassLoader {

    public Class g(byte[] b) throws Exception {
        // 通过反射调用definClass，从而绕过雷火
        Class<? extends justTest> clazz = this.getClass();
        Method defineClass = clazz.getDeclaredMethod("defineClass", new Class[]{Integer.class, Integer.class});
        defineClass.setAccessible(true);
        return (Class)defineClass.invoke(this,new Object[]{b, 0, b.length});
    }


    public static void main(String[] args) {
        System.out.println("xxx");
    }



}
