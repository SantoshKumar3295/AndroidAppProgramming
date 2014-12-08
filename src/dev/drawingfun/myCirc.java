package dev.drawingfun;

import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Paint;
import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.view.MotionEvent;

public class myCirc implements DrawElement {
    public float mStartX;
    public float mStartY;
    public float mEnd;

    public float mx;
    public float my;
    public Paint mPaint;
    public int mColor;
    public float mSize;
    private static final float TOUCH_TOLERANCE = 4;
    public static final int id = 2;

    public int getId() {    
        return id;
    }
    public void setStart(float x, float y) {
        mStartX = x;
        mStartY = y;
    }
    public myCirc() {
        mStartX = 0;
        mStartY = 0;
        mEnd = 0;
        mColor = 0;
        mSize = 0;
        mPaint = null;
    }

    public myCirc myCircWrap(float startx, float starty, float end, Paint myPaint, int color, float size) {
        myCirc result = new myCirc();
        result.mStartX = startx;
        result.mStartY = starty;
        result.mEnd = end;
        result.mPaint = myPaint;
        result.mColor = color;
        result.mPaint.setColor(myPaint.getColor());
        result.mSize = size;
        result.mPaint.setStrokeWidth(size);
        return result;
    }

    public void setPoints(float startx, float starty, float end) {
        mStartX = startx;
        mStartY = starty;
        mEnd = end;
    }
}

