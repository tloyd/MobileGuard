package cc.springwind.mobileguard.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import cc.springwind.mobileguard.db.BlackListDBHelp;
import cc.springwind.mobileguard.db.entity.BlackListEntity;

/**
 * Created by HeFan on 2016/7/3.
 */
public class BlackListDao {
    private BlackListDBHelp mBlackListDBHelp;
    private static BlackListDao mBlackListDao = null;

    private BlackListDao(Context context) {
        mBlackListDBHelp = new BlackListDBHelp(context);
    }

    public static BlackListDao getInstance(Context context) {
        if (mBlackListDao == null) {
            mBlackListDao = new BlackListDao(context);
        }
        return mBlackListDao;
    }

    public void insert(String phone, String mode) {
        //1,开启数据库,准备做写入操作
        SQLiteDatabase db = mBlackListDBHelp.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("phone", phone);
        values.put("mode", mode);
        db.insert("blacknumber", null, values);

        db.close();
    }

    public void delete(String phone) {
        SQLiteDatabase db = mBlackListDBHelp.getWritableDatabase();

        db.delete("blacknumber", "phone = ?", new String[]{phone});

        db.close();
    }

    public void update(String phone, String mode) {
        SQLiteDatabase db = mBlackListDBHelp.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("mode", mode);

        db.update("blacknumber", contentValues, "phone = ?", new String[]{phone});

        db.close();
    }

    public List<BlackListEntity> findAll() {
        SQLiteDatabase db = mBlackListDBHelp.getWritableDatabase();

        Cursor cursor = db.query("blacknumber", new String[]{"phone", "mode"}, null, null, null, null, "_id desc");
        List<BlackListEntity> blackNumberList = new ArrayList<BlackListEntity>();
        while (cursor.moveToNext()) {
            BlackListEntity blackNumberInfo = new BlackListEntity();
            blackNumberInfo.phone = cursor.getString(0);
            blackNumberInfo.mode = cursor.getString(1);
            blackNumberList.add(blackNumberInfo);
        }
        cursor.close();
        db.close();
        return blackNumberList;
    }

    public List<BlackListEntity> find(int index) {
        SQLiteDatabase db = mBlackListDBHelp.getWritableDatabase();

        Cursor cursor = db.rawQuery("select phone,mode from blacknumber order by _id desc limit ?,20;", new
                String[]{index + ""});

        List<BlackListEntity> blackNumberList = new ArrayList<BlackListEntity>();
        while (cursor.moveToNext()) {
            BlackListEntity blackNumberInfo = new BlackListEntity();
            blackNumberInfo.phone = cursor.getString(0);
            blackNumberInfo.mode = cursor.getString(1);
            blackNumberList.add(blackNumberInfo);
        }
        cursor.close();
        db.close();

        return blackNumberList;
    }

    public int getCount() {
        SQLiteDatabase db = mBlackListDBHelp.getWritableDatabase();
        int count = 0;
        Cursor cursor = db.rawQuery("select count(*) from blacknumber;", null);
        if (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        db.close();
        return count;
    }

    public int getMode(String phone) {
        SQLiteDatabase db = mBlackListDBHelp.getWritableDatabase();
        int mode = 0;
        Cursor cursor = db.query("blacknumber", new String[]{"mode"}, "phone = ?", new String[]{phone}, null, null,
                null);
        if (cursor.moveToNext()) {
            mode = cursor.getInt(0);
        }

        cursor.close();
        db.close();
        return mode;
    }
}
