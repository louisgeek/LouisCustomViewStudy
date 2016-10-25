package com.louisgeek.louiscustomviewstudy;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by louisgeek on 2016/10/24.
 */
public class CustomView06 extends View{
    private float currentValue = 0;     // 用于纪录当前的位置,取值范围[0,1]映射Path的整个长度

    private float[] posLocation;                // 当前点的实际位置
    private float[] tanValue;                // 当前点的tangent值,用于计算图片所需旋转的角度
    private Bitmap mBitmap;             // 箭头图片
    private Matrix mMatrix;             // 矩阵,用于对图片进行一些操作
    private   Context mContext;
    private  int mWidth,mHeight;
    private Paint mPaint;
    public CustomView06(Context context) {
        this(context,null);
    }

    public CustomView06(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CustomView06(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext=context;
        init();
    }
    private void init() {
        posLocation = new float[2];
        tanValue = new float[2];
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 16;       // 缩放图片  即变为原来的1/16
        mBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.arrow, options);

        mMatrix = new Matrix();

        mPaint=new Paint();
        mPaint.setStrokeWidth(8);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.BLACK);
        mPaint.setAntiAlias(true);

        ValueAnimator va=ValueAnimator.ofFloat(0f,1f).setDuration(300*1000);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                invalidate();
            }
        });
        va.start();
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

        Path path = new Path();                                 // 创建 Path
        path.addCircle(0, 0, 200, Path.Direction.CW);           // 添加一个圆形
        PathMeasure measure = new PathMeasure(path, false);     // 创建 PathMeasure

        currentValue += 0.005;  //进度递增                                // 计算当前的位置在总长度上的比例[0,1]
        if (currentValue >= 1) {
            currentValue = 0;
        }

        //======================WAY ONE
      /*  measure.getPosTan(measure.getLength() * currentValue, posLocation, tanValue);        // 获取当前位置的坐标以及趋势
       *//* float degrees=Math.toDegrees();角度
        float radians=Math.toRadians(); 弧度
        *//*
        double radians=Math.atan2(tanValue[1], tanValue[0]);
        float degrees = (float) Math.toDegrees(radians);// 计算图片旋转角度
        mMatrix.reset(); // 重置Matrix
        mMatrix.postRotate(degrees, mBitmap.getWidth() / 2, mBitmap.getHeight() / 2);   // 旋转图片
        mMatrix.postTranslate(posLocation[0] - mBitmap.getWidth() / 2, posLocation[1] - mBitmap.getHeight() / 2);   // 将图片绘制中心调整到与当前点重合
       */ //======================WAY TWO

        measure.getMatrix(measure.getLength()*currentValue,mMatrix,PathMeasure.POSITION_MATRIX_FLAG|PathMeasure.TANGENT_MATRIX_FLAG);
        //矩阵对旋转角度默认为图片的左上角 前乘调整旋转中心
        mMatrix.preTranslate(- mBitmap.getWidth() / 2,- mBitmap.getHeight() / 2);//getMatrix会重置Matrix 采用前乘

        canvas.drawPath(path, mPaint);                                   // 绘制 Path
        canvas.drawBitmap(mBitmap, mMatrix, null);                     // 绘制箭头
    }
}
