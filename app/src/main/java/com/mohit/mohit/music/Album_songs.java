package com.mohit.mohit.music;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;


import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class Album_songs extends AppCompatActivity {
    Intent intent;
    String albumName;
    long albumId;
    String albumart;
    ArrayList arrayList;
    ImageView image;
    RecyclerView recyclerView;
    int pos;
    SharedPreferences sharedPref;
    Intent serviceIntent;
    String currentFile;
    Intent newAlbumSong;
    public static String SONG="com.mohit,mohit.music.action.song";
    SharedPreferences sharedPreferences;
    public static final String NEW_ALBUM_SONG="com.example.mohit.music.newalbumsong";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences=getSharedPreferences("theme",Context.MODE_PRIVATE);
        String theme=sharedPreferences.getString("theme","");
        switch(theme){
            case "green":
                setTheme(R.style.AppThemeGreen);
                break;
            case "black":
                setTheme(R.style.AppThemeBlack);
                break;
            case "light_green":
                setTheme(R.style.AppThemeLightGreen);
                break;
            case "light_red":
                setTheme(R.style.AppThemeLightRed);
                break;
            case "wood":
                setTheme(R.style.AppThemeWood);
                break;
            case "sky_blue":
                setTheme(R.style.AppThemeskyblue);
                break;
            case "red":
                setTheme(R.style.AppThemeRed);
                break;
            default:
                setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_songs);
        newAlbumSong=new Intent(NEW_ALBUM_SONG);
        intent=getIntent();
        albumName=intent.getStringExtra("albumName");
        albumId=intent.getLongExtra("albumId",-1);
        image=(ImageView)findViewById(R.id.album_image);
        Uri ablubArtUri=Uri.parse("content://media/external/audio/albumart");
        Uri uri= ContentUris.withAppendedId(ablubArtUri,albumId);
        Glide.with(this)
                .load(uri)
                .error(R.drawable.mohit)
                .into(image);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.MyToolbar);
        setSupportActionBar(toolbar);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapse_toolbar);
        collapsingToolbar.setTitle(albumName);
        collapsingToolbar.setExpandedTitleColor(Color.WHITE);
        collapsingToolbar.setCollapsedTitleTextColor(Color.WHITE);

        TypedValue typedValue = new TypedValue();

        TypedArray a = obtainStyledAttributes(typedValue.data, new int[] { R.attr.colorPrimary });
        int color = a.getColor(0, 0);
        a.recycle();
        collapsingToolbar.setContentScrimColor(color);

        collectdata(this);
        try{
            serviceIntent=new Intent(this,MusicService.class);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


    }

    public void collectdata(Context context){
        recyclerView=(RecyclerView)findViewById(R.id.album_song_recycler);
        arrayList=AlbumSongsLoader.getSongsForAlbum(context,albumId);
        AlbumSongAdapter albumSongAdapter=new AlbumSongAdapter(context,arrayList);
        recyclerView.setAdapter(albumSongAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        recyclerView.addOnItemTouchListener(new RecyclerItemTouch(this,recyclerView, new RecyclerItemTouch.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Song localItem=(Song) arrayList.get(position);
                pos=position;
                currentFile=localItem.path;
                sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt("pos",pos);
                editor.putString("list","album");
                editor.putLong("albumId",albumId);
                editor.commit();

                serviceIntent.putExtra("songName", currentFile);
                try {
                    serviceIntent.setAction(SONG);
                    startService(serviceIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                sendBroadcast(newAlbumSong);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }){

        });

    }
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_fade_out, R.anim.stay);
    }



}
