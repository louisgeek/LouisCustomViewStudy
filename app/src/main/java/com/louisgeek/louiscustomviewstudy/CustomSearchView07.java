package com.louisgeek.louiscustomviewstudy;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import static android.content.ContentValues.TAG;

/**
 * Created by louisgeek on 2016/10/24.
 */

public class CustomSearchView07 extends View {
    private Path mPath_search;
    private  Path mPath_outSide_circle;
    private Paint mPaint;

    private PathMeasure mPathMeasure_search;
    private PathMeasure mPathMeasure_outSide_circle;

    private  int mScreenWidth,mScreenHeight,mViewWidth,mViewHeight;

    private  final int STATE_STOP=0;
    private  final int STATE_START_LOAD=1;
    private  final int STATE_LOADING=2;
    private  final int STATE_END_LOADING=3;

    private int mCurrentState=STATE_STOP;

    private int mOutSideCircleColor=Color.parseColor("#3F51B5");
    private int mInsideSearchColor=Color.parseColor("#31C5F6");
    private int mOutSideCircleFrameHeight=this.dp2px(10);
    private int mInsideSearchCircleFrameHeight=this.dp2px(10);
    private int mOutSideCircleRadius=this.dp2px(30);
    private int mInsideSearchCircleRadius=this.dp2px(15);


    private ValueAnimator mValueAnimator_Loading;
    private ValueAnimator mValueAnimator_StartLoad;
    private ValueAnimator mValueAnimator_EndLoading;

    private float mStartLoadState_Progress;
    private float mLoadingState_Progress;
    private float mEndLoadingState_Progress;


    private boolean mSearchStop=true;


    private Animator.AnimatorListener mAnimatorListener;
    public CustomSearchView07(Context context) {
        this(context,null);
    }

    public CustomSearchView07(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CustomSearchView07(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.CustomSearchView07);
        mOutSideCircleColor = ta.getColor(R.styleable.CustomSearchView07_outSideCircleColor, mOutSideCircleColor);
        mInsideSearchColor = ta.getColor(R.styleable.CustomSearchView07_insideSearchColor, mInsideSearchColor);
        mOutSideCircleFrameHeight = ta.getDimensionPixelOffset(R.styleable.CustomSearchView07_outSideCircleFrameHeight, mOutSideCircleFrameHeight);
        mInsideSearchCircleFrameHeight= ta.getDimensionPixelOffset(R.styleable.CustomSearchView07_insideSearchCircleFrameHeight, mInsideSearchCircleFrameHeight);
        mOutSideCircleRadius = ta.getDimensionPixelOffset(R.styleable.CustomSearchView07_outSideCircleRadius, mOutSideCircleRadius);
        mInsideSearchCircleRadius= ta.getDimensionPixelOffset(R.styleable.CustomSearchView07_insideSearchCircleRadius, mInsideSearchCircleRadius);

            //
        ta.recycle();

        init();
    }
    //动画开始入口
    public void startSearch() {
        if (mSearchStop) {
            mCurrentState=STATE_STOP;
            mHandler.sendEmptyMessage(0);
        }
    }
    //动画开始入口
    public void startSearchDelayed(long delayMillis) {
        if (mSearchStop) {
            mCurrentState=STATE_STOP;
            mHandler.sendEmptyMessageDelayed(0, delayMillis);
        }
    }
    public void stopSearchLoading() {
        mSearchStop = true;
        mCurrentState=STATE_END_LOADING;
        mValueAnimator_EndLoading.start();
        //
        //mValueAnimator_StartLoad.cancel();
        //mValueAnimator_Loading.cancel();
    }

