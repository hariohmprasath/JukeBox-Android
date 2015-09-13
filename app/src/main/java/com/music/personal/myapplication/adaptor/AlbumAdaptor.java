package com.music.personal.myapplication.adaptor;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.music.personal.myapplication.AlbumShowFragment;
import com.music.personal.myapplication.R;
import com.music.personal.myapplication.pojo.Album;
import com.music.personal.myapplication.pojo.Song;
import com.music.personal.myapplication.utils.Constants;
import com.music.personal.myapplication.utils.Utils;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hrajagopal on 9/5/15.
 */
public class AlbumAdaptor extends RecyclerView.Adapter<AlbumAdaptor.AlbumViewHolder> {

    public static final String ALBUM_INFO = "ALBUM_INFO";

    private List<Album> albumList;
    private Context context;
    private FragmentManager fragmentManager;

    public AlbumAdaptor(List<Album> albumList, Context context,
                        FragmentManager fragmentManager) {
        this.albumList = albumList;
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public AlbumViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View albumItemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.album_list_item, null);
        return new AlbumViewHolder(albumItemView, albumList, fragmentManager);
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

    public static class AlbumViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView albumArt;
        public TextView albumName;
        public TextView musicDirector;
        private List<Album> albumList;
        private FragmentManager fragmentManager;

        public AlbumViewHolder(View itemView, List<Album> albumList, FragmentManager fragmentManager) {
            super(itemView);
            this.albumList = albumList;
            this.fragmentManager = fragmentManager;
            itemView.setOnClickListener(this);

            this.albumArt = (ImageView) itemView.findViewById(R.id.albumArt);
            this.albumName = (TextView) itemView.findViewById(R.id.albumName);
            this.musicDirector = (TextView) itemView.findViewById(R.id.musicDirector);
        }

        @Override
        public void onClick(final View v) {
            OkHttpClient client = new OkHttpClient();
            int selectedPosition = getLayoutPosition();
            Log.v("ALBUM_INFO", "Item selected " + selectedPosition);

            Album targetObject = albumList.get(selectedPosition);

            Request request = new Request.Builder()
                    .url(Constants.ALBUM_INFO_URL + "/" + targetObject.getAlbumName())
                    .build();

            try {
                Callback albumCallBack = new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        Log.e(ALBUM_INFO, e.getMessage());
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        try {
                            String data = response.body().string();
                            JSONObject object = new JSONObject(data);
                            String albumName = object.getString("albumName");
                            Object songNameObject = object.get("songNameList");

                            Album album = new Album();
                            album.setAlbumName(albumName);

                            if (!object.isNull("music"))
                                album.setMusicDirector(object.getString("music"));

                            if (!object.isNull("thumbNail"))
                                album.setThumbNailUrl(object.getString("thumbNail"));

                            if (songNameObject != null) {
                                List<Song> listOfSongs = new ArrayList<>();
                                JSONArray songsArray = object.getJSONArray("songNameList");
                                JSONArray songUrlArray = object.getJSONArray("songUrlList");

                                for (int i = 0; i < songsArray.length(); i++) {
                                    Song x = new Song();
                                    x.setSongName(songsArray.get(i).toString());
                                    x.setSongUrl(songUrlArray.get(i).toString());
                                    listOfSongs.add(x);
                                }

                                album.setListOfSongs(listOfSongs);
                            }


                            // Hide the keyboard once search is complete
                            InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                            AlbumShowFragment albumShowFragment = AlbumShowFragment.newInstance(album);
                            FragmentTransaction transaction = fragmentManager.beginTransaction();
                            transaction.setCustomAnimations(R.anim.enter_to_right, R.anim.exit_to_left);
                            transaction.replace(R.id.container, albumShowFragment, Constants.TAG_FRAGMENT);
                            transaction.addToBackStack(Constants.ALBUM_SHOW_STACK);
                            transaction.commit();
                        } catch (Exception e) {
                            Log.e(ALBUM_INFO, e.getMessage());
                        }

                    }
                };

                client.newCall(request).enqueue(albumCallBack);
            } catch (Exception requestError) {
                Log.e(ALBUM_INFO, requestError.getMessage());
            }
        }
    }
}
