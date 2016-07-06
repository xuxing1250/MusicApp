package com.example.administrator.musicapp.Viewpager;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.musicapp.R;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerActivity extends Activity {
    private View mView1, mView2, mView3;
    private List<View> mListView;
    private PagerAdapter pagerAdaper;
    private List<String> mTitleList;
    private ViewPager mViewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menuactivity);


    }
}
