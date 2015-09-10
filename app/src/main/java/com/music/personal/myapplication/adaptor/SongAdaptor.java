package com.music.personal.myapplication.adaptor;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.music.personal.myapplication.R;
import com.music.personal.myapplication.pojo.Song;

import java.util.List;

/**
 * Created by hrajagopal on 9/7/15.
 */
public class SongAdaptor extends RecyclerView.Adapter<SongAdaptor.SongViewHolder> {

    private List<Song> songList;

    public SongAdaptor(List<Song> songList) {
        super();
        this.songList = songList;
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
        return new SongViewHolder(view);
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder {
        private TextView songName;
        private View viewLine;

        public SongViewHolder(View itemView) {
            super(itemView);

            this.songName = (TextView) itemView.findViewById(R.id.songName);
            this.viewLine = itemView.findViewById(R.id.lineView);

        }
    }
}
