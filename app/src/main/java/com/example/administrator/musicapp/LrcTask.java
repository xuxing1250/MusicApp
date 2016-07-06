package com.example.administrator.musicapp;

import android.content.Intent;

import java.util.TimerTask;

/**
 * Created by Administrator on 2016/6/27.
 */
public class LrcTask extends TimerTask {
    @Override
    public void run() {
        //获取歌曲播放的位置
//        LrcAcitivity.this.runOnUiThread(new Runnable() {
//            public void run() {
//                //滚动歌词
//                mLrcView.seekLrcToTime(timePassed);
//            }
//        });
        Intent intent=new Intent();
        intent.setAction("com.example.administrator.musicapp.ACTION_DURANTION_POST");
    }
}
