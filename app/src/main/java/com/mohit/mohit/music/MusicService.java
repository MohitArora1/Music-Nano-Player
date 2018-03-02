package com.mohit.mohit.music;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.AudioEffect;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.RemoteViews;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;


public class MusicService extends Service{
    static MediaPlayer player=null;
    private static final int UPDATE_FREQENCY=500;
    SharedPreferences sharedPref;
    String sngName;
    AudioManager am;
    //... variable for seekbar prosessing........
    Intent seekIntent;

    int mediaPos;
    int mediaMax;
    private final android.os.Handler handler = new android.os.Handler();
    public static int songEnded;
    public static final String BROADCAST_ACTION = "com.mohit.mohit.music.seekprogress";
    public static final String BROADCAST_ON_COMPLETE="com.mohit.mohit.music.oncomplete";
    public static final String BROADCAST_ON_PLAY_PAUSE="com.mohit.mohit.music.playpause";
    public static final String BROADCAST_UPDATE_NAV_HEADER="com.mohit.mohit.music.updatenavheader";
    Intent updateNavHeader;
    Intent Play_Pause;
    Intent onComplete;
    ArrayList newAlbumSong;
    ArrayList arrayList;
    ArrayList recentlyArray;
    int focusResult;
    Notification notification;
    int headState=1;
    boolean headsetConnected=false;
    public static String PLAY_ACTION="com.mohit.mohit.music.action.play";
    public static String PREV_ACTION="com.mohit.mohit.music.action.prev";
    public static String NEXT_ACTION="com.mohit.mohit.music.action.next";
    public static String SONG="com.mohit,mohit.music.action.song";
    public static String CLOSE="com.mohit.mohit.music.action.close";
    RemoteViews remoteViews;
    TelephonyManager mgr;
    @Override
    public void onCreate() {
        super.onCreate();
        player = new MediaPlayer();
        player.setOnCompletionListener(onCompletion);
        player.setOnErrorListener(onError);
        seekIntent =new Intent(BROADCAST_ACTION);
        onComplete=new Intent(BROADCAST_ON_COMPLETE);
        Play_Pause=new Intent(BROADCAST_ON_PLAY_PAUSE);
        arrayList=SongsLoader.getSongs(getBaseContext());
        // Request audio focus for playback
        am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        am.loadSoundEffects();
        focusResult = am.requestAudioFocus(focusChangeListener,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN);

        mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if(mgr != null) {
            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }

        registerReceiver(headsetReceiver,new IntentFilter(Intent.ACTION_HEADSET_PLUG));
        updateNavHeader=new Intent(BROADCAST_UPDATE_NAV_HEADER);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override

    public void onDestroy() {
        super.onDestroy();

        stopPlay();
        unregisterReceiver(broadcastReceiver);

        mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if(mgr != null) {
            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
        unregisterReceiver(headsetReceiver);
        Play_Pause.putExtra("button","pause");
        sendBroadcast(Play_Pause);
    }
    public static long playingId(){
        if(player!=null)
                return player.getAudioSessionId();
             else
                return -1;
    }
    public static boolean playing(){
        if(player!=null) {
            if (player.isPlaying())
                return true;
            else
                return false;
        }
        else return false;

    }

    public void setNotification(){
        Intent intent=new Intent(getApplicationContext(),Main2Activity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(Main2Activity.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent= stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews=new RemoteViews(getPackageName(),R.layout.notification);
        RemoteViews smallNotification =new RemoteViews(getPackageName(),R.layout.small_notification);
        Song song=getSongData();
        String list=sharedPref.getString("list","");
        if(song!=null || list.equals("search")) {
            if(song!=null){
                Uri uri = getAlbumArt(song.albumId);
                remoteViews.setImageViewUri(R.id.notification_image, uri);
                remoteViews.setTextViewText(R.id.notification_song_name, song.title);
                remoteViews.setTextViewText(R.id.notification_artist_name, song.artist);

                smallNotification.setImageViewUri(R.id.status_bar_icon, uri);
                smallNotification.setTextViewText(R.id.status_bar_track_name, song.title);
                smallNotification.setTextViewText(R.id.status_bar_artist_name, song.artist);
            }else if(list.equals("search")){
                Uri uri = getAlbumArt(sharedPref.getLong("searchId",-1));
                remoteViews.setImageViewUri(R.id.notification_image, uri);
                remoteViews.setTextViewText(R.id.notification_song_name, sharedPref.getString("searchSong",""));
                remoteViews.setTextViewText(R.id.notification_artist_name, sharedPref.getString("searchArtist",""));

                smallNotification.setImageViewUri(R.id.status_bar_icon, uri);
                smallNotification.setTextViewText(R.id.status_bar_track_name, sharedPref.getString("searchSong",""));
                smallNotification.setTextViewText(R.id.status_bar_artist_name, sharedPref.getString("searchArtist",""));
            }
            Intent playIntent = new Intent(this, MusicService.class);
            playIntent.setAction(PLAY_ACTION);
            PendingIntent playPendingIntent = PendingIntent.getService(this, 0, playIntent, 0);
            remoteViews.setOnClickPendingIntent(R.id.noti_play, playPendingIntent);
            smallNotification.setOnClickPendingIntent(R.id.status_bar_play,playPendingIntent);

            Intent closeIntent = new Intent(this, MusicService.class);
            closeIntent.setAction(CLOSE);
            PendingIntent closePendingIntent = PendingIntent.getService(this, 0, closeIntent, 0);
            remoteViews.setOnClickPendingIntent(R.id.noti_close, closePendingIntent);
            smallNotification.setOnClickPendingIntent(R.id.status_bar_close,closePendingIntent);

            if (player.isPlaying()) {
                remoteViews.setImageViewResource(R.id.noti_play, android.R.drawable.ic_media_pause);
                smallNotification.setImageViewResource(R.id.status_bar_play,android.R.drawable.ic_media_pause);
            } else {
                remoteViews.setImageViewResource(R.id.noti_play, android.R.drawable.ic_media_play);
                smallNotification.setImageViewResource(R.id.status_bar_play,android.R.drawable.ic_media_play);
            }
            Intent prevIntent = new Intent(this, MusicService.class);
            prevIntent.setAction(PREV_ACTION);
            PendingIntent prevPendingIntent = PendingIntent.getService(this, 0, prevIntent, 0);
            remoteViews.setOnClickPendingIntent(R.id.noti_prev, prevPendingIntent);
            smallNotification.setOnClickPendingIntent(R.id.status_bar_prev,prevPendingIntent);

            Intent nextIntent = new Intent(this, MusicService.class);
            nextIntent.setAction(NEXT_ACTION);
            PendingIntent nextPendingIntent = PendingIntent.getService(this, 0, nextIntent, 0);
            remoteViews.setOnClickPendingIntent(R.id.noti_next, nextPendingIntent);
            smallNotification.setOnClickPendingIntent(R.id.status_bar_next,nextPendingIntent);

            notification = new NotificationCompat.Builder(this)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setCustomBigContentView(remoteViews)
                    .setOngoing(true)
                    .setContent(smallNotification)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pendingIntent)
                    .build();
            startForeground(101, notification);
        }
    }
    public void removeNotification(){
        stopForeground(true);
    }
    public Uri getAlbumArt(long albumArt){
        Uri ablubArtUri=Uri.parse("content://media/external/audio/albumart");
        return ContentUris.withAppendedId(ablubArtUri,albumArt);

    }
    public Song getSongData(){
        Song song;
        sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE);
        int pos=sharedPref.getInt("pos",-1);
        String list=sharedPref.getString("list","");
        long albumId=sharedPref.getLong("albumId",-1);
        if(list.equals("song") && pos<arrayList.size()){
                song=(Song)arrayList.get(pos);
        }
        else if(list.equals("album")){
            newAlbumSong=AlbumSongsLoader.getSongsForAlbum(getBaseContext(),albumId);
            if(pos>-1 && pos<newAlbumSong.size()){
                song=(Song)newAlbumSong.get(pos);

            }
            else song=null;
        }
        else if(list.equals("recentlyadded")){
            recentlyArray=RecentlyAddedLoader.getRecentlyAddedSongs(getBaseContext());
            if(pos>-1 && pos<recentlyArray.size()){
                song=(Song)recentlyArray.get(pos);

            }
            else song=null;

        }
        else
            song=null;
        return song;
    }

    @Override

    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent.getAction().equals(PLAY_ACTION)){
            if(player.isPlaying()){
                player.pause();
                setNotification();
                Play_Pause.putExtra("button","pause");
                sendBroadcast(Play_Pause);
                sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("isPlaying",false);
                editor.commit();
            }
            else {
                player.start();
                setNotification();
                Play_Pause.putExtra("button","play");
                sendBroadcast(Play_Pause);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("isPlaying",true);
                editor.commit();
            }

        }
        else if(intent.getAction().equals(PREV_ACTION)){
            getPrevSong();
        }

        else if(intent.getAction().equals(NEXT_ACTION)){
            nextSongFromNotification();
        }

        else if(intent.getAction().equals(SONG)){
            registerReceiver(broadcastReceiver,new IntentFilter(Main2Activity.BROADCAST_SEEKBAR));
            sngName=intent.getStringExtra("songName");
            sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE);
            String last = sharedPref.getString("lastSong", "");
            int i = sharedPref.getInt("lastPosition", 123);
            if(!sngName.equals(last)){
                startPlay(sngName);
                sendBroadcast(updateNavHeader);
            }
            else {
                if (!last.equals("")) {
                    startPlay(last);
                    player.seekTo(i);
                }

            }
            updatePosition();
            sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("lastSong",sngName);
            editor.putBoolean("isPlaying",true);
            editor.apply();
        }else if(intent.getAction().equals(CLOSE)){
            stopSelf();
        }

