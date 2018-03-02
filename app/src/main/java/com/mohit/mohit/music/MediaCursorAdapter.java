package com.mohit.mohit.music;

import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;
import es.claucookie.miniequalizerlibrary.EqualizerView;


public class MediaCursorAdapter extends RecyclerView.Adapter<MediaCursorAdapter.MyOwnHolder> implements RecyclerViewFastScroller.BubbleTextGetter {
    Context context;
    List<Song> arrayList;
    View myOwnView;

    MediaCursorAdapter(Context context,List<Song> arrayList){
        this.context=context;
        this.arrayList=arrayList;
    }
    @Override
    public MyOwnHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater myInflater=LayoutInflater.from(context);
        myOwnView=myInflater.inflate(R.layout.listitem,parent,false);
        return new MyOwnHolder(myOwnView);
    }

    @Override
    public void onBindViewHolder(MyOwnHolder holder, int position) {

        Song localItem=arrayList.get(position);
        holder.songName.setText(localItem.title);
        holder.title.setText(localItem.artist);
        myOwnView.setTag(localItem.path);
        long albumArt=localItem.albumId;
        Uri ablubArtUri=Uri.parse("content://media/external/audio/albumart");
        Uri uri= ContentUris.withAppendedId(ablubArtUri,albumArt);
        Glide.with(context)
                .load(uri)
                .into(holder.img);

    }

    @Override
    public int getItemCount() {
        return (null != arrayList ? arrayList.size() : 0);
    }

    @Override
    public String getTextToShowInBubble(int pos) {
        if (arrayList == null || arrayList.size() == 0)
            return "";
        Character ch = arrayList.get(pos).title.charAt(0);
        if (Character.isDigit(ch)) {
            return "#";
        } else
            return Character.toString(ch);
    }

    public class MyOwnHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView songName;
        TextView title;
        public MyOwnHolder(View itemView) {
            super(itemView);
            img=(ImageView)itemView.findViewById(R.id.image);
            songName=(TextView)itemView.findViewById(R.id.displaynames);
            title=(TextView)itemView.findViewById(R.id.title);
        }
    }
/*public byte[] Image(String s){
    MediaMetadataRetriever metaRetriver= new MediaMetadataRetriever();
    metaRetriver.setDataSource(s);
    byte[] art={0};
    Bitmap songImage=null;
    try{
        art = metaRetriver.getEmbeddedPicture();
        //songImage = BitmapFactory.decodeByteArray(art, 0, art.length);
    }
    catch (Exception e)
    {
        e.printStackTrace();
    }
    return art;
}*/
}
