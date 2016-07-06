package com.example.administrator.musicapp.Activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.musicapp.AppConstant;
import com.example.administrator.musicapp.LrcAcitivity;
import com.example.administrator.musicapp.MainActivity;
import com.example.administrator.musicapp.MediaUtil;
import com.example.administrator.musicapp.Mp3info;
import com.example.administrator.musicapp.PlayerService;
import com.example.administrator.musicapp.R;
import com.example.administrator.musicapp.Viewpager.MypagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FirtsActivity extends Activity {
    private View mView1, mView2, mView3;
    private List<View> mListView;
    private PagerAdapter pagerAdaper;
    private List<String> mTitleList;
    private ViewPager mViewPager;

    private ImageView mLike;
    private ImageView mList;
    private ImageView mDownload;
    private ImageView mRecent;
    private ImageView mMusicStore;
    private ImageView mChannel;
    private ImageView mChartGroups;
    private ImageView mTolrc;

    private TextView mLocalList;
    private TextView mAlbum;
    private TextView mSinger;

    private ListView mMusicList;
    private List<Mp3info> mMp3infos;
    private Context mContext;
    private List<HashMap<String, String>> mMp3list;
    private SimpleAdapter mAdapter;
    private int mIndex=0;                           //列表点击时的下标
    private boolean isPlay;                         //播放按钮状态
    private boolean isPause;                        //播放按钮状态
    private boolean isFirstTime=true;               // 判断是否是第一次播放
    private int mCurrentTime;                       //当前进度条进度
    private int duration;                           //当前音乐歌曲长度
    private String duration_formate;                //格式化后的duration
    private String duration_formate_current;


    private TextView text_duration;                 //当前时间


    private SeekBar mSeekbar;       //滑动进度条
    private HomeBraodcast2 mHomeBraodcast;

    public static final  String ACTION_DURATION="com.example.administrator.musicapp.ACTION_DURANTION";            //当前播放为位置
    public static final  String ACTION_DURATION_POST="com.example.administrator.musicapp.ACTION_DURANTION_POST";
    public static final  String TAG="com.example.administrator.musicapp.FirtsActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menuactivity);
        LayoutInflater mLy=getLayoutInflater().from(this);

        Intent intent=getIntent();
        mIndex=intent.getIntExtra("current",0);
        mCurrentTime =intent.getIntExtra("current_duration",0);

        //获取歌词集合对象
        mMp3infos = MediaUtil.getMp3Infos(this);


        mViewPager=(ViewPager)findViewById(R.id.view_pager);
        mView1=mLy.inflate(R.layout.layout1,null);
        mView2=mLy.inflate(R.layout.layout2,null);
        mView3=mLy.inflate(R.layout.layout3,null);

        mListView = new ArrayList<View>();
        mTitleList = new ArrayList<String>();
        mListView.add(mView1);
        mListView.add(mView2);
        mListView.add(mView3);

//        mTitleList.add("看");
//        mTitleList.add("听");
//        mTitleList.add("找");

        pagerAdaper = new MypagerAdapter(mListView);
        mViewPager.setAdapter(pagerAdaper);
        getView();
        update();
        mLocalList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(FirtsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        //进度条设置
        mSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mCurrentTime = progress;
                    mIndex=getIntent().getIntExtra("current",0);
                    Log.d("SeekBar","mIndex"+mIndex);
                    Intent intent = new Intent();
                    intent.putExtra("current", mIndex);
                    intent.putExtra("MSG", AppConstant.PROGRESS_CHANGE);
                    intent.putExtra("Seek", mCurrentTime);
                    intent.setClass(FirtsActivity.this, PlayerService.class);
                    startService(intent);               //启动服务
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });
       //切换歌词界面
       mTolrc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FirtsActivity.this, LrcAcitivity.class);
                intent.putExtra("current", mIndex);
                Log.d(TAG, "-------" + mIndex);
                intent.putExtra("current_duration", mCurrentTime);
                startActivity(intent);

//                淡入淡出效果
//                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//                左右滑动
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);

            }
        });

        //切换歌曲列表界面
        mLocalList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FirtsActivity.this,LocalMusicListActivity.class);
                intent.putExtra("current",mIndex);
                startActivity(intent);
            }
        });
    }
//    动态注册广播
    protected void onResume() {
        super.onResume();
        mHomeBraodcast = new HomeBraodcast2();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_DURATION);
        intentFilter.addAction(ACTION_DURATION_POST);
        registerReceiver(mHomeBraodcast, intentFilter);

    }

    //取消注册广播
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mHomeBraodcast);
    }

    //跟新进度条和下方文字
    private void update() {
        Mp3info mp3Info = mMp3infos.get(mIndex);
        //获取歌曲长度 设定seekbar最大值
        duration=(int)mp3Info. getDuration();
        mSeekbar.setMax(duration);
        mAlbum.setText(mp3Info.getTitle());
        mSinger.setText(mp3Info.getArtist());

    }

    private void getView(){
        mLike = (ImageView)mView1. findViewById(R.id.image_like);
        mList = (ImageView)mView1. findViewById(R.id.image_list);
        mDownload = (ImageView)mView1. findViewById(R.id.image_download);
        mRecent = (ImageView)mView1. findViewById(R.id.image_recent);
        mMusicStore = (ImageView)mView1. findViewById(R.id.image_songData);
        mChannel = (ImageView)mView1. findViewById(R.id.image_channel);
        mChartGroups = (ImageView)mView1. findViewById(R.id.image_groups);
        mLocalList=(TextView)mView1. findViewById(R.id.text_sdCard);

        mTolrc = (ImageView)findViewById(R.id.toLrc);
        mAlbum = (TextView) findViewById(R.id.songname);
        mSinger = (TextView) findViewById(R.id.singer);

        mSeekbar=(SeekBar)findViewById(R.id.mSeekbar);
    }
    class HomeBraodcast2 extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String s=intent.getAction();
            Log.d("FirastActivity","----------"+s);
            switch (s){
                case "com.example.administrator.musicapp.ACTION_DURANTION":
                    mCurrentTime = intent.getIntExtra("duration",0);
                    mIndex = intent.getIntExtra("current",0);
                    Log.d("First","mIndex--------"+mIndex);
                    mSeekbar.setProgress(mCurrentTime);
                    break;
                case "com.example.administrator.musicapp.ACTION_DURANTION_POST":
                    mIndex = intent.getIntExtra("current",0);
                    Log.d("First","mIndex--------"+mIndex);
                    update();
                    break;
            }
        }
    }

}