    private void init() {
        mPaint=new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        mPath_search=new Path();
        mPath_outSide_circle=new Path();

        mPathMeasure_search=new PathMeasure();
        mPathMeasure_outSide_circle=new PathMeasure();

        /**
         * 公用动画监听  控制一个动画结束通知接下来的动画做某事
         */
        mAnimatorListener=new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mHandler.sendEmptyMessage(0);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        };
        //
        mValueAnimator_StartLoad =ValueAnimator.ofFloat(0f,1f).setDuration(3*1000);
        mValueAnimator_StartLoad.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
            mStartLoadState_Progress= (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        mValueAnimator_StartLoad.addListener(mAnimatorListener);

        //
        mValueAnimator_Loading =ValueAnimator.ofFloat(0f,1f).setDuration(3*1000);
        mValueAnimator_Loading.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mLoadingState_Progress= (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        mValueAnimator_Loading.addListener(mAnimatorListener);

        //
        mValueAnimator_EndLoading=ValueAnimator.ofFloat(0f,1f).setDuration(3*1000);
        mValueAnimator_EndLoading.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mEndLoadingState_Progress= (float) animation.getAnimatedValue();
                invalidate();
            }
        });




    }


    /**
     * 思路
     *
     * STATE_STOP 是入口
     * 每次发消息都是状态mCurrentState都是逐步向下进展
     *
     * 通过前一个状态来控制下一个状态的执行
     */
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (mCurrentState){
                case STATE_STOP:
                    //
                    mValueAnimator_StartLoad.start();
                    Log.d(TAG, "handleMessage: STATE_STOP");
                    mCurrentState=STATE_START_LOAD;
                    break;
                case STATE_START_LOAD:

                    mValueAnimator_Loading.start();
                    mSearchStop=false;
                    //
                    Log.d(TAG, "handleMessage: STATE_START_LOAD");
                    mCurrentState=STATE_LOADING;
                    break;
                case STATE_LOADING:
                    if (!mSearchStop){
                        mValueAnimator_Loading.start();
                    }else{
                        mCurrentState=STATE_END_LOADING;
                        //
                        mValueAnimator_EndLoading.start();
                    }
                    break;
                case STATE_END_LOADING:
                    mCurrentState=STATE_STOP;
                    break;
            }
        }
    };


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        /*mWidth =getMeasuredWidth();
         mHeight = getMeasuredHeight();*/

       /* int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);*/
        /**
        * 实际的内容宽和高
        */
        int contentWidthSize = mOutSideCircleRadius*2+mOutSideCircleFrameHeight/2+this.getPaddingLeft()+this.getPaddingRight();
        int contentHeightSize = mOutSideCircleRadius*2+mOutSideCircleFrameHeight/2+this.getPaddingTop()+this.getPaddingBottom();
        //getDefaultSize()
        int width = resolveSize(contentWidthSize, widthMeasureSpec);
        int height = resolveSize(contentHeightSize, heightMeasureSpec);

        /**
         * 修正xml设置太小的情况
         */
        width=width<contentWidthSize?contentWidthSize:width;
        height=height<contentHeightSize?contentHeightSize:height;

        setMeasuredDimension(width, height);

        mViewWidth = width;
        mViewHeight = height;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //
        canvas.translate(mViewWidth/2,mViewHeight/2);
        Log.d(TAG, "onDraw:111 ");
        /**
         * 搜索的圆弧
         */
        int search_circle_R=mInsideSearchCircleRadius;
        RectF rectf_search=new RectF(-search_circle_R,-search_circle_R,search_circle_R,search_circle_R);
        mPath_search.addArc(rectf_search,45f,359.99f);
       //#### canvas.drawPath(mPath_search,mPaint);
        /**
         * 测量路径  获取45度起点的坐标
         */
        float[] pos_search=new float[2];
        mPathMeasure_search.setPath(mPath_search,false);
        mPathMeasure_search.getPosTan(0,pos_search,null);//距离0  就是起点的位置
        float x_search_start=pos_search[0];
        float y_search_start=pos_search[1];
        Log.d(TAG, "onDraw: x_search_start,y_search_start:"+x_search_start+","+y_search_start);

        /**
         * 画外侧的圆
         */
        int outSide_circle_R=mOutSideCircleRadius;
        RectF rectf_outSide_circle=new RectF(-outSide_circle_R,-outSide_circle_R,outSide_circle_R,outSide_circle_R);
        mPath_outSide_circle.addArc(rectf_outSide_circle,45f,359.99f);

        /**
         * 测量路径  获取45度起点的坐标
         */
        float[] pos_outSide_circle=new float[2];
        mPathMeasure_outSide_circle.setPath(mPath_outSide_circle,false);
        mPathMeasure_outSide_circle.getPosTan(0,pos_outSide_circle,null);//距离0  就是起点的位置
        float x_outSide_circle_start=pos_outSide_circle[0];
        float y_outSide_circle_start=pos_outSide_circle[1];

        Log.d(TAG, "onDraw: x_outSide_circle_start,y_outSide_circle_start:"+x_outSide_circle_start+","+y_outSide_circle_start);
        //连接到搜索圆弧的起点  搜索的斜杠
        //###mPath_outSide_circle.lineTo(x_search_start,y_search_start);
       ///### mPath_search.lineTo(x_outSide_circle_start,y_outSide_circle_start);
        //
        //####  canvas.drawPath(mPath_outSide_circle,mPaint);

        /**
         *
         */
        switch (mCurrentState){
            case STATE_STOP:
                /**
                 * 就画搜索
                 */
                //连接到搜索圆弧的起点  搜索的斜杠
                mPath_search.lineTo(x_outSide_circle_start,y_outSide_circle_start);
                //
                mPaint.setColor(mInsideSearchColor);
                mPaint.setStrokeWidth(mInsideSearchCircleFrameHeight);
                canvas.drawPath(mPath_search,mPaint);
                break;
            case STATE_START_LOAD:
                Log.d(TAG, "onDraw: STATE_START_LOAD:");
                Path dstPath = new Path();
                float startD=mStartLoadState_Progress*mPathMeasure_search.getLength();
                mPathMeasure_search.getSegment(startD,mPathMeasure_search.getLength(),dstPath,true);
                mPaint.setColor(mInsideSearchColor);
                mPaint.setStrokeWidth(mInsideSearchCircleFrameHeight);
                canvas.drawPath(dstPath,mPaint);
                break;
            case STATE_LOADING:
                Log.d(TAG, "onDraw: STATE_LOADING:");
                /**
                 * 转动的圆弧先小后大后小
                 */
                Path dstPath_loading = new Path();
                /**
                 * 逆时针 从大到小
                 */
                float stopD_loading=mPathMeasure_outSide_circle.getLength()*(1-mLoadingState_Progress);
                /**
                 * 1/2的进度减去当前进度  的绝对值  ： 1/2 到  0  到  1/2
                 * 再用1/2减去【所得的绝对值】：0到1/2到0
                 * 1/2 会整除(用1.0f/2或者直接用0.5)
                 */
                float yuanHuBiLi=0.5f-Math.abs(0.5f-mLoadingState_Progress);
                Log.d(TAG, "onDraw: yuanHuBiLi:"+yuanHuBiLi);
                float yuanHuMax=mPathMeasure_outSide_circle.getLength()/3;
                float startD_loading=stopD_loading-yuanHuBiLi*yuanHuMax;
                mPathMeasure_outSide_circle.getSegment(startD_loading,stopD_loading,dstPath_loading,true);
                mPaint.setColor(mOutSideCircleColor);
                mPaint.setStrokeWidth(mOutSideCircleFrameHeight);
                canvas.drawPath(dstPath_loading,mPaint);
                break;
            case STATE_END_LOADING:
                Log.d(TAG, "onDraw: STATE_END_LOADING:");
                Path dstPath_backSearch = new Path();
                float startD_backSearch=(1-mEndLoadingState_Progress)*mPathMeasure_search.getLength();
                mPathMeasure_search.getSegment(startD_backSearch,mPathMeasure_search.getLength(),dstPath_backSearch,true);
                mPaint.setColor(mInsideSearchColor);
                mPaint.setStrokeWidth(mInsideSearchCircleFrameHeight);
                canvas.drawPath(dstPath_backSearch,mPaint);
                break;
        }
    }


    //

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
