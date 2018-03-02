package com.mohit.mohit.music;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.util.ArrayList;

/**
 * Created by Mohit on 20-03-2017.
 */

public class AlbumSongsLoader {
    public static ArrayList<Song> getSongsForAlbum(Context context,long albumId){
        Cursor cursor=getCursorForAlbumSongs(context,albumId);
        ArrayList arrayList=new ArrayList();
        if(cursor!=null && cursor.moveToFirst())
            do {
                String title=cursor.getString(0);
                String artist=cursor.getString(1);
                String album=cursor.getString(2);
                long duration=cursor.getLong(3);
                String path=cursor.getString(4);
                long id=cursor.getLong(5);
                long albumID=cursor.getLong(6);
                arrayList.add(new Song(title,artist,album,duration,path,id,albumID));
            }while(cursor.moveToNext());

        return arrayList;
    }

    public static Cursor getCursorForAlbumSongs(Context context,long albumId){
        String string = "is_music=1 AND title != '' AND album_id=" + albumId;
        Cursor cursor=context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,new String []{"title","artist", "album", "duration",MediaStore.Audio.Media.DATA,"_id","album_id"},string,null,MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        return cursor;
    }
}
