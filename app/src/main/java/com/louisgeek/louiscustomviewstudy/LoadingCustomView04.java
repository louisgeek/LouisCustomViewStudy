package com.louisgeek.louiscustomviewstudy;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by louisgeek on 2016/10/19.
 */
public class LoadingCustomView04 extends View {
    private static final String TAG = "LoadingCustomView04";

    private static final int PROGRESSBAR_WIDTH = 550;
    /*进度条样式*/
    public static final int SOLID = 1;//实心
    public static final int SOLID_AND_FRAME = 2;//实心加边框
    public static final int HOLLOW = 3;//空心

    /***/
    private int mStartAngle_LeftArc = 90;//左边半圆或弧度的初始角度
    private int mStartAngle_RightArc_One = -90;//右边半圆或弧度上面的那部分的初始角度
    private int mStartAngle_RightArc_Two = 0;//右边半圆或弧度下面的那部分的初始角度


    private int mProgressBankgroundColor = Color.parseColor("#FA8900");
    private int mProgressColor = Color.parseColor("#98C73B");
    private float mProgress;//当前的进度
    private int mProgressBarFrameHeight = this.dp2px(5);
    private int mProgressBarBankgroundStyle = SOLID;//默认实心
    private int mProgressBarHeight = this.dp2px(20);//进度条总高度
    private int mProgressBarWidth = PROGRESSBAR_WIDTH;//进度条总长度
    //
    private boolean mHasCoordinate = false;//是否绘制参考坐标系

    /***/
    private Paint mPaint;
    private int mViewWidth, mViewHeight;
    private int mScreenWidth, mScreenHeight;
    private boolean mHasBankground = true;//是否绘制背景
    private float mProgressMaxWidth;//进度最大宽度
    private float mProgressLoadingWidth;//当前进度条宽度

    private float mOneArcProgress;//半圆占用的最大的进度
    private float mRectWidth;//进度条中间矩形的最大宽度
    private int mProgressBarWidthWithoutFrame;
    private int mProgressBarHeightWithoutFrame;

    private float mRadius;//进度条内左右两个半圆的最大半径

    private int mDuration = 5 * 1000;//动画执行时间
    private Context mContext;

    public LoadingCustomView04(Context context) {
        this(context, null);
    }

