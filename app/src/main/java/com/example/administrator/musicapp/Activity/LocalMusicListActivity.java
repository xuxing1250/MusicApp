package com.example.administrator.musicapp.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.administrator.musicapp.LrcContent;
import com.example.administrator.musicapp.MediaUtil;
import com.example.administrator.musicapp.Mp3info;
import com.example.administrator.musicapp.MusicListAdapter;
import com.example.administrator.musicapp.R;
import com.example.administrator.musicapp.Viewpager.MypagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/7/3.
 */
public class LocalMusicListActivity extends Activity {
    private ViewPager mViewPager;
    private View mView1,mView2,mView3,mView4;
    private List<View> mViewList;
    private MypagerAdapter mMypagerAdapter;
    private ListView mListView;
    private SimpleAdapter mAdapter;
    private List<Mp3info> mMp3infos;
    private List<HashMap<String, String>> mMp3list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loacl_music_list);
        mViewList=new ArrayList<View>();
        /**
            Viewpager 分屏
         */
        LayoutInflater mLy = getLayoutInflater().from(this);
        mView1=mLy.inflate(R.layout.listlayout1,null);
        mView2=mLy.inflate(R.layout.listlayout2,null);
        mView3=mLy.inflate(R.layout.listlayout3,null);
        mView3=mLy.inflate(R.layout.listlayout4,null);

        mViewList.add(mView1);
        mViewList.add(mView2);
        mViewList.add(mView3);
        mViewList.add(mView4);
        getView();
        /**
         * ViewPager 适配器
         */
        mMypagerAdapter = new MypagerAdapter(mViewList);
        mViewPager.setAdapter(mMypagerAdapter);

        /**
         * ListView 适配器
         */
        mMp3infos = MediaUtil.getMp3Infos(this);
        mMp3list = MediaUtil.getMusicMaps(mMp3infos);
        mAdapter=new SimpleAdapter(this,mMp3list,R.layout.music_list_item_layout,new String[]{"title", "Artist", "Duration"},
                new int[]{R.id.music_title, R.id.music_Artist, R.id.music_duration} );


        mListView.setAdapter(mAdapter);

    }
    private void getView(){
        mViewPager = (ViewPager)findViewById(R.id.view_pager2);
        mListView = (ListView)mView1.findViewById(R.id.listlay_listview);
    }
}
