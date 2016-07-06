package com.example.administrator.musicapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.util.Xml;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.musicapp.Activity.FirtsActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class LrcAcitivity extends Activity {
    private ListView mMusicList;
    private List<Mp3info> mMp3infos;
    private Context context;
    private List<HashMap<String, String>> mp3list;
    private SimpleAdapter mAdapter;
    private int mIndex;                           //列表点击时的下标
    private int play_mode = 1;                    //播放模式  默认顺序播放
    private boolean isPlay;                     //播放按钮状态
    private boolean isPause;                    //播放按钮状态
    private boolean isFirstTime = true;           // 判断是否是第一次播放
    private int current_seekbar;                //当前进度条进度
    private int duration;                       //当前音乐歌曲长度
    private String duration_formate;            //格式化后的duration
    private String duration_formate_current;
    private String borad;
    private String songs;                       //歌曲名
    private String album;                       //专辑名
    private String lrcpath = "url/storage/sdcard/Music/bigclock.lrc/";                        //歌曲目录


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
    private ImageView background;               //背景图案

    public LrcTextView lrcTextView;             //歌词文本
    private Mp3info mp3info;
    private String mLrcPath;                    //歌词路径


    private SeekBar mSeekbar;                   //滑动进度条

    private String mp3Path;                     //mp3 url；
    private Bitmap bp;                          //背景图案

    //自定义广播接收器
    private HomeBraodcast homeBraodcast;
    //自定义歌词管理器

    private List<LrcContent> lrcRows;
    private DefaultLrcBuilder defaultLrcBuilder;


    //发送给服务的行为action
    public static final String REPEAT = "com.example.administrator.musicapp.REPEAT_MOOD";  //单曲循环
    public static final String RANDOM = "com.example.administrator.musicapp.RANDOM_MOOD";  //随机播放
    public static final String ACTION_DURATION = "com.example.administrator.musicapp.ACTION_DURANTION";  //当前播放为位置
    public static final String ACTION_DURATION_POST = "com.example.administrator.musicapp.ACTION_DURANTION_POST";  //当前播放为位置
    public static final String ACTION_PLAY_COMPLETE = "com.exampl.administrator.musicapp.ACTION_PLAY_COMPLETE";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lrc_acitivity);


        mMp3infos = MediaUtil.getMp3Infos(this);
        mp3list = MediaUtil.getMusicMaps(mMp3infos);
        mp3info= mMp3infos.get(mIndex);
        //获取音乐ID long 专辑ID；
        mp3Path=mp3info.getUrl();
        long id=mp3info.getId();
        long albumID=mp3info.getAlbumId();



        //获取专辑封面
//        bp=MediaUtil.getArtwork(context,id,albumID,true,true);

        //获取歌词对象
        defaultLrcBuilder = new DefaultLrcBuilder();
        mLrcPath=mp3info.getUrl().replace(".mp3",".lrc");
//        Log.d("getcontent","--------"+mLrcPath);

//        String lrc=getContent(mLrcPath);
        String lrc=getContent("test.lrc");
        lrcRows = defaultLrcBuilder.getLrcRows(lrc);

//        获取界面相关控件
        previous = (Button) findViewById(R.id.previous_music1);
        repeat = (Button) findViewById(R.id.shuffle_music1);
        play = (Button) findViewById(R.id.play_music1);
        shuffle = (Button) findViewById(R.id.shuffle_music1);
        next = (Button) findViewById(R.id.next_music1);
        list_songs = (Button) findViewById(R.id.list_songs);
        back_to = (Button) findViewById(R.id.back);
        background=(ImageView)findViewById(R.id.background);

        name_songs = (marqueeText) findViewById(R.id.name_songs);
        name_albums = (marqueeText) findViewById(R.id.name_albums);
        mSeekbar = (SeekBar) findViewById(R.id.seek_bar2);

        text_duration = (TextView) findViewById(R.id.duration_time);
        text_duration_current = (TextView) findViewById(R.id.current_time);

        //设定歌词
        lrcTextView = (LrcTextView) findViewById(R.id.lrc_songs);
        lrcTextView.setLrcRows(lrcRows);
//        Log.d("lrcRows", "----" + lrcRows);



        //设置背景
