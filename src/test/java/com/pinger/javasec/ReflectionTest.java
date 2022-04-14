package com.pinger.javasec;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.junit.jupiter.api.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.lang.reflect.Constructor;

/**
 * @author : p1n93r
 * @date : 2021/3/30 14:45
 * 测试反射相关
 */
public class ReflectionTest {

    @Test
    public void test1() throws Exception {
        Class<?> clazz = Class.forName("java.lang.Runtime");
        clazz.getMethod("exec", String.class).invoke(clazz.getMethod("getRuntime").invoke(clazz),"mspaint");
    }

    @Test
    public void test2() throws Exception{
        Class<?> clazz = Class.forName("java.lang.ProcessBuilder");
        clazz.getMethod("start").invoke(clazz.getConstructor(String[].class).newInstance(new String[][]{{"calc"}}));
    }

    @Test
    public void test3() throws Exception{
        Class<?> clazz = Class.forName("java.lang.Runtime");
        Constructor<?> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        clazz.getMethod("exec", String.class).invoke(constructor.newInstance(),"mspaint");
    }







    @Test
    public void testOne() throws Exception{


        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        ScriptEngine js = scriptEngineManager.getEngineByName("js");
        System.out.println(js.eval("java.lang.Runtime.getRuntime().exec('mspaint')"));


    }


































}
