package com.music.personal.myapplication.utils;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;

import com.music.personal.myapplication.MusicPlayerFragment;
import com.music.personal.myapplication.R;
import com.music.personal.myapplication.listener.NotificationListener;
import com.music.personal.myapplication.pojo.Song;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by hrajagopal on 9/10/15.
 */
public class LoadBitMaps extends AsyncTask<String, Void, Void> {

    public static final String PLAY_OR_PAUSE = "PlayOrPause";
    public static final String PREVIOUS = "Previous";
    public static final String NEXT = "Next";
    private Activity activity;
    private Song target;
    private Context context;

    public LoadBitMaps(Context context, Activity activity, Song target) {
        this.activity = activity;
        this.target = target;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(String... str) {

        try {
            URL url = new URL(str[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            final Bitmap myBitmap = BitmapFactory.decodeStream(input);

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String identifier = PLAY_OR_PAUSE;
                    Intent playPauseIntent = new Intent(identifier);
                    PendingIntent pendingPlayOrPause = PendingIntent.getBroadcast(context, 0, playPauseIntent, 0);
                    IntentFilter playFilter = new IntentFilter();
                    playFilter.addAction(identifier);

                    identifier = PREVIOUS;
                    Intent previousIntent = new Intent(identifier);
                    PendingIntent pendingPrevious = PendingIntent.getBroadcast(context, 0, previousIntent, 0);
                    IntentFilter previousFilter = new IntentFilter();
                    previousFilter.addAction(identifier);

                    identifier = NEXT;
                    Intent nextIntent = new Intent(identifier);
                    PendingIntent pendingNext = PendingIntent.getBroadcast(context, 0, nextIntent, 0);
                    IntentFilter nextFilter = new IntentFilter();
                    nextFilter.addAction(identifier);


                    Intent openIntent = new Intent(context, NotificationListener.class);
                    openIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    PendingIntent pendingOpenIntent = PendingIntent.getActivity(context, 0, openIntent, 0);


                    //Define Notification Manager
                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context.getApplicationContext())
                            .setSmallIcon(R.drawable.play_music)
                            .setLargeIcon(myBitmap)
                            .setContentTitle(target.getSongName())
                            .setContentText(target.getAlbumName())
                            .setAutoCancel(false);

                    mBuilder.setContentIntent(pendingOpenIntent);

                    mBuilder.addAction(android.R.drawable.ic_media_previous, "", pendingPrevious);
                    mBuilder.addAction(android.R.drawable.ic_media_pause, "", pendingPlayOrPause);
                    mBuilder.addAction(android.R.drawable.ic_media_next, "", pendingNext);


                    BroadcastReceiver receiver = new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            String action = intent.getAction();
                            FragmentManager fragmentManager = ((Activity) context).getFragmentManager();
                            Fragment fragment = fragmentManager.findFragmentByTag(Constants.MUSIC_PLAYER_FRAGMENT);
                            MusicPlayerFragment playerFragment = null;
                            MediaPlayer player = null;
                            if (fragment != null) {
                                playerFragment = (MusicPlayerFragment) fragment;
                                player = playerFragment.getMediaPlayer();
                            }
                            switch (action) {
                                case PREVIOUS:
                                    if (playerFragment != null)
                                        playerFragment.playPreviousTrack();
                                    break;
                                case PLAY_OR_PAUSE:
                                    if (player != null) {
                                        if (player.isPlaying())
                                            player.pause();
                                        else
                                            player.start();
                                    }
                                    break;
                                case NEXT:
                                    if (playerFragment != null)
                                        playerFragment.playNextTrack();
                                    break;
                            }
                        }
                    };


                    activity.registerReceiver(receiver, playFilter);
                    activity.registerReceiver(receiver, previousFilter);
                    activity.registerReceiver(receiver, nextFilter);
                    //Display notification
                    notificationManager.notify(-999, mBuilder.build());
                }
            });

        } catch (IOException e) {
            e.printStackTrace();

        }
        return null;
    }


    @Override
    protected void onPostExecute(Void v) {
        // do something
    }

}