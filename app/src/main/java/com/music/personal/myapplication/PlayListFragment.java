package com.music.personal.myapplication;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.music.personal.myapplication.adaptor.PlayListAdaptor;
import com.music.personal.myapplication.listener.SimpleItemTouchHolderCallback;
import com.music.personal.myapplication.pojo.Song;
import com.music.personal.myapplication.utils.Constants;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link PlayListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlayListFragment extends Fragment {

    private List<Song> playList;
    private View view;
    private int currentSongPosition;

    @Bind(R.id.playListView)
    RecyclerView playListView;

    public static PlayListFragment newInstance(List<Song> playList, int currentSongPosition) {
        PlayListFragment fragment = new PlayListFragment();
        fragment.setPlayList(playList);
        fragment.setCurrentSongPosition(currentSongPosition);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public PlayListFragment() {
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
        view = inflater.inflate(R.layout.fragment_play_list, container, false);
        ButterKnife.bind(this, view);

        PlayListAdaptor adaptor = new PlayListAdaptor(playList, view.getContext(), currentSongPosition, getFragmentManager());
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        playListView.setAdapter(adaptor);
        playListView.setLayoutManager(layoutManager);
        playListView.setItemAnimator(new DefaultItemAnimator());
        ItemTouchHelper.Callback callback = new SimpleItemTouchHolderCallback(adaptor);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(playListView);

        return view;
    }

    public List<Song> getPlayList() {
        return playList;
    }

    public void setPlayList(List<Song> playList) {
        this.playList = playList;
    }

    public int getCurrentSongPosition() {
        return currentSongPosition;
    }

    public void setCurrentSongPosition(int currentSongPosition) {
        this.currentSongPosition = currentSongPosition;
    }
}
