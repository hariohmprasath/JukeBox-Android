package com.music.personal.myapplication.persistance.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.music.personal.myapplication.persistance.CacheSong;
import com.music.personal.myapplication.persistance.interfaces.IHelper;
import com.music.personal.myapplication.pojo.Song;
import com.music.personal.myapplication.utils.Utils;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by hrajagopal on 9/11/15.
 */
public class CacheSongHelperImpl implements IHelper<Song> {
    @Override
    public void addRecords(SQLiteOpenHelper helper, List<Song> records) {
        if (helper != null && records != null && records.size() > 0) {
            SQLiteDatabase db = helper.getWritableDatabase();

            for (Song x : records) {
                ContentValues values = new ContentValues();
                values.put(CacheSong.CacheEntry.COLUMN_ALBUM_NAME, Utils.getSearchableData(x.getAlbumName()));
                values.put(CacheSong.CacheEntry.COLUMN_SONG_NAME, Utils.getSearchableData(x.getSongName()));
                values.put(CacheSong.CacheEntry.COLUMN_REMOTE_LOCATION, x.getSongUrl());
                values.put(CacheSong.CacheEntry.COLUMN_ONSITE_LOCATION, Utils.getSearchableData(x.getCacheLocation()));
                values.put(CacheSong.CacheEntry.COLUMN_CACHED_ON, Utils.getSearchableData(Calendar.getInstance().getTime().toString()));

                db.insert(CacheSong.CacheEntry.TABLE_NAME, null, values);
            }

        }
    }

    @Override
    public void deleteRecord(SQLiteOpenHelper helper, String pk) {
        if (helper != null && pk != null) {
            SQLiteDatabase db = helper.getWritableDatabase();
            String selection = CacheSong.CacheEntry.COLUMN_ALBUM_NAME + " = ?";
            String[] selectionArgs = {String.valueOf(Utils.getSearchableData(pk))};
            db.delete(CacheSong.CacheEntry.TABLE_NAME, selection, selectionArgs);
        }
    }

    @Override
    public List<Song> getAllRecords(SQLiteOpenHelper helper) {
        return filterRecords(helper, null, null);
    }

    @Override
    public Song getRecord(SQLiteOpenHelper helper, String pk) {
        String selection = CacheSong.CacheEntry.COLUMN_ALBUM_NAME + " = ?";
        String[] selectionArgs = {String.valueOf(Utils.getSearchableData(pk))};

        List<Song> songList = filterRecords(helper, selection, selectionArgs);
        if (songList != null && songList.size() > 0)
            return songList.get(0);

        return null;
    }

    public List<Song> getAllRecordsForAlbum(SQLiteOpenHelper helper, String albumName) {
        if (helper != null && albumName != null) {
            try {
                String selection = CacheSong.CacheEntry.COLUMN_ALBUM_NAME + " = ?";
                String[] selectionArgs = {String.valueOf(Utils.getSearchableData(albumName))};
                return filterRecords(helper, selection, selectionArgs);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private List<Song> filterRecords(SQLiteOpenHelper helper, String selection, String selectionArgs[]) {
        if (helper != null) {
            SQLiteDatabase db = helper.getWritableDatabase();

            String[] projection = {CacheSong.CacheEntry.COLUMN_SONG_NAME, CacheSong.CacheEntry.COLUMN_ALBUM_NAME,
                    CacheSong.CacheEntry.COLUMN_REMOTE_LOCATION, CacheSong.CacheEntry.COLUMN_ONSITE_LOCATION, CacheSong.CacheEntry.COLUMN_CACHED_ON};
            String sortOrder = CacheSong.CacheEntry.COLUMN_ALBUM_NAME + " ASC";

            Cursor cursor = db.query(
                    CacheSong.CacheEntry.TABLE_NAME, projection, selection, selectionArgs, null,
                    null, sortOrder);

            List<Song> listOfSongs = new LinkedList<>();
            if (cursor.moveToFirst()) {
                do {
                    Song x = new Song();
                    String albumName = cursor.getString(cursor.getColumnIndexOrThrow(CacheSong.CacheEntry.COLUMN_ALBUM_NAME));
                    String remoteLocation = cursor.getString(cursor.getColumnIndexOrThrow(CacheSong.CacheEntry.COLUMN_REMOTE_LOCATION));
                    String songName = cursor.getString(cursor.getColumnIndexOrThrow(CacheSong.CacheEntry.COLUMN_SONG_NAME));
                    String cacheLocation = cursor.getString(cursor.getColumnIndexOrThrow(CacheSong.CacheEntry.COLUMN_ONSITE_LOCATION));
                    String cachedOn = cursor.getString(cursor.getColumnIndexOrThrow(CacheSong.CacheEntry.COLUMN_CACHED_ON));

                    x.setAlbumName(albumName);
                    x.setSongName(songName);
                    x.setSongUrl(remoteLocation);
                    x.setCacheLocation(cacheLocation);
                    x.setCachedOn(cachedOn);

                    listOfSongs.add(x);
                } while (cursor.moveToNext());
            }

            return listOfSongs;
        }

        return null;
    }
}
