package com.music.personal.myapplication;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.music.personal.myapplication.adaptor.SongAdaptor;
import com.music.personal.myapplication.persistance.CacheSongHelper;
import com.music.personal.myapplication.persistance.impl.CacheSongHelperImpl;
import com.music.personal.myapplication.pojo.Album;
import com.music.personal.myapplication.pojo.Song;
import com.music.personal.myapplication.utils.Constants;
import com.music.personal.myapplication.utils.OfflineTask;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;


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
    private CacheSongHelper helper;
    private CacheSongHelperImpl service;

    @Bind(R.id.albumName)
    TextView albumName;

    @Bind(R.id.musicDirector)
    TextView musicDirector;

    @Bind(R.id.albumArtThumbNail)
    ImageView albumArtThumbNail;

    @Bind(R.id.songView)
    RecyclerView recyclerView;

    Switch offlineSwitch;

    public static AlbumShowFragment newInstance(Album album) {
        AlbumShowFragment fragment = new AlbumShowFragment();
        fragment.setAlbum(album);
        Bundle args = new Bundle();
        args.putParcelable("album", album);
        fragment.setArguments(args);
        return fragment;
    }

    public AlbumShowFragment() {
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
        ButterKnife.bind(this, view);
        this.helper = new CacheSongHelper(view.getContext());
        this.service = new CacheSongHelperImpl();
        this.offlineSwitch = (Switch) view.findViewById(R.id.offlineSwitch);

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

                List<Song> targetSongs = album.getListOfSongs();

                // Get whether available in cache
                List<Song> cacheSongs = service.getAllRecordsForAlbum(helper, album.getAlbumName());
                if (cacheSongs != null && cacheSongs.size() > 0) {
                    targetSongs = cacheSongs;
                    offlineSwitch.setChecked(true);
                }

                Song[] songArray = new Song[targetSongs.size()];
                targetSongs.toArray(songArray);

                SongAdaptor adaptor = new SongAdaptor(targetSongs, getFragmentManager());
                recyclerView.setLayoutManager(linerLayoutManager);
                recyclerView.setAdapter(adaptor);
                recyclerView.setItemAnimator(new DefaultItemAnimator());

                // Set listener
                offlineSwitch.setOnCheckedChangeListener(new OfflineSwitchListener(targetSongs));
            }
        }

        return view;
    }

    class OfflineSwitchListener implements CompoundButton.OnCheckedChangeListener {
        private List<Song> listOfSongs;

        public OfflineSwitchListener(List<Song> listOfSongs) {
            this.listOfSongs = listOfSongs;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Song[] params = new Song[listOfSongs.size()];
            listOfSongs.toArray(params);
            OfflineTask offlineTask = null;

            if (isChecked)
                offlineTask = new OfflineTask(view.getContext(), true);
            else
                offlineTask = new OfflineTask(view.getContext(), false);

            offlineTask.execute(params);
        }
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }
}
