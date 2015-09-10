package com.music.personal.myapplication.adaptor;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.music.personal.myapplication.R;
import com.music.personal.myapplication.pojo.Album;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by hrajagopal on 9/5/15.
 */
public class AlbumAdaptor extends RecyclerView.Adapter<AlbumAdaptor.AlbumViewHolder> {

    private List<Album> albumList;
    private Context context;

    public AlbumAdaptor(List<Album> albumList, Context context) {
        this.albumList = albumList;
        this.context = context;
    }

    @Override
    public AlbumViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View albumItemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.album_list_item, null);
        return new AlbumViewHolder(albumItemView);
    }

    @Override
    public void onBindViewHolder(AlbumViewHolder albumViewHolder, int i) {
        Album x = albumList.get(i);
        if (x != null) {
            albumViewHolder.albumName.setText(x.getAlbumName());
            albumViewHolder.musicDirector.setText(x.getMusicDirector());
            Picasso.with(this.context).load(x.getThumbNailUrl()).into(albumViewHolder.albumArt);
        }
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public static class AlbumViewHolder extends RecyclerView.ViewHolder {
        public ImageView albumArt;
        public TextView albumName;
        public TextView musicDirector;

        public AlbumViewHolder(View itemView) {
            super(itemView);

            this.albumArt = (ImageView) itemView.findViewById(R.id.albumArt);
            this.albumName = (TextView) itemView.findViewById(R.id.albumName);
            this.musicDirector = (TextView) itemView.findViewById(R.id.musicDirector);
        }
    }
}
