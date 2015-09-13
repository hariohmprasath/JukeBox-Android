package com.music.personal.myapplication.persistance.interfaces;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

/**
 * Created by hrajagopal on 9/11/15.
 */
public interface IHelper<T> {
    public void addRecords(SQLiteOpenHelper helper, List<T> records);
    public void deleteRecord(SQLiteOpenHelper helper, String pk);
    public List<T> getAllRecords(SQLiteOpenHelper helper);
    public T getRecord(SQLiteOpenHelper helper, String pk);
}
