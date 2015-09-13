package com.music.personal.myapplication.persistance;

import android.provider.BaseColumns;

/**
 * Created by hrajagopal on 9/11/15.
 */
public class CacheSong {
    public CacheSong(){}

    public static abstract class CacheEntry implements BaseColumns{
        public static final String TABLE_NAME="CACHE_SONG";
        public static final String COLUMN_ALBUM_NAME = "ALBUM_NAME";
        public static final String COLUMN_SONG_NAME = "SONG_NAME";
        public static final String COLUMN_REMOTE_LOCATION = "REMOTE_LOCATION";
        public static final String COLUMN_ONSITE_LOCATION = "ONSITE_LOCATION";
        public static final String COLUMN_CACHED_ON = "CACHED_ON";
    }
}
