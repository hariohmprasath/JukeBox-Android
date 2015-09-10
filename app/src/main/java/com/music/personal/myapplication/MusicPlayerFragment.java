package com.music.personal.myapplication;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.music.personal.myapplication.pojo.Song;
import com.music.personal.myapplication.utils.Constants;
import com.music.personal.myapplication.utils.LoadBitMaps;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link MusicPlayerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MusicPlayerFragment extends Fragment {

    private static List<Song> playlist;
    private static MediaPlayer mediaPlayer;
    private static int currentSongPosition = 0;

    private View view;
    private Handler handler;

    @Bind(R.id.albumArt)
    ImageView albumArt;

    @Bind(R.id.songName)
    TextView songName;

    @Bind(R.id.albumName)
    TextView albumName;

    @Bind(R.id.seekBar)
    SeekBar seekBar;

    @Bind(R.id.playPauseButton)
    ImageView playPauseButton;

    @Bind(R.id.previousButton)
    ImageView previousButton;

    @Bind(R.id.nextButton)
    ImageView nextButton;

    @Bind(R.id.currentPositionTxt)
    TextView currentPositionTxt;

    @Bind(R.id.maxDurationTxt)
    TextView maxDurationTxt;


    public static MusicPlayerFragment newInstance(List<Song> playlist) {
        MusicPlayerFragment fragment = new MusicPlayerFragment();
        if (playlist != null) {
            MusicPlayerFragment.currentSongPosition = 0;
            MusicPlayerFragment.playlist = playlist;
            Bundle args = new Bundle();
            fragment.setArguments(args);
        }
        return fragment;
    }

    public MusicPlayerFragment() {
        handler = new Handler();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            // Inflate the layout for this fragment
            view = inflater.inflate(R.layout.fragment_music_player_fragment, container, false);
            ButterKnife.bind(this, view);

            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                prepareMediaPlayer();
                updateCounter(true);
            } else {
                Song x = playlist.get(currentSongPosition);
                String imageSource = x.getAlbumArt();
                if (imageSource == null)
                    imageSource = Constants.DEFAULT_IMAGE_URL;
                Picasso.with(view.getContext()).load(imageSource).into(albumArt);
                albumName.setText(x.getAlbumName());
                songName.setText(x.getSongName());
                seekBar.setMax(mediaPlayer.getDuration());
                maxDurationTxt.setText(getDurationTxt(mediaPlayer.getDuration()));
                toggleIcon();
            }

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int currentSeekPosition = mediaPlayer.getCurrentPosition();
                    seekBar.setProgress(currentSeekPosition);
                    currentPositionTxt.setText(getDurationTxt(currentSeekPosition));
                    handler.postDelayed(this, 1000);
                }
            });

            addSeekBarListener();

            return view;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void addSeekBarListener() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                    mediaPlayer.seekTo(progress);

                // Play next track only the current song completes
                if (progress >= (seekBar.getMax() - 2000))
                    playNextTrack();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void prepareMediaPlayer() {
        try {
            setTrack();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    seekBar.setMax(mediaPlayer.getDuration());
                    maxDurationTxt.setText(getDurationTxt(mediaPlayer.getDuration()));
                }
            });
            mediaPlayer.prepareAsync();
            play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.playPauseButton)
    public void playOrPause() {
        if (mediaPlayer.isPlaying())
            pause();
        else
            play();
    }

    private void pause() {
        mediaPlayer.pause();
        playPauseButton.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_play));
    }

    private void play() {
        if (!mediaPlayer.isPlaying())
            mediaPlayer.start();
        playPauseButton.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_pause));
    }

    private void toggleIcon() {
        if (mediaPlayer.isPlaying())
            playPauseButton.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_pause));
        else
            playPauseButton.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_play));
    }

    @OnClick(R.id.nextButton)
    public void playNextTrack() {
        try {
            resetMedia();
            prepareMediaPlayer();
            play();
            updateCounter(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @OnClick(R.id.previousButton)
    public void playPreviousTrack() {
        try {
            resetMedia();
            updateCounter(false);
            prepareMediaPlayer();
            play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.showPlayList)
    public void showPlaylist() {
        PlayListFragment playListFragment = PlayListFragment.newInstance(playlist, currentSongPosition);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up);
        transaction.replace(R.id.container, playListFragment, null);
        transaction.commit();
    }

    private void setTrack() throws IOException {
        if (playlist != null && currentSongPosition < playlist.size()) {
            final Song x = playlist.get(currentSongPosition);
            mediaPlayer.setDataSource(x.getSongUrl());
            String imageSource = x.getAlbumArt();
            if (imageSource == null)
                imageSource = Constants.DEFAULT_IMAGE_URL;

            Picasso.with(view.getContext()).load(imageSource).into(albumArt);
            albumName.setText(x.getAlbumName());
            songName.setText(x.getSongName());

            new LoadBitMaps(view.getContext(), getActivity(), x).execute(imageSource);
        } else
            Toast.makeText(getActivity(), "Reached end of the list", Toast.LENGTH_SHORT).show();
    }

    public void playFromPlaylist(int currentSongPosition) {
        try {
            MusicPlayerFragment.currentSongPosition = (currentSongPosition - 1);
            resetMedia();
            prepareMediaPlayer();
            play();
            updateCounter(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void resetMedia() {
        mediaPlayer.reset();
        seekBar.setProgress(0);
        seekBar.setMax(999999999);
    }

    private void updateCounter(boolean isIncrement) {
        if (isIncrement)
            MusicPlayerFragment.currentSongPosition++;
        else
            MusicPlayerFragment.currentSongPosition--;
    }

    private String getDurationTxt(int currentSongPosition) {
        StringBuffer buf = new StringBuffer();

        int minutes = (currentSongPosition % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = ((currentSongPosition % (1000 * 60 * 60)) % (1000 * 60)) / 1000;

        buf.append(String.format("%02d", minutes))
                .append(":")
                .append(String.format("%02d", seconds));

        return buf.toString();
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

}
