package com.music.personal.myapplication.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by hrajagopal on 9/5/15.
 */
public class Album implements Parcelable {
    private String albumName;
    private String musicDirector;
    private String thumbNailUrl;

    private List<Song> listOfSongs;

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getMusicDirector() {
        return musicDirector;
    }

    public void setMusicDirector(String musicDirector) {
        this.musicDirector = musicDirector;
    }

    public String getThumbNailUrl() {
        return thumbNailUrl;
    }

    public void setThumbNailUrl(String thumbNailUrl) {
        this.thumbNailUrl = thumbNailUrl;
    }

    public List<Song> getListOfSongs() {
        return listOfSongs;
    }

    public void setListOfSongs(List<Song> listOfSongs) {
        this.listOfSongs = listOfSongs;
    }

    @Override
    public int hashCode() {
        return albumName.hashCode();
    }

    @Override
    public String toString() {
        return albumName;
    }

    @Override
    public boolean equals(Object o) {
        return albumName.equals(((Album) o).getAlbumName());
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.albumName);
        dest.writeString(this.musicDirector);
        dest.writeString(this.thumbNailUrl);
        dest.writeTypedList(listOfSongs);
    }

    public Album() {
    }

    protected Album(Parcel in) {
        this.albumName = in.readString();
        this.musicDirector = in.readString();
        this.thumbNailUrl = in.readString();
        this.listOfSongs = in.createTypedArrayList(Song.CREATOR);
    }

    public static final Creator<Album> CREATOR = new Creator<Album>() {
        public Album createFromParcel(Parcel source) {
            return new Album(source);
        }

        public Album[] newArray(int size) {
            return new Album[size];
        }
    };
}
