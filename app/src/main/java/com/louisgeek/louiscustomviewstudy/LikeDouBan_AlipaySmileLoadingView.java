package com.louisgeek.louiscustomviewstudy;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
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
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import static android.content.ContentValues.TAG;

/**
 * 支付宝的稍微和豆瓣有点区别，下面是仿豆瓣App加载中loading，支付宝自行调整下即可。
 * Created by louisgeek on 2016/10/24.
 */

public class LikeDouBan_AlipaySmileLoadingView extends View {
    private final int STATE_STOP = 0;
    private final int STATE_START_LOAD = 1;
    private final int STATE_LOADING = 2;
    private final int STATE_END_LOADING = 3;
    private final int STATE_TURN_SMILE = 4;
    private int mCurrenState = STATE_STOP;
    private Paint mPaint;
    private int mScreenWidth, mScreenHeight, mViewWidth, mViewHeight;
    private ValueAnimator mValueAnimatorStartLoad;
    private ValueAnimator mValueAnimatorLoading;
    private ValueAnimator mValueAnimatorTurnSmile;
    private ValueAnimator mValueAnimatorEndLoading;

    private ValueAnimator mValueAnimatorHoldSimle;

    private float mLoadingProgress;
    private float mStartLoadProgress;
    private float mTurnSmileProgress;
    private float mEndLoadingProgress;

    private float mHoldSimleProgress;
    private float mRaidus = this.dp2px(30);
    private  int mSmileCircleFrameHeight= this.dp2px(10);
    private int flagLoadingCount;
    private int flagHoldSmileCount;
    private boolean mStopLoading=true;

    private int START_END_DURATION=800;
    private ValueAnimator.AnimatorListener mAnimatorListener;
    private  int mSmileColor= Color.parseColor("#2d832e");


    public LikeDouBan_AlipaySmileLoadingView(Context context) {
        this(context, null);
    }

