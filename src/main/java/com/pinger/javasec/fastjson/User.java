package com.pinger.javasec.fastjson;

import java.util.Properties;

/**
 * @author : p1n93r
 * @date : 2021/8/2 13:13
 * 一个简单的用于测试FastJson反序列化的pojo
 */
public class User {
    /**
     * 私有属性，有getter&setter方法
     */
    private String name;

    /**
     * 共有属性，无getter&setter方法
     */
    public int age;

    /**
     * 私有属性，无getter&setter方法
     */
    private String info;

    /**
     * 公有属性，有getter&setter方法
     */
    public String address;

    /**
     * 共有属性，有getter方法，无setter方法
     */
    private Properties _id;


    public User() {
        System.out.println("call User default Constructor");
    }

    public String getName() {
        System.out.println("call User getName");
        return name;
    }

    public void setName(String name) {
        System.out.println("call User setName");
        this.name = name;
    }

    public String getAddress() {
        System.out.println("call User getAddress");
        return address;
    }

    public void setAddress(String address) {
        System.out.println("call User setAddress");
        this.address = address;
    }


    private Properties getId() {
        System.out.println("call User getId");
        return _id;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", info='" + info + '\'' +
                ", address='" + address + '\'' +
                ", _id='" + _id + '\'' +
                '}';
    }
}
