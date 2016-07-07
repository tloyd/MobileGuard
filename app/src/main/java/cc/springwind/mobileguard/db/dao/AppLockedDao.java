package cc.springwind.mobileguard.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import cc.springwind.mobileguard.db.AppLockedDBHelp;

/**
 * Created by HeFan on 2016/7/3.
 */
public class AppLockedDao {
    private final Context context;
    private AppLockedDBHelp mAppLockedDBHelp;
    private static AppLockedDao mAppLockedDao = null;

    private AppLockedDao(Context context) {
        this.context = context;
        mAppLockedDBHelp = new AppLockedDBHelp(context);
    }

    public static AppLockedDao getInstance(Context context) {
        if (mAppLockedDao == null) {
            mAppLockedDao = new AppLockedDao(context);
        }
        return mAppLockedDao;
    }

    public void insert(String packageName) {
        SQLiteDatabase db = mAppLockedDBHelp.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("packageName", packageName);
        db.insert("applock", null, values);

        db.close();

        context.getContentResolver().notifyChange(Uri.parse("content://applock/change"), null);
    }

    public void delete(String phone) {
        SQLiteDatabase db = mAppLockedDBHelp.getWritableDatabase();

        db.delete("applock", "packageName = ?", new String[]{phone});

        db.close();

        context.getContentResolver().notifyChange(Uri.parse("content://applock/change"), null);
    }

    public List<String> findAll() {
        SQLiteDatabase db = mAppLockedDBHelp.getWritableDatabase();

        Cursor cursor = db.query("applock", new String[]{"packageName"}, null, null, null, null, "_id desc");
        List<String> appLockedList = new ArrayList<>();
        while (cursor.moveToNext()) {
            String packageName = cursor.getString(0);
            appLockedList.add(packageName);
        }
        cursor.close();
        db.close();
        return appLockedList;
    }

}
