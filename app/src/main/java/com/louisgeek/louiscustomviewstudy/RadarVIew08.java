package com.louisgeek.louiscustomviewstudy;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import static android.content.ContentValues.TAG;

/**
 * Created by louisgeek on 2016/10/27.
 */

public class RadarView08 extends View {
    private Paint mPaint;
    private int mViewWidth, mViewHeight,mScreenWidth,mScreenHeight;
    /**
     *文字与边缘点的距离
     */
    private final float mText_ContentDisance = 10;
    /**
     *线的粗细
    */
    private  int mLinesWidth = this.dp2px(1);

    //顺时针  对应的数据
    // private float[] mDataValue={1f,1f,1f,1f,0.8f,0.6f,0.4f,1f,0.4f,1f};//10
    private float[] mDataValue = {1f, 0.1f, 1f, 1f, 0.8f, 0.6f, 0.4f, 0.4f};//8
    //private float[] mDataValue={1f,0.8f,0.6f,0.4f,1f,0.4f};//6
    // private float[] mDataValue={1f,0.8f,0.6f,0.4f};//4
    // private float[] mDataValue={1f,1f,1f};//3
    private String[] mDataName = {"豌豆荚", "应用宝", "百度91安卓", "安智市场", "小米应用商店", "OPPO应用商店", "魅族应用商店", "360手机助手", "华为应用市场", "移动MM商店"};

    /**
     * 最大形状的半径
     */
   private int mRadius = this.dp2px(50);//max
    /**
     * 小圆点半径
     */
    private int mPointRadius = this.dp2px(2);
    private int mPointColor = Color.GRAY;

    private int mContentColor=Color.BLUE;
    private int mLinesColor=Color.LTGRAY;
    private int mTextColor=Color.RED;
    private int mTextSize=this.dp2px(14);
    /**
     * 角度
     */
    private int mDegreesAngle;
    private int mContentAlpha=150;//must in (0-225)
    //点的数量
    private int mPointsCount;

    private Paint mPaintText=new Paint();

    private  Context mContext;

    public RadarView08(Context context) {
        this(context, null);
    }

    public RadarView08(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadarView08(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext=context;
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.RadarView08);
       mLinesWidth = ta.getDimensionPixelOffset(R.styleable.RadarView08_linesWidth, mLinesWidth);
        mRadius = ta.getDimensionPixelOffset(R.styleable.RadarView08_maxCircleRadius, mRadius);
        mPointRadius = ta.getDimensionPixelOffset(R.styleable.RadarView08_pointRadius, mPointRadius);
        mContentColor = ta.getColor(R.styleable.RadarView08_contentColor, mContentColor);
        mPointColor = ta.getColor(R.styleable.RadarView08_pointColor, mPointColor);
        mLinesColor = ta.getColor(R.styleable.RadarView08_linesColor, mLinesColor);
        mContentAlpha = ta.getInteger(R.styleable.RadarView08_contentAlpha, mContentAlpha);
        mTextColor = ta.getColor(R.styleable.RadarView08_titleColor, mTextColor);
        mTextSize = ta.getDimensionPixelOffset(R.styleable.RadarView08_titleTextSize, mTextSize);
        //
        ta.recycle();
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.LTGRAY);
        mPaint.setStrokeWidth(mLinesWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        //
        mPaintText=new Paint();
        mPaintText.setColor(mTextColor);
        mPaintText.setTextSize(mTextSize);
        mPaintText.setAntiAlias(true);
        mPaintText.setStyle(Paint.Style.FILL);


        //must in (0-225)
        mContentAlpha=mContentAlpha<0?0:mContentAlpha;
        mContentAlpha=mContentAlpha>255?255:mContentAlpha;
        Log.d(TAG, "init: mContentAlpha:"+mContentAlpha);
        //
        mPointsCount = mDataValue.length;
        mDegreesAngle = 360 / mPointsCount;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
         /*mWidth =getMeasuredWidth();
         mHeight = getMeasuredHeight();*/

//        mScreenWidth=getScreenWidth(mContext);
//        mScreenHeight=getScreenHeight(mContext);
       /* int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);*/



        int maxLen=0;
        String maxLenName=mDataName[0];
        for (int i = 0; i < mDataValue.length; i++) {
           int len= mDataName[i].length();
            if (len>maxLen){
                maxLen=len;
                maxLenName=mDataName[i];
            }

        }
        float fixWidth_Height=mPaintText.measureText(maxLenName);
        /**
         * 实际的内容宽和高
         */
        int contentWidthSize = (int) (fixWidth_Height*2+mRadius*2+Math.max(mPointRadius,mLinesWidth)*1.0f/2+this.getPaddingLeft()+this.getPaddingRight());
        int contentHeightSize = (int) (fixWidth_Height*2+mRadius*2+Math.max(mPointRadius,mLinesWidth)*1.0f/2+this.getPaddingTop()+this.getPaddingBottom());


        //getDefaultSize()
        int width = resolveSize(contentWidthSize, widthMeasureSpec);
        int height = resolveSize(contentHeightSize, heightMeasureSpec);

        /**
         * 修正xml设置太小的情况
         */
        width=width<contentWidthSize?contentWidthSize:width;
        height=height<contentHeightSize?contentHeightSize:height;

        /**
         *
         */
        setMeasuredDimension(width, height);

        mViewWidth = width;
        mViewHeight = height;


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //
        canvas.translate(mViewWidth/ 2, mViewHeight / 2);

        if (mPointsCount > 10 || mPointsCount < 3) {
            Log.e(TAG, "onDraw: 只能是3~10个数据");
            return;
        }
        drawFramesAndLines(canvas);
        //
        drawContent(canvas);

        drawTexts(canvas);
    }

