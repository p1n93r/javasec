package com.pinger.javasec.jndi;

import javax.naming.Context;
import javax.naming.InitialContext;

/**
 * @author : p1n93r
 * @date : 2021/8/9 10:13
 * 模拟脆弱的客户端
 */
public class WeakClient {
    public static void main(String[] args)throws Exception {
        String url = "ldap://192.168.0.117:23457/DirList8";
        Context ctx = new InitialContext();
        ctx.lookup(url);
    }
}
