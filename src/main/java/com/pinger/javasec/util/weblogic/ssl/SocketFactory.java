package com.pinger.javasec.util.weblogic.ssl;


import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.net.Socket;
import java.security.SecureRandom;

/**
 * Created by nike on 17/6/29.
 */
public class SocketFactory {
    private SocketFactory() {
    }

    public static Socket newSocket(String host, int port,Boolean isHttps) throws Exception {
        Socket socket = null;
        if (isHttps) {
            SSLContext context = SSLContext.getInstance("SSL");
            // 初始化
            context.init(null,
                    new TrustManager[]{new TrustManagerImpl()},
                    new SecureRandom());
            SSLSocketFactory factory = context.getSocketFactory();
            socket = factory.createSocket(host, port);
        } else {
            socket = new Socket(host, port);
        }

        return socket;
    }
}
