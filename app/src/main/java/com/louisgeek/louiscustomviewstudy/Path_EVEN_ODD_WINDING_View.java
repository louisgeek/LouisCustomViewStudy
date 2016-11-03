package com.louisgeek.louiscustomviewstudy;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by louisgeek on 2016/10/28.
 */

public class Path_EVEN_ODD_WINDING_View extends View {

    private  Paint mPaint;
    private int mWidth,mHeight;
    public Path_EVEN_ODD_WINDING_View(Context context) {
        this(context,null);
    }

    public Path_EVEN_ODD_WINDING_View(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public Path_EVEN_ODD_WINDING_View(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }
    private void init() {
        mPaint=new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.FILL);                   // 设置画布模式为填充
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

        canvas.translate(mWidth / 2, mHeight / 2);          // 移动画布(坐标系)


        Path path = new Path();                                     // 创建Path

        //#### path.setFillType(Path.FillType.EVEN_ODD);                   // 设置Path填充模式为 奇偶规则
        path.setFillType(Path.FillType.INVERSE_EVEN_ODD);            // 反奇偶规则

        path.addRect(-200,-200,200,200, Path.Direction.CW);         // 给Path中添加一个矩形

       //#### canvas.drawPath(path, mPaint);                       // 绘制Path


        //==============================================

        Path path2 = new Path();                                     // 创建Path

// 添加小正方形 (通过这两行代码来控制小正方形边的方向,从而演示不同的效果)
 path2.addRect(-200, -200, 200, 200, Path.Direction.CW);//顺时针
        //####     path2.addRect(-200, -200, 200, 200, Path.Direction.CCW);//逆时针

// 添加大正方形
        path2.addRect(-300, -300, 300, 300, Path.Direction.CCW);

        path2.setFillType(Path.FillType.WINDING);                    // 设置Path填充模式为非零环绕规则

      //####  canvas.drawPath(path2, mPaint);                       // 绘制Path



        ///=========================================================

        drawPathOpTest(canvas);
    }

    private void drawPathOpTest(Canvas canvas) {
        Path path = new Path();
        Path path2 = new Path();
        Path path3 = new Path();
        Path path4 = new Path();

        path.addCircle(0, 0, 200, Path.Direction.CW);//最大的圆
        path2.addRect(0, -200, 200, 200, Path.Direction.CW);//右边矩形
        path3.addCircle(0, -100, 100, Path.Direction.CW);//上面的小圆
        path4.addCircle(0, 100, 100, Path.Direction.CCW);//下面的小圆


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            path.op(path2, Path.Op.DIFFERENCE);
            path.op(path3, Path.Op.UNION);
            path.op(path4, Path.Op.DIFFERENCE);
        }else{
            /**
             * Api 19下采用画布的clip实现
             */
            canvas.clipPath(path2, Region.Op.DIFFERENCE);
            canvas.clipPath(path3,Region.Op.UNION);
            canvas.clipPath(path4,Region.Op.DIFFERENCE);
            //canvas.clipRect()

        }
        canvas.drawPath(path, mPaint);

    }
}
