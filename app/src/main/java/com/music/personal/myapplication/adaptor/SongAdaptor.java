package com.music.personal.myapplication.adaptor;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.music.personal.myapplication.MusicPlayerFragment;
import com.music.personal.myapplication.R;
import com.music.personal.myapplication.pojo.Song;
import com.music.personal.myapplication.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hrajagopal on 9/7/15.
 */
public class SongAdaptor extends RecyclerView.Adapter<SongAdaptor.SongViewHolder> {

    private List<Song> songList;
    private FragmentManager fragmentManager;

    public SongAdaptor(List<Song> songList, FragmentManager fragmentManager) {
        super();
        this.songList = songList;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public void onBindViewHolder(SongViewHolder holder, int position) {
        if (position == 0)
            holder.viewLine.setVisibility(View.INVISIBLE);

        Song x = songList.get(position);
        if (x != null) {
            holder.songName.setText(x.getSongName());
        }
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_list_item, null);
        return new SongViewHolder(view, songList, fragmentManager);
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView songName;
        private View viewLine;
        private List<Song> songList;
        private FragmentManager fragmentManager;

        public SongViewHolder(View itemView, List<Song> songList, FragmentManager fragmentManager) {
            super(itemView);

            this.songList = songList;
            this.fragmentManager = fragmentManager;
            this.songName = (TextView) itemView.findViewById(R.id.songName);
            this.viewLine = itemView.findViewById(R.id.lineView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int selectedPosition = getLayoutPosition();
            List<Song> playList = new ArrayList<>();
            for (int i = selectedPosition; i < songList.size(); i++) {
                playList.add(songList.get(i));
            }

            Fragment fragment = fragmentManager.findFragmentByTag(Constants.MUSIC_PLAYER_FRAGMENT);
            if (fragment == null)
                fragment = MusicPlayerFragment.newInstance();

            ((MusicPlayerFragment) fragment).initializePlayer(playList, 0);
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.container, fragment, Constants.MUSIC_PLAYER_FRAGMENT);
            transaction.setCustomAnimations(R.anim.enter_to_right, R.anim.exit_to_left);
            transaction.addToBackStack(Constants.MUSIC_PLAYER_STACK);
            transaction.commit();
        }
    }
}
