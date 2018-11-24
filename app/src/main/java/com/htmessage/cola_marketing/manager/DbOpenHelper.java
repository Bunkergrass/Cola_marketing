package com.htmessage.cola_marketing.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.htmessage.cola_marketing.HTApp;
import com.htmessage.cola_marketing.domain.InviteMessgeDao;
import com.htmessage.cola_marketing.domain.MomentsMessageDao;
import com.htmessage.cola_marketing.domain.UserDao;

public class DbOpenHelper extends SQLiteOpenHelper {

    private static final String USERNAME_TABLE_CREATE = "CREATE TABLE "
            + UserDao.TABLE_NAME + " ("
            + UserDao.COLUMN_NAME_NICK + " TEXT, "
            + UserDao.COLUMN_NAME_AVATAR + " TEXT, "
            + UserDao.COLUMN_NAME_INFO+ " TEXT, "
            + UserDao.COLUMN_NAME_ID + " TEXT PRIMARY KEY);";
    private static final String INIVTE_MESSAGE_TABLE_CREATE = "CREATE TABLE "
            + InviteMessgeDao.TABLE_NAME + " ("
            + InviteMessgeDao.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + InviteMessgeDao.COLUMN_NAME_FROM + " TEXT, "
            + InviteMessgeDao.COLUMN_NAME_GROUP_ID + " TEXT, "
            + InviteMessgeDao.COLUMN_NAME_GROUP_Name + " TEXT, "
            + InviteMessgeDao.COLUMN_NAME_REASON + " TEXT, "
            + InviteMessgeDao.COLUMN_NAME_STATUS + " INTEGER, "
            + InviteMessgeDao.COLUMN_NAME_ISINVITEFROMME + " INTEGER, "
            + InviteMessgeDao.COLUMN_NAME_UNREAD_MSG_COUNT + " INTEGER, "
            + InviteMessgeDao.COLUMN_NAME_TIME + " TEXT, "
            + InviteMessgeDao.COLUMN_NAME_GROUPINVITER + " TEXT); ";
    private static final String MOMENTS_TABLE_CREATE = "CREATE TABLE "
            + MomentsMessageDao.TABLE_NAME + " ("
            + MomentsMessageDao.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,  "
            + MomentsMessageDao.COLUMN_NAME_AVATAR + " TEXT, "
            + MomentsMessageDao.COLUMN_NAME_USERID+ " TEXT, "
            + MomentsMessageDao.COLUMN_NAME_USERNICK+ " TEXT, "
            + MomentsMessageDao.COLUMN_NAME_TIME+ " TEXT, "
            + MomentsMessageDao.COLUMN_NAME_CONTENT+ " TEXT, "
            + MomentsMessageDao.COLUMN_NAME_TYPE+ " INTEGER, "
            + MomentsMessageDao.COLUMN_NAME_STATUS+ " INTEGER, "
            + MomentsMessageDao.COLUMN_NAME_IMAGEURL+ " TEXT, "
            + MomentsMessageDao.COLUMN_NAME_MOMENTS_ID + " TEXT); ";

    private static final int DATABASE_VERSION = 1;
    private static DbOpenHelper instance;

    private DbOpenHelper(Context context) {
        super(context, getUserDatabaseName(), null, DATABASE_VERSION);
        Log.d("SDKDbOpenHelper----->",getUserDatabaseName());
    }

    public static DbOpenHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DbOpenHelper(context.getApplicationContext());
        }
        return instance;
    }

    private static String getUserDatabaseName() {
        return HTApp.getInstance().getUsername()+ "_app.db";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(USERNAME_TABLE_CREATE);
        db.execSQL(INIVTE_MESSAGE_TABLE_CREATE);
        db.execSQL(MOMENTS_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void closeDB() {
        if (instance != null) {
            try {
                SQLiteDatabase db = instance.getWritableDatabase();
                db.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            instance = null;
        }
    }

}
