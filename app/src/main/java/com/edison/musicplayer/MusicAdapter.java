package com.edison.musicplayer;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by 相思湖陈建州 on 2016/4/26.
 */
public class MusicAdapter extends BaseAdapter{
    private String[] songdata;
    private Context context;

    public MusicAdapter(String[] data,Context context){
        super();
        this.songdata = data;
        this.context = context;

    }
    @Override
    public int getCount(){
        return songdata.length;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View view = View.inflate(context,R.layout.music2,null);

        TextView tvMusicitem = (TextView)view.findViewById(R.id.geming2);
        tvMusicitem.setText(songdata[position]);
        return view;
    }
    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