    public LoadingCustomView04(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingCustomView04(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.LoadingCustomView04);
        mProgressBankgroundColor = ta.getColor(R.styleable.LoadingCustomView04_progressBankgroundColor, mProgressBankgroundColor);
        mProgressColor = ta.getColor(R.styleable.LoadingCustomView04_progressColor, mProgressColor);
        mProgress = ta.getFloat(R.styleable.LoadingCustomView04_progress, mProgress);
        mProgress=mProgress/100;//目标进度0-1
        mProgressBarFrameHeight = ta.getDimensionPixelOffset(R.styleable.LoadingCustomView04_progressBarFrameHeight, mProgressBarFrameHeight);
        mProgressBarBankgroundStyle = ta.getInteger(R.styleable.LoadingCustomView04_progressBarBankgroundStyle, mProgressBarBankgroundStyle);
        //
        ta.recycle();

        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        /*mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setColor(Color.GREEN);*/

        mScreenWidth = getScreenWidth(mContext);
        Log.d(TAG, "init: mScreenWidth:" + mScreenWidth);
        mScreenHeight = getScreenHeight(mContext);
        Log.d(TAG, "init: mScreenHeight:" + mScreenHeight);

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 1f).setDuration(mDuration);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //##mProgress = (float) valueAnimator.getAnimatedValue();
                //invalidate();
            }
        });
        valueAnimator.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // mWidth =getMeasuredWidth();
        //mHeight = getMeasuredHeight();
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width = resolveSize(widthSize, widthMeasureSpec);
        int height = resolveSize(heightSize, heightMeasureSpec);

        //
        mViewWidth = width;
        mViewHeight = height>width?width:height;//
        //
        mProgressBarWidth = mViewWidth;
        mProgressBarHeight = mViewHeight;
        setMeasuredDimension(width, mViewHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mHasCoordinate) {
            drawCoordinate(canvas);
            drawCoordinateOnCenter(canvas);
        }
        //
        switch (mProgressBarBankgroundStyle) {
            case SOLID:
                mProgressBarFrameHeight = 0;
                break;
            case SOLID_AND_FRAME:
                //mProgressBarFrameHeight=0;
                break;
            case HOLLOW:
                //mProgressBarFrameHeight=0;
                break;
        }
        /**
         * 处理笔触的大小
         */
        mProgressBarWidthWithoutFrame = mProgressBarWidth - mProgressBarFrameHeight * 2;//不包含边框的进度条宽
        mProgressBarHeightWithoutFrame = mProgressBarHeight - mProgressBarFrameHeight * 2;//不包含边框的进度条高
        //
        mRadius = mProgressBarHeightWithoutFrame / 2;
        //
        mRectWidth = mProgressBarWidthWithoutFrame - 2 * mRadius;//矩形的宽度
        mProgressMaxWidth = mProgressBarWidthWithoutFrame;
        mOneArcProgress = mRadius / mProgressBarWidth;//半圆最大的 进度
        if (mHasBankground) {
            drawBankground(canvas);
        }


        mProgressLoadingWidth = mProgressMaxWidth * mProgress;

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mProgressColor);
        //

        //canvas.translate(-(mRadiusMax-mArcLeftWidth),0);//向左偏移半圆剩余的宽  保证左边对齐

        if (mProgress <= 0){
            return;
        }
        if (mProgress <= mOneArcProgress) {
            drawLeftArc(canvas);

        } else if (mProgress > mOneArcProgress && mProgress <= (1 - mOneArcProgress)) {
            drawLeftArc(canvas);
            drawCenterRect(canvas);

        } else {
            drawLeftArc(canvas);
            drawCenterRect(canvas);
            drawRightArc(canvas);
        }
        // Log.d(TAG, "onDraw: mProgressNow:"+mProgressNow);

    }

    /**
     * 画默认坐标系
     *
     * @param canvas
     */
    private void drawCoordinate(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(6f);
        canvas.drawLine(0, 0, mViewWidth, 0, mPaint);//X 轴
        canvas.drawLine(0, 0, 0, mViewHeight, mPaint);//y 轴
    }

    /**
     * 画居中坐标系
     *
     * @param canvas
     */
    private void drawCoordinateOnCenter(Canvas canvas) {
        canvas.save();
        canvas.translate(mViewWidth / 2, mViewHeight / 2);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(Color.YELLOW);
        mPaint.setStrokeWidth(6f);
        canvas.drawLine(-mViewWidth / 2, 0, mViewWidth / 2, 0, mPaint);//X 轴
        canvas.drawLine(0, -mViewHeight / 2, 0, mViewHeight / 2, mPaint);//y 轴
        canvas.restore();
    }

    /**
     * 画边框背景
     */
    private void drawBankground(Canvas canvas) {
        //边框背景
        mPaint.setColor(mProgressBankgroundColor);
        mPaint.setStrokeWidth(mProgressBarFrameHeight);
        //移动到第一个半圆圆心
        canvas.translate(mRadius + mProgressBarFrameHeight, mProgressBarHeight / 2);
        switch (mProgressBarBankgroundStyle) {
            case SOLID:
                //进度条实心
                mPaint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(0, 0, mRadius, mPaint);
                RectF rectF_Center = new RectF(0, -mRadius, mRectWidth, mRadius);
                canvas.drawRect(rectF_Center, mPaint);
                canvas.drawCircle(mRectWidth, 0, mRadius, mPaint);
                break;
            case SOLID_AND_FRAME:
                //进度条实心加边框
                mPaint.setStyle(Paint.Style.FILL);//FILL_AND_STROKE画时候  笔触右半边会和内容重合 差一半笔触!!!
                float radiusTemp = mRadius + mProgressBarFrameHeight;
                canvas.drawCircle(0, 0, radiusTemp, mPaint);
                RectF rectF = new RectF(0, -radiusTemp, mRectWidth, radiusTemp);
                canvas.drawRect(rectF, mPaint);
                canvas.drawCircle(mRectWidth, 0, radiusTemp, mPaint);
                break;
            case HOLLOW:
                //进度条空心
                mPaint.setStyle(Paint.Style.STROKE);//STROKE画时候  笔触右半边会和内容重合 差一半笔触!!!
                //
                //画 左边半圆环
                float newRadius = mRadius + mProgressBarFrameHeight / 2;
                RectF rectF_Left_Right = new RectF(-newRadius, -newRadius, newRadius, newRadius);
                canvas.drawArc(rectF_Left_Right, mStartAngle_LeftArc, 180, false, mPaint);
                canvas.save();
                canvas.translate(mRectWidth, 0);
                //画 右边半圆环
                canvas.drawArc(rectF_Left_Right, -mStartAngle_LeftArc, 180, false, mPaint);
                canvas.restore();
                //画 两条平行线
                canvas.drawLine(0, -newRadius, mRectWidth, -newRadius, mPaint);
                canvas.drawLine(0, newRadius, mRectWidth, newRadius, mPaint);
                break;
        }
    }

    /**
     * 画半圆左侧的任意部分
     */
    private void drawLeftArc(Canvas canvas) {

        float progressBarWidthNowTemp = mProgressLoadingWidth < mRadius ? mProgressLoadingWidth : mRadius;//当前进度条不能超过左边圆的半径
        float leftArcWidth = progressBarWidthNowTemp;
        RectF rectF = new RectF(-mRadius, -mRadius, mRadius, mRadius);
        /**
         * ∠A 指的是  x轴和竖直切线的夹角  demo图见 https://code.aliyun.com/hi31588535/outside_chain/raw/master/blog_custom_view_show_pic.png
         */
        double LinBian = mRadius - leftArcWidth;//直角三角形∠A邻边
        double cosValue = LinBian / mRadius;//cosA=邻边/斜边

        double radian = Math.acos(cosValue);//反余弦   返回值单位是弧度
        // 用角度表示的角
        double angle = Math.toDegrees(radian);//转化角度

        float startAngle = (float) (mStartAngle_LeftArc + (90 - angle));
        float sweepAngle = (float) angle * 2;

        // Log.d(TAG, "onDraw: angle" + angle);//直角三角形 锐角A （∠A的） sinA=对边/斜边  cosA=邻边/斜边  tanA=对边/邻边
        canvas.drawArc(rectF, startAngle, sweepAngle, false, mPaint);
    }

    /**
     * 画中间矩形部分
     */
    private void drawCenterRect(Canvas canvas) {
        float rectAndLeftArcMaxWidth = mProgressMaxWidth - mRadius;//所有进度条减去右边 就是左边和矩形

        float progressBarWidthNowTemp = mProgressLoadingWidth < rectAndLeftArcMaxWidth ? mProgressLoadingWidth : rectAndLeftArcMaxWidth;
        float rectWidth = progressBarWidthNowTemp - mRadius;//当前进度条减去左边半圆

        rectWidth = rectWidth < rectAndLeftArcMaxWidth ? rectWidth : rectAndLeftArcMaxWidth;
        RectF rectFCenter = new RectF(0, -mRadius, rectWidth, mRadius);
        canvas.drawRect(rectFCenter, mPaint);
    }

    /**
     * 画半圆右侧的任意部分  分2个圆弧  1个三角形  demo图 见https://code.aliyun.com/hi31588535/outside_chain/raw/master/blog_custom_view_show_pic2.png
     */
    private void drawRightArc(Canvas canvas) {
        float rectAndLeftArcMaxWidth = mProgressMaxWidth - mRadius;//所有进度条减去右边 就是左边和矩形

        float progressBarWidthNowTemp = mProgressLoadingWidth < mProgressMaxWidth ? mProgressLoadingWidth : mProgressMaxWidth;

        float rightArcWidth = progressBarWidthNowTemp - rectAndLeftArcMaxWidth;//当前进度条减去左边半圆和矩形

        float rectWidth = rectAndLeftArcMaxWidth - mRadius;

        canvas.translate(rectWidth, 0);//

        RectF rectF = new RectF(-mRadius, -mRadius, mRadius, mRadius);

        double LinBian = rightArcWidth;//直角三角形∠B邻边
        double cosValue = LinBian / mRadius;//cosB=邻边/斜边

        double radian = Math.acos(cosValue);//反余弦   返回值单位是弧度
        // 用角度表示的角
        double angle = Math.toDegrees(radian);//转化角度

        float sweepAngle = (float) (90 - angle);

        float startAngleOne = (float) mStartAngle_RightArc_One;
        float startAngleTwo = (float) (mStartAngle_RightArc_Two + angle);


        canvas.drawArc(rectF, startAngleOne, sweepAngle, true, mPaint);//绘制上面的圆弧
        canvas.drawArc(rectF, startAngleTwo, sweepAngle, true, mPaint);//绘制下面的圆弧

        //画三角形
        Path pathTriangle = new Path();
        double DuiBian = Math.sqrt((mRadius * mRadius - LinBian * LinBian));//开平方   邻边的平方加上对边的平方的斜边的平方
        pathTriangle.moveTo(0, 0);
        pathTriangle.lineTo((float) LinBian, (float) DuiBian);
        pathTriangle.lineTo((float) LinBian, -(float) DuiBian);
        pathTriangle.close();
        canvas.drawPath(pathTriangle, mPaint);

    }

    public void setProgress(float progress) {

        mProgress = progress/100;
        invalidate();
    }

    public void setProgressBarBankgroundStyle(int progressBarBankgroundStyle) {
        mProgressBarBankgroundStyle = progressBarBankgroundStyle;
        invalidate();
    }

    public void setProgressColor(int progressColor) {
        mProgressColor = progressColor;
        invalidate();
    }

    public void setProgressBankgroundColor(int progressBankgroundColor) {
        mProgressBankgroundColor = progressBankgroundColor;
        invalidate();
    }
    public void setProgressBarFrameHeight(int progressBarFrameHeight) {
        mProgressBarFrameHeight = progressBarFrameHeight;
        invalidate();
    }

    //
    public int dp2px(int dpValue) {
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getResources().getDisplayMetrics());
        return px;
    }

    //获取屏幕的宽度
    public static int getScreenWidth(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        float density = displayMetrics.density;
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        return width;
    }

    //获取屏幕的高度
    public static int getScreenHeight(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        float density = displayMetrics.density;
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        return height;
    }
}
