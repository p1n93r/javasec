package com.pinger.javasec.fastjson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;

/**
 * @author : p1n93r
 * @date : 2021/8/2 13:16
 * 测试FastJson反序列化的逻辑
 */
public class Main {
    public static void main(String[] args) {
        //序列化
        String jsonString = "{\"@type\":\"com.pinger.javasec.fastjson.User\",\"name\":\"pinger\",\"address\":\"QT\",\"info\":\"QT Security Researcher\",\"age\":\"3\",\"_id\":{}}";
        System.out.println("jsonString=" + jsonString);

        System.out.println("-----------------------------------------------\n\n");
        //通过parse方法进行反序列化，返回的是@type指定的类对象
        System.out.println("JSON.parse(String)：");
        Object obj1 = JSON.parse(jsonString, Feature.SupportNonPublicField);
        System.out.println("parse反序列化对象名称:" + obj1.getClass().getName());
        System.out.println("parse反序列化：" + obj1);
        System.out.println("-----------------------------------------------\n");

        //通过parseObject,不指定类，返回的是一个JSONObject
        System.out.println("JSON.parseObject(String)：");
        Object obj2 = JSON.parseObject(jsonString);
        System.out.println("parseObject反序列化对象名称:" + obj2.getClass().getName());
        System.out.println("parseObject反序列化:" + obj2);
        System.out.println("-----------------------------------------------\n");

        //通过parseObject,指定为object.class
        System.out.println("JSON.parseObject(String, Class)：");
        Object obj3 = JSON.parseObject(jsonString, Object.class);
        System.out.println("parseObject反序列化对象名称:" + obj3.getClass().getName());
        System.out.println("parseObject反序列化:" + obj3);
        System.out.println("-----------------------------------------------\n");

        //通过parseObject,指定为User.class
        System.out.println("JSON.parseObject(String, Class)：");
        Object obj4 = JSON.parseObject(jsonString, User.class);
        System.out.println("parseObject反序列化对象名称:" + obj4.getClass().getName());
        System.out.println("parseObject反序列化:" + obj4);
        System.out.println("-----------------------------------------------\n");
    }
}
