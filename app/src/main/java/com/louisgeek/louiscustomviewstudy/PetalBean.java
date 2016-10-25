package com.louisgeek.louiscustomviewstudy;

/**
 * Created by louisgeek on 2016/10/20.
 */
public class PetalBean {
     // 旋转角度
    private int rotateAngleMax;
     // 旋转方向  1 顺时针，-1 逆时针
    private int rotateDirection;

    //运动曲线的振幅
    private int swing;

    //运动曲线的相位
    private int phase;


    public int getPhase() {
        return phase;
    }

    public void setPhase(int phase) {
        this.phase = phase;
    }


    public float getX_random() {
        return x_random;
    }

    public void setX_random(float x_random) {
        this.x_random = x_random;
    }

    private float x_random;

    public int getRotateAngleMax() {
        return rotateAngleMax;
    }

    public void setRotateAngleMax(int rotateAngleMax) {
        this.rotateAngleMax = rotateAngleMax;
    }

    public int getRotateDirection() {
        return rotateDirection;
    }

    public void setRotateDirection(int rotateDirection) {
        this.rotateDirection = rotateDirection;
    }

    public int getSwing() {
        return swing;
    }

    public void setSwing(int swing) {
        this.swing = swing;
    }


    @Override
    public String toString() {
        return "PetalBean{" +
                "rotateAngleMax=" + rotateAngleMax +
                ", rotateDirection=" + rotateDirection +
                ", swing=" + swing +
                ", phase=" + phase +
                ", x_random=" + x_random +
                '}';
    }
}
