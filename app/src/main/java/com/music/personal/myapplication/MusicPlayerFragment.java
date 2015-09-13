package com.music.personal.myapplication;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
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
import java.net.URLEncoder;
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
    private static boolean isInitialized = true;

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


    public static MusicPlayerFragment newInstance() {
        MusicPlayerFragment fragment = new MusicPlayerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
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

            // First time initialization of media player
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            }

            // Re-initialize it in case of new song is selected from album/playlist
            if (isInitialized) {
                resetMedia();
                prepareMediaPlayer();
                setPauseIcon();
            } else {
                setTrack();
                setMaxDuration();
                toggleIcon();
            }

            // Thread to update seek bar, current position timing every 1 second
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int currentSeekPosition = mediaPlayer.getCurrentPosition();
                    seekBar.setProgress(currentSeekPosition);
                    currentPositionTxt.setText(getDurationTxt(currentSeekPosition));
                    handler.postDelayed(this, 1000);
                }
            });

            // Add seek bar listener
            addSeekBarListener();

            // Reset initialization flag
            MusicPlayerFragment.isInitialized = false;

            return view;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void initializePlayer(List<Song> playlist, int currentSongPosition) {
        if (playlist != null) {
            MusicPlayerFragment.playlist = playlist;
            MusicPlayerFragment.currentSongPosition = currentSongPosition;
            MusicPlayerFragment.isInitialized = true;
        }
    }

    public void initializePlayer(int currentSongPosition) {
        MusicPlayerFragment.currentSongPosition = currentSongPosition;
        MusicPlayerFragment.isInitialized = true;
    }

    public void syncPlaylist(List<Song> playlist) {
        if (playlist != null)
            MusicPlayerFragment.playlist = playlist;
    }

    /**
     * Seek bar listener
     */
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
                // Don't do anything
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Don't do anything
            }
        });
    }

    /**
     * 1. Set albumArt, songName, albumName and send notification
     * 2. Set seekBar max duration & max duration text
     * 3. Prepare async sync and start playing
     */
    private void prepareMediaPlayer() {
        try {
            setTrack();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    setMaxDuration();
                }
            });

            if (playlist != null && playlist.size() > 0)
                mediaPlayer.prepareAsync();

            play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setMaxDuration() {
        seekBar.setMax(mediaPlayer.getDuration());
        maxDurationTxt.setText(getDurationTxt(mediaPlayer.getDuration()));
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
        setPlayIcon();
    }

    private void play() {
        if (!mediaPlayer.isPlaying())
            mediaPlayer.start();
        setPauseIcon();
    }

    private void toggleIcon() {
        if (mediaPlayer.isPlaying())
            setPauseIcon();
        else
            setPlayIcon();
    }

    private void setPlayIcon() {
        playPauseButton.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_play));
    }

    private void setPauseIcon() {
        playPauseButton.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_pause));
    }

    @OnClick(R.id.nextButton)
    public void playNextTrack() {
        try {
            MusicPlayerFragment.isInitialized = true;
            updateCounter(true);
            resetMedia();
            prepareMediaPlayer();
            play();
            MusicPlayerFragment.isInitialized = false;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @OnClick(R.id.previousButton)
    public void playPreviousTrack() {
        try {
            MusicPlayerFragment.isInitialized = true;
            resetMedia();
            prepareMediaPlayer();
            updateCounter(false);
            play();
            MusicPlayerFragment.isInitialized = false;
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

    /**
     * Set AlbumArt, songName, albumName & send notification
     * Set the track to media player only in-case of re-initialization needed.
     *
     * @throws IOException
     */
    private void setTrack() throws IOException {
        if (playlist != null && currentSongPosition < playlist.size()) {
            Song x = playlist.get(currentSongPosition);

            String mediaUrl = x.getSongUrl();
            mediaUrl = mediaUrl.replaceAll("\\s", "%20");
            if (x.getCacheLocation() != null && x.getCacheLocation().trim().length() > 0)
                mediaUrl = x.getCacheLocation();

            if (MusicPlayerFragment.isInitialized)
                mediaPlayer.setDataSource(mediaUrl);


            String imageSource = x.getAlbumArt();
            if (imageSource == null)
                imageSource = Constants.DEFAULT_IMAGE_URL;

            Picasso.with(view.getContext()).load(imageSource).into(albumArt);
            albumName.setText(x.getAlbumName());
            songName.setText(x.getSongName());

            // Send notification to android action bar
            new LoadBitMaps(view.getContext(), getActivity(), x).execute(imageSource);
        } else if (playlist != null && playlist.size() != 0)
            Toast.makeText(getActivity(), "Reached end of the list", Toast.LENGTH_SHORT).show();
    }

    private void resetMedia() {
        if (mediaPlayer != null)
            mediaPlayer.reset();

        if (seekBar != null) {
            seekBar.setProgress(0);
            seekBar.setMax(999999999);
        }

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
