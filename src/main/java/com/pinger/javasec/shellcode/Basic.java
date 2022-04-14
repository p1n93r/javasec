package com.pinger.javasec.shellcode;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * @author : p1n93r
 * @date : 2021/11/30 23:14
 */
public class Basic {

    public static void useUnsafe()throws Exception{
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            Unsafe unsafe = (Unsafe) field.get(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
