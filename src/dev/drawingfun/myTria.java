package dev.drawingfun;

import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Paint;
import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.view.MotionEvent;

public class myTria implements DrawElement {
    public float mStartX;
    public float mStartY;
    public float mMidX;
    public float mMidY;
    public float mEndX;
    public float mEndY;
    //public float mx;
    //public float my;
    public Paint mPaint;
    public int mColor;
    public float mSize;
    private static final float TOUCH_TOLERANCE = 4;
    public static final int id = 5;

    public int getId() {    
        return id;
    }
    public void setStart(float x, float y) {
        mStartX = x;
        mStartY = y;
    }
    public void setMid(float x, float y) {
        mMidX = x;
        mMidY = y;
    }
    public myTria() {
        mStartX = 0;
        mStartY = 0;
        mMidX = 0;
        mMidY = 0;
        mEndX = 0;
        mEndY = 0;
        mColor = 0;
        mSize = 0;
        mPaint = null;
    }

    public myTria myTriaWrap(float startx, float starty, float midx, float midy, float endx, float endy, Paint myPaint, int color, float size) {
        myTria result = new myTria();
        result.mStartX = startx;
        result.mStartY = starty;
        result.mMidX = midx;
        result.mMidY = midy;
        result.mEndX = endx;
        result.mEndY = endy;
        result.mPaint = myPaint;
        result.mColor = color;
        result.mPaint.setColor(myPaint.getColor());
        result.mSize = size;
        result.mPaint.setStrokeWidth(size);
        return result;
    }

    public void setPoints(float startx, float starty, float midx, float midy, float endx, float endy) {
        mStartX = startx;
        mStartY = starty;
        mMidX = midx;
        mMidY = midy;
        mEndX = endx;
        mEndY = endy;
    }
}

