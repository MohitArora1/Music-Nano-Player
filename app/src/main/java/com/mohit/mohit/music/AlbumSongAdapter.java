package com.mohit.mohit.music;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by Mohit on 16-03-2017.
 */

public class AlbumSongAdapter extends RecyclerView.Adapter<AlbumSongAdapter.MyOwnHolder> {

    Context context;
    List<Song> arrayList;
    View view;
    AlbumSongAdapter(Context context, List<Song> arrayList){
        this.context=context;
        this.arrayList=arrayList;

    }

    @Override
    public MyOwnHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        view= layoutInflater.inflate(R.layout.albumsonglist,parent,false);
        return new MyOwnHolder(view);
    }

    @Override
    public void onBindViewHolder(MyOwnHolder holder, int position) {
        Song localItem=arrayList.get(position);
        holder.songName.setText(localItem.title);
        holder.artistName.setText(localItem.artist);
        long ablubArt=localItem.albumId;
        Uri ablubArtUri=Uri.parse("content://media/external/audio/albumart");
        Uri uri= ContentUris.withAppendedId(ablubArtUri,ablubArt);
        Glide.with(context)
                .load(uri)
                .into(holder.image);


    }

    @Override
    public int getItemCount() {
        return (null != arrayList ? arrayList.size() : 0);
    }

    public class MyOwnHolder extends RecyclerView.ViewHolder {
       ImageView image;
        TextView songName;
        TextView artistName;
        public MyOwnHolder(View itemView) {
            super(itemView);
            image=(ImageView)itemView.findViewById(R.id.album_song_image);
            songName=(TextView)itemView.findViewById(R.id.songname);
            artistName=(TextView)itemView.findViewById(R.id.artist_name);

        }
    }
}
