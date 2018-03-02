package com.mohit.mohit.music;

import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class Main2Activity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener{
    SeekBar seekBar;
    private int seekMax;
    private static int songEnded=0;
    boolean mBroadcastIsRegistered;
    Intent serviceIntent;
    Boolean isMovingSeekBar=false;
    TextView SongName;
    TextView AlbumName;
    ImageView ImageOfSong;
    ImageButton Prev;
    ImageView Play;
    boolean isPlaying;
    SharedPreferences sharedPref;
    String currentFile;
    int pos;
    String list;
    ImageButton Next;
    TextView TimeElapse;
    TextView Duration;
    ArrayList arrayList;
    ArrayList newAlbumSong;
    ArrayList recentlyArray;
    ImageButton shuffel;
    ImageButton repeat;
    ImageButton backArrow;
    public static String SONG="com.mohit,mohit.music.action.song";
    long albumId;
    public static final String BROADCAST_SEEKBAR="com.example.mohit.music.sendseekbar";
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main2);
        serviceIntent=new Intent(this,MusicService.class);
        SongName =(TextView) findViewById(R.id.name_of_song);
        AlbumName=(TextView) findViewById(R.id.name_of_Album);
        ImageOfSong=(ImageView) findViewById(R.id.image_of_song);
        Prev=(ImageButton) findViewById(R.id.prev);
        Next=(ImageButton) findViewById(R.id.next);
        Play=(ImageButton) findViewById(R.id.play);
        TimeElapse=(TextView)findViewById(R.id.time_elapse);
        Duration=(TextView)findViewById(R.id.duration);
        shuffel=(ImageButton)findViewById(R.id.shuffel);
        repeat=(ImageButton)findViewById(R.id.repeat);
        backArrow=(ImageButton)findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(onButtonClicked);
        Play.setOnClickListener(onButtonClicked);
        Next.setOnClickListener(onButtonClicked);
        Prev.setOnClickListener(onButtonClicked);
        shuffel.setOnClickListener(onButtonClicked);
        repeat.setOnClickListener(onButtonClicked);
        seekBar = (SeekBar)findViewById(R.id.seekbar);
        seekBar.setOnSeekBarChangeListener(seekBarChanged);
        intent=new Intent(BROADCAST_SEEKBAR);
        seekBar.setOnSeekBarChangeListener(this);


        arrayList=SongsLoader.getSongs(getBaseContext());
        recentlyArray=RecentlyAddedLoader.getRecentlyAddedSongs(getBaseContext());
        setResources();


        registerReceiver(broadcastReceiver,new IntentFilter(
                MusicService.BROADCAST_ACTION));
        mBroadcastIsRegistered=true;
        registerReceiver(newBroadcast,new IntentFilter(MusicService.BROADCAST_ON_COMPLETE));
        registerReceiver(playPausebutton,new IntentFilter(MusicService.BROADCAST_ON_PLAY_PAUSE));
    }

    BroadcastReceiver playPausebutton=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getStringExtra("button").equals("pause")){
                Play.setImageResource(R.drawable.ic_play_arrow_black_24px);
            }
            else{
                Play.setImageResource(R.drawable.ic_pause_black_24px);
            }
        }
    };

    BroadcastReceiver newBroadcast =new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setListData();
        }
    };

    private BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUi(intent);
        }
    };
    private void updateUi(Intent serviceintent)
    {
        String counter=serviceintent.getStringExtra("counter");
        String mediamax=serviceintent.getStringExtra("mediamax");
        String strSongEnded=serviceintent.getStringExtra("song_end");
        int seekProgress=Integer.parseInt(counter);
        seekMax=Integer.parseInt(mediamax);
        int sec=(seekMax/1000)%60;
        int min=((seekMax)/(1000*60))%60;
        String duration;
        if(sec<10){
            duration=min+":0"+sec;
        }
        else{
            duration=min+":"+sec;
        }
        int sec1=(seekProgress/1000)%60;
        int min1=((seekProgress)/(1000*60))%60;
        String timeElapse;
        if(sec1<10) {
            timeElapse = min1 + ":0" + sec1;
        }
        else{
            timeElapse=min1+":"+sec1;
        }
        TimeElapse.setText(timeElapse);
        Duration.setText(duration);;
        songEnded=Integer.parseInt(strSongEnded);
        seekBar.setMax(seekMax);
        seekBar.setProgress(seekProgress);

    }
    private View.OnClickListener onButtonClicked =new View.OnClickListener(){
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.play:
                    isPlaying=sharedPref.getBoolean("isPlaying",false);
                    if (isPlaying) {
                        try{
                            stopService(serviceIntent);
                            Play.setImageResource(R.drawable.ic_play_arrow_black_24px);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        SharedPreferences.Editor editor=sharedPref.edit();
                        editor.putBoolean("isPlaying",false);
                        editor.commit();
                    } else {
                        currentFile=sharedPref.getString("lastSong","");
                        serviceIntent.putExtra("songName", currentFile);
                        try{
                            serviceIntent.setAction(SONG);
                            startService(serviceIntent);
                            Play.setImageResource(R.drawable.ic_pause_black_24px);
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        SharedPreferences.Editor editor=sharedPref.edit();
                        editor.putBoolean("isPlaying",true);
                        editor.commit();

                    }
                    break;
                case R.id.next:
                    sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE);
                    pos=sharedPref.getInt("pos",-1);
                    pos++;
                    list=sharedPref.getString("list","");
                    Random random=new Random();
                    long albumId=sharedPref.getLong("albumId",-1);
                    boolean shuffelBoolean=sharedPref.getBoolean("shuffel",false);
                    if(list.equals("song")){
                        if(pos<arrayList.size()) {
                            if(shuffelBoolean){
                                pos=random.nextInt(arrayList.size())+0;
                            }
                            Song localItem=(Song)arrayList.get(pos);
                            String sngName =localItem.path;
                            serviceIntent.setAction(SONG);
                            serviceIntent.putExtra("songName", sngName);
                            try {
                                startService(serviceIntent);
                                setSongData(localItem);
                                Play.setImageResource(R.drawable.ic_pause_black_24px);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            SharedPreferences.Editor editor=sharedPref.edit();
                            editor.putInt("pos",pos);
                            editor.commit();
                        }
                    }else if(list.equals("album")){
                        newAlbumSong=AlbumSongsLoader.getSongsForAlbum(Main2Activity.this,albumId);
                        if(pos<newAlbumSong.size()){
                            if(shuffelBoolean){
                                pos=random.nextInt(newAlbumSong.size())+0;
                            }
                            Song localItem=(Song)newAlbumSong.get(pos);
                            String sngName =localItem.path;
                            serviceIntent.setAction(SONG);
                            serviceIntent.putExtra("songName", sngName);
                            try {
                                startService(serviceIntent);
                                setSongData(localItem);
                                Play.setImageResource(R.drawable.ic_pause_black_24px);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            SharedPreferences.Editor editor=sharedPref.edit();
                            editor.putInt("pos",pos);
                            editor.commit();
                        }
                    }else if(list.equals("recentlyadded")){
                        if(pos<recentlyArray.size()){
                            if(shuffelBoolean){
                                pos=random.nextInt(recentlyArray.size())+0;
                            }
                            Song localItem=(Song)recentlyArray.get(pos);
                            String sngName =localItem.path;
                            serviceIntent.setAction(SONG);
                            serviceIntent.putExtra("songName", sngName);
                            try {
                                startService(serviceIntent);
                                setSongData(localItem);
                                Play.setImageResource(R.drawable.ic_pause_black_24px);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            SharedPreferences.Editor editor=sharedPref.edit();
                            editor.putInt("pos",pos);
                            editor.commit();
                        }
                    }

                    break;
                case R.id.prev:
                    sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE);
                    pos=sharedPref.getInt("pos",-1);
                    list=sharedPref.getString("list","");
                    albumId=sharedPref.getLong("albumId",-1);
                   pos--;
                    if(list.equals("song")){
                        if(pos>-1) {
                            Song localItem=(Song)arrayList.get(pos);
                            String sngName=localItem.path;
                            serviceIntent.setAction(SONG);
                            serviceIntent.putExtra("songName", sngName);
                            try {
                                startService(serviceIntent);
                                setSongData(localItem);
                                Play.setImageResource(R.drawable.ic_pause_black_24px);
                            }catch (Exception e){
                                    e.printStackTrace();
                            }
                            SharedPreferences.Editor editor=sharedPref.edit();
                            editor.putInt("pos",pos);
                            editor.commit();
                        }
                    }else if(list.equals("album")){
                        newAlbumSong=AlbumSongsLoader.getSongsForAlbum(Main2Activity.this,albumId);
                        if(pos>-1){
                            Song localItem=(Song)newAlbumSong.get(pos);
                            String sngName =localItem.path;
                            serviceIntent.setAction(SONG);
                            serviceIntent.putExtra("songName", sngName);
                            try {
                                startService(serviceIntent);
                                setSongData(localItem);
                                Play.setImageResource(R.drawable.ic_pause_black_24px);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            SharedPreferences.Editor editor=sharedPref.edit();
                            editor.putInt("pos",pos);
                            editor.commit();
                        }
                    }else if(list.equals("recentlyadded")){
                        if(pos>-1){
                            Song localItem=(Song)recentlyArray.get(pos);
                            String sngName =localItem.path;
                            serviceIntent.setAction(SONG);
                            serviceIntent.putExtra("songName", sngName);
                            try {
                                startService(serviceIntent);
                                setSongData(localItem);
                                Play.setImageResource(R.drawable.ic_pause_black_24px);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            SharedPreferences.Editor editor=sharedPref.edit();
                            editor.putInt("pos",pos);
                            editor.commit();
                        }
                    }
                    break;
                case R.id.shuffel:
                    boolean s=sharedPref.getBoolean("shuffel",false);
                    if(s){
                        SharedPreferences.Editor editor=sharedPref.edit();
                        editor.putBoolean("shuffel",false);
                        editor.commit();
                        shuffel.setImageResource(R.drawable.ic_shuffle_black_24px);
                    }else {
                        SharedPreferences.Editor editor=sharedPref.edit();
                        editor.putBoolean("shuffel",true);
                        editor.putBoolean("repeat",false);
                        editor.commit();
                        repeat.setImageResource(R.drawable.ic_repeat_black_24px);
                        shuffel.setImageResource(R.drawable.ic_shuffle_primary_24px);
                    }

                    break;
                case R.id.repeat:

                    boolean s1=sharedPref.getBoolean("repeat",false);
                    SharedPreferences.Editor editor=sharedPref.edit();
                    if(s1){
                        editor.putBoolean("repeat",false);
                        editor.commit();
                        repeat.setImageResource(R.drawable.ic_repeat_black_24px);
                    }
                    else {
                        editor.putBoolean("repeat",true);
                        editor.putBoolean("shuffel",false);
                        editor.commit();
                        shuffel.setImageResource(R.drawable.ic_shuffle_black_24px);
                        repeat.setImageResource(R.drawable.ic_repeat_one_black_24px);
                    }

                    break;
                case R.id.back_arrow:
                   finish();
                    overridePendingTransition(R.anim.stay, R.anim.slide_down);
                    break;
            }
        }

    };
    public void onResume()
    {
        super.onResume();
        registerReceiver(broadcastReceiver,new IntentFilter(
                MusicService.BROADCAST_ACTION));
        registerReceiver(newBroadcast,new IntentFilter(MusicService.BROADCAST_ON_COMPLETE));
        mBroadcastIsRegistered=true;

        setResources();

    }

    public void setResources(){
        sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE);
        boolean shuffelBoolean=sharedPref.getBoolean("shuffel",false);
        boolean repeatBoolean=sharedPref.getBoolean("repeat",false);
        int i=sharedPref.getInt("lastPosition",123);
        int seekmax=sharedPref.getInt("duration",1);
        seekBar.setMax(seekmax);
        seekBar.setProgress(i);
        int sec=(seekmax/1000)%60;
        int min=((seekmax)/(1000*60))%60;
        String duration;
        if(sec<10){
            duration=min+":0"+sec;
        }
        else{
            duration=min+":"+sec;
        }
        int sec1=(i/1000)%60;
        int min1=((i)/(1000*60))%60;
        String timeElapse;
        if(sec1<10) {
            timeElapse = min1 + ":0" + sec1;
        }
        else{
            timeElapse=min1+":"+sec1;
        }
        TimeElapse.setText(timeElapse);
        Duration.setText(duration);

        setListData();

        if(shuffelBoolean){
            shuffel.setImageResource(R.drawable.ic_shuffle_primary_24px);
        }
        else {
            shuffel.setImageResource(R.drawable.ic_shuffle_black_24px);
        }

        if(repeatBoolean){
            repeat.setImageResource(R.drawable.ic_repeat_one_black_24px);
        }else {
            repeat.setImageResource(R.drawable.ic_repeat_black_24px);
        }



    }

    public void setListData(){
        sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE);
        isPlaying=sharedPref.getBoolean("isPlaying",false);
        int pos=sharedPref.getInt("pos",-1);
        list=sharedPref.getString("list","");
        albumId=sharedPref.getLong("albumId",-1);


        setButtonResources(isPlaying);

        if(list.equals("song")) {
            if (pos > -1 && pos<arrayList.size()) {
                Song localItem = (Song) arrayList.get(pos);
                setSongData(localItem);
            }
        }
        else if(list.equals("album")){
            newAlbumSong = AlbumSongsLoader.getSongsForAlbum(this, albumId);
            if(pos>-1 && pos<newAlbumSong.size()) {
                Song localItem = (Song) newAlbumSong.get(pos);
                setSongData(localItem);
            }

        }
        else if(pos>-1 && pos<recentlyArray.size() && list.equals("recentlyadded")){
            Song localItem = (Song) recentlyArray.get(pos);
            setSongData(localItem);
        }
        else if(list.equals("search")){
            String songName=sharedPref.getString("searchSong","");
            String songArtist=sharedPref.getString("searchArtist","");
            long albumArt=sharedPref.getLong("searchId",-1);
            ImageView image=(ImageView)findViewById(R.id.image_of_song);
            Uri albumArtUri=Uri.parse("content://media/external/audio/albumart");
            Uri uri= ContentUris.withAppendedId(albumArtUri,albumArt);
            Glide.with(this)
                    .load(uri.toString())
                    .error(R.drawable.mohit)
                    .into(image);
            SongName.setText(songName);
            AlbumName.setText(songArtist);
        }
    }

    public  void setSongData(Song song){
        imageLoad(song);
        SongName.setText(song.title);
        AlbumName.setText(song.artist);
    }

    public void setButtonResources(boolean isPlaying){
        if(isPlaying){
            Play.setImageResource(R.drawable.ic_pause_black_24px);
        }
        else
        {
            Play.setImageResource(R.drawable.ic_play_arrow_black_24px);
        }
    }

    void imageLoad(Song song) {
        long albumArt=song.albumId;
        ImageView image=(ImageView)findViewById(R.id.image_of_song);
        Uri albumArtUri=Uri.parse("content://media/external/audio/albumart");
        Uri uri= ContentUris.withAppendedId(albumArtUri,albumArt);
             Glide.with(this)
                    .load(uri.toString())
                     .error(R.drawable.mohit)
                    .into(image);
    }


    @Override
    public void onPause()
    {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
        unregisterReceiver(newBroadcast);
    }


    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.stay, R.anim.slide_down);
    }
    private SeekBar.OnSeekBarChangeListener seekBarChanged = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            isMovingSeekBar = false;
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar){
            isMovingSeekBar =true;
        }
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromuse){
            if(isMovingSeekBar){
                Log.i("onSeekBarChangeListener","onProgressChange");
            }
        }
    };

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if(b){
            int seekpos=seekBar.getProgress();
            intent.putExtra("seekpos",seekpos);
            sendBroadcast(intent);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(playPausebutton);

    }
}