    public LikeDouBan_AlipaySmileLoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LikeDouBan_AlipaySmileLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.LikeDouBan_AlipaySmileLoadingView);
        mRaidus = ta.getDimension(R.styleable.LikeDouBan_AlipaySmileLoadingView_smileCircleRadius, mRaidus);
        mSmileCircleFrameHeight = ta.getDimensionPixelOffset(R.styleable.LikeDouBan_AlipaySmileLoadingView_smileCircleFrameHeight, mSmileCircleFrameHeight);
        mSmileColor = ta.getColor(R.styleable.LikeDouBan_AlipaySmileLoadingView_smileColor, mSmileColor);

        //
        ta.recycle();


        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setStrokeWidth(mSmileCircleFrameHeight);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setColor(mSmileColor);
        mPaint.setStrokeCap(Paint.Cap.ROUND);


        mAnimatorListener = new Animator.AnimatorListener() {
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

        mValueAnimatorStartLoad = ValueAnimator.ofFloat(0f, 1f).setDuration(START_END_DURATION);
        mValueAnimatorStartLoad.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mStartLoadProgress = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        mValueAnimatorStartLoad.addListener(mAnimatorListener);
//
        mValueAnimatorLoading = ValueAnimator.ofFloat(0f, 1f).setDuration(700);
        mValueAnimatorLoading.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mLoadingProgress = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        mValueAnimatorLoading.addListener(mAnimatorListener);

        mValueAnimatorTurnSmile= ValueAnimator.ofFloat(0f, 1f).setDuration(500);
        mValueAnimatorTurnSmile.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mTurnSmileProgress = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        mValueAnimatorTurnSmile.addListener(mAnimatorListener);


        mValueAnimatorEndLoading= ValueAnimator.ofFloat(0f, 1f).setDuration(START_END_DURATION);
        mValueAnimatorEndLoading.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mEndLoadingProgress = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        mValueAnimatorEndLoading.addListener(mAnimatorListener);

        mValueAnimatorHoldSimle= ValueAnimator.ofFloat(0f, 1f).setDuration(500);
        mValueAnimatorHoldSimle.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mHoldSimleProgress = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        mValueAnimatorHoldSimle.addListener(mAnimatorListener);



        /*mHandler.sendEmptyMessage(0);//start*/
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //
        switch (mCurrenState) {
             case STATE_STOP:
                 Log.d(TAG, "handleMessage: STATE_STOP");
                 mCurrenState = STATE_START_LOAD;
                 mValueAnimatorStartLoad.start();
                 break;
                case STATE_START_LOAD:
                    Log.d(TAG, "handleMessage: STATE_START_LOAD");
                    mValueAnimatorLoading.start();
                    mStopLoading=false;
                    mCurrenState = STATE_LOADING;
                    break;
                case STATE_LOADING:
                    Log.d(TAG, "handleMessage: STATE_LOADING");
                    if (!mStopLoading){
                        if (flagLoadingCount<0) {
                            flagLoadingCount++;
                            mValueAnimatorLoading.start();
                        }else {
                            flagLoadingCount=0;
                            //
                            mCurrenState = STATE_END_LOADING;
                            mValueAnimatorEndLoading.start();
                        }
                    }else{
                        //结束
                        mCurrenState = STATE_END_LOADING;
                        mValueAnimatorEndLoading.start();
                    }
                    break;
                case STATE_END_LOADING:
                    Log.d(TAG, "handleMessage: STATE_END_LOADING");
                    /**
                     * 结束加载 到 翻转微笑....
                     */
                    mValueAnimatorTurnSmile.start();
                    mCurrenState = STATE_TURN_SMILE;
                 break;
             case STATE_TURN_SMILE:
                 Log.d(TAG, "handleMessage: STATE_SMILE");
                 if (!mStopLoading){
                     if (flagHoldSmileCount<1){
                         flagHoldSmileCount++;
                         /**
                          * 保持微笑 。。。。
                          */
                         mValueAnimatorHoldSimle.start();
                     }else{
                         flagHoldSmileCount=0;

                         //继续。。。。。
                         mCurrenState = STATE_START_LOAD;
                         mValueAnimatorStartLoad.start();
                     }

                 }else {
                     //结束
                     mCurrenState = STATE_STOP;

                 }
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
        int contentWidthSize = (int) (mRaidus*2+mSmileCircleFrameHeight/2+this.getPaddingLeft()+this.getPaddingRight());
        int contentHeightSize = (int) (mRaidus*2+mSmileCircleFrameHeight/2+this.getPaddingTop()+this.getPaddingBottom());
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

        canvas.translate(mViewWidth / 2, mViewHeight / 2);

        /**
         * 笑脸
         */
        RectF rectf_circle = new RectF(-mRaidus, -mRaidus, mRaidus, mRaidus);
        Path path = new Path();


        /**
         * 眼睛初始
         */
        float eyeXDisance = mRaidus *2/3;
        float eyeYDisance = (float) Math.sqrt(mRaidus * mRaidus - eyeXDisance * eyeXDisance);
        /*###  canvas.drawPoint(-eyeXDisance,-eyeYDisance,mPaint);
        canvas.drawPoint(eyeXDisance,-eyeYDisance,mPaint);*/


        switch (mCurrenState) {
            case STATE_STOP:
                /**
                 *画停止的笑脸
                 */
                Log.d(TAG, "onDraw: STATE_STOP");
                //下巴
                path.addArc(rectf_circle, 0f, 179.99f);
                canvas.drawPath(path, mPaint);
                //眼睛
                canvas.drawPoint(-eyeXDisance, -eyeYDisance, mPaint);
                canvas.drawPoint(eyeXDisance, -eyeYDisance, mPaint);
                break;
            case STATE_START_LOAD:
                /**
                 *画左脸颊吸收眼睛到右脸颊
                 */
                Log.d(TAG, "onDraw: STATE_START_LOAD");
                path.addArc(rectf_circle, 0f, 359.99f);
                PathMeasure pathMeasure = new PathMeasure();
                pathMeasure.setPath(path, false);

                //float loadingDisance=pathMeasure.getLength()/2+pathMeasure.getLength()/4*mStartLoadProgress;
                Path dst = new Path();
                //假设 整个进度 起点转过了90度，就是长度的1/4
                float startD = pathMeasure.getLength() / 4 * mStartLoadProgress;
                //假设 整个进度  终点转过了180度再加上长度的1/4
                float loadingDisance = pathMeasure.getLength() / 2 + pathMeasure.getLength() / 4 * mStartLoadProgress;

                pathMeasure.getSegment(startD, startD + loadingDisance, dst, true);
                canvas.drawPath(dst, mPaint);
                //眼睛应该同时顺时针旋转一部分
                float eyeXDisance_left =eyeXDisance-mStartLoadProgress*mRaidus/3;
                float eyeYDisance_left = (float) Math.sqrt(mRaidus * mRaidus - eyeXDisance_left * eyeXDisance_left);
                float eyeXDisance_right =eyeXDisance+mStartLoadProgress*mRaidus/3;
                float eyeYDisance_right = (float) Math.sqrt(mRaidus * mRaidus - eyeXDisance_right * eyeXDisance_right);
                canvas.drawPoint(-eyeXDisance_left, -eyeYDisance_left, mPaint);//左边
                canvas.drawPoint(eyeXDisance_right, -eyeYDisance_right, mPaint);//右边
                break;
            case STATE_LOADING:
                /**
                 *右边点转圈  回到右边点   用于重复动作
                 */
                Log.d(TAG, "onDraw: STATE_LOADING");
                float startAngle = 90f + mLoadingProgress * 360;
                path.addArc(rectf_circle, startAngle, 359.99f * 3 / 4);
                canvas.drawPath(path, mPaint);
                break;
            case STATE_END_LOADING:
                /**
                 *释放眼睛  到笑脸横向
                 */
                Log.d(TAG, "onDraw: STATE_END_LOADING");
                path.addArc(rectf_circle, 90f, 359.99f);
                PathMeasure pathMeasureEndLoading = new PathMeasure();
                pathMeasureEndLoading.setPath(path, false);

                Path dstEndLoading = new Path();
                //假设 整个进度 起点转过了180度，就是长度1/2
                float startDEndLoading = pathMeasureEndLoading.getLength()/2*mEndLoadingProgress;
                //假设 整个进度  终点转过了180度 再加上长度的1/4
                float endLoadingDisance = pathMeasureEndLoading.getLength()/2+pathMeasureEndLoading.getLength() / 4 * (1-mEndLoadingProgress);

                pathMeasureEndLoading.getSegment(startDEndLoading, startDEndLoading + endLoadingDisance, dstEndLoading, true);
                canvas.drawPath(dstEndLoading, mPaint);

                //眼睛应该同时顺时针旋转一部分
                float eyeXDisance_end_left_default= mRaidus;
                float eyeYDisance_end_left_default = (float) Math.sqrt(mRaidus * mRaidus - eyeXDisance_end_left_default * eyeXDisance_end_left_default);
                float eyeXDisance_end_left =eyeXDisance_end_left_default-mEndLoadingProgress*mRaidus*1/3;
                float eyeYDisance_end_left = (float) Math.sqrt(mRaidus * mRaidus - eyeXDisance_end_left * eyeXDisance_end_left);
                //
                canvas.drawPoint(-eyeXDisance_end_left, -eyeYDisance_end_left, mPaint);//左边

                float eyeXDisance_end_right_default= 0;
                float eyeYDisance_end_right_default = (float) Math.sqrt(mRaidus * mRaidus - eyeXDisance_end_right_default * eyeXDisance_end_right_default);
                float eyeXDisance_end_right =eyeXDisance_end_right_default+mEndLoadingProgress*mRaidus*2/3;
                float eyeYDisance_end_right = (float) Math.sqrt(mRaidus * mRaidus - eyeXDisance_end_right * eyeXDisance_end_right);
                canvas.drawPoint(-eyeXDisance_end_right, eyeYDisance_end_right, mPaint);
                break;
            case STATE_TURN_SMILE:
                /**
                 *笑脸翻转
                 */
                Log.d(TAG, "onDraw: STATE_SMILE");
                //下巴
                float startAngleSmile = -90f + mTurnSmileProgress * 90;
                path.addArc(rectf_circle, startAngleSmile, 179.99f);
                canvas.drawPath(path, mPaint);
                /**
                 * 眼睛翻转前的初始
                 * 上面的点  top
                 * 下面的点  bottom
                 */
                float eyeTurn_X_top_default = mRaidus*2/3;
                float eyeTurn_Y_top_default = (float) Math.sqrt(mRaidus * mRaidus - eyeTurn_X_top_default * eyeTurn_X_top_default);
                float eyeTurn_X_top=0;

                float eyeTurn_X_bottom_default = mRaidus*2/3;
                float eyeTurn_Y_bottom_default = (float) Math.sqrt(mRaidus * mRaidus - eyeTurn_Y_top_default * eyeTurn_Y_top_default);
                float eyeTurn_X_bottom=0;
                float eyeTurn_Y_bottom=0;
                float oneProgress=1.0f/3;
                float oneProgressThenLeave=1-oneProgress;
                //top的1/3段
                if (mTurnSmileProgress<oneProgress){
                    eyeTurn_X_top=eyeTurn_X_top_default-mRaidus*2/3*(mTurnSmileProgress/oneProgress);
                    eyeTurn_X_top=-1*eyeTurn_X_top;
                    /***/
                    eyeTurn_X_bottom=mRaidus*2/3+mRaidus*1/3*(mTurnSmileProgress/oneProgress);
                    eyeTurn_Y_bottom=(float) Math.sqrt(mRaidus * mRaidus - eyeTurn_X_bottom * eyeTurn_X_bottom);

                }else if (mTurnSmileProgress>=oneProgress){
                    eyeTurn_X_top=mRaidus*2/3*(mTurnSmileProgress-oneProgress)/oneProgressThenLeave;
                    /***/
                    eyeTurn_X_bottom=mRaidus-mRaidus*1/3*(mTurnSmileProgress-oneProgress)/oneProgressThenLeave;
                    eyeTurn_Y_bottom=(float) Math.sqrt(mRaidus * mRaidus - eyeTurn_X_bottom*eyeTurn_X_bottom);
                    eyeTurn_Y_bottom=-1*eyeTurn_Y_bottom;
                }
                float eyeTurn_Y_top=(float) Math.sqrt(mRaidus * mRaidus - eyeTurn_X_top * eyeTurn_X_top);

           /*     //bottom 的1/3段
                float tempProgress=1.0f/3;
                float tempProgressLeave=1-tempProgress;
                if (mTurnSmileProgress<tempProgress){
                    *//***//*
                    eyeTurn_X_bottom=mRaidus*2/3+mRaidus*1/3*(mTurnSmileProgress/tempProgress);
                    eyeTurn_Y_bottom=(float) Math.sqrt(mRaidus * mRaidus - eyeTurn_X_bottom * eyeTurn_X_bottom);
                }else if(mTurnSmileProgress>=tempProgress){
                    eyeTurn_X_bottom=mRaidus-mRaidus*1/3*(mTurnSmileProgress-tempProgress)/tempProgressLeave;
                    eyeTurn_Y_bottom=(float) Math.sqrt(mRaidus * mRaidus - eyeTurn_X_bottom*eyeTurn_X_bottom);
                    eyeTurn_Y_bottom=-1*eyeTurn_Y_bottom;
                }
*/

                /**
                 * top  y一直是负的
                 */
                canvas.drawPoint(eyeTurn_X_top, -eyeTurn_Y_top, mPaint);


                /**
                 * bottom  x一直是负的
                 */
               canvas.drawPoint(-eyeTurn_X_bottom, eyeTurn_Y_bottom, mPaint);
                break;
        }
    }

    public void setLoadingProgress(float loadingProgress) {
        mLoadingProgress = loadingProgress;
    }
    //动画开始入口
    public void startLoad() {
        if (mStopLoading) {
            mCurrenState=STATE_STOP;
            mHandler.sendEmptyMessage(0);
        }
    }
    //动画开始入口
    public void startLoadDelayed(long delayMillis) {
        if (mStopLoading) {
            mCurrenState=STATE_STOP;
            mHandler.sendEmptyMessageDelayed(0, delayMillis);
        }
    }
    public void stopLoading() {
        if (!mStopLoading) {
            mStopLoading = true;
        }
    }

    //
    public int dp2px(int dpValue) {
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getResources().getDisplayMetrics());
        return px;
    }
}
