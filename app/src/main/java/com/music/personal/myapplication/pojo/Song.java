package com.music.personal.myapplication.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hrajagopal on 9/7/15.
 */
public class Song implements Parcelable {
    private String songName;
    private String songUrl;
    private String albumArt;
    private String albumName;
    private String cacheLocation;
    private String cachedOn;

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSongUrl() {
        return songUrl;
    }

    public void setSongUrl(String songUrl) {
        this.songUrl = songUrl;
    }

    public String getAlbumArt() {
        return albumArt;
    }

    public void setAlbumArt(String albumArt) {
        this.albumArt = albumArt;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.songName);
        dest.writeString(this.songUrl);
        dest.writeString(this.albumArt);
        dest.writeString(this.albumName);
    }

    public Song() {
    }

    protected Song(Parcel in) {
        this.songName = in.readString();
        this.songUrl = in.readString();
        this.albumArt = in.readString();
        this.albumName = in.readString();
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        public Song createFromParcel(Parcel source) {
            return new Song(source);
        }

        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    @Override
    public String toString() {
        return this.getAlbumName()+"--"+this.getSongName();
    }

    public String getCacheLocation() {
        return cacheLocation;
    }

    public void setCacheLocation(String cacheLocation) {
        this.cacheLocation = cacheLocation;
    }

    public String getCachedOn() {
        return cachedOn;
    }

    public void setCachedOn(String cachedOn) {
        this.cachedOn = cachedOn;
    }
}