    private void drawContent(Canvas canvas) {
        Paint paintContent = new Paint();
        //paintContent.setColor(mContentColor);
        paintContent.setAntiAlias(true);
        paintContent.setStyle(Paint.Style.FILL);
        paintContent.setStrokeCap(Paint.Cap.ROUND);
        Path path = new Path();

        for (int i = 0; i < mPointsCount; i++) {
            //！！！加上原有占width的笔触  mPointsCount-1条线 mLineWidth/2*(mPointsCount-1)
            float nowContentDisance = (mRadius - mLinesWidth / 2) * mDataValue[i];
            if (i == 0) {
                path.moveTo(nowContentDisance, 0);
                //绘制小圆点
                paintContent.setColor(mPointColor);
                canvas.drawCircle(nowContentDisance, 0, mPointRadius, paintContent);
            } else {
                float nowDegreesAngle = mDegreesAngle * i;
                double nowRadiansAngle = Math.toRadians(nowDegreesAngle);
                //sinA=对边/Radius
                //cosA=领边/Radius
                float tempX = (float) (nowContentDisance * Math.cos(nowRadiansAngle));
                float tempY = (float) (nowContentDisance * Math.sin(nowRadiansAngle));
                path.lineTo(tempX, tempY);
                //绘制小圆点
                paintContent.setColor(mPointColor);
                canvas.drawCircle(tempX, tempY, mPointRadius, paintContent);
            }
        }
        paintContent.setColor(mContentColor);
        paintContent.setAlpha(mContentAlpha);
        canvas.drawPath(path, paintContent);
    }