        return START_STICKY;
    }
    private final Runnable updatePositionRunable = new Runnable() {
        @Override
        public void run() {
            logMediaPosition();
            handler.postDelayed(updatePositionRunable,UPDATE_FREQENCY);
        }
    };
    private void updatePosition() {
        handler.removeCallbacks(updatePositionRunable);
        handler.postDelayed(updatePositionRunable,UPDATE_FREQENCY);
    }
    private void logMediaPosition()
    {
        if(player.isPlaying())
        {
            mediaPos=player.getCurrentPosition();
            mediaMax=player.getDuration();
            seekIntent.putExtra("counter",String.valueOf(mediaPos));
            seekIntent.putExtra("mediamax",String.valueOf(mediaMax));
            seekIntent.putExtra("song_end",String.valueOf(songEnded));
            sendBroadcast(seekIntent);
        }
    }

    private BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateSeekbar(intent);
        }
    };

    public void updateSeekbar(Intent intent)
    {
        int seekpos=intent.getIntExtra("seekpos",0);
        if(player.isPlaying()){
            handler.removeCallbacks(updatePositionRunable);
            player.seekTo(seekpos);
            updatePosition();
        }
    }


    public void startPlay(String file)
    {
        player.stop();
        player.reset();
        if (focusResult == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            try {
                player.setDataSource(file);
                player.prepare();
                player.start();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        setNotification();
        final Intent intent = new Intent(AudioEffect.ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION);
        intent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, playingId());
        intent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, getPackageName());
        sendBroadcast(intent);

    }
    public void stopPlay(){
        removeNotification();

        Play_Pause.putExtra("button","pause");
        sendBroadcast(Play_Pause);
        sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("lastSong",sngName);
        editor.putBoolean("isPlaying",false);
        editor.putInt("lastPosition",player.getCurrentPosition());
        editor.putInt("duration",player.getDuration());
        editor.apply();

        final Intent audioEffectsIntent = new Intent(

                AudioEffect.ACTION_CLOSE_AUDIO_EFFECT_CONTROL_SESSION);

        audioEffectsIntent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, playingId());

        audioEffectsIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, getPackageName());

        sendBroadcast(audioEffectsIntent);


        player.stop();
        player.reset();
        am.abandonAudioFocus(focusChangeListener);
    }
    private MediaPlayer.OnCompletionListener onCompletion= new MediaPlayer.OnCompletionListener() {
        @Override

        public void onCompletion(MediaPlayer mp) {
            nextSongFromService();

        }
    };


    public void getPrevSong(){
        sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE);
        int pos=sharedPref.getInt("pos",-1);
        pos--;
        String list=sharedPref.getString("list","");
        long albumId=sharedPref.getLong("albumId",-1);
        if(list.equals("song")){


            if(pos>-1 && pos<arrayList.size()){
                Song localItem=(Song)arrayList.get(pos);
                sngName=localItem.path;
                sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("lastSong",sngName);
                editor.putBoolean("isPlaying",true);
                editor.putInt("pos",pos);
                editor.putString("list","song");
                editor.commit();
                startPlay(sngName);
                updatePosition();
            }
            else{
                stopPlay();
            }
        }
        else if(list.equals("album")){
            newAlbumSong=AlbumSongsLoader.getSongsForAlbum(getBaseContext(),albumId);
            if(pos>-1 && pos<newAlbumSong.size()){
                Song localItem=(Song)newAlbumSong.get(pos);
                sngName=localItem.path;
                sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("lastSong",sngName);
                editor.putBoolean("isPlaying",true);
                editor.putInt("pos",pos);
                editor.putString("list","album");
                editor.commit();
                startPlay(sngName);
                updatePosition();
            }
        }
        else if(list.equals("recentlyadded")){
            recentlyArray=RecentlyAddedLoader.getRecentlyAddedSongs(getBaseContext());
            if(pos>-1 && pos<recentlyArray.size()){
                Song localItem=(Song)recentlyArray.get(pos);
                sngName=localItem.path;
                sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("lastSong",sngName);
                editor.putBoolean("isPlaying",true);
                editor.putInt("pos",pos);
                editor.putString("list","recentlyadded");
                editor.apply();
                startPlay(sngName);
                updatePosition();
            }

        }
        else {
            stopPlay();
        }
        sendBroadcast(onComplete);
    }

    public void nextSongFromNotification(){
        sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE);
        int pos=sharedPref.getInt("pos",-1);
        pos++;
        getNextSong(pos);
    }
    public void nextSongFromService(){
        sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE);
        int pos=sharedPref.getInt("pos",-1);
        boolean repeat=sharedPref.getBoolean("repeat",false);
        if(repeat){
            getNextSong(pos);
        }
        else {
            pos++;
            getNextSong(pos);
        }
    }

    public void getNextSong(int pos){
        sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE);
        String list=sharedPref.getString("list","");
        long albumId=sharedPref.getLong("albumId",-1);
        boolean shuffel=sharedPref.getBoolean("shuffel",false);
        Random random=new Random();
        if(list.equals("song")){


            if(pos>-1 &&pos<arrayList.size()){
                if(shuffel){
                    pos=random.nextInt(arrayList.size()) + 0;
                }
                Song localItem=(Song)arrayList.get(pos);
                sngName=localItem.path;
                sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("lastSong",sngName);
                editor.putBoolean("isPlaying",true);
                editor.putInt("pos",pos);
                editor.putString("list","song");
                editor.commit();
                startPlay(sngName);
                updatePosition();
            }
            else{
                stopPlay();
            }
        }
        else if(list.equals("album")){
            newAlbumSong=AlbumSongsLoader.getSongsForAlbum(getBaseContext(),albumId);
            if(pos>-1 && pos<newAlbumSong.size()){
                if(shuffel){
                    pos=random.nextInt(newAlbumSong.size()) + 0;
                }
                Song localItem=(Song)newAlbumSong.get(pos);
                sngName=localItem.path;
                sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("lastSong",sngName);
                editor.putBoolean("isPlaying",true);
                editor.putInt("pos",pos);
                editor.putString("list","album");
                editor.commit();
                startPlay(sngName);
                updatePosition();
            }
            else{
                stopPlay();
            }
        }
        else if(list.equals("recentlyadded")){
            recentlyArray=RecentlyAddedLoader.getRecentlyAddedSongs(getBaseContext());
            if(pos>-1 && pos<recentlyArray.size()){
                if(shuffel){
                    pos=random.nextInt(recentlyArray.size()) + 0;
                }
                Song localItem=(Song)recentlyArray.get(pos);
                sngName=localItem.path;
                sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("lastSong",sngName);
                editor.putBoolean("isPlaying",true);
                editor.putInt("pos",pos);
                editor.putString("list","recentlyadded");
                editor.commit();
                startPlay(sngName);
                updatePosition();
            }else{
                stopPlay();
            }

        }
        else {
            stopPlay();
        }
        sendBroadcast(onComplete);
    }

    private MediaPlayer.OnErrorListener onError = new MediaPlayer.OnErrorListener() {
        public boolean onError(MediaPlayer mp, int what, int extra) {
            return false;
        }
    };

    private AudioManager.OnAudioFocusChangeListener focusChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {
                public void onAudioFocusChange(int focusChange) {
                    AudioManager am =(AudioManager)getSystemService(Context.AUDIO_SERVICE);
        switch (focusChange){
            case AudioManager.AUDIOFOCUS_LOSS:
                if(player!=null){
                    player.pause();
                    setNotification();
                    Play_Pause.putExtra("button","pause");
                    sendBroadcast(Play_Pause);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean("isPlaying",false);
                    editor.commit();
                }

                break;
            case AudioManager.AUDIOFOCUS_GAIN:
                if(!player.isPlaying())
                    player.start();
                setNotification();
                Play_Pause.putExtra("button","play");
                sendBroadcast(Play_Pause);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("isPlaying",true);
                editor.commit();

        }

    }
};


    PhoneStateListener phoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            if (state == TelephonyManager.CALL_STATE_RINGING) {
                //Incoming call: Pause music

                if(player.isPlaying()){
                    player.pause();
                    setNotification();
                    Play_Pause.putExtra("button","pause");
                    sendBroadcast(Play_Pause);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean("isPlaying",false);
                    editor.commit();
                }

            } else if(state == TelephonyManager.CALL_STATE_IDLE) {
                //Not in call: Play music
                if(!player.isPlaying()){
                    player.start();
                    setNotification();
                    Play_Pause.putExtra("button","play");
                    sendBroadcast(Play_Pause);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean("isPlaying",true);
                    editor.commit();
                }

            } else if(state == TelephonyManager.CALL_STATE_OFFHOOK) {
                //A call is dialing, active or on hold
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    };

    private BroadcastReceiver headsetReceiver =new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {



            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                int state = intent.getIntExtra("state", -1);
                if(state==0 && headsetConnected){
                    headsetConnected=false;
                    headState=0;
                }
                else if(state==1){
                    headsetConnected=true;
                    headState=1;
                }


                switch (headState) {
                    case 0:
                        //headset unpluged
                        if(headsetConnected)
                            player.pause();
                            setNotification();
                        break;
                    case 1:
                        //headset pluged

                        break;
                }
            }
        }

    };
}
