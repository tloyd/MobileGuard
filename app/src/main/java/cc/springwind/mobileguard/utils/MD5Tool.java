package cc.springwind.mobileguard.utils;

import org.xutils.common.util.MD5;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by HeFan on 2016/6/25 0025.
 */
public class MD5Tool {

    private static MessageDigest md5;

    public static String getMD5Encode(String password) {
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        StringBuffer buf = new StringBuffer("");
        byte[] bytes = md5.digest(password.getBytes());
        for (byte b : bytes) {
            int i = b & 0xff;
            String hexString = Integer.toHexString(i);
            if (hexString.length() < 2)
                hexString = "0" + hexString;
            buf.append(hexString);
        }
        return buf.toString();
    }

    public static String getMD5EncodeWithSalt(String password){
        return MD5.md5(password+ Constants.SALT);
    }
}
