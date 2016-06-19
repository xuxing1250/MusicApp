package com.example.administrator.musicapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class MainActivity extends Activity {
    private ListView mMusicList;
    private List<Mp3info> mp3infos;
    private Context context;
    private List<HashMap<String, String>> mp3list;
    private SimpleAdapter mAdapter;
    private int post; //列表点击时的下标
    private int play_mode=1;  //播放模式  默认顺序播放
    private boolean isPlay; //播放按钮状态
    private boolean isPause; //播放按钮状态
    private boolean isFirstTime=true; // 判断是否是第一次播放
    private int current_seekbar;        //当前进度条进度
    private int duration;           //当前音乐歌曲长度
    private String duration_formate; //格式化后的duration
    private String duration_formate_current;

    private Button previous; //前一首
    private Button repeat;   //重复播放
    private Button play;     //播放
    private Button shuffle; //前一首
    private Button next; //前一首
    private Button mode_choise; //模式选择
    private TextView singer;       //歌唱家
    private TextView text_duration;       //时间


    private ImageButton lrc_play;   //显示歌词
    private SeekBar mSeekbar;       //滑动进度条

    private HomeBraodcast homeBraodcast;
    //自定义广播接收器


//    发送给服务的行为action
    public static final  String REPEAT="com.example.administrator.musicapp.REPEAT_MOOD";  //单曲循环
    public static final  String RANDOM="com.example.administrator.musicapp.RANDOM_MOOD";  //随机播放
    public static final  String ACTION_DURATION="com.example.administrator.musicapp.ACTION_DURANTION";  //当前播放为位置


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);

        //动态注册广播
        homeBraodcast = new HomeBraodcast();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_DURATION);
        registerReceiver(homeBraodcast, intentFilter);

        mp3infos = MediaUtil.getMp3Infos(this);
        mp3list = MediaUtil.getMusicMaps(mp3infos);
        mAdapter = new SimpleAdapter(this, mp3list, R.layout.music_list_item_layout, new String[]{
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

        Intent intent=getIntent();
        post=intent.getIntExtra("current",0);
        current_seekbar=intent.getIntExtra("current_duration",0);
        update();

        mMusicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mp3infos != null) {
                    //跟新进度条时间
                    update();
                    Intent intent = new Intent();
                    post = position;
//                    intent.putExtra("url", mp3Info.getUrl());
                    intent.putExtra("MSG", AppConstant.PLAY_MSG);
                    intent.putExtra("current", post);
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
                    current_seekbar = mSeekbar.getProgress();
                    Mp3info mp3Info = mp3infos.get(post);
                    duration = (int) mp3Info.getDuration();
                    mSeekbar.setMax(duration);
                    update();
                    Intent intent = new Intent();
                    intent.putExtra("current", post);
                    intent.putExtra("MSG", AppConstant.PROGRESS_CHANGE);
                    intent.putExtra("Seek", current_seekbar);
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
                intent.putExtra("current", post);
                intent.putExtra("current_duration", current_seekbar);
                startActivity(intent);
            }
        });
    }
    //取消注册广播
    protected void onStop() {
        super.onStop();
        unregisterReceiver(homeBraodcast);
    }
    //    跟新进度条和下方文字
    private void update() {
        Mp3info mp3Info = mp3infos.get(post);
//                    获取歌曲长度 设定seekbar最大值
        duration=(int)mp3Info.getDuration();
        singer.setText(mp3Info.getArtist());

        mSeekbar.setMax(duration);
        duration_formate=MediaUtil.formatTime(mp3Info.getDuration());//整首歌曲长度
        duration_formate_current=MediaUtil.formatTime(current_seekbar);   // 当前歌曲进度
        text_duration.setText(duration_formate_current + "/" + duration_formate);
    }

    public class HomeBraodcast extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            current_seekbar=intent.getIntExtra("duration",0);
            Mp3info mp3Info=mp3infos.get(post);
            duration=(int)mp3Info.getDuration();
            mSeekbar.setProgress(current_seekbar);
            duration_formate=MediaUtil.formatTime(duration);//整首歌曲长度
            duration_formate_current=MediaUtil.formatTime(current_seekbar);   // 当前歌曲进度
            text_duration.setText(duration_formate_current + "/" + duration_formate);
        }
    }

}


