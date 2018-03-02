package com.mohit.mohit.music;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class AlbumFragment extends Fragment {
    RecyclerView recyclerView;
    AlbumAdapter albumAdapter;
    ArrayList arrayList;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.album_fragment, container, false);
        recyclerView=(RecyclerView)view.findViewById(R.id.recycler_album);

           collectdata();

        return view;
    }
    void collectdata()
    {
        arrayList=AlbumLoader.getAlbums(getContext());
            albumAdapter=new AlbumAdapter(getActivity(),arrayList);
            recyclerView.setAdapter(albumAdapter);
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));
            recyclerView.addOnItemTouchListener(
                    new RecyclerItemTouch(this.getContext(), recyclerView, new RecyclerItemTouch.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Album localItem=(Album)arrayList.get(position);
                            String albumName=localItem.album;
                            long albumId=localItem.albumId;
                            String albumart=localItem.albumart;
                            Intent intent;
                            intent=new Intent(getContext(),Album_songs.class);
                            intent.putExtra("albumName",albumName);
                            intent.putExtra("albumId",albumId);
                            startActivity(intent);
                            getActivity().overridePendingTransition(R.anim.activity_fade_in, R.anim.stay);
                        }

                        @Override
                        public void onLongItemClick(View view, int position) {
                            // do whatever
                        }
                    })
            );
        }


}