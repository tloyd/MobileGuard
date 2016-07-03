package cc.springwind.mobileguard.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by HeFan on 2016/7/3.
 */
public class BlackListDBHelp extends SQLiteOpenHelper {
    public final static String BLACKLIST_DB = "blacklist.db";
    private static final String CREATE_SQL = "create table blacknumber (_id integer primary key autoincrement , phone" +
            " varchar(20), mode varchar(5));";

    public BlackListDBHelp(Context context) {
        super(context, BLACKLIST_DB, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
