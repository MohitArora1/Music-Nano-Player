package com.mohit.mohit.music;

import android.renderscript.ScriptIntrinsicYuvToRGB;

/**
 * Created by Mohit on 19-03-2017.
 */

public class Song {

    public final String title;
    public final String artist;
    public final String album;
    public final long duration;
    public final String path;
    public final long id;
    public final long albumId;
    public final String thumbUri;

    Song(){
        this.title="";
        this.artist="";
        this.album="";
        this.duration=-1;
        this.path="";
        this.id=-1;
        this.albumId=-1;
        this.thumbUri="";
    }
    Song(String title,String artist,String album,long duration,String path,long id,long albumId){
        this.title=title;
        this.artist=artist;
        this.album=album;
        this.duration=duration;
        this.path=path;
        this.id=id;
        this.albumId=albumId;
        this.thumbUri="";
    }
    Song(String title,String artist,String album,long duration,String path,long id,long albumId,String thumbUri){
        this.title=title;
        this.artist=artist;
        this.album=album;
        this.duration=duration;
        this.path=path;
        this.id=id;
        this.albumId=albumId;
        this.thumbUri=thumbUri;
    }

}
