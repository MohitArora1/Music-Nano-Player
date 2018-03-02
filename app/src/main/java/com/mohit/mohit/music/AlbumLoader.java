package com.mohit.mohit.music;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.util.ArrayList;

/**
 * Created by Mohit on 19-03-2017.
 */

public class AlbumLoader {
    public static ArrayList<Album> getAlbums(Context context){


        Cursor cursor=getCursorForAlbum(context);
        ArrayList arrayList=new ArrayList();
        if(cursor!=null && cursor.moveToFirst())
            do {
                String albumname=cursor.getString(0);
                String artist=cursor.getString(1);
                long albumId=cursor.getLong(2);
                String albumart=cursor.getString(3);
                arrayList.add(new Album(albumname,artist,albumId,albumart));
            }while(cursor.moveToNext());
        return arrayList;
    }
    public static Cursor getCursorForAlbum(Context context){

        Cursor cursor=context.getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,new String[]{"album","artist","_id", MediaStore.Audio.Albums.ALBUM_ART},null,null,MediaStore.Audio.Albums.DEFAULT_SORT_ORDER);
        return cursor;

    }

}
