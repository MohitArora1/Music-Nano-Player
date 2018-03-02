package com.mohit.mohit.music;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.audiofx.AudioEffect;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    SearchView searchView;
    MediaCursorAdapter mediaCursorAdapter;
    SharedPreferences sharedPreferences;
    public static String SONG="com.mohit,mohit.music.action.song";
    SharedPreferences sharedPref;
    Intent serviceIntent;
    ArrayList arrayList=new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sharedPreferences=getSharedPreferences("theme", Context.MODE_PRIVATE);
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
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        try{
            serviceIntent=new Intent(this,MusicService.class);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.expandActionView();
        MenuItemCompat.setOnActionExpandListener(menu.findItem(R.id.action_search), new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                finish();
                return false;
            }
        });


        if(searchItem!=null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if(searchView!=null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(this.getComponentName()));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    arrayList.clear();
                    setSearchResult(newText);
                    mediaCursorAdapter.notifyDataSetChanged();
                    return false;
                }
            });
            searchView.setQueryHint(getString(R.string.search));
            searchView.setIconifiedByDefault(false);
            searchView.setIconified(false);
        }
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setSearchResult(String query){
        if(query!=null) {
            arrayList = SongsLoader.getSearchSong(this, query);
            mediaCursorAdapter = new MediaCursorAdapter(this, arrayList);
            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.search_recycler);
            recyclerView.setAdapter(mediaCursorAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            recyclerView.addOnItemTouchListener(new RecyclerItemTouch(this, recyclerView, new RecyclerItemTouch.OnItemClickListener(){

                @Override
                public void onItemClick(View view, int position) {
                    Song song=(Song)arrayList.get(position);
                    String songName=song.path;

                    sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("list","search");
                    editor.putString("searchSong",song.title);
                    editor.putString("searchArtist",song.artist);
                    editor.putLong("searchId",song.albumId);
                    editor.commit();

                    serviceIntent.putExtra("songName", songName);
                    serviceIntent.setAction(SONG);
                    try {
                        startService(serviceIntent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onLongItemClick(View view, int position) {

                }
            }));
        }
    }

}
