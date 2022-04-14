package com.pinger.javasec;

public class AsoutputBase64 {
    String res;

    public AsoutputBase64(String var1) {
        this.res = this.Base64Encode(var1);
    }

    public String Base64Encode(String var1) {
        String var2 = System.getProperty("java.version");

        try {
            String var3 = "";
            Class var4;
            Object var5;
            if (var2.compareTo("1.9") >= 0) {
                var4 = Class.forName("java.util.Base64");
                var5 = var4.getMethod("getEncoder").invoke(var4);
                var3 = (String)var5.getClass().getMethod("encodeToString", byte[].class).invoke(var5, var1.getBytes());
            } else {
                var4 = Class.forName("sun.misc.BASE64Encoder");
                var5 = var4.getDeclaredConstructor().newInstance();
                var3 = (String)var5.getClass().getMethod("encode", byte[].class).invoke(var5, var1.getBytes());
            }

            return var3;
        } catch (Exception var6) {
            return var1;
        }
    }

    public String toString() {
        return this.res;
    }
}
