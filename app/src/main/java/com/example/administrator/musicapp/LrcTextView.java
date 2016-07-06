package com.example.administrator.musicapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/23.
 */
public class LrcTextView extends TextView {
    private int width=0;                //歌词视图宽度(当前的view)
    private int height=0;               //歌词视图高度
    private float textSize;                 //文本字体大小
    private float textHeight;               //文本字体高度
    private Paint currentPaint;             //当前画笔对象
    private Paint notcurrentPaint;          //非当前画笔对象
    private int   rowNum=0;                   //集合下标
    int rowY=0;
    private int rowX= 0;
    private int highLightRow ;               //高亮行的行数
    private String higtLightRowContent;     //高亮行内容
    private List<LrcContent> lrcRows;       //歌词集合
    private DefaultLrcBuilder defaultLrcBuilder;                 //歌词处理类
    private String path;                    //文件目录
    private int mLrcFontSize=24;            //歌词字体大小
    private int highLightRowY;       //当前歌词行Y坐标
    private int mPaddingY=10;             //俩行歌词之间的间距

    public LrcTextView(Context context) {
        super(context);
        init();
    }

    public LrcTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public LrcTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    public LrcTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    //初始化画笔参数
   protected void init(){
       currentPaint=new Paint();
       currentPaint.setColor(Color.RED);
       currentPaint.setTextSize(36);
       currentPaint.setTextAlign(Paint.Align.CENTER);

       notcurrentPaint=new Paint();
       notcurrentPaint.setColor(Color.WHITE);
       notcurrentPaint.setTextSize(24);
       notcurrentPaint.setTextAlign(Paint.Align.CENTER);
   }

    //获取List<LrcContent>歌词集合
    public void setLrcRows(List<LrcContent> lrcRows){
        this.lrcRows=lrcRows;
        invalidate();
    }

    /**
     * 分以下三步来绘制歌词：
     *
     *  第1步：高亮地画出正在播放的那句歌词
     *  第2步：画出正在播放的那句歌词的上面可以展示出来的歌词
     *  第3步：画出正在播放的那句歌词的下面的可以展示出来的歌词
     */


    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        //1.画出高亮部分的那句歌词
        higtLightRowContent=lrcRows.get(highLightRow).getLrc();
//        setText("");
        width=getWidth();
        height=getHeight();
        highLightRowY=height/2;
        canvas.drawText(higtLightRowContent, width/2, height/2, currentPaint);


        //2.画出高亮部分上面可现实的歌词
        rowNum =highLightRow- 1;
        rowX=width/2;
        rowY = highLightRowY - mPaddingY-mLrcFontSize ;
//        String text = lrcRows.get(rowNum).getLrc();
//        canvas.drawText(text, rowX, rowY, notcurrentPaint);
//        Log.d("text","-----------"+text);
//        Log.d("rowX", "-----------" + rowX);
//        Log.d("rowY","-----------"+rowY);
        while( rowY > -mLrcFontSize && rowNum >= 0){
            String text = lrcRows.get(rowNum).getLrc();
            canvas.drawText(text, rowX, rowY, notcurrentPaint);
            rowY -=  (mPaddingY + mLrcFontSize);
            rowNum --;
        }
        //3.画出高亮部分下面可现实的歌词
        rowNum =highLightRow+ 1;
        rowY = highLightRowY + mPaddingY + mLrcFontSize;
        while( rowY < height && rowNum <lrcRows.size()){
            String text = lrcRows.get(rowNum).getLrc();
            canvas.drawText(text, rowX, rowY, notcurrentPaint);
            rowY +=  (mPaddingY + mLrcFontSize);
            rowNum ++;
        }
    }
    //指定需要高亮的行数
    public void seekLrc(int position){
        LrcContent lrc=lrcRows.get(position);
        highLightRow = position;
        invalidate();
    }

    /**
     * 播放的时候调用该方法滚动歌词，高亮正在播放的那句歌词
     * @param time
     */
    public void seekLrcToTime(long time) {

        for (int i = 0;i < lrcRows.size(); i++) {
            LrcContent lrcCurrent =lrcRows.get(i);
            LrcContent lrcNext = i + 1 == lrcRows.size() ? null : lrcRows.get(i+1);
            if ((time >= lrcCurrent.getTime() && lrcNext != null && time < lrcNext.getTime())
                    || (time > lrcCurrent.getTime() && lrcNext == null)) {
                seekLrc(i);
                return;
            }
        }
    }

}
