package com.example.administrator.musicapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.administrator.musicapp.Activity.FirtsActivity;
import com.example.administrator.musicapp.Viewpager.ViewPagerActivity;

import java.util.HashMap;
import java.util.List;

public class MainActivity extends Activity  {
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
    private TextView singer;                        //歌唱家
    private Button mViewpager;


    private TextView text_duration;                 //当前时间
    private ImageButton lrc_play;   //显示歌词


    private SeekBar mSeekbar;       //滑动进度条
    private HomeBraodcast mHomeBraodcast;


    private GestureDetector gestureDetector;    //手势监听器


//    发送给服务的行为action
    public static final  String REPEAT="com.example.administrator.musicapp.REPEAT_MOOD";  //单曲循环

    //自定义广播接收器
    public static final String TAG="MainActivity";
    public static final  String ACTION_DURATION="com.example.administrator.musicapp.ACTION_DURANTION";            //当前播放为位置
    public static final  String ACTION_DURATION_POST="com.example.administrator.musicapp.ACTION_DURANTION_POST";  //当前播放为位置



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);

        mMp3infos = MediaUtil.getMp3Infos(this);
        mMp3list = MediaUtil.getMusicMaps(mMp3infos);
        mAdapter = new SimpleAdapter(this, mMp3list, R.layout.music_list_item_layout, new String[]{
                "title", "Artist", "Duration"}, new int[]{
                R.id.music_title, R.id.music_Artist, R.id.music_duration
        });
        mMusicList = (ListView) findViewById(R.id.music_list);
        mMusicList.setAdapter(mAdapter);

//        获取界面相关控件
        lrc_play = (ImageButton) findViewById(R.id.music_album);
        mSeekbar = (SeekBar) findViewById(R.id.seek_bar);
        text_duration = (TextView) findViewById(R.id.singer);
        singer = (TextView) findViewById(R.id.duration);
        mViewpager=(Button)findViewById(R.id.play_mode);

        Intent intent=getIntent();
        mIndex =intent.getIntExtra("current",0);
        mCurrentTime =intent.getIntExtra("current_duration",0);
        update();

        mViewpager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, FirtsActivity.class);
                startActivity(intent);
            }
        });

        mMusicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mMp3infos != null) {
                    //跟新进度条时间

                    mIndex = position;
                    update();
                    Intent intent = new Intent();
//                    intent.putExtra("url", mp3Info.getUrl());
                    intent.putExtra("MSG", AppConstant.CLICK_PLAY_MSG);
                    intent.putExtra("current", mIndex);
                    intent.setClass(MainActivity.this, PlayerService.class);
                    startService(intent);       //启动服务
                    isFirstTime = false;
                    isPlay = true;
                }
            }
        });


//        进度条设置
        mSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mCurrentTime =progress;
                    Log.d(TAG,"progress"+progress);
                    duration_formate_current=MediaUtil.formatTime(mCurrentTime);   // 当前歌曲进度
                    text_duration.setText(duration_formate_current + "/" + duration_formate);
                    Intent intent = new Intent();
                    intent.putExtra("current", mIndex);
                    intent.putExtra("MSG", AppConstant.PROGRESS_CHANGE);
                    intent.putExtra("Seek", mCurrentTime);
                    intent.setClass(MainActivity.this, PlayerService.class);
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
        lrc_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LrcAcitivity.class);
                intent.putExtra("current", mIndex);
                intent.putExtra("current_duration", mCurrentTime);
                startActivity(intent);

//                淡入淡出效果
//                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//                左右滑动
//                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //动态注册广播
        mHomeBraodcast = new HomeBraodcast();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_DURATION);
        registerReceiver(mHomeBraodcast, intentFilter);

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
        singer.setText(mp3Info. getArtist());

        mSeekbar.setMax(duration);
        duration_formate=MediaUtil.formatTime(mp3Info.getDuration());//整首歌曲长度
        duration_formate_current=MediaUtil.formatTime(mCurrentTime);   // 当前歌曲进度
        text_duration.setText(duration_formate_current + "/" + duration_formate);
    }




    class HomeBraodcast extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
//            mCurrentTime =intent.getIntExtra("duration",0);
//            Mp3info mp3Info= mMp3infos.get(mIndex);
//            duration=(int)mp3Info.getDuration();
//            mSeekbar.setProgress(mCurrentTime);
//            duration_formate=MediaUtil.formatTime(duration);//整首歌曲长度
//            duration_formate_current=MediaUtil.formatTime(mCurrentTime);   // 当前歌曲进度
//            text_duration.setText(duration_formate_current + "/" + duration_formate);

        }
    }

}


