package com.louisgeek.louiscustomviewstudy;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by louisgeek on 2016/10/21.
 */
public class LoadingCustomView05 extends View {
    private static final String TAG = "LoadingCustomView05";

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
    private float mPetalDisance;
    private int mProgressBarFrameHeight = this.dp2px(8);
    private int mProgressBarBankgroundStyle = SOLID;//默认实心
    private int mProgressBarHeight = this.dp2px(20);//进度条总高度
    private int mProgressBarWidth = PROGRESSBAR_WIDTH;//进度条总长度
    //
    private boolean mHasCoordinate = false;//是否绘制参考坐标系
    //右边小圆圆环边框
    float mRightStrokeWidth = this.dp2px(5);//
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


    private List<PetalBean> mPetalBeanList = new ArrayList<>();
    private int mPetalCount = 15;
    private boolean hasSinglePetal = false;
    private boolean hasInitDataed = false;
    private int again = 0;

    private int mProgressContentColor = Color.parseColor("#FE4D0E");
    ValueAnimator mValueAnimator;
    boolean mAnimatorIsStart = false;
    boolean mPosthasStart = false;

    public LoadingCustomView05(Context context) {
        this(context, null);
    }

    public LoadingCustomView05(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingCustomView05(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.LoadingCustomView05);
        mProgressBankgroundColor = ta.getColor(R.styleable.LoadingCustomView05_progressBankgroundColor2, mProgressBankgroundColor);
        mProgressColor = ta.getColor(R.styleable.LoadingCustomView05_progressColor2, mProgressColor);
        mProgress = ta.getFloat(R.styleable.LoadingCustomView05_progress2, mProgress);
        mProgress = mProgress / 100;//目标进度0-1
        mProgressBarFrameHeight = ta.getDimensionPixelOffset(R.styleable.LoadingCustomView05_progressBarFrameHeight2, mProgressBarFrameHeight);
        mProgressBarBankgroundStyle = ta.getInteger(R.styleable.LoadingCustomView05_progressBarBankgroundStyle2, mProgressBarBankgroundStyle);
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

     /*   mValueAnimator = ValueAnimator.ofFloat(0f, 1f).setDuration(mDuration);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
               mProgress = (float) valueAnimator.getAnimatedValue();

               //invalidate();

            }
        });
        mValueAnimator.start();*/

        //  mHandler.postDelayed(mRunnable, 1 * 1000);
        mHandler.post(mRunnable);

    }

    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            // Log.d(TAG, "run: mPetalTime" + mPetalTime);
            // if (mPetalTime < mPetalTimeLong) {
            mPetalDisance++;
            // }
            invalidate();
            // handler自带方法实现定时器
            //mPetalTime=mPetalTime>mPetalTimeLong?mPetalTimeLong:mPetalTime;
            try {

                //  mHandler.postDelayed(this, 1*1000);//每隔1s执行
                mHandler.postDelayed(this, 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

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
        mViewHeight = height > width ? width : height;//
        //
        mProgressBarWidth = mViewWidth;
        mProgressBarHeight = mViewHeight;
        setMeasuredDimension(width, mViewHeight);

        //
        // if (!hasInitDataed&&mProgressBarHeightWithoutFrame!=0){
        initPetalBeanListData();
        //      hasInitDataed=true;
        //    Log.d(TAG, "initPetalBeanListData");
        // }

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
                mProgressBarFrameHeight = 0;//重置 没有高度
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
        //===============================================
        drawPetal(canvas);

        drawFlowerBankground(canvas);
        drawFlower(canvas);
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
        canvas.save();
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
        canvas.restore();
    }

    /**
     * 绘制花的背景
     *
     * @param canvas
     */
    private void drawFlowerBankground(Canvas canvas) {
       /* Log.d(TAG, "drawFlowerBankground: mProgressBarHeight" + mProgressBarHeight);
        Log.d(TAG, "drawFlowerBankground: mProgressBarHeightWithoutFrame" + mProgressBarHeightWithoutFrame);
        Log.d(TAG, "drawFlowerBankground: mRadius" + mRadius);*/
        // canvas.save();
        //画白色圆环
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(mRightStrokeWidth);
        canvas.drawCircle(mRectWidth, 0, mProgressBarHeight / 2 - mRightStrokeWidth / 2, mPaint);//要覆盖外面的边框  绘制圆环 可以处理下笔触的宽度产生的影响
        mPaint.setColor(mProgressContentColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(mRectWidth, 0, mProgressBarHeight / 2 - mRightStrokeWidth, mPaint);//绘制内圆
        // canvas.drawBitmap(flowerBitmap,rect_src,rectF_to,null);
    }

    /**
     * 绘制花
     *
     * @param canvas
     */
    private void drawFlower(Canvas canvas) {
        canvas.save();
        int flowerSetup = 5;
        Bitmap flowerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.flower);
        //移动到右边要画圆的点(图片自身左上角)
        canvas.translate(mRectWidth, 0);
        //## RectF rectF_to=new RectF(-mProgressBarHeight/2,-mProgressBarHeight/2,mProgressBarHeight/2,mProgressBarHeight/2);
        // 定义矩阵对象
        Matrix matrix = new Matrix();
        //把图片偏移到圆的中心    x一半  y一半
        matrix.postTranslate(-flowerBitmap.getWidth() / 2, -flowerBitmap.getHeight() / 2);
        // canvas.drawBitmap(flowerBitmap,matrix,null);
        /**
         * 这里花是正方形
         *
         * 对角线长度要小于圆的直径
         *
         * 或者理解成是花的缩放比例要参考圆的内切矩形  而不是圆的边距
         */
        double flowerBitmapDuiJiaoXian = Math.sqrt(flowerBitmap.getWidth() * flowerBitmap.getWidth() + flowerBitmap.getHeight() * flowerBitmap.getHeight());

        double scaleW = (mProgressBarHeight - 2 * mRightStrokeWidth) / flowerBitmapDuiJiaoXian;
        double scaleH = (mProgressBarHeight - 2 * mRightStrokeWidth) / flowerBitmapDuiJiaoXian;
        //图片大小缩放到所需
        matrix.postScale((float) scaleW, (float) scaleH);
        if (mProgress < 1) {
            //要旋转的角度
            matrix.postRotate(360 * mPetalDisance / (mRectWidth + mRadius) * flowerSetup);//每次步长
        }
        //Bitmap newBitmap = Bitmap.createBitmap(flowerBitmap, 0, 0, newWidth, newHeight,
        //    matrix, false);
        // 在画布上绘制旋转后的位图
        //canvas.drawBitmap(newBitmap,rect_imgSrc,rectF_to,null);
        // 得到新的图片
        canvas.drawBitmap(flowerBitmap, matrix, null);
        //
        canvas.restore();
    }

    /**
     * 绘制花瓣
     *
     * @param canvas
     */
    private void drawPetal(Canvas canvas) {
      /*  if (mProgress >= 1) {
            return;
        }*/
        //Log.d(TAG, "drawPetal: xxxxx"+mProgress);
        canvas.save();

        canvas.translate(mRectWidth, 0);

        /**
         * 默认花瓣图片资源的角度和花朵的左上角花瓣一致
         */
        Bitmap petalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.petal);

        if (hasSinglePetal) {
            /**
             * 画右边的花瓣旋转  可选
             */
            Matrix matrix = new Matrix();
            matrix.postTranslate(-petalBitmap.getWidth(), -petalBitmap.getWidth());

            double flowerBitmapDuiJiaoXian = Math.sqrt(petalBitmap.getWidth() * petalBitmap.getWidth() + petalBitmap.getHeight() * petalBitmap.getHeight());

            double scaleW = mProgressBarHeight * 1.0f / flowerBitmapDuiJiaoXian * 0.5;//花瓣是花朵的一半
            double scaleH = mProgressBarHeight * 1.0f / flowerBitmapDuiJiaoXian * 0.5;
            matrix.postScale((float) scaleW, (float) scaleH);//执行到此时，花瓣图片和花朵的左上角花瓣重叠(花瓣资源和花朵资源的花瓣size可能不一致)
            //
            //要旋转的角度
            //### matrix.postRotate(360 * mPetalTime / mPetalTimeLong * 4);//每次步长
            canvas.drawBitmap(petalBitmap, matrix, null);
        }
        /**
         * 画花瓣自我旋转和运动
         */

        /**
         * 正弦函数曲线
         *  y=Asin(ωx+φ)+k    x是直角坐标系x轴上的数值，y是在同一直角坐标系上函数对应的y值
         *  A    振幅，当物体作轨迹符合正弦曲线的直线往复运动时，其值为行程的1/2
         *  (ωx+φ)  相位，反应变量y所处的状态    φ 初相，x=0时的相位
         *  k   偏距，反应在坐标系上的图像为图像的整体上移或下移
         *
         *  A表示这个振动的振幅，往返一次所需的时间 T=2π/ω，称为这个振动的周期，
         *  单位时间内往返振动的次数 f=1/T=ω/2π称为振动的频率，称为相位，x＝0时的相位叫初相。
         *
         *  //思路
         *  当前坐标系与之坐标系重合   设k=0  看情况变
         *  振幅大概是进度条高度的一半  设A   看情况变
         *        设φ=0   看情况变
         *
         * 设周期T最多是一半的进度条   ,ω=2π/T
         */
        /**init*/
        /*float petalPathTranslateX=mRectWidth;
        canvas.translate(petalPathTranslateX,0);*/

        for (int i = 0; i < mPetalBeanList.size(); i++) {
            PetalBean petalBean = mPetalBeanList.get(i);

            Matrix matrixPetal = new Matrix();
            matrixPetal.postTranslate(-petalBitmap.getWidth(), -petalBitmap.getWidth());
            double flowerBitmapDuiJiaoXian = Math.sqrt(petalBitmap.getWidth() * petalBitmap.getWidth() + petalBitmap.getHeight() * petalBitmap.getHeight());

            double scaleW_Petal = mProgressBarHeight * 1.0f / flowerBitmapDuiJiaoXian * 0.5;//花瓣是花朵的一半
            double scaleH_Petal = mProgressBarHeight * 1.0f / flowerBitmapDuiJiaoXian * 0.5;

            float nowScaleW_Petal = (float) (scaleW_Petal / 2);
            float nowScaleH_Petal = (float) (scaleH_Petal / 2);//除以2是为了再小点 运动美观
            matrixPetal.postScale(nowScaleW_Petal, nowScaleH_Petal);
            //
            float nowPetalWidth = petalBitmap.getWidth() * nowScaleW_Petal;
            float nowPetalHeight = petalBitmap.getHeight() * nowScaleH_Petal;
            /**
             * 画正弦函数曲线
             */
   /* mPaint.setStrokeCap(Paint.Cap.ROUND);
    mPaint.setStrokeWidth(5f);
    double w=2*Math.PI/(mRadiusMax*4);
    float x=mProgressNow*mRectWidthMax;
    double y=mProgressBarHeight/2*Math.sin(w*x+0)+0;
    canvas.drawPoint(x, (float) y,mPaint);*/
            //  Log.d(TAG, "drawPetal: getPhase"+petalBean.getPhase());
            //  Log.d(TAG, "drawPetal: getSwing"+petalBean.getSwing());

            double w = 2 * Math.PI / (petalBean.getPhase());
            float x = petalBean.getX_random() * (mRectWidth + mRadius);
            float translateX = x + mPetalDisance;
            float xNew = translateX % (mRectWidth + mRadius);
            // Log.d(TAG, "drawPetal: xNew:"+xNew);
            double y = petalBean.getSwing() * Math.sin(w * xNew + 0) + nowPetalHeight / 2;//k 值看情况
            //Log.d(TAG, "drawPetal: x:"+translateX+",y:"+y);
            //canvas.drawPoint(-x, (float) y,mPaint);
            //mPetalDisance 递增  (mRectWidth+mRadius)路程花瓣最大转4圈  看情况改
            float angle = 4 * mPetalDisance / (mRectWidth + mRadius) * petalBean.getRotateAngleMax() * petalBean.getRotateDirection();
            //花瓣自我旋转的角度
            matrixPetal.postRotate(angle, -nowScaleW_Petal * petalBitmap.getWidth() / 2, -nowScaleH_Petal * petalBitmap.getHeight() / 2);//每次步长

            matrixPetal.postTranslate(-xNew, (float) y);//取余 往复运动

            //matrixPetal.postScale(1/2,1/2);
            //if (random%3==0){
            // matrixPetal.postRotate((float) (angleTack*angle));//替换
            // }
            //

            double leftCanReachWidth = Math.sqrt(mRadius * mRadius - petalBean.getSwing() * petalBean.getSwing());//根据振幅计算左边不超过半圆的要求时候的宽度

            if (translateX >= (mRectWidth + mRadius)) {//初始第一屏不显示,大于后显示
                // canvas.drawPoint(-x,(float) y,mPaint);
                if (xNew <= (mRectWidth + leftCanReachWidth - nowPetalWidth)) {//左侧不超过半圆
                    canvas.drawBitmap(petalBitmap, matrixPetal, null);
                }

//&&
            }
            if (translateX >= 2 * (mRectWidth + mRadius)-nowPetalWidth) {//初始第一屏不显示,第二屏到了最左侧显示loading
                mProgress = mProgress + 0.001f;
                beginLoading(canvas);

               /* *//**//*if (!mAnimatorIsStart) {
                    mValueAnimator.start();
                    mAnimatorIsStart = true;
                }*//**//**/
            }
        }
        canvas.restore();
        //
        //
    }

