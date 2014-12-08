package dev.drawingfun;

import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Paint;
import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.view.MotionEvent;

public class myLine implements DrawElement {
    public float mStartX;
    public float mStartY;
    public float mEndX;
    public float mEndY;

    public float mx;
    public float my;
    public Path mPath;  
    public Paint mPaint;
    public int mColor;
    public float mSize;
    private static final float TOUCH_TOLERANCE = 4;
    public static final int id = 3;

    public int getId() {    
        return id;
    }
    public void setStart(float x, float y) {
        mStartX = x;
        mStartY = y;
    }
    public void setEnd(float x, float y) {
        mEndX = x;
        mEndY = y;
    }
    public myLine() {
        mStartX = 0;
        mStartY = 0;
        mEndX = 0;
        mEndY = 0;
        mColor = 0;
        mSize = 0;
        mPaint = null;
    }

    public myLine myLineWrap(float startx, float starty, float endx, float endy, Paint myPaint, int color, float size) {
        myLine result = new myLine();
        result.mStartX = startx;
        result.mStartY = starty;
        result.mEndX = endx;
        result.mEndY = endy;
        result.mPaint = myPaint;
        result.mColor = color;
        result.mPaint.setColor(myPaint.getColor());
        result.mSize = size;
        result.mPaint.setStrokeWidth(size);
        return result;
    }

    public void setPoints(float startx, float starty, float endx, float endy) {
        mStartX = startx;
        mStartY = starty;
        mEndX = endx;
        mEndY = endy;
    }
}
