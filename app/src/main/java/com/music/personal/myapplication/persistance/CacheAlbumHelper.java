package com.music.personal.myapplication.persistance;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.music.personal.myapplication.utils.Constants;

/**
 * Created by hrajagopal on 9/11/15.
 */
public class CacheAlbumHelper extends SQLiteOpenHelper {

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + CacheAlbum.CacheEntry.TABLE_NAME + " (" +
                    CacheAlbum.CacheEntry.COLUMN_ALBUM_NAME + " " + TEXT_TYPE + " PRIMARY KEY" + COMMA_SEP +
                    CacheAlbum.CacheEntry.MUSIC_DIRECTOR + " " + TEXT_TYPE + ")";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + CacheAlbum.CacheEntry.TABLE_NAME;

    public CacheAlbumHelper(Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
        SQLiteDatabase db = this.getWritableDatabase();
        onCreate(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }

}
