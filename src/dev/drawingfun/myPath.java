package dev.drawingfun;

import android.graphics.Path;
import android.graphics.Color;
import android.graphics.Paint;

//public class myPath extends Path {  // slightly strange here with a path member
public class myPath { 
    public Path mpath;  
    public Paint mPaint;
    public int mcolor;
    public float msize;
    public boolean mfill;
    
    public myPath() {
        super();
        mPaint = null;
        mcolor = 0;
        msize = 0;
        mfill = false;
    }

    public myPath myPathWrap(Path mPath, Paint myPaint, int color, float size, boolean fill) {
        myPath result = new myPath();
        result.mpath = mPath;
        result.mPaint = myPaint;
        result.mcolor = color;
        result.mPaint.setColor(myPaint.getColor());
        result.msize = size;
        result.mPaint.setStrokeWidth(size);
        mfill = fill;
        if (mfill)
            result.mPaint.setStyle(Paint.Style.STROKE);
        else 
            result.mPaint.setStyle(Paint.Style.FILL);
        return result;
    }
}

