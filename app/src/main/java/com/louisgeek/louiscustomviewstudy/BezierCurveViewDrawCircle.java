package com.louisgeek.louiscustomviewstudy;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by louisgeek on 2016/10/28.
 */

public class BezierCurveViewDrawCircle extends View {

    /**
     * 控制点和半径的比例 详见：http://spencermortensen.com/articles/bezier-circle/
     */
    private final float C = 0.551915024494f;

    private  Paint mPaint;
    private  int mWidth,mHeight;

    private float mDisance;//控制点离数据点的距离

    //顺时针
    private  PointF[] mPointF_Data;
    private  PointF[] mPointF_Control;
    boolean drawControl=true;

    float mRadius=150f;

    public BezierCurveViewDrawCircle(Context context) {
        this(context, null);
    }
    public BezierCurveViewDrawCircle(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }
    public BezierCurveViewDrawCircle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

         init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(4f);
        mPaint.setStyle(Paint.Style.STROKE);

        //
        mDisance=mRadius*C;

        /**
         * init
         */
        mPointF_Data=new PointF[2*4];
        for (int i = 0; i < mPointF_Data.length; i++) {
            mPointF_Data[i]=new PointF();
        }
        mPointF_Control=new PointF[2*4];
        for (int i = 0; i < mPointF_Control.length; i++) {
            mPointF_Control[i]=new PointF();
        }
        //图解 https://code.aliyun.com/hi31588535/outside_chain/raw/master/jiangjietu.png
        //屏幕坐标系的第一象限
        mPointF_Data[0].set(mRadius,0);//点P3
        mPointF_Data[1].set(0,mRadius);//点P0
        //屏幕坐标系的第二象限
        mPointF_Data[2].set(0,mRadius);//点P0
        mPointF_Data[3].set(-mRadius,0);//点P9
        //屏幕坐标系的第三象限
        mPointF_Data[4].set(-mRadius,0);//点P9
        mPointF_Data[5].set(0,-mRadius);//点P6
        //屏幕坐标系的第四象限
        mPointF_Data[6].set(0,-mRadius);//点P6
        mPointF_Data[7].set(mRadius,0);//点P3

        //屏幕坐标系的第一象限
        mPointF_Control[0].set(mRadius,mDisance);//点P2
        mPointF_Control[1].set(mDisance,mRadius);//点P1
        //屏幕坐标系的第二象限
        mPointF_Control[2].set(-mDisance,mRadius);//点P11
        mPointF_Control[3].set(-mRadius,mDisance);//点P10
        //屏幕坐标系的第三象限
        mPointF_Control[4].set(-mRadius,-mDisance);//点P8
        mPointF_Control[5].set(-mDisance,-mRadius);//点P7
        //屏幕坐标系的第四象限
        mPointF_Control[6].set(mDisance,-mRadius);//点P5
        mPointF_Control[7].set(mRadius,-mDisance);//点P4


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

        canvas.translate(mWidth/2,mHeight/2);



        Path path=new Path();
        //屏幕坐标系的第1象限
        path.moveTo(mPointF_Data[0].x,mPointF_Data[0].y);
        path.cubicTo(mPointF_Control[0].x,mPointF_Control[0].y,mPointF_Control[1].x,mPointF_Control[1].y,mPointF_Data[1].x,mPointF_Data[1].y);

        //屏幕坐标系的第2象限
        path.moveTo(mPointF_Data[2].x,mPointF_Data[2].y);
        path.cubicTo(mPointF_Control[2].x,mPointF_Control[2].y,mPointF_Control[3].x,mPointF_Control[3].y,mPointF_Data[3].x,mPointF_Data[3].y);

        //屏幕坐标系的第3象限
        path.moveTo(mPointF_Data[4].x,mPointF_Data[4].y);
        path.cubicTo(mPointF_Control[4].x,mPointF_Control[4].y,mPointF_Control[5].x,mPointF_Control[5].y,mPointF_Data[5].x,mPointF_Data[5].y);

        //屏幕坐标系的第4象限
        path.moveTo(mPointF_Data[6].x,mPointF_Data[6].y);
        path.cubicTo(mPointF_Control[6].x,mPointF_Control[6].y,mPointF_Control[7].x,mPointF_Control[7].y,mPointF_Data[7].x,mPointF_Data[7].y);


        canvas.drawPath(path,mPaint);

        if (drawControl){
            drawControlLine(canvas);
        }
    }

    private void drawControlLine(Canvas canvas) {
        mPaint.setColor(Color.GRAY);
        canvas.drawLine(mPointF_Data[0].x,mPointF_Data[0].y,mPointF_Control[0].x,mPointF_Control[0].y,mPaint);
        mPaint.setColor(Color.YELLOW);
        canvas.drawLine(mPointF_Data[1].x,mPointF_Data[1].y,mPointF_Control[1].x,mPointF_Control[1].y,mPaint);
        mPaint.setColor(Color.LTGRAY);
        canvas.drawLine(mPointF_Data[2].x,mPointF_Data[2].y,mPointF_Control[2].x,mPointF_Control[2].y,mPaint);
        mPaint.setColor(Color.parseColor("#5DFED0"));
        canvas.drawLine(mPointF_Data[3].x,mPointF_Data[3].y,mPointF_Control[3].x,mPointF_Control[3].y,mPaint);
        mPaint.setColor(Color.BLACK);
        canvas.drawLine(mPointF_Data[4].x,mPointF_Data[4].y,mPointF_Control[4].x,mPointF_Control[4].y,mPaint);
        mPaint.setColor(Color.BLUE);
        canvas.drawLine(mPointF_Data[5].x,mPointF_Data[5].y,mPointF_Control[5].x,mPointF_Control[5].y,mPaint);
        mPaint.setColor(Color.GREEN);
        canvas.drawLine(mPointF_Data[6].x,mPointF_Data[6].y,mPointF_Control[6].x,mPointF_Control[6].y,mPaint);
        mPaint.setColor(Color.parseColor("#FE6EEB"));
        canvas.drawLine(mPointF_Data[7].x,mPointF_Data[7].y,mPointF_Control[7].x,mPointF_Control[7].y,mPaint);

    }


}
