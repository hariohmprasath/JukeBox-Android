package com.music.personal.myapplication.adaptor;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.music.personal.myapplication.MusicPlayerFragment;
import com.music.personal.myapplication.R;
import com.music.personal.myapplication.adaptor.interfaces.ItemTouchHelperAdapter;
import com.music.personal.myapplication.pojo.Song;
import com.music.personal.myapplication.utils.Constants;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

/**
 * Created by hrajagopal on 9/7/15.
 */
public class PlayListAdaptor extends RecyclerView.Adapter<PlayListAdaptor.SongViewHolder> implements ItemTouchHelperAdapter {

    private List<Song> songList;
    private Context context;
    private int currentSongPosition;
    private FragmentManager fragmentManager;

    public PlayListAdaptor(List<Song> songList, Context context,
                           int currentSongPosition, FragmentManager fragmentManager) {
        super();
        this.songList = songList;
        this.context = context;
        this.currentSongPosition = currentSongPosition;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public void onBindViewHolder(SongViewHolder holder, int position) {
        if (position == 0)
            holder.viewLine.setVisibility(View.INVISIBLE);

        Song x = songList.get(position);
        if (x != null) {
            holder.songName.setText(x.getSongName());
            holder.albumName.setText(x.getAlbumName());
            String imageSource = x.getAlbumArt();
            if (imageSource == null)
                imageSource = Constants.DEFAULT_IMAGE_URL;

            Picasso.with(context).load(imageSource).into(holder.thumbNail);
            if (position == currentSongPosition - 1) {
                holder.songName.setTypeface(null, Typeface.BOLD);
                holder.songName.setPaintFlags(holder.songName.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            }
        }
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_item, null);
        return new SongViewHolder(view, fragmentManager);
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView songName;
        private TextView albumName;
        private ImageView thumbNail;
        private View viewLine;
        private FragmentManager fragmentManager;

        public SongViewHolder(View itemView, FragmentManager fragmentManager) {
            super(itemView);

            this.fragmentManager = fragmentManager;
            this.songName = (TextView) itemView.findViewById(R.id.songName);
            this.albumName = (TextView) itemView.findViewById(R.id.albumName);
            this.thumbNail = (ImageView) itemView.findViewById(R.id.albumArt);
            this.viewLine = itemView.findViewById(R.id.lineView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int selectedPosition = getLayoutPosition();
            FragmentManager fragmentManager = this.fragmentManager;
            Fragment fragment = fragmentManager.findFragmentByTag(Constants.MUSIC_PLAYER_FRAGMENT);
            if (fragment != null) {
                ((MusicPlayerFragment) fragment).initializePlayer(selectedPosition);
                fragmentManager.popBackStack();
            }
        }
    }

    @Override
    public void onItemDismiss(int position) {
        songList.remove(position);
        notifyItemRemoved(position);
        syncPlayList();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(songList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(songList, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        syncPlayList();
    }

    private void syncPlayList() {
        Fragment fragment = fragmentManager.findFragmentByTag(Constants.MUSIC_PLAYER_FRAGMENT);
        if (fragment != null)
            ((MusicPlayerFragment) fragment).syncPlaylist(songList);
    }
}
