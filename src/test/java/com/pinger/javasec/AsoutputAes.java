import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.UUID;

public class AsoutputAes {
    String res;

    public AsoutputAes() {}

    public AsoutputAes(String input) {
        this.res = this.aesEncode(input);
    }

    private String aesEncode(String input){
        try {
            byte[] raw = randomKey().getBytes("utf-8");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec ips = new IvParameterSpec(raw);
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ips);
            byte[] encrypted = cipher.doFinal(input.getBytes("utf-8"));
            String encoded = base64Encode(encrypted);
            String key = base64Encode(raw);
            return key+encoded;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private String base64Encode(byte[] base64Byte) throws Exception {
        String version = System.getProperty("java.version");
        Class clazz;
        Object encoder;
        String ret;
        if (version.compareTo("1.9") >= 0) {
            clazz = Class.forName("java.util.Base64");
            encoder = clazz.getMethod("getEncoder").invoke(clazz);
            ret = (String)encoder.getClass().getMethod("encodeToString", byte[].class).invoke(encoder,base64Byte);
        } else {
            clazz = Class.forName("sun.misc.BASE64Encoder");
            encoder = clazz.getDeclaredConstructor().newInstance();
            ret = (String)encoder.getClass().getMethod("encode", byte[].class).invoke(encoder, base64Byte);
        }
        return ret;
    }

    private String randomKey(){
        return UUID.randomUUID().toString().replaceAll("-","").substring(0,16);
    }

    public String toString() {
        return this.res;
    }
}
