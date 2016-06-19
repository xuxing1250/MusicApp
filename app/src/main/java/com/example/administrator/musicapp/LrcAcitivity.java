package com.example.administrator.musicapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
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

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class LrcAcitivity extends Activity {
    private ListView mMusicList;
    private List<Mp3info> mp3infos;
    private Context context;
    private List<HashMap<String, String>> mp3list;
    private SimpleAdapter mAdapter;
    private int post;                           //列表点击时的下标
    private int play_mode=1;                    //播放模式  默认顺序播放
    private boolean isPlay;                     //播放按钮状态
    private boolean isPause;                    //播放按钮状态
    private boolean isFirstTime=true;           // 判断是否是第一次播放
    private int current_seekbar;                //当前进度条进度
    private int duration;                       //当前音乐歌曲长度
    private String duration_formate;            //格式化后的duration
    private String duration_formate_current;

    private Button previous;                    //前一首
    private Button repeat;                      //重复播放
    private Button play;                        //播放
    private Button shuffle;                     //前一首
    private Button next;                        //前一首
    private Button list_songs;                  //列表
    private Button back_to;                     //后退


    private TextView name_songs;                //歌名
    private TextView name_albums;               //专辑

    private TextView text_duration_current;     //显示当前时间文本
    private TextView text_duration;             //显示歌曲总长文本


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
        setContentView(R.layout.activity_lrc_acitivity);
//        动态注册广播

        homeBraodcast=new HomeBraodcast();
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(ACTION_DURATION);
        registerReceiver(homeBraodcast,intentFilter);

        mp3infos =MediaUtil.getMp3Infos(this);
        mp3list = MediaUtil.getMusicMaps(mp3infos);

//        获取界面相关控件
        previous=(Button)findViewById(R.id.previous_music1);
        repeat=(Button)findViewById(R.id.shuffle_music1);
        play=(Button)findViewById(R.id.play_music1);
        shuffle=(Button)findViewById(R.id.shuffle_music1);
        next=(Button)findViewById(R.id.next_music1);
        list_songs=(Button)findViewById(R.id.list_songs);
        back_to=(Button)findViewById(R.id.back);

        name_songs=(TextView)findViewById(R.id.name_songs);
        name_albums=(TextView)findViewById(R.id.name_albums);
        mSeekbar=(SeekBar)findViewById(R.id.seek_bar2);

        text_duration=(TextView)findViewById(R.id.duration_time);
        text_duration_current=(TextView)findViewById(R.id.current_time);

        Intent intent=getIntent();
        post=intent.getIntExtra("current",0);
        current_seekbar=intent.getIntExtra("current_duration",0);
        mSeekbar.setMax((int) mp3infos.get(post).getDuration());
        mSeekbar.setProgress(current_seekbar);

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
                    intent.setClass(LrcAcitivity.this, PlayerService.class);
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
//        模式选择      弹出菜单
        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });



        //后退按钮
        back_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LrcAcitivity.this,MainActivity.class);
                intent.putExtra("current_duration",current_seekbar);
                intent.putExtra("current",post);
                startActivity(intent);
            }
        });
        //播放按钮
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pause();
            }
        });
        //上一首按钮事件
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previous();
            }
        });
        //下一首按钮事件
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });

    }
        //显示菜单
    private void showPopupMenu(View view) {
        PopupMenu popupMenu=new PopupMenu(this,view);
        //Menu 布局
        popupMenu.getMenuInflater().inflate(R.menu.menu,popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.order:
                        play_mode = 1;            //顺序播放
                        Toast.makeText(LrcAcitivity.this, "顺序播放模式", Toast.LENGTH_SHORT).show();
                        shuffle.setBackgroundResource(R.drawable.playlist_sign);
                        break;
                    case R.id.single:
                        play_mode = 2;            //单曲循环
                        Toast.makeText(LrcAcitivity.this, "单曲循环模式", Toast.LENGTH_SHORT).show();
                        shuffle.setBackgroundResource(R.drawable.repeat_none);
                        break;
                    case R.id.random:
                        play_mode = 3;            //随机播放
                        Toast.makeText(LrcAcitivity.this, "随机播放模式", Toast.LENGTH_SHORT).show();
                        shuffle.setBackgroundResource(R.drawable.shuffle_none);

                        break;
                }
                return false;
            }
        });

        popupMenu.show();
    }
    //取消注册广播
    protected void onStop() {
        super.onStop();
        unregisterReceiver(homeBraodcast);
    }
    //跟新进度条和下方文字
    private void update() {
        Mp3info mp3Info = mp3infos.get(post);
    //获取歌曲长度 设定seekbar最大值
        duration=(int)mp3Info.getDuration();
        mSeekbar.setMax(duration);
        duration_formate=MediaUtil.formatTime(mp3Info.getDuration());      //整首歌曲长度
        duration_formate_current=MediaUtil.formatTime(current_seekbar);    // 当前歌曲进度
        text_duration.setText(duration_formate);
        text_duration_current.setText(duration_formate_current);
    }


    //随机播放
    private void random(){
        Intent intent=new Intent("com.exampl.administrator.musicapp.ACTION_MODE");
        intent.putExtra("MSG", AppConstant.PLAY_RANDOM);
        sendBroadcast(intent);
        Toast.makeText(LrcAcitivity.this, "已设置为随机播放", Toast.LENGTH_SHORT).show();
        play_mode=3;
    }
    //顺序播放
    private void order(){
        Intent intent=new Intent("com.exampl.administrator.musicapp.ACTION_MODE");
        intent.putExtra("MSG",AppConstant.PLAY_MENU);
        sendBroadcast(intent);
        Toast.makeText(LrcAcitivity.this, "已设置为顺序播放", Toast.LENGTH_SHORT).show();
        play_mode=2;

    }

    //单曲循环
    private void repeat() {
        Intent intent=new Intent("com.exampl.administrator.musicapp.ACTION_MODE");
        intent.putExtra("MSG",AppConstant.PLAY_SINGLE);
        sendBroadcast(intent);
        Toast.makeText(LrcAcitivity.this, "已设置为单曲循环", Toast.LENGTH_SHORT).show();
        play_mode=1;

    }

    private void pause(){
        Intent intent=new Intent();
//        if(isFirstTime){                                          //判断是否是第一次播放
//            intent.putExtra("current",0);
//            intent.putExtra("MSG", AppConstant.PLAY_MSG);
//            intent.setClass(MainActivity.this, PlayerService.class);
//            startService(intent);               //启动服务
//            play.setBackgroundResource(R.drawable.pause);
//            isFirstTime=false;
//            isPlay=true;
//        }else {
        if (isPlay){                                                //如果是播放状态 则暂停
            intent.putExtra("current",post);
            intent.putExtra("MSG",AppConstant.PAUSE_MSG);
            intent.setClass(LrcAcitivity.this, PlayerService.class);
            startService(intent);               //启动服务
            play.setBackgroundResource(R.drawable.play);
            isPlay=false;
            isPause=true;
        }else if(isPause){                                           //如果是暂停状态 则播放
            intent.putExtra("current",post);
            intent.putExtra("MSG",AppConstant.CONTINUE_MSG);
            intent.setClass(LrcAcitivity.this, PlayerService.class);
            startService(intent);               //启动服务
            play.setBackgroundResource(R.drawable.pause);
            isPlay=true;
            isPause=false;
        }
//        }
    }


    //下一首
    private void next(){
        if(play_mode==AppConstant.PLAY_RANDOM){             //随机模式下 下一首任然为随机
            post=new Random().nextInt(mp3infos.size()-1);
            update();
            Intent intent=new Intent();
            intent.putExtra("current",post);
            intent.putExtra("MSG",AppConstant.NEXT_MSG);
            intent.setClass(LrcAcitivity.this, PlayerService.class);
            startService(intent);               //启动服务
        }else {
            if (post==mp3infos.size()-1){
                Toast.makeText(LrcAcitivity.this, "没有下一首了", Toast.LENGTH_SHORT).show();
            }else {
                post=post+1;
                update();
                Intent intent=new Intent();
                intent.putExtra("current",post);
                intent.putExtra("MSG",AppConstant.NEXT_MSG);
                intent.setClass(LrcAcitivity.this, PlayerService.class);
                startService(intent);                           //启动服务
            }
        }

    }

    //上一首
    private void previous(){
        if(play_mode==AppConstant.PLAY_RANDOM){                 //随机模式下 上一首任然为随机
            post=new Random().nextInt(mp3infos.size()-1);
            update();
            Intent intent=new Intent();
            intent.putExtra("current",post);
            intent.putExtra("MSG",AppConstant.PRIVIOUS_MSG);
            intent.setClass(LrcAcitivity.this, PlayerService.class);
            startService(intent);               //启动服务
        }else {
            if (post==0){
                Toast.makeText(LrcAcitivity.this, "没有上一首了", Toast.LENGTH_SHORT).show();
            }else {
                post=post-1;
                update();
                Intent intent=new Intent();
                intent.putExtra("current",post);
                intent.putExtra("MSG",AppConstant.PRIVIOUS_MSG);
                intent.setClass(LrcAcitivity.this, PlayerService.class);
                startService(intent);                                           //启动服务
            }
        }
    }

    public class HomeBraodcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            current_seekbar=intent.getIntExtra("duration",0);
            Mp3info mp3Info=mp3infos.get(post);
            duration=(int)mp3Info.getDuration();
            mSeekbar.setProgress(current_seekbar);
            duration_formate=MediaUtil.formatTime(duration);                    //整首歌曲长度
            duration_formate_current=MediaUtil.formatTime(current_seekbar);     // 当前歌曲进度
            text_duration.setText(duration_formate);
            text_duration_current.setText(duration_formate_current);
        }
    }
}
