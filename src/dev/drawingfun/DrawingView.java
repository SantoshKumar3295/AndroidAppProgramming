package dev.drawingfun;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.TypedValue;

public class DrawingView extends View  {

    private Path drawPath;
    private Paint drawPaint, canvasPaint;
    private float brushSize, lastBrushSize;
    private boolean erase = false;
    
    // initial color
    private int paintColor = 0xFF660000;
    // canvas
    private Canvas drawCanvas;
    // canvas bitmap
    private Bitmap canvasBitmap;

    
    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDrawing();
    }

    private void setupDrawing() {
        // get drawing area setup for interaction
        drawPath = new Path();
        drawPaint = new Paint();
        // set initial color
        drawPaint.setColor(paintColor);
        // initial path property
        // set anti-alias, stroke join & cap style to make drawing look smoother
        drawPaint.setAntiAlias(true);
        //drawPaint.setStrokeWidth(20);
        drawPaint.setStrokeWidth(brushSize);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);

        brushSize = getResources().getInteger(R.integer.medium_size);
        lastBrushSize = brushSize;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // view given size
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // draw view
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // retrieve the x and y positions of the user touch
        float touchX = event.getX();
        float touchY = event.getY();
        // detect user touch
        switch(event.getAction()){
        case MotionEvent.ACTION_DOWN:
            drawPath.moveTo(touchX, touchY);
            break;
        case MotionEvent.ACTION_MOVE:
            drawPath.lineTo(touchX, touchY);
            break;
        case MotionEvent.ACTION_UP:
            drawCanvas.drawPath(drawPath, drawPaint);
            drawPath.reset();
            break;
        default:
            return false;
        }
        invalidate(); // cause onDraw method to execute
        return true;
    }

    public void setColor(String newColor) {
        // invalidate the view first
        invalidate();

        // parse and set color for drawing
        paintColor = Color.parseColor(newColor);
        drawPaint.setColor(paintColor);
    }

    public void setBrushSize(float newSize) {
        // update size
        float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, newSize, getResources().getDisplayMetrics());
        brushSize = pixelAmount;
        drawPaint.setStrokeWidth(brushSize);
    }

    public void setLastBrushSize(float lastSize) {
        lastBrushSize = lastSize;
    }
    public float getLastBrushSize() {
        return lastBrushSize;
    }

    public void setErase(boolean isErase) {
        // set erase true or false
        erase = isErase;
        if (erase)
            drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        else drawPaint.setXfermode(null);
    }

    public void startNew() {
        // clear the canvas and updates the display
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }
}
