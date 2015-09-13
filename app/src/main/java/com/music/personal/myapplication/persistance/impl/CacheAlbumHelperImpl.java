package com.music.personal.myapplication.persistance.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.music.personal.myapplication.persistance.CacheAlbum;
import com.music.personal.myapplication.persistance.interfaces.IHelper;
import com.music.personal.myapplication.pojo.Album;
import com.music.personal.myapplication.utils.Utils;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by hrajagopal on 9/11/15.
 */
public class CacheAlbumHelperImpl implements IHelper<Album> {
    @Override
    public void addRecords(SQLiteOpenHelper helper, List<Album> records) {
        if (helper != null && records != null && records.size() > 0) {
            SQLiteDatabase db = helper.getWritableDatabase();

            for (Album x : records) {
                ContentValues values = new ContentValues();
                values.put(CacheAlbum.CacheEntry.ID, Utils.getSearchableData(x.getAlbumName()));
                values.put(CacheAlbum.CacheEntry.COLUMN_ALBUM_NAME, x.getAlbumName());
                values.put(CacheAlbum.CacheEntry.MUSIC_DIRECTOR, x.getAlbumName());
                values.put(CacheAlbum.CacheEntry.REMOTE_ALBUM_ART, Utils.getSearchableData(x.getAlbumName()));
                values.put(CacheAlbum.CacheEntry.ID, Utils.getSearchableData(x.getAlbumName()));


                db.insert(CacheAlbum.CacheEntry.TABLE_NAME, null, values);
            }

        }
    }

    @Override
    public void deleteRecord(SQLiteOpenHelper helper, String pk) {
        if (helper != null && pk != null) {
            SQLiteDatabase db = helper.getWritableDatabase();
            String selection = CacheAlbum.CacheEntry.COLUMN_ALBUM_NAME + " = ?";
            String[] selectionArgs = {String.valueOf(pk)};
            db.delete(CacheAlbum.CacheEntry.TABLE_NAME, selection, selectionArgs);
        }
    }

    @Override
    public List<Album> getAllRecords(SQLiteOpenHelper helper) {
        if (helper != null) {
            try {
                SQLiteDatabase db = helper.getWritableDatabase();

                String[] projection = {CacheAlbum.CacheEntry.COLUMN_ALBUM_NAME};

                String sortOrder = CacheAlbum.CacheEntry.COLUMN_ALBUM_NAME + " ASC";

                Cursor cursor = db.query(
                        CacheAlbum.CacheEntry.TABLE_NAME, projection, null, null, null,
                        null, sortOrder);

                List<Album> listOfAlbums = new LinkedList<>();
                cursor.moveToFirst();
                do {
                    Album x = new Album();
                    String albumName = cursor.getString(cursor.getColumnIndexOrThrow(CacheAlbum.CacheEntry.COLUMN_ALBUM_NAME));
                    x.setAlbumName(albumName);
                    listOfAlbums.add(x);
                } while (cursor.moveToNext());

                return listOfAlbums;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    public Album getRecord(SQLiteOpenHelper helper, String pk) {
        if (helper != null) {
            SQLiteDatabase db = helper.getWritableDatabase();

            String[] projection = {CacheAlbum.CacheEntry.COLUMN_ALBUM_NAME};
            String sortOrder = CacheAlbum.CacheEntry.COLUMN_ALBUM_NAME + " ASC";
            String selection = CacheAlbum.CacheEntry.COLUMN_ALBUM_NAME + " = ?";
            String[] selectionArgs = {String.valueOf(pk)};

            Cursor cursor = db.query(
                    CacheAlbum.CacheEntry.TABLE_NAME, projection, selection, selectionArgs, null,
                    null, sortOrder);

            if (cursor.moveToFirst()) {
                Album x = new Album();
                String albumName = cursor.getString(cursor.getColumnIndexOrThrow(CacheAlbum.CacheEntry.COLUMN_ALBUM_NAME));
                x.setAlbumName(albumName);
                return x;
            }
        }

        return null;
    }
}
