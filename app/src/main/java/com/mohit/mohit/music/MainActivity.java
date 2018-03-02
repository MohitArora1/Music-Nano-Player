package com.mohit.mohit.music;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.audiofx.AudioEffect;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Toolbar toolbar;
    ViewPager viewPager;
    TabLayout tabLayout;
    Dialog dialog;
    SharedPreferences sharedPreferences;
    SharedPreferences sharedPref;
    String list;
    String currentFile;
    boolean isPlaying;
    ArrayList arrayList;
    ArrayList albumSongList;
    ArrayList recentlyArray;
    TextView songName;
    TextView artistName;
    ImageView imageView;

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
        setContentView(R.layout.activity_main);




        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        viewPager = (ViewPager) findViewById(R.id.tabanim_viewpager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabanim_tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                switch (tab.getPosition()) {
                    case 0:

                        break;
                    case 1:

                        break;
                    case 2:

                        break;
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);

            imageView=(ImageView) header.findViewById(R.id.imageView);
            songName=(TextView) header.findViewById(R.id.nav_song_name);
            artistName=(TextView)header.findViewById(R.id.nav_artist_name);
            arrayList=SongsLoader.getSongs(this);
            recentlyArray=RecentlyAddedLoader.getRecentlyAddedSongs(this);
            setHeader();


        registerReceiver(updateNavHeaderBroadcast,new IntentFilter(MusicService.BROADCAST_UPDATE_NAV_HEADER));


    }


    BroadcastReceiver updateNavHeaderBroadcast=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setHeader();
        }
    };

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new SongsFragment(),"Songs");
        adapter.addFrag(new AlbumFragment(),"Albums");
        adapter.addFrag(new PlaylistFragment(),"Playlists");
        viewPager.setAdapter(adapter);
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);



        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.Equalizer) {
            Intent intent=new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
            intent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, MusicService.playingId());
            if((intent.resolveActivity(getPackageManager())!=null)){
                startActivityForResult(intent,0);
            }else{
                Toast.makeText(this,"Equalizer not found", Toast.LENGTH_LONG).show();

            }

            return true;
        }
        if(id==R.id.action_search){
            startActivity(new Intent(this,SearchActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.


        int id = item.getItemId();

        if (id == R.id.nav_now_playing) {

            startActivity(new Intent(this,Main2Activity.class));
        } else if (id == R.id.nav_library) {

        } else if (id == R.id.nav_theme) {
            dialog=new Dialog(this);
            dialog.setContentView(R.layout.theme);
            ImageButton blue,lightred,lightgreen,wood,black,skyblue,green,red;
            blue=(ImageButton)dialog.findViewById(R.id.default_activity_button);
            blue.setOnClickListener(onbutton);
            lightgreen=(ImageButton)dialog.findViewById(R.id.light_green);
            lightgreen.setOnClickListener(onbutton);
            lightred=(ImageButton)dialog.findViewById(R.id.light_red);
            lightred.setOnClickListener(onbutton);
            wood=(ImageButton)dialog.findViewById(R.id.wood);
            wood.setOnClickListener(onbutton);
            black=(ImageButton)dialog.findViewById(R.id.black);
            black.setOnClickListener(onbutton);
            skyblue=(ImageButton)dialog.findViewById(R.id.sky_blue);
            skyblue.setOnClickListener(onbutton);
            green=(ImageButton)dialog.findViewById(R.id.green);
            green.setOnClickListener(onbutton);
            red=(ImageButton)dialog.findViewById(R.id.red);
            red.setOnClickListener(onbutton);
            dialog.show();

        }  else if (id == R.id.nav_about_us) {
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.about_us);
            dialog.setTitle("About Us");

            // set the custom dialog components - text, image and button


            Button dialogButton = (Button) dialog.findViewById(R.id.dialog);
            // if button is clicked, close the custom dialog
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();
        }
        else if(id==R.id.nav_contact_us){
            Intent email = new Intent(Intent.ACTION_SEND);
            email.putExtra(Intent.EXTRA_EMAIL, new String[]{"mohitarora19966@gmail.com"});
            email.putExtra(Intent.EXTRA_SUBJECT, "subject");
            email.putExtra(Intent.EXTRA_TEXT, "message");
            email.setType("message/rfc822");
            startActivity(Intent.createChooser(email, "Choose an Email client :"));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

  private View.OnClickListener onbutton =new View.OnClickListener(){

        @Override
        public void onClick(View view) {
            sharedPreferences=getSharedPreferences("theme",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedPreferences.edit();
                switch(view.getId()){
                    case R.id.light_red:
                        editor.putString("theme","light_red");
                        editor.commit();
                        dialog.dismiss();
                        recreate();
                        break;
                    case R.id.wood:
                        editor.putString("theme","wood");
                        editor.commit();
                        dialog.dismiss();
                        recreate();
                        break;
                    case R.id.sky_blue:
                        editor.putString("theme","sky_blue");
                        editor.commit();
                        dialog.dismiss();
                        recreate();
                        break;
                    case R.id.light_green:
                        editor.putString("theme","light_green");
                        editor.commit();
                        dialog.dismiss();
                        recreate();
                        break;
                    case R.id.black:
                        editor.putString("theme","black");
                        editor.commit();
                        dialog.dismiss();
                        recreate();
                        break;
                    case R.id.default_activity_button:
                        editor.putString("theme","default");
                        editor.commit();
                        dialog.dismiss();
                        recreate();
                        break;
                    case R.id.green:
                        editor.putString("theme","green");
                        editor.commit();
                        dialog.dismiss();
                        recreate();
                        break;
                    case R.id.red:
                        editor.putString("theme","red");
                        editor.commit();
                        dialog.dismiss();
                        recreate();

                }
        }
    };


    public void setHeader() {

        sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE);
        currentFile = sharedPref.getString("lastSong", "");
        int pos = sharedPref.getInt("pos", -1);
        isPlaying = sharedPref.getBoolean("isPlaying", false);
        list = sharedPref.getString("list", "");
        if (list.equals("song")) {
            if (pos > -1 && pos < arrayList.size()) {
                Song localItem = (Song) arrayList.get(pos);
                setData(localItem);
            }
        } else if (list.equals("album")) {
            long albumId = sharedPref.getLong("albumId", -1);
            if (albumId > -1) {
                albumSongList = AlbumSongsLoader.getSongsForAlbum(this, albumId);
                pos = sharedPref.getInt("pos", -1);
                if (pos > -1 && pos < albumSongList.size()) {
                    Song localItem = (Song) albumSongList.get(pos);
                    setData(localItem);
                }
            } else if (pos > -1 && pos < recentlyArray.size() && list.equals("recentlyadded")) {
                Song localItem = (Song) recentlyArray.get(pos);
                setData(localItem);
            }else if(list.equals("search")){
                imageLoad(sharedPref.getLong("searchId",-1));
                songName.setText(sharedPref.getString("searchSong",""));
                artistName.setText(sharedPref.getString("searchArtist",""));
            }
        }
    }

    void setData(Song song){
        imageLoad(song.albumId);
        songName.setText(song.title);
        artistName.setText(song.artist);
    }

    void imageLoad(long albumArt){
        Uri albumArtUri=Uri.parse("content://media/external/audio/albumart");
        Uri uri= ContentUris.withAppendedId(albumArtUri,albumArt);
        Glide.with(this)
                .load(uri.toString())
                .into(imageView);
    }

    public void onResume(){
        super.onResume();
        setHeader();
    }

    @Override
    public void onStop()
    {
        super.onStop();
    }
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(updateNavHeaderBroadcast);
    }
}
