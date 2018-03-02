package com.mohit.mohit.music;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.util.ArrayList;



public class SongsLoader {
    public static ArrayList<Song> getSongs(Context context){
        Cursor cursor=makeSongCursor(context);
        ArrayList arraylist=new ArrayList();
        if(cursor!=null && cursor.moveToFirst())
            do{
                String title=cursor.getString(0);
                String artist=cursor.getString(1);
                String album=cursor.getString(2);
                long duration=cursor.getLong(3);
                String path=cursor.getString(4);
                long id=cursor.getLong(5);
                long albumId=cursor.getLong(6);
                String thumbUri = cursor.getString( cursor.getColumnIndex( MediaStore.Images.Thumbnails.DATA ) );
                arraylist.add(new Song(title,artist,album,duration,path,id,albumId,thumbUri));
            }while(cursor.moveToNext());
        if(cursor!=null){
            cursor.close();
        }



        return arraylist;
    }

    public static ArrayList<Song> getSearchSong(Context context,String query){
        Cursor cursor=makeSearchCursor(context,query);
        Cursor cursor1=makeSearchCursor1(context ,query);
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
                String thumbUri = cursor.getString( cursor.getColumnIndex( MediaStore.Images.Thumbnails.DATA ) );
                arrayList.add(new Song(title,artist,album,duration,path,id,albumId,thumbUri));
            }while(cursor.moveToNext());
        if(cursor!=null){
            cursor.close();
        }

        if(cursor1!=null && cursor1.moveToFirst())
            do{
                String title=cursor1.getString(0);
                String artist=cursor1.getString(1);
                String album=cursor1.getString(2);
                long duration=cursor1.getLong(3);
                String path=cursor1.getString(4);
                long id=cursor1.getLong(5);
                long albumId=cursor1.getLong(6);
                String thumbUri = cursor1.getString( cursor1.getColumnIndex( MediaStore.Images.Thumbnails.DATA ) );
                arrayList.add(new Song(title,artist,album,duration,path,id,albumId,thumbUri));
            }while(cursor1.moveToNext());
        if(cursor1!=null){
            cursor1.close();
        }

        return arrayList;
    }

    public static Cursor makeSearchCursor1(Context context,String query){

        String Selection="is_music=1 AND title != ''" + " AND title LIKE ?";

        String[] Selection1=new String[]{"%_" + query + "%"};
        return context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,new String[]{"title","artist", "album", "duration",MediaStore.Audio.Media.DATA,"_id","album_id"},Selection,Selection1, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
    }

    public static Cursor makeSearchCursor(Context context,String query){

        String Selection="is_music=1 AND title != ''" + " AND title LIKE ?";

        String[] Selection1=new String[]{query+"%"};
        return context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,new String[]{"title","artist", "album", "duration",MediaStore.Audio.Media.DATA,"_id","album_id"},Selection,Selection1, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
    }

    public static Cursor makeSongCursor(Context context){
        String selectionStatement = "is_music=1 AND title != ''";
       return context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,new String[]{"title","artist", "album", "duration",MediaStore.Audio.Media.DATA,"_id","album_id"},selectionStatement,null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
    }

}