//        background.setImageBitmap(bp);

        Intent intent = getIntent();
        mIndex = intent.getIntExtra("current", 0);
        current_seekbar = intent.getIntExtra("current_duration", 0);
        mSeekbar.setMax((int) mMp3infos.get(mIndex).getDuration());
        mSeekbar.setProgress(current_seekbar);
        updateMusicText();


        //进度条设置
        mSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    current_seekbar = progress;
                    duration_formate_current = MediaUtil.formatTime(current_seekbar);    // 当前歌曲进度
                    text_duration_current.setText(duration_formate_current);

                    Intent intent = new Intent();
                    intent.putExtra("current", mIndex);
                    Log.d("---------------", "mIndex" + mIndex);
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
                Intent intent = new Intent(LrcAcitivity.this, FirtsActivity.class);
                intent.putExtra("current_duration", current_seekbar);
                intent.putExtra("current", mIndex);
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

    @Override
    protected void onResume() {
        super.onResume();
        //动态注册广播
        homeBraodcast = new HomeBraodcast();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_DURATION);
        intentFilter.addAction(ACTION_DURATION_POST);
        intentFilter.addAction(ACTION_PLAY_COMPLETE);
        registerReceiver(homeBraodcast, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(homeBraodcast);
    }

    //显示菜单
    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        //Menu 布局
        popupMenu.getMenuInflater().inflate(R.menu.menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.order:
                        play_mode = 1;            //顺序播放
                        shuffle.setBackgroundResource(R.drawable.playlist_sign);
                        order();
                        break;
                    case R.id.single:
                        play_mode = 2;            //单曲循环
                        shuffle.setBackgroundResource(R.drawable.repeat_none);
                        repeat();
                        break;
                    case R.id.random:
                        play_mode = 3;            //随机播放
                        shuffle.setBackgroundResource(R.drawable.shuffle_none);
                        random();
                        break;
                }
                updateMusicText();
                return false;
            }
        });

        popupMenu.show();
    }

    //取消注册广播
    protected void onStop() {
        super.onStop();
    }

    //跟新进度条和下方文字
    private void update() {
        Mp3info mp3Info = mMp3infos.get(mIndex);
        //获取歌曲长度 设定seekbar最大值
        duration = (int) mp3Info.getDuration();
        mSeekbar.setMax(duration);
        duration_formate = MediaUtil.formatTime(mp3Info.getDuration());      //整首歌曲长度
        duration_formate_current = MediaUtil.formatTime(current_seekbar);    // 当前歌曲进度
        text_duration.setText(duration_formate);
        text_duration_current.setText(duration_formate_current);
    }


    //随机播放
    private void random() {
        Intent intent = new Intent("com.exampl.administrator.musicapp.ACTION_MODE");
        intent.putExtra("MSG", AppConstant.PLAY_RANDOM);
        sendBroadcast(intent);
        Toast.makeText(LrcAcitivity.this, "已设置为随机播放", Toast.LENGTH_SHORT).show();
        play_mode = 3;
    }

    //顺序播放
    private void order() {
        Intent intent = new Intent("com.exampl.administrator.musicapp.ACTION_MODE");
        intent.putExtra("MSG", AppConstant.PLAY_MENU);
        sendBroadcast(intent);
        Toast.makeText(LrcAcitivity.this, "已设置为顺序播放", Toast.LENGTH_SHORT).show();
        play_mode = 2;

    }

    //单曲循环
    private void repeat() {
        Intent intent = new Intent("com.exampl.administrator.musicapp.ACTION_MODE");
        intent.putExtra("MSG", AppConstant.PLAY_SINGLE);
        sendBroadcast(intent);
        Toast.makeText(LrcAcitivity.this, "已设置为单曲循环", Toast.LENGTH_SHORT).show();
        play_mode = 1;

    }

    private void pause() {
        Intent intent = new Intent();
        if(isFirstTime){                                                //判断是否是第一次播放
            intent.putExtra("current",mIndex);
            intent.putExtra("MSG", AppConstant.PLAY_MSG);
            intent.setClass(LrcAcitivity.this, PlayerService.class);
            startService(intent);               //启动服务
            play.setBackgroundResource(R.drawable.pause);
            isFirstTime=false;
            isPlay=false;
            isPause=true;
        }else {
        if (isPlay) {                                                   //如果是播放状态 则暂停
            intent.putExtra("current", mIndex);
            intent.putExtra("MSG", AppConstant.PAUSE_MSG);
            intent.setClass(LrcAcitivity.this, PlayerService.class);
            startService(intent);               //启动服务
            play.setBackgroundResource(R.drawable.play);
            isPlay = false;
            isPause = true;
        } else if (isPause) {                                           //如果是暂停状态 则播放
            intent.putExtra("current", mIndex);
            intent.putExtra("MSG", AppConstant.CONTINUE_MSG);
            intent.setClass(LrcAcitivity.this, PlayerService.class);
            startService(intent);               //启动服务
            play.setBackgroundResource(R.drawable.pause);
            isPlay = true;
            isPause = false;
        }
        }
    }


    //下一首
    private void next() {
        if (play_mode == AppConstant.PLAY_RANDOM) {             //随机模式下 下一首任然为随机
            mIndex = new Random().nextInt(mMp3infos.size() - 1);
            update();
            Intent intent = new Intent();
            intent.putExtra("current", mIndex);
            intent.putExtra("MSG", AppConstant.NEXT_MSG);
            intent.setClass(LrcAcitivity.this, PlayerService.class);
            startService(intent);               //启动服务
            updateMusicText();
        } else {
            if (mIndex == mMp3infos.size() - 1) {
                Toast.makeText(LrcAcitivity.this, "没有下一首了", Toast.LENGTH_SHORT).show();
            } else {
                mIndex = mIndex + 1;
                update();
                Intent intent = new Intent();
                intent.putExtra("current", mIndex);
                intent.putExtra("MSG", AppConstant.NEXT_MSG);
                intent.setClass(LrcAcitivity.this, PlayerService.class);
                startService(intent);                           //启动服务
            }
        }
        updateMusicText();

    }

    //上一首
    private void previous() {
        if (play_mode == AppConstant.PLAY_RANDOM) {                 //随机模式下 上一首任然为随机
            mIndex = new Random().nextInt(mMp3infos.size() - 1);
            update();
            Intent intent = new Intent();
            intent.putExtra("current", mIndex);
            intent.putExtra("MSG", AppConstant.PRIVIOUS_MSG);
            intent.setClass(LrcAcitivity.this, PlayerService.class);
            startService(intent);               //启动服务
        } else {
            if (mIndex == 0) {
                Toast.makeText(LrcAcitivity.this, "没有上一首了", Toast.LENGTH_SHORT).show();
            } else {
                mIndex = mIndex - 1;
                update();
                Intent intent = new Intent();
                intent.putExtra("current", mIndex);
                intent.putExtra("MSG", AppConstant.PRIVIOUS_MSG);
                intent.setClass(LrcAcitivity.this, PlayerService.class);
                startService(intent);                                           //启动服务
            }
            updateMusicText();
        }
    }

    //    更新歌曲标题
    private void updateMusicText() {
        songs = mMp3infos.get(mIndex).getTitle();
        name_songs.setText(songs);
        album = mMp3infos.get(mIndex).getAlbum();
        name_albums.setText(album);
    }

    public class HomeBraodcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            borad = intent.getAction();
            switch (borad) {
                case ACTION_DURATION:
                    current_seekbar = intent.getIntExtra("duration", 0);
                    Mp3info mp3Info = mMp3infos.get(mIndex);
                    duration = (int) mp3Info.getDuration();
                    mSeekbar.setProgress(current_seekbar);
                    duration_formate = MediaUtil.formatTime(duration);                    //整首歌曲长度
                    duration_formate_current = MediaUtil.formatTime(current_seekbar);     // 当前歌曲进度
                    text_duration.setText(duration_formate);
                    text_duration_current.setText(duration_formate_current);
//                    获取歌曲播放的位置
                    LrcAcitivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            //滚动歌词
                            lrcTextView.seekLrcToTime(current_seekbar);
                        }
                    });
                    break;
                case ACTION_DURATION_POST:
                    mIndex=intent.getIntExtra("current",0);
                    updateMusicText();
                    Log.d("LrcActivity","ACTION_DURATION_POST"+mIndex);
                    break;
                    }
        }
    }

    public String getContent(String path) {
        String s="";
        try {
//            File f=new File(path);
//            FileInputStream fl=new FileInputStream();

            InputStreamReader isr = new InputStreamReader(getResources().getAssets().open(path));
//            InputStreamReader isr = new InputStreamReader(fl,"UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String result = "";
            String line ="";
//            byte[] mBuffer = new byte[fl.available()];
//            fl.read(mBuffer);
            while ((line = br.readLine()) != null) {
                if (line.trim().equals(""))
                    continue;
                result += line + "\r\n";
            }
            return result;
//            s = new String(mBuffer, "utf-8");


        } catch (Exception e) {
            e.printStackTrace();
        }
//        Log.d("Content", "--------" );
        return s;
    }

}





