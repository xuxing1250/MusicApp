package com.example.administrator.musicapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/6/26.
 */
public class LrcView extends TextView {
    private Paint currentPaint;
    private Paint notcurrentPaint;
    private float widht;
    private float height;


    public LrcView(Context context) {
        super(context);
        init();
    }

    public LrcView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LrcView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public LrcView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }
    //初始化画笔
    protected void init(){
        currentPaint=new Paint();
        currentPaint.setColor(Color.RED);
        currentPaint.setTextSize(30);
        currentPaint.setTextAlign(Paint.Align.CENTER);

        notcurrentPaint=new Paint();
        notcurrentPaint.setColor(Color.BLACK);
        notcurrentPaint.setTextSize(24);
        notcurrentPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        setText("111111111111111111");
        widht=getWidth();
        height=getHeight();

        canvas.drawText("111111111111111",widht/2,100,currentPaint);
        Log.d("widht","widht"+widht);
        Log.d("height","height"+height);
    }
}
