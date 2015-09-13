package com.music.personal.myapplication.persistance;

import android.provider.BaseColumns;

/**
 * Created by hrajagopal on 9/11/15.
 */
public class CacheAlbum {
    public CacheAlbum(){}

    public static abstract class CacheEntry implements BaseColumns{
        public static final String TABLE_NAME="CACHE_ALBUM";
        public static final String ID = "ID";
        public static final String COLUMN_ALBUM_NAME = "ALBUM_NAME";
        public static final String MUSIC_DIRECTOR = "MUSIC_DIRECTOR";
        public static final String REMOTE_ALBUM_ART = "REMOTE_ALBUM_ART";
        public static final String CACHED_ALBUM_ART = "CACHED_ALBUM_ART";

    }
}
