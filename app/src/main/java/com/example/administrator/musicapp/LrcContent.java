package com.example.administrator.musicapp;

/**
 * Created by Administrator on 2016/6/23.
 */

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**

  ----------------------------------歌词类-------------------------------

 */
public class LrcContent implements Comparable<LrcContent> {
    public static  final String TAG="LrcRow";

    private String lrc;                             //当前该行歌词内容            关了灯把房间整理好
    private String lrcTime;                         //当前歌词开始播放的时间       格式如下 [02:34.14]
    /** 该行歌词要开始播放的时间，由[02:34.14]格式转换为long型，
     * 即将2分34秒14毫秒都转为毫秒后 得到的long型值：time=02*60*1000+34*1000+14
     */
    private long   time;                            //歌词时间

    public LrcContent(String lrc,String lrcTime,long time){           //
        this.lrc=lrc;
        this.lrcTime=lrcTime;
        this.time=time;
        Log.d(TAG,"  lrcTime--"+lrcTime+"  time--"+time+"  lrc--"+lrc);

    }

    public String getLrc() {
        return lrc;
    }

    public void setLrc(String lrc) {
        this.lrc = lrc;
    }

    public String getLrcTime() {
        return lrcTime;
    }

    public void setLrcTime(String lrcTime) {
        this.lrcTime = lrcTime;
    }

    public long getTime() {
        return time;
    }


    public void setTime(long time) {
        this.time = time;
    }
    public String  toString (){
        return "[" + lrcTime + " ]"  + lrc;
    }

    //创建歌词容器
    public static List<LrcContent> createlist(String singleLineLrc){
        /**
         一行歌词只有一个时间的  例如：徐佳莹   《我好想你》
         [01:15.33]我好想你 好想你

         一行歌词有多个时间的  例如：草蜢 《失恋战线联盟》
         [02:34.14][01:07.00]当你我不小心又想起她
         [02:45.69][02:42.20][02:37.69][01:10.60]就在记忆里画一个叉
         **/
        List<LrcContent> lrcContentList= null;
        try {
            if(singleLineLrc.indexOf("[") != 0 || singleLineLrc.indexOf("]") != 9 ){
                return null;
            }
            //找到最后一个‘]'的位置
            int lastindexOfRightBracket=singleLineLrc.lastIndexOf("]");
            String  lrcRow=singleLineLrc.substring(lastindexOfRightBracket+1, singleLineLrc.length());
//            Log.d(TAG,lrcRow);
            //[02:45.69][02:42.20][02:37.69][01:10.60] --------> -02:45.69-02:42.20-02:37.69-01:10.60
            String times=singleLineLrc.substring(0,lastindexOfRightBracket+1).replace("[","-").replace("]", "-");
//            Log.d(TAG,times);

            String AarryTimes[]=times.split("-");
            for (int i=0;i<AarryTimes.length;i++){
//                Log.d(TAG,AarryTimes[i]);

            }


            lrcContentList = new ArrayList<LrcContent>();
            for (String temp:AarryTimes){
                if(temp.trim().length() == 0){
                    continue;
                }
                LrcContent lrcContent=new LrcContent(lrcRow,temp,formate(temp));
                lrcContentList.add(lrcContent);

    //            temp = temp.replace('.', ':');
    //            String[] timearray=temp.split(":");
    //            for (int i=0;i<timearray.length;i++){
    //                Log.d("temp","--------------"+timearray[i]);
    //            }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return lrcContentList;

    }

    private static long formate(String temp) {
        //因为给如的字符串的时间格式为XX:XX.XX,返回的long要求是以毫秒为单位
        //将字符串 XX:XX.XX 转换为 XX:XX:XX
        temp = temp.replace('.', ':');
        //将字符串 XX:XX:XX 拆分
        String[] timeData = temp.split(":");
        for (int i=0;i<timeData.length;i++){
//                Log.d("temp-format","--------------"+timeData[i]);
            }
        // mm:ss:SS
        int minute = Integer.parseInt(timeData[0]);
//        Log.d("minute","-----------"+minute);
        int second = Integer.parseInt(timeData[1]);
//        Log.d("second","-----------"+second);
        int millisecond = Integer.parseInt(timeData[2]);
//        Log.d("milisecond","-----------"+millisecond);


        return minute * 60 * 1000 + second* 1000 +millisecond;
    }

    @Override
    public int compareTo(LrcContent another) {
        return (int)(this.time - another.time);
    }
}
