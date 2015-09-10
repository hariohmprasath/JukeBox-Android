package com.music.personal.myapplication;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.music.personal.myapplication.adaptor.SongAdaptor;
import com.music.personal.myapplication.pojo.Album;
import com.music.personal.myapplication.pojo.Song;
import com.music.personal.myapplication.utils.Constants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link AlbumShowFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlbumShowFragment extends Fragment {

    private View view;
    private Album album;
    private SongSelectionListener songSelectionListener;

    @Bind(R.id.albumName)
    TextView albumName;

    @Bind(R.id.musicDirector)
    TextView musicDirector;

    @Bind(R.id.albumArtThumbNail)
    ImageView albumArtThumbNail;

    @Bind(R.id.songView)
    RecyclerView recyclerView;

    public static AlbumShowFragment newInstance(Album album) {
        AlbumShowFragment fragment = new AlbumShowFragment();
        fragment.setAlbum(album);
        Bundle args = new Bundle();
        args.putParcelable("album", album);
        fragment.setArguments(args);
        return fragment;
    }

    public AlbumShowFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_album_show, container, false);
        songSelectionListener = new SongSelectionListener();
        ButterKnife.bind(this, view);

        Album album = getArguments().getParcelable("album");
        LinearLayoutManager linerLayoutManager = new LinearLayoutManager(view.getContext());

        if (album != null) {
            albumName.setText(album.getAlbumName());
            musicDirector.setText(album.getMusicDirector());
            String albumUrl = album.getThumbNailUrl();
            if (album.getThumbNailUrl() == null || ("null").equals(album.getThumbNailUrl()))
                albumUrl = Constants.DEFAULT_IMAGE_URL;

            Picasso.with(view.getContext()).load(albumUrl).into(albumArtThumbNail);

            if (album.getListOfSongs() != null) {
                for (Song x : album.getListOfSongs()) {
                    x.setAlbumName(album.getAlbumName());
                    x.setAlbumArt(album.getThumbNailUrl());
                }

                Song[] songArray = new Song[album.getListOfSongs().size()];
                album.getListOfSongs().toArray(songArray);
                SongAdaptor adaptor = new SongAdaptor(album.getListOfSongs());
                recyclerView.setLayoutManager(linerLayoutManager);
                recyclerView.setAdapter(adaptor);
                recyclerView.addOnItemTouchListener(songSelectionListener);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
            }
        }

        return view;
    }

    class SongSelectionListener implements RecyclerView.OnItemTouchListener {
        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            if (e.getAction() == MotionEvent.ACTION_DOWN) {
                int selectedPosition = rv.getChildAdapterPosition(rv.findChildViewUnder(e.getX(), e.getY()));
                List<Song> playList = new ArrayList<>();
                for (int i = selectedPosition; i < getAlbum().getListOfSongs().size(); i++) {
                    playList.add(getAlbum().getListOfSongs().get(i));
                }

                Fragment fragment = MusicPlayerFragment.newInstance(playList);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, fragment, Constants.MUSIC_PLAYER_FRAGMENT);
                transaction.setCustomAnimations(R.anim.enter_to_right, R.anim.exit_to_left);
                transaction.addToBackStack(Constants.MUSIC_PLAYER_STACK);
                transaction.commit();
            }
            return true;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }
}
