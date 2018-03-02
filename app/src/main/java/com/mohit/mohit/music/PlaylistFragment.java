package com.mohit.mohit.music;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;

public class PlaylistFragment extends Fragment {
    ArrayList playlistArray;
    View view;
    RecyclerView recyclerView;
    CardView RecentlyAdded;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.playlist_fragment, container, false);
        RecentlyAdded=(CardView)view.findViewById(R.id.recently_added);
        RecentlyAdded.setOnClickListener(clicked);
        MobileAds.initialize(getContext(),"ca-app-pub-8446341903786610~8385161887");
        AdView mAdView = (AdView) view.findViewById(R.id.on_my_app_ad);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        return view;

    }


    private View.OnClickListener clicked=new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.recently_added:
                    startActivity(new Intent(getActivity(), RecentlyAdded.class));
                    break;
            }
        }
    };

}
