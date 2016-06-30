package cc.springwind.mobileguard.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by HeFan on 2016/6/24 0024.
 */
public class StreamTool {
    /**
     * 从流中获取字符串并返回
     *
     * @param is
     * @return
     */
    public static String stream2String(InputStream is) {
        ByteArrayOutputStream mByteArrayOutputStream=new ByteArrayOutputStream();
        byte buffer[]=new byte[1024];
        int length=-1;
        try {
            while ((length=is.read(buffer))!=-1){
                mByteArrayOutputStream.write(buffer,0,length);
            }
            return mByteArrayOutputStream.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
                mByteArrayOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
