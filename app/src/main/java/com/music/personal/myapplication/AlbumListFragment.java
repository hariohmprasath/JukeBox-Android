package com.music.personal.myapplication;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.music.personal.myapplication.adaptor.AlbumAdaptor;
import com.music.personal.myapplication.persistance.CacheAlbumHelper;
import com.music.personal.myapplication.persistance.impl.CacheAlbumHelperImpl;
import com.music.personal.myapplication.pojo.Album;
import com.music.personal.myapplication.utils.Constants;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * <p/>
 * to handle interaction events.
 * Use the {@link AlbumListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlbumListFragment extends Fragment {
    private List<Album> listOfAlbum;
    private List<Album> completeAlbumList;
    private List<Album> offlineList;
    private View view;
    private CacheAlbumHelper albumHelper;
    private CacheAlbumHelperImpl albumService;
    private boolean isPlaylist;

    @Bind(R.id.albumList)
    RecyclerView albumListView;

    @Bind(R.id.searchText)
    EditText searchText;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AlbumListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AlbumListFragment newInstance(boolean isPlaylist) {
        AlbumListFragment fragment = new AlbumListFragment();
        fragment.isPlaylist = isPlaylist;
        return fragment;
    }

    public AlbumListFragment() {
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
        view = inflater.inflate(R.layout.fragment_album_list, container, false);
        this.albumHelper = new CacheAlbumHelper(view.getContext());
        this.albumService = new CacheAlbumHelperImpl();
        ButterKnife.bind(this, view);

        this.offlineList = this.albumService.getAllRecords(this.albumHelper);
        LinearLayoutManager linerLayoutManager = new LinearLayoutManager(view.getContext());
        OkHttpClient client = new OkHttpClient();
        listOfAlbum = new ArrayList<>();
        albumListView.setLayoutManager(linerLayoutManager);
        Request request = new Request.Builder()
                .url(Constants.URL + "/"+String.valueOf(isPlaylist))
                .build();

        try {
            Callback albumCallBack = new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    Log.e("MainActivity", e.getMessage());
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    try {

                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONArray albumArray = jsonObject.getJSONArray("albumJSON");
                        for (int i = 0; i < albumArray.length(); i++) {
                            JSONObject x = albumArray.getJSONObject(i);

                            Album target = new Album();
                            target.setAlbumName(x.getString("albumName"));
                            Object music = x.get("music");
                            if (music != null && !("null").equals(music))
                                target.setMusicDirector(music.toString());

                            Object thumbNail = x.get("thumbNail");
                            if (thumbNail != null && !("null").equals(thumbNail))
                                target.setThumbNailUrl(thumbNail.toString());
                            else
                                target.setThumbNailUrl(Constants.DEFAULT_IMAGE_URL);

                            listOfAlbum.add(target);
                        }
                        completeAlbumList = new ArrayList<>(listOfAlbum);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AlbumAdaptor dataAdaptor = new AlbumAdaptor(listOfAlbum, view.getContext(),
                                        getFragmentManager());
                                albumListView.setAdapter(dataAdaptor);
                                albumListView.setItemAnimator(new DefaultItemAnimator());

                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            client.newCall(request).enqueue(albumCallBack);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    @OnTextChanged(R.id.searchText)
    public void onSearchTxtChanged(CharSequence searchTerm) {
        try {
            listOfAlbum = new ArrayList<>(completeAlbumList);
            if (searchTerm != null && searchTerm.toString().trim().length() > 0) {
                Iterator<Album> itr = listOfAlbum.iterator();
                while (itr.hasNext()) {
                    Album x = itr.next();
                    String original = getSearchableData(x.getAlbumName());
                    String userInput = getSearchableData(searchTerm.toString());
                    if (!original.startsWith(userInput))
                        itr.remove();
                }
            }

            AlbumAdaptor dataAdaptor = new AlbumAdaptor(listOfAlbum, view.getContext(),
                    getFragmentManager());
            albumListView.setAdapter(dataAdaptor);
            albumListView.setItemAnimator(new DefaultItemAnimator());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String getSearchableData(String data) {
        if (data != null) {
            data = data.replaceAll(Pattern.quote(" "), "");
            data = data.toLowerCase();

            return data.trim();
        }

        return null;
    }
}
