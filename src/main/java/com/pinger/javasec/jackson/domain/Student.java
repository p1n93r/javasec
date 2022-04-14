package com.pinger.javasec.jackson.domain;

import java.util.Properties;

/**
 * @author : p1n93r
 * @date : 2021/8/25 9:49
 */
public class Student {

    private String name;

    public Properties getConnection(){
        System.out.println("调用了getConnection");
        return new Properties();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
