package com.music.personal.myapplication.listener;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.music.personal.myapplication.AlbumShowFragment;
import com.music.personal.myapplication.R;
import com.music.personal.myapplication.pojo.Album;
import com.music.personal.myapplication.pojo.Song;
import com.music.personal.myapplication.utils.Constants;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hrajagopal on 9/8/15.
 */
public class AlbumSelectionListener implements RecyclerView.OnItemTouchListener {

    public static final String ALBUM_INFO = "ALBUM_INFO";
    private List<Album> listOfAlbum = null;
    private OkHttpClient client;
    private FragmentManager fragmentManager;

    public AlbumSelectionListener(List<Album> listOfAlbum, FragmentManager fragmentManager) {
        super();
        client = new OkHttpClient();
        this.listOfAlbum = listOfAlbum;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public boolean onInterceptTouchEvent(final RecyclerView rv, MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            int selectedPosition = rv.getChildAdapterPosition(rv.findChildViewUnder(e.getX(), e.getY()));
            Log.v("ALBUM_INFO", "Item selected " + selectedPosition);

            Album targetObject = listOfAlbum.get(selectedPosition);

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

        return true;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    public List<Album> getListOfAlbum() {
        return listOfAlbum;
    }

    public void setListOfAlbum(List<Album> listOfAlbum) {
        this.listOfAlbum = listOfAlbum;
    }
}
