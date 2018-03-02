package com.mohit.mohit.music;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;


public class SongsFragment extends Fragment {
    RecyclerView recyclerView;
    MediaCursorAdapter mediaAdapter;
    String currentFile;
    Intent serviceIntent;
    SharedPreferences sharedPref;
    View view;
    ArrayList arrayList;
    int pos;
    Intent newSong;
    public static String SONG="com.mohit,mohit.music.action.song";
    public static final String NEW_SONG="com.example.mohit.music.newsong";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.songs_fragment, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        newSong=new Intent(NEW_SONG);
        if (Build.VERSION.SDK_INT >= 23) {
            // Marshmallow+
            if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE  ) != PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE  ) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        123);
            } else {
                collectdata(getContext());
            }
        } else {
            collectdata(getContext());
            // Pre-Marshmallow
        }


        try{
            serviceIntent=new Intent(getActivity(),MusicService.class);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        return view;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 123: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    collectdata(getContext());
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
   /* public void mainActivity(View v)
    {
        startActivity(new Intent(this.getContext(),Main2Activity.class));
        getActivity().overridePendingTransition(R.anim.slide_up, R.anim.stay);
    }*/

void collectdata(final Context context)
{
    arrayList=SongsLoader.getSongs(context);


        mediaAdapter = new MediaCursorAdapter(this.getContext(),arrayList);
        recyclerView.setAdapter(mediaAdapter);
        recyclerView.setItemViewCacheSize(20);
        final RecyclerViewFastScroller fastScroller = (RecyclerViewFastScroller) view.findViewById(R.id.fastscroller);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false)
        {
            @Override

            public void onLayoutChildren(final RecyclerView.Recycler recycler, final RecyclerView.State state) {
                super.onLayoutChildren(recycler, state);
                //TODO if the items are filtered, considered hiding the fast scroller here
                final int firstVisibleItemPosition = findFirstVisibleItemPosition();
                if (firstVisibleItemPosition != 0) {
                    // this avoids trying to handle un-needed calls
                    if (firstVisibleItemPosition == -1)
                        //not initialized, or no items shown, so hide fast-scroller
                        fastScroller.setVisibility(View.GONE);
                    return;
                }
                final int lastVisibleItemPosition = findLastVisibleItemPosition();
                int itemsShown = lastVisibleItemPosition - firstVisibleItemPosition + 1;
                //if all items are shown, hide the fast-scroller
                fastScroller.setVisibility(mediaAdapter.getItemCount() > itemsShown ? View.VISIBLE : View.GONE);
            }
        });
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        fastScroller.setViewsToUse(R.layout.recycler_view_fast_scroller__fast_scroller, R.id.fastscroller_bubble, R.id.fastscroller_handle);
        fastScroller.setRecyclerView(recyclerView);

        recyclerView.addOnItemTouchListener(
                new RecyclerItemTouch(this.getContext(), recyclerView, new RecyclerItemTouch.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // do whatever
                        view.setSelected(true);
                        Song localItem=(Song) arrayList.get(position);
                        pos=position;
                        currentFile=localItem.path;
                        sharedPref = getActivity().getSharedPreferences("myPref", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putInt("pos",pos);
                        editor.putString("list","song");
                        editor.commit();

                        serviceIntent.putExtra("songName", currentFile);
                        serviceIntent.setAction(SONG);
                        try {
                            getContext().startService(serviceIntent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        getContext().sendBroadcast(newSong);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );


}
public void onResume()
{
    super.onResume();
}


}
