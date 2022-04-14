package com.pinger.javasec;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

/**
 * @author : p1n93r
 * @date : 2021/7/8 15:31
 */
public class SafeObjectInputStream extends ObjectInputStream {
    public SafeObjectInputStream(InputStream in) throws IOException {
        super(in);
    }

    @Override
    protected Class<?> resolveClass(ObjectStreamClass classDesc) throws IOException, ClassNotFoundException {
        String className = classDesc.getName();
        // BlackList exploits; eg: don't allow RMI here
        if (className.contains("java.util.HashMap")) {
            return null;
        }
        return super.resolveClass(classDesc);
    }
}
