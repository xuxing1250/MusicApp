package com.example.administrator.musicapp;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2016/6/16.
 */
public class PlayerService extends Service {
    private MediaPlayer mMediaPlayer=new MediaPlayer();
    private String path;        //路径
    private boolean isPause;    //是否暂停
    private boolean isPlay;     //是否播放
    private int msg;            //播放信息
    private int current ;     //当前播放位置
    private List<Mp3info> mp3infos; //播放列表数组
    private int state=1 ;             //播放模式   默认顺序播放
    private Mp3info mp3info;     //当前播放歌曲对象
    private int seek;           //指向定点位置播放
    private int currentDuration;    //当前播放位置
    private int duration;           //当前歌曲长度

    public static final String ACTION_MODE="com.exampl.administrator.musicapp.ACTION_MODE";

    private Myboradcast myboradcast;



    public PlayerService() {
    }





    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    //handle消息处理
    public  Handler handler=new Handler(){
        public void handleMessage(Message msn) {
            if(msn.what==1){
                if(mMediaPlayer!=null){
                    currentDuration=mMediaPlayer.getCurrentPosition();
                    Intent intent=new Intent();
                    intent.setAction("com.example.administrator.musicapp.ACTION_DURANTION");
                    intent.putExtra("duration",currentDuration);
                    sendBroadcast(intent);
                    handler.sendEmptyMessageDelayed(1, 1000);
                }
            }
        }
    };


    //    播放完成时自动播放下一首  循环播放
    @Override
    public void onCreate() {
        super.onCreate();
//               动态注册模式切换广播
        myboradcast=new Myboradcast();
        IntentFilter intentfilter=new IntentFilter();
        intentfilter.addAction(ACTION_MODE);
        registerReceiver(myboradcast, intentfilter);
        Log.d("service", "service create");
        mp3infos=MediaUtil.getMp3Infos(this);
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                switch (state){
                    case AppConstant.PLAY_SINGLE:               //单曲循环
                        mMediaPlayer.start();
                    break;
                case AppConstant.PLAY_MENU:                 //顺序播放
                    if (current < mp3infos.size()-1) {
                        current++;
                    } else {
                        current = 0;
                    }
                    Intent broadintent = new Intent();
                    broadintent.putExtra("current", current);
                    sendBroadcast(broadintent);
                    path = mp3infos.get(current).getUrl();
                    play(0);
                    break;
                case AppConstant.PLAY_RANDOM:               //随机播放
                    current=new Random().nextInt(mp3infos.size() - 1);
                    Log.d("current", "onCompletion: ");
                    path=mp3infos.get(current).getUrl();
                    play(0);
                    break;
                }
            }
        });


    }


//    循环播放


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        if(mMediaPlayer.isPlaying()){
            stop();
            Log.d("stop", "stop");
        }

        current=intent.getIntExtra("current", 0);
        Log.d("1", "1");
        path=mp3infos.get(current).getUrl();
        msg=intent.getIntExtra("MSG", 0);
        seek=intent.getIntExtra("Seek",-1);
        switch (msg){
            case AppConstant.PLAY_MSG:
                play(0);                                     //播放
                break;
            case AppConstant.CONTINUE_MSG:
                resume();                                   //继续播放
                break;
            case AppConstant.PAUSE_MSG:
                pause();                                    //暂停
                break;
            case AppConstant.STOP_MSG:
                stop();                                     //停止
                break;
            case AppConstant.NEXT_MSG:
                next();                                     //下一首
                break;
            case AppConstant.PRIVIOUS_MSG:
                privious();                                 //上一首
                break;
            case AppConstant.PROGRESS_CHANGE:
                 play(seek);                                //进度改变
                break;
        }

        return super.onStartCommand(intent, flags, startId);

    }



    private void privious() {
//        Intent sendIntent = new Intent(UPDATE_ACTION);
//        sendIntent.putExtra("current", current);
//        // 发送广播，将被Activity组件中的BroadcastReceiver接收到
//        sendBroadcast(sendIntent);
        play(0);
    }
//    下一首
    private void next() {

        play(0);
    }


    //播放
    private void play(int currentime) {
        try {
            mMediaPlayer.reset();           //初始化
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.prepareAsync();         //缓冲
            mMediaPlayer.setOnPreparedListener(new PreparedListener(currentime));  //注册监听器
            handler.sendEmptyMessage(1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        isPlay=true;
        isPause=false;
    }

    //暂停
    private void pause() {
        if(mMediaPlayer!=null&&isPlay){
            mMediaPlayer.pause();
            isPause=true;
            isPlay=false;
        }
    }
    //停止
    private void stop() {
        if (mMediaPlayer!=null){
            mMediaPlayer.stop();
            try {
                mMediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //继续播放
    private void resume(){
        if (isPause){
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mMediaPlayer.start();
                    Log.d("start", "start");
                }
            });
            isPause=false;
            isPlay=true;
        }
        }

    public  class Myboradcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            state=intent.getIntExtra("MSG",0);
//            switch (state){
//                case AppConstant.PLAY_SINGLE:               //单曲循环
//                    mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                        @Override
//                        public void onCompletion(MediaPlayer mp) {
//                            mMediaPlayer.start();
//                        }
//                    });
//                    break;
//                case AppConstant.PLAY_MENU:                 //顺序播放
//
//                    break;
//                case AppConstant.PLAY_RANDOM:               //随机播放
//                    current=new Random(mp3infos.size()-1).nextInt();
//                    path=mp3infos.get(current).getUrl();
//                    play();
//                    break;
//            }
        }
    }


    private final class PreparedListener implements MediaPlayer.OnPreparedListener {
        private int currentTime;

        public PreparedListener(int currentTime) {
            this.currentTime = currentTime;
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            mMediaPlayer.start(); // 开始播放
            if (currentTime > 0) { // 如果音乐不是从头播放
                mMediaPlayer.seekTo(currentTime);
            }

        }
    }
}
