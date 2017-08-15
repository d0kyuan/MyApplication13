package com.rayyuan.www.myapplication;

/**
 * Created by rayyu on 2016/12/4.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbAdapter {

    public static final String KEY_ROWID = "_id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_TIME = "time";
    public static final String KEY_Sender = "sender";
    private static final String TAG = "DbAdapter";
    private DatabaseHelper mDbHelper;
    private static SQLiteDatabase mDb;

    private static final String DATABASE_NAME = "Messagedb";
    private static final String SQLITE_TABLE = "Message";
    private static final int DATABASE_VERSION = 1;

    private final Context mCtx;





    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i(TAG, "資料庫創建");
            db.execSQL("CREATE TABLE if not exists tb_game_his(" +
                   "_id integer PRIMARY KEY autoincrement," +
                    "Grecord ," +
                    "Gtime," +
                    "Gmode,Rcount,Fcount,isupload );");
            db.execSQL("CREATE TABLE if not exists tb_config(" +
                    "_id integer PRIMARY KEY autoincrement," +
                    "Cname ," +
                    "Cvalue );");
            db.execSQL("CREATE TABLE if not exists tb_game_det (" +
                    "_id integer PRIMARY KEY autoincrement," +
                    "Gid varchar(50), Q_text varchar(50)," +
                    "Q varchar(50), " +
                    "A varchar(50)," +
                    "realA varchar(50));");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + SQLITE_TABLE);
            onCreate(db);
        }
    }

    public DbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public DbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }
    public  static void create_table(){
        mDb.execSQL("CREATE TABLE if not exists tb_game_his(" +
                "_id integer PRIMARY KEY autoincrement," +
                "Grecord ," +
                "Gtime," +
                "Gmode,Rcount,Fcount,isupload );");
        mDb.execSQL("CREATE TABLE if not exists tb_config(" +
                "_id integer PRIMARY KEY autoincrement," +
                "Cname ," +
                "Cvalue );");
        mDb.execSQL("CREATE TABLE if not exists tb_game_det (" +
                "_id integer PRIMARY KEY autoincrement," +
                "Gid varchar(50), Q_text varchar(50)," +
                "Q varchar(50), " +
                "A varchar(50)," +
                "realA varchar(50));");
    }
    public void close() {
        if (mDbHelper != null) {
            mDbHelper.close();
        }
    }
    public static Cursor useq(String a){
        Cursor c = mDb.rawQuery(a,null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }
    public static long add_conf(String cn ,String cv){
        ContentValues initialValues = new ContentValues();
        initialValues.put("Cname", cn);
        initialValues.put("Cvalue", cv);
        long a =  mDb.insert("tb_config", null, initialValues);
        return a;
    }
    public static long  add_game_his_db(String title,
                            String message,
                                        String mode,String Rcount,String Fcount,String isupload) {
        Log.i("myva",title+":"+message);
        ContentValues initialValues = new ContentValues();
        initialValues.put("Grecord", title);
        initialValues.put("Gtime", message);
        initialValues.put("Gmode", mode);
        initialValues.put("Rcount", Rcount);
        initialValues.put("Fcount", Fcount);
        initialValues.put("isupload", isupload);
        long a =  mDb.insert("tb_game_his", null, initialValues);
        return a;
    }
    public static void  add_game_det_db(String Gid,
                                        String Q, String A, String realA,String Qtext) {
        Log.i("myva",Gid+":"+Q);
        ContentValues initialValues = new ContentValues();
        initialValues.put("Gid", Gid);
        initialValues.put("Q_text", Qtext);
        initialValues.put("Q", Q);
        initialValues.put("A", A);
        initialValues.put("realA", realA);
        mDb.insert("tb_game_det", null, initialValues);
    }
    public  static boolean delete(int id) {

        return mDb.delete(SQLITE_TABLE, KEY_ROWID + "=" + id, null) > 0;

    }


    public static boolean deleteAllCountries() {

        int doneDelete = 0;
        doneDelete = mDb.delete(SQLITE_TABLE, null, null);
        Log.w(TAG, Integer.toString(doneDelete));
        return doneDelete > 0;

    }



    public Cursor fetchAllCountries() {

        Cursor mCursor = mDb.query(SQLITE_TABLE, new String[]{KEY_ROWID,
                        KEY_TITLE, KEY_MESSAGE,KEY_TIME,KEY_Sender},
                null, null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }



}