package com.louisgeek.louiscustomviewstudy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by louisgeek on 2016/10/19.
 */
public class BitmapCustomView03 extends View{
    private Paint mPaint;
    private float mStrokeWidth=15f;
    private Bitmap mBitmap;
    private int pageCount=13;
    private int nowPageIndex=1;

    private int mWidth,mHeight;

    private Handler mHandler;
    private int mAnimDuration=2*1000;

    public BitmapCustomView03(Context context) {
        this(context,null);
    }

    public BitmapCustomView03(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BitmapCustomView03(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        mPaint=new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setColor(Color.YELLOW);
        mPaint.setAntiAlias(true);


        mBitmap= BitmapFactory.decodeResource(getResources(),R.drawable.checkmark);



        mHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                if (nowPageIndex<pageCount){
                 nowPageIndex++;
                }else {
                 nowPageIndex=0;
                }
                 invalidate();
                 mHandler.sendEmptyMessageDelayed(1,mAnimDuration/pageCount);
            }
        };
        mHandler.sendEmptyMessageDelayed(0,mAnimDuration/pageCount);//start
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth=getMeasuredWidth();
        mHeight=getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int singleBitmapWidth=mBitmap.getWidth()/pageCount;

        canvas.translate(mWidth/2,mHeight/2);//到中心
        canvas.drawCircle(0,0,singleBitmapWidth,mPaint);

        Rect rectBitmap=new Rect(nowPageIndex*(mBitmap.getWidth()/pageCount),0,(nowPageIndex+1)*(mBitmap.getWidth()/pageCount),mBitmap.getHeight());
        Rect rectWhere=new Rect(-singleBitmapWidth/2,-singleBitmapWidth/2,singleBitmapWidth/2,singleBitmapWidth/2);
        canvas.drawBitmap(mBitmap,rectBitmap,rectWhere,null);
    }


}
