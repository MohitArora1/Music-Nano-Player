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
 * Created by Mohit on 13-03-2017.
 */

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.MyOwnHolder> {
    Context context;
    List<Album> arraylist;

    View view;
    AlbumAdapter(Context context,List<Album> arraylist){
        this.context=context;
        this.arraylist=arraylist;
    }

    @Override
    public MyOwnHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        view =layoutInflater.inflate(R.layout.albumlist,parent,false);
        return new MyOwnHolder(view);
    }

    @Override
    public void onBindViewHolder(MyOwnHolder holder, int position) {
        Album localItem=arraylist.get(position);
        holder.albumName.setText(localItem.album);
        holder.artistName.setText(localItem.artist);
        long albumArt=localItem.albumId;
        Uri ablubArtUri=Uri.parse("content://media/external/audio/albumart");
        Uri uri= ContentUris.withAppendedId(ablubArtUri,albumArt);
        Glide.with(context)
                .load(uri.toString())
                .fitCenter()
                .error(R.drawable.mohit)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return (null != arraylist ? arraylist.size() : 0);
    }

    public class MyOwnHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView albumName;
        TextView artistName;
        public MyOwnHolder(View itemView) {
            super(itemView);
            imageView=(ImageView) itemView.findViewById(R.id.album_image);
            albumName=(TextView)itemView.findViewById(R.id.album_name);
            artistName=(TextView)itemView.findViewById(R.id.artist_name);
        }
    }
}
