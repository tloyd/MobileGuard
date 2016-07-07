package cc.springwind.mobileguard.engine;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by HeFan on 2016/7/4.
 */
public class SmsBackup {
    private static int index = 0;

    public static void backup(Context context, String path, Callback callback) {
        Cursor cursor;
        FileOutputStream outputStream;
        try {
            File file = new File(path);
            outputStream = new FileOutputStream(file);
            cursor = context.getContentResolver().query(Uri.parse("content://sms/"), new String[]{"address", "date",
                            "type", "body"}, null,
                    null, null);

            XmlSerializer newSerializer = Xml.newSerializer();
            newSerializer.setOutput(outputStream, "utf-8");
            newSerializer.startDocument("utf-8", true);

            newSerializer.startTag(null, "smss");

            if (callback != null) {
                callback.setMax(cursor.getCount());
            }

            while (cursor.moveToNext()) {
                newSerializer.startTag(null, "sms");

                newSerializer.startTag(null, "address");
                newSerializer.text(cursor.getString(0));
                newSerializer.endTag(null, "address");

                newSerializer.startTag(null, "date");
                newSerializer.text(cursor.getString(1));
                newSerializer.endTag(null, "date");

                newSerializer.startTag(null, "type");
                newSerializer.text(cursor.getString(2));
                newSerializer.endTag(null, "type");

                newSerializer.startTag(null, "body");
                newSerializer.text(cursor.getString(3));
                newSerializer.endTag(null, "body");

                newSerializer.endTag(null, "sms");

                index++;
                Thread.sleep(500);

                if (callback != null) {
                    callback.setProgress(index);
                }
            }
            newSerializer.endTag(null, "smss");
            newSerializer.endDocument();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface Callback {

        void setMax(int count);

        void setProgress(int index);
    }
}