    /**
     * 绘制文字
     */
    private void drawTexts(Canvas canvas) {
       /* Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        float fontHeight = fontMetrics.descent - fontMetrics.ascent;*/

        /**
         * 参考圆
         */
        //###canvas.drawCircle(0,0,mRadius+mText_ContentDisance,mPaint);

        for (int i = 0; i < mPointsCount; i++) {
            if (i == 0) {
                //获取文字度量信息
                Paint.FontMetrics fontMetrics = mPaintText.getFontMetrics();
                float textHeight = fontMetrics.descent - fontMetrics.ascent;
                float baseLineYDis = textHeight / 2 - Math.abs(fontMetrics.descent);
                float tempXFix = mText_ContentDisance;
                float tempYFix = baseLineYDis;
                canvas.drawText(mDataName[i], mRadius + tempXFix, 0 + tempYFix, mPaintText);
            } else {
                float nowDegreesAngle = mDegreesAngle * i;
                double nowRadiansAngle = Math.toRadians(nowDegreesAngle);
                //sinA=对边/Radius
                //cosA=领边/Radius
                float tempX = (float) (mRadius * Math.cos(nowRadiansAngle));
                float tempY = (float) (mRadius * Math.sin(nowRadiansAngle));
                //
               /* Rect rect_bounds=new Rect();
                paintText.getTextBounds(mDataName[i],0,mDataName[i].length(),rect_bounds);
                int textWidth=rect_bounds.width();
                int textHeight=rect_bounds.height();*/
                //获取文字度量信息
                Paint.FontMetrics fontMetrics = mPaintText.getFontMetrics();
                float textHeight = fontMetrics.descent - fontMetrics.ascent;
                float textWidth = mPaint.measureText(mDataName[i]);

                float baseLineYDis = textHeight / 2 - Math.abs(fontMetrics.descent);
                float tempXFix;
                float tempYFix;
                if (nowDegreesAngle > 0 && nowDegreesAngle <= 90) {
                    tempXFix = mText_ContentDisance - textWidth / 2;
                    tempYFix = mText_ContentDisance + textHeight / 2 + baseLineYDis;
                    canvas.drawText(mDataName[i], tempX + tempXFix, tempY + tempYFix, mPaintText);
                } else if (nowDegreesAngle > 90 && nowDegreesAngle < 180) {
                    tempXFix = mText_ContentDisance + textWidth;
                    tempYFix = mText_ContentDisance + textHeight / 2 + baseLineYDis;
                    canvas.drawText(mDataName[i], tempX - tempXFix, tempY + tempYFix, mPaintText);
                } else if (nowDegreesAngle == 180) {
                    //###tempXFix=mText_ContentDisance+textWidth;//这里感觉计算不准 改变计算方式弥补下
                    /** 换个计算方法*/
                    Rect rectBounds = new Rect();
                    mPaintText.getTextBounds(mDataName[i], 0, mDataName[i].length(), rectBounds);
                    int textNewWidth = rectBounds.width();
                    tempXFix = mText_ContentDisance + textNewWidth;
                    tempYFix = baseLineYDis;
                    //mRadius
                    canvas.drawText(mDataName[i], -mRadius - tempXFix, 0 + tempYFix, mPaintText);
                } else if (nowDegreesAngle > 180 && nowDegreesAngle <= 270) {
                    tempXFix = mText_ContentDisance + textWidth;
                    tempYFix = mText_ContentDisance + textHeight / 2 - baseLineYDis;
                    canvas.drawText(mDataName[i], tempX - tempXFix, tempY - tempYFix, mPaintText);
                } else if (nowDegreesAngle > 270 && nowDegreesAngle <= 360) {
                    tempXFix = mText_ContentDisance - textWidth / 2;
                    tempYFix = mText_ContentDisance + textHeight / 2 - baseLineYDis;
                    canvas.drawText(mDataName[i], tempX + tempXFix, tempY - tempYFix, mPaintText);
                }
            }
        }


    }

    private void drawFramesAndLines(Canvas canvas) {
        Path path = new Path();
        int mDisance = mRadius / (mPointsCount - 1);
        /**
         * 循环出多个正六边形
         */
        for (int j = 0; j < mPointsCount - 1; j++) {
            float nowRadius = mDisance * ((mPointsCount - 1) - j);

            /**
             * 画每一个正六边形
             */
            for (int i = 0; i < mPointsCount; i++) {
                // path.reset();
                /**
                 * 顺时针  从最右边画
                 */
                if (i == 0) {
                    path.moveTo(nowRadius, 0);

                    /**
                     * 画从中点到最右边点的线
                     */
                    Path pathLines = new Path();
                    pathLines.lineTo(nowRadius, 0);
                    mPaint.setColor(mLinesColor);
                    canvas.drawPath(pathLines, mPaint);
                } else {
                    float nowDegreesAngle = mDegreesAngle * i;
                    double nowRadiansAngle = Math.toRadians(nowDegreesAngle);
                    //sinA=对边/Radius
                    //cosA=领边/Radius
                    float tempX = (float) (nowRadius * Math.cos(nowRadiansAngle));
                    float tempY = (float) (nowRadius * Math.sin(nowRadiansAngle));
                    path.lineTo(tempX, tempY);
                    /**
                     * 画从当前点到中点的线
                     */
                    Path pathLines = new Path();
                    pathLines.moveTo(tempX, tempY);
                    pathLines.lineTo(0, 0);
                    mPaint.setColor(mLinesColor);
                    canvas.drawPath(pathLines, mPaint);
                }

            }
            //
            path.close();
            //
            canvas.drawPath(path, mPaint);
        }
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
