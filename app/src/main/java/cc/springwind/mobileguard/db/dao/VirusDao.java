package cc.springwind.mobileguard.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HeFan on 2016/7/7.
 */
public class VirusDao {
    public static String path = "data/data/cc.springwind.mobileguard/files/antivirus.db";

    public static List<String> getVirusMD5List() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.query("datable", new String[]{"md5"}, null, null, null, null, null, null);
        List<String> virusMD5List = new ArrayList<>();
        while (cursor.moveToNext()) {
            virusMD5List.add(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return virusMD5List;
    }
}
