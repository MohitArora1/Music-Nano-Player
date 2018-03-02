package com.mohit.mohit.music;

/**
 * Created by Mohit on 19-03-2017.
 */

public class Album {
    public final String album;
    public final String artist;
    public final long albumId;
    public final String albumart;
    Album(){
        this.album="";
        this.artist="";
        this.albumId=-1;
        this.albumart="";

    }
    Album(String album,String artist,long albumId,String albumart){
        this.album=album;
        this.artist=artist;
        this.albumId=albumId;
        this.albumart=albumart;
    }
}
