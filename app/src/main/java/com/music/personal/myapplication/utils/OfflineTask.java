package com.music.personal.myapplication.utils;

import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;

import com.music.personal.myapplication.R;
import com.music.personal.myapplication.persistance.CacheAlbumHelper;
import com.music.personal.myapplication.persistance.CacheSongHelper;
import com.music.personal.myapplication.persistance.impl.CacheAlbumHelperImpl;
import com.music.personal.myapplication.persistance.impl.CacheSongHelperImpl;
import com.music.personal.myapplication.persistance.interfaces.IHelper;
import com.music.personal.myapplication.pojo.Album;
import com.music.personal.myapplication.pojo.Song;
import com.squareup.okhttp.internal.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Created by hrajagopal on 9/11/15.
 */
public class OfflineTask extends AsyncTask<Song, Void, Void> {

    private Context context;
    private CacheSongHelper songHelper;
    private CacheAlbumHelper albumHelper;
    private IHelper<Song> songService;
    private IHelper<Album> albumService;
    private boolean isOffline;

    public OfflineTask(Context context, boolean isOffline) {
        super();
        this.context = context;
        this.isOffline = isOffline;
        this.songHelper = new CacheSongHelper(context);
        this.songService = new CacheSongHelperImpl();

        this.albumHelper = new CacheAlbumHelper(context);
        this.albumService = new CacheAlbumHelperImpl();
    }

    @Override
    protected Void doInBackground(Song... params) {
        long totalSize = 0;
        if (params != null && params.length > 0) {
            totalSize = params.length;
            if (this.isOffline) {
                List<Album> albumList = new ArrayList<>();
                for (int i = 0; i < totalSize; i++) {
                    String completePath = saveFile(params[i].getSongUrl());
                    String notificationTxt = "Saved " + params[i].getSongName() + " - (" + (i + 1) + "/" + params.length + ")";
                    showNotification(notificationTxt, -1000);

                    params[i].setCacheLocation(completePath);
                    params[i].setCachedOn(Calendar.getInstance().getTime().toString());

                    Album x = new Album();
                    x.setAlbumName(params[i].getAlbumName());
                    albumList.add(x);
                }

                // Update database after download
                this.songService.addRecords(this.songHelper, Arrays.asList(params));

                // Update album database with the index information
                this.albumService.addRecords(this.albumHelper, albumList);
            } else {
                for (int i = 0; i < totalSize; i++) {
                    String completePath = params[i].getCacheLocation();
                    if (completePath != null && completePath.trim().length() > 0) {
                        File deleteFile = new File(completePath);
                        if (deleteFile.exists())
                            deleteFile.delete();
                    }
                }

                // Remove the entry from table and show notification
                this.songService.deleteRecord(this.songHelper, params[0].getAlbumName());

                // Remove album index from the database
                this.albumService.deleteRecord(this.albumHelper, params[0].getAlbumName());

                showNotification("Removed " + params[0].getAlbumName() + " from device", -999);
            }
        }

        return null;
    }

    private String saveFile(String url) {
        if (url != null && url.trim().length() > 0) {
            InputStream input = null;
            FileOutputStream output = null;
            HttpURLConnection connection = null;
            File pathFile = null;
            try {
                String fileSplit[] = url.split("/");
                String fileName = fileSplit[fileSplit.length - 1];
                String completePath = context.getFilesDir().getPath() + "/" + Utils.getSearchableData(fileName);
                URL sourceUrl = new URL(url);
                connection = (HttpURLConnection) sourceUrl.openConnection();
                connection.connect();

                pathFile = new File(completePath);
                if (!pathFile.exists())
                    pathFile.createNewFile();

                input = connection.getInputStream();
                output = new FileOutputStream(pathFile);

                byte data[] = new byte[4096];
                int count;
                while ((count = input.read(data)) != -1) {
                    output.write(data, 0, count);
                }

                return completePath;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();

            }
        }

        return null;
    }

    private void showNotification(String notificationTxt, int notificationId) {
        if (notificationTxt != null) {
            //Define Notification Manager
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context.getApplicationContext())
                    .setSmallIcon(R.drawable.play_music)
                    .setContentTitle(notificationTxt);

            //Display notification
            notificationManager.notify(notificationId, mBuilder.build());
        }
    }
}
