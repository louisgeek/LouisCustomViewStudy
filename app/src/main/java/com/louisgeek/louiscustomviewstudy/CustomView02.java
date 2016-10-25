package com.louisgeek.louiscustomviewstudy;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Picture;
import android.util.AttributeSet;
import android.view.View;

/**
 * Picture 录制
 * Created by louisgeek on 2016/10/19.
 */
public class CustomView02 extends View{
    private Paint mPaint;
    private float mStrokeWidth=10f;
    private Picture mPicture;
    public CustomView02(Context context) {
        this(context,null);
    }

    public CustomView02(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CustomView02(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint=new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setColor(Color.GREEN);

        recordingCanvas();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPicture(mPicture);

    }

    void recordingCanvas(){
        mPicture=new Picture();
        Canvas canvasFromPicture=mPicture.beginRecording(500,500);

        // 创建一个画笔
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.FILL);

        // 位移
        canvasFromPicture.translate(250,250);
        // 绘制一个圆
        canvasFromPicture.drawCircle(0,0,100,paint);

        mPicture.endRecording();

    }
}
