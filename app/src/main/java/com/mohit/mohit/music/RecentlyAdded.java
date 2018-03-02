package com.mohit.mohit.music;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;


import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class RecentlyAdded extends AppCompatActivity {
ArrayList arrayList;
    RecyclerView recyclerView;
    ImageView image;
    int pos=0;
    String currentFile;
    SharedPreferences sharedPref;
    Intent serviceIntent;
    SharedPreferences sharedPreferences;
    Intent newfromRecentlyAdded;
    public static String SONG="com.mohit,mohit.music.action.song";
    public static final String NEW__FROM_RECENTLY_ADDED_SONG="com.example.mohit.music.newrecetlyaddedsong";

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
        setContentView(R.layout.activity_recently_added);
        image=(ImageView)findViewById(R.id.recent_image);
        newfromRecentlyAdded=new Intent(NEW__FROM_RECENTLY_ADDED_SONG);

            collectData(this);
            try{
                serviceIntent=new Intent(this,MusicService.class);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

    }


    public void collectData(Context context){
        arrayList=RecentlyAddedLoader.getRecentlyAddedSongs(context);
        RecentlyAddedAdapter recentlyAddedAdapter=new RecentlyAddedAdapter(context,arrayList);
        recyclerView=(RecyclerView)findViewById(R.id.recent_recycler);
        recyclerView.setAdapter(recentlyAddedAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        recyclerView.addOnItemTouchListener(
                new RecyclerItemTouch(this, recyclerView, new RecyclerItemTouch.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // do whatever
                        Song localItem=(Song)arrayList.get(position);
                        long ablubArt=localItem.albumId;
                        Uri ablubArtUri=Uri.parse("content://media/external/audio/albumart");
                        Uri uri= ContentUris.withAppendedId(ablubArtUri,ablubArt);
                        Glide.with(getApplication())
                                .load(uri)
                                .into(image);
                        pos=position;
                        currentFile=localItem.path;
                        sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putInt("pos",pos);
                        editor.putString("list","recentlyadded");
                        editor.commit();

                        serviceIntent.putExtra("songName", currentFile);
                        try {
                            serviceIntent.setAction(SONG);
                            startService(serviceIntent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        sendBroadcast(newfromRecentlyAdded);

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );

    }



}
