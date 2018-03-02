package com.mohit.mohit.music;

import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class BottomFragment extends Fragment {
    RelativeLayout layout;
    ImageButton playButton;
    SharedPreferences sharedPref;
    String currentFile;
    boolean isPlaying;
    TextView selectedFile;
    Intent serviceIntent;
    int pos;
    String list;
    ArrayList arrayList;
    TextView ArtistName;
    ImageView imageView;
    ArrayList albumSongList;
    long albumId;
    ArrayList recentlyArray;
    View view;
    public static String SONG="com.mohit,mohit.music.action.song";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.bottom_fragment, container, false);
        selectedFile=(TextView)view.findViewById(R.id.selectedfile);
        ArtistName=(TextView)view.findViewById(R.id.artistName);
        imageView=(ImageView)view.findViewById(R.id.songImage);
        playButton=(ImageButton)view.findViewById(R.id.play);
        layout=(RelativeLayout) view.findViewById(R.id.newlayout1);
        layout.setOnClickListener(onButtonClick);
        playButton.setOnClickListener(onButtonClick);

        try{
            serviceIntent=new Intent(getContext(),MusicService.class);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        arrayList=SongsLoader.getSongs(getContext());
        recentlyArray=RecentlyAddedLoader.getRecentlyAddedSongs(getContext());
        setResources();
        getActivity().registerReceiver(newBroadCastReciver,new IntentFilter(SongsFragment.NEW_SONG));
        getActivity().registerReceiver(broadcastReceiver,new IntentFilter(MusicService.BROADCAST_ON_COMPLETE));
        getActivity().registerReceiver(newAlbumSongReciver,new IntentFilter(Album_songs.NEW_ALBUM_SONG));
        getActivity().registerReceiver(newRecentlyAddedReciver,new IntentFilter(RecentlyAdded.NEW__FROM_RECENTLY_ADDED_SONG));
        getActivity().registerReceiver(playPausebutton,new IntentFilter(MusicService.BROADCAST_ON_PLAY_PAUSE));
        return view;
    }


    BroadcastReceiver playPausebutton=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getStringExtra("button").equals("pause")){
                playButton.setImageResource(R.drawable.ic_play_arrow_black_24px);
            }
            else{
                playButton.setImageResource(R.drawable.ic_pause_black_24px);
            }
        }
    };

    BroadcastReceiver newRecentlyAddedReciver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setResources();
        }
    };

BroadcastReceiver newAlbumSongReciver=new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        setResources();
    }
};
    BroadcastReceiver newBroadCastReciver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setResources();
        }
    };
    BroadcastReceiver broadcastReceiver =new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setResources();
        }
    };


    public void setResources(){
        sharedPref = getActivity().getSharedPreferences("myPref", Context.MODE_PRIVATE);
        currentFile=sharedPref.getString("lastSong","");
        int pos=sharedPref.getInt("pos",-1);
        list=sharedPref.getString("list","");
        isPlaying=sharedPref.getBoolean("isPlaying",false);
        albumId=sharedPref.getLong("albumId",-1);
        albumSongList = AlbumSongsLoader.getSongsForAlbum(getContext(), albumId);

        if(isPlaying){
            playButton.setImageResource(R.drawable.ic_pause_black_24px);
        }
        else {
            playButton.setImageResource(R.drawable.ic_play_arrow_black_24px);
        }

        if (list.equals("song") && pos > -1 && pos<arrayList.size()) {
                    Song localItem = (Song) arrayList.get(pos);
                    setSongData(localItem);
        } else if (list.equals("album") && pos > -1 && pos<albumSongList.size()) {
                    albumId = sharedPref.getLong("albumId", -1);
                    pos = sharedPref.getInt("pos", -1);
                    Song localItem = (Song) albumSongList.get(pos);
                    setSongData(localItem);
        }
        else if(pos>-1 && pos<recentlyArray.size() && list.equals("recentlyadded")){
                Song localItem = (Song) recentlyArray.get(pos);
                setSongData(localItem);
        }
        else if(list.equals("search")){
            String songName=sharedPref.getString("searchSong","");
            String songArtist=sharedPref.getString("searchArtist","");
            long albumArt=sharedPref.getLong("searchId",-1);
            Uri albumArtUri=Uri.parse("content://media/external/audio/albumart");
            Uri uri= ContentUris.withAppendedId(albumArtUri,albumArt);
            Glide.with(this)
                    .load(uri.toString())
                    .asBitmap()
                    .into(imageView);
            selectedFile.setText(songName);
            ArtistName.setText(songArtist);
        }
    }

    public void setSongData(Song song){
        imageLoad(song);
        selectedFile.setText(song.title);
        ArtistName.setText(song.artist);
    }
    private View.OnClickListener onButtonClick=new View.OnClickListener() {
        @Override

        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.play:
                    isPlaying = sharedPref.getBoolean("isPlaying", false);
                    if (isPlaying) {
                        try {
                            getActivity().stopService(serviceIntent);
                            playButton.setImageResource(R.drawable.ic_play_arrow_black_24px);

                        } catch (Exception e) {
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
                            getActivity().startService(serviceIntent);
                            playButton.setImageResource(R.drawable.ic_pause_black_24px);
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        SharedPreferences.Editor editor=sharedPref.edit();
                        editor.putBoolean("isPlaying",true);
                        editor.commit();
                    }
                    break;
                case R.id.newlayout1:
                    startActivity(new Intent(getContext(),Main2Activity.class));
                    getActivity().overridePendingTransition(R.anim.slide_up, R.anim.stay);
            }
        }
    };

    public void onResume(){
        super.onResume();
       setResources();

    }
    public void onDestroy(){
        super.onDestroy();
        getActivity().unregisterReceiver(newBroadCastReciver);
        getActivity().unregisterReceiver(broadcastReceiver);
        getActivity().unregisterReceiver(newAlbumSongReciver);
        getActivity().unregisterReceiver(playPausebutton);
        getActivity().unregisterReceiver(newRecentlyAddedReciver);
    }
    void imageLoad(Song song){
        long albumArt=song.albumId;
        Uri ablubArtUri=Uri.parse("content://media/external/audio/albumart");
        Uri uri= ContentUris.withAppendedId(ablubArtUri,albumArt);
        Glide.with(this)
                .load(uri.toString())
                .error(R.drawable.mohit)
                .into(imageView);
    }

}
