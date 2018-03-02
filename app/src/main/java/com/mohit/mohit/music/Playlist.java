package com.mohit.mohit.music;

/**
 * Created by Mohit on 28-03-2017.
 */

public class Playlist {
    public final long id;
    public final String name;

    Playlist(){
        this.id=-1;
        this.name="";
    }
    Playlist(long id,String name){
        this.id=id;
        this.name=name;
    }
}
