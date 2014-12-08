package dev.drawingfun;

import android.graphics.Path;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Canvas;
import android.view.MotionEvent;

public class myPath implements DrawElement { 
    public Path mPath;  
    public Paint mPaint;
    public Canvas mCanvas;
    public int mColor;
    public float mSize;
    //public boolean isDrawing = false;
    private static final float TOUCH_TOLERANCE = 4;
    public static final int id = 4;
    public float mx;
    public float my;

    public int getId() {    
        return id;
    }

    public myPath() {
        super();
        mPaint = null;
        mColor = 0;
        mSize = 0;
    }

    public myPath myPathWrap(Path myPath, Paint myPaint, int color, float size) {
        myPath result = new myPath();
        result.mPath = myPath;
        result.mPaint = myPaint;
        result.mColor = color;
        result.mPaint.setColor(myPaint.getColor());
        result.mSize = size;
        result.mPaint.setStrokeWidth(size);
        /*
        mFill = fill;
        if (mFill)
            result.mPaint.setStyle(Paint.Style.STROKE);
        else 
            result.mPaint.setStyle(Paint.Style.FILL);
        */
        return result;
    }
    
        /*
    public void onDraw(Canvas canvas) {

        for(DrawElement p: paths){
            mPaint.setColor(p.mColor);
            mPaint.setStrokeWidth(p.mSize);
            canvas.drawPath(p.mPath, mPaint);
        }

        mPaint.setColor(mColor);
        mPaint.setStrokeWidth(mSize);
        canvas.drawPath(mPath, mPaint); 
    }
    
    public boolean onTouchEvent(MotionEvent event, Canvas canvas) {
            switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            isDrawing = true;
            //touch_start(mx, my);
            //invalidate();
            break;
        case MotionEvent.ACTION_MOVE:
            //touch_move(mx, my);
            //invalidate();
            break;
        case MotionEvent.ACTION_UP:
            isDrawing = false;
            //touch_up();
            //invalidate();
            break;
        }
        return true;
    }
        */
}

