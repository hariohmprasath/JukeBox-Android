package com.music.personal.myapplication.listener;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Toast;

import com.music.personal.myapplication.HomeActivity;
import com.music.personal.myapplication.MusicPlayerFragment;
import com.music.personal.myapplication.R;
import com.music.personal.myapplication.utils.Constants;

/**
 * Created by hrajagopal on 9/10/15.
 */
public class NotificationListener extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        processIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        processIntent(intent);
    }

    private void processIntent(Intent intent) {
        Toast.makeText(NotificationListener.this, "Thanks for using Juke Box", Toast.LENGTH_SHORT).show();
    }
}
