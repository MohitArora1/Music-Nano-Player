package com.mohit.mohit.music;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.util.ArrayList;

/**
 * Created by Mohit on 22-03-2017.
 */

public class RecentlyAddedLoader {
    public static ArrayList<Song> getRecentlyAddedSongs(Context context){

        Cursor cursor=getCursorForRecentlyAdded(context);
        ArrayList arrayList=new ArrayList();
        if(cursor!=null && cursor.moveToFirst())
            do{
                String title=cursor.getString(0);
                String artist=cursor.getString(1);
                String album=cursor.getString(2);
                long duration=cursor.getLong(3);
                String path=cursor.getString(4);
                long id=cursor.getLong(5);
                long albumId=cursor.getLong(6);
                arrayList.add(new Song(title,artist,album,duration,path,id,albumId));

            }while(cursor.moveToNext());
        if(cursor!=null){
            cursor.close();
        }


        return arrayList;
    }
    private static Cursor getCursorForRecentlyAdded(Context context){
        long fourWeeksAgo = (System.currentTimeMillis() / 1000) - (4 * 3600 * 24 * 7);
        final StringBuilder selection = new StringBuilder();
        selection.append(MediaStore.Audio.AudioColumns.IS_MUSIC + "=1");
        selection.append(" AND " + MediaStore.Audio.AudioColumns.TITLE + " != ''");
        selection.append(" AND " + MediaStore.Audio.Media.DATE_ADDED + ">");
        selection.append(fourWeeksAgo);

        Cursor cursor=context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,new String[]{"title","artist", "album", "duration",MediaStore.Audio.Media.DATA,"_id","album_id"},selection.toString(),null,MediaStore.Audio.Media.DATE_ADDED + " DESC");

        return cursor;
    }
}