    private void beginLoading(Canvas canvas) {
        canvas.save();
        canvas.translate(-mRectWidth, 0);
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
        canvas.restore();
    }


    private void initPetalBeanListData() {
        if (mProgressBarHeightWithoutFrame == 0) {
            return;
        }
        for (int i = 0; i < mPetalCount; i++) {
            PetalBean petalBean = new PetalBean();
            /***/
            //强转int  生成1,2随机数
            int randomDirection = (int) (Math.random() * 2 + 1);//Math.random() 生成[0，1)随机数  Math.random()*2+1   [1，3)
            //变相生成1,-1
            int rotateDirection = randomDirection % 2 == 0 ? 1 : -1;
            petalBean.setRotateDirection(rotateDirection);
            /***/
            int randomAngleMax = (int) (Math.random() * 360 + 1);//[1，361)
            petalBean.setRotateAngleMax(randomAngleMax);
            /***/
            float swingMax = mProgressBarHeightWithoutFrame * 1.0f / 3;//
            float swingMin = mProgressBarHeightWithoutFrame * 1.0f / 4;//

            int randomSwing = (int) (Math.random() * (swingMax - swingMin) + swingMin);//[swingMin，swingMax+1)
            petalBean.setSwing(randomSwing);
            /***/
            float phaseMax = mProgressBarWidthWithoutFrame * 1.0f / 2;//最大相位是  宽度
            float phaseMin = mProgressBarWidthWithoutFrame * 1.0f / 4;//最大相位是  宽度
            //强转int  生成1到 mSwingLevel+1的随机数
            int randommPhase = (int) (Math.random() * (phaseMax - phaseMin) + phaseMin);//[PhaseMin，PhaseMax+1)  Math.random()*(n-m)+m
            petalBean.setPhase(randommPhase);
            /***/
            //强转int  生成0-1的随机数
            double x_random = Math.random();//[0，1)
          /*  if (i>0){
                if (mPetalBeanList.get(i-1).getX_random()-x_random<0.1){
                   double x_randomTemp=x_random+0.1;
                    if (x_randomTemp>1){
                        x_random=x_random-0.1;
                    }else{
                        x_random=x_randomTemp;
                    }

                }
            }*/
            petalBean.setX_random((float) x_random);
            //
            // Log.d(TAG, "initPetalBeanListData:petalBean "+petalBean);

            mPetalBeanList.add(petalBean);
        }

    }

    public void setProgressOld(float progress) {
        mProgress = progress / 100;
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
