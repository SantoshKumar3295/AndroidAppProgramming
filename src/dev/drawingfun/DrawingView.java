package dev.drawingfun;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
//import android.view.View.OnTouchListener;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.TypedValue;
import java.util.ArrayList;

//public class DrawingView extends View implements OnTouchListener {
public class DrawingView extends View {
    private float brushSize, lastBrushSize;
    private boolean erase = false;
    // initial color
    private int paintColor = 0xFF660000;

    private Path drawPath;
    private Paint drawPaint, canvasPaint;
    private Canvas drawCanvas;
    private Bitmap canvasBitmap;

    // add ArrayList here for undo/redo
    private ArrayList<Path> paths = new ArrayList<Path>();
    private ArrayList<Path> undonePaths = new ArrayList<Path>();
    private boolean isDrawing = false;
    private static float mx;
    private static float my;
    private static float mStartX;
    private static float mStartY;
    private static int mCurrentShape;

    public void setmCurrentShape(int x) {
        if (x >= 0 && x <= 5) {
            mCurrentShape = x;
        }
    }
    
    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //setupDrawing();
        // get drawing area setup for interaction
        drawPath = new Path();
        drawPaint = new Paint();
        // set initial color
        drawPaint.setColor(paintColor);
        // initial path property
        // set anti-alias, stroke join & cap style to make drawing look smoother
        drawPaint.setAntiAlias(true);
        drawPaint.setDither(true);  // for shapes
        //drawPaint.setStrokeWidth(20);
        drawPaint.setStrokeWidth(brushSize);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);

        brushSize = getResources().getInteger(R.integer.medium_size);
        lastBrushSize = brushSize;

        drawCanvas = new Canvas();
        //paths.add(drawPath); // to avoid the very first not working undo
        //canvasBitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_launcher); // I don't have this one
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // view given size
        super.onSizeChanged(w, h, oldw, oldh);

        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);  //1, only, doesn't work
        drawCanvas = new Canvas(canvasBitmap);                              //2
    }

    // modified here for undo/redo
    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private void touch_start(float x, float y) {
        undonePaths.clear(); //mark
        drawPath.reset();
        drawPath.moveTo(x, y);
        mX = x;
        mY = y;
    }
    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE){
            drawPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            mX = x;
            mY = y;
        }
    }
    private void touch_up() {
        drawPath.lineTo(mX, mY);
        // commit the path to our offscreen
        drawCanvas.drawPath(drawPath, drawPaint);
        // kill this so we don't double draw
        paths.add(drawPath);
        //drawPath.reset();
        drawPath = new Path();
    }
    public void onClickUndo() {
        if (paths.size() > 0){
            undonePaths.add(paths.remove(paths.size()-1));
            invalidate();
        } else {
        }
        // toaster the user
    }
    public void onClickRedo() {
        if (undonePaths.size() > 0){
            paths.add(undonePaths.remove(undonePaths.size()-1));
            invalidate();
        } else {
        }
        // toaster the user
    }

    public void setColor(int newColor) {
        invalidate();
        drawPaint.setColor(newColor);
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
        if (erase) {
            drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            //drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        } else {
            drawPaint.setXfermode(null);
        }
    }

    public void startNew() {
        // clear the canvas and updates the display
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }

    /*
    @Override
    protected void onDraw(Canvas canvas) {
        // draw view
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);  //conflicts with previous two lines //3
        canvas.drawPath(drawPath, drawPaint);                                                    //4

        // for undo/redo
        //for(Path p: paths){
        //canvas.drawPath(p, //drawPaint);
        //}
        //canvas.drawPath(drawPath, drawPaint);  // in order to draw the in-progress Path
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
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
        invalidate(); // cause onDraw method to execute, commented for undo/redo
        return true;
    }
*/
    
    //------------------------------------------------------------------------------------
    //--------- Modified Here for Shapes -------------------------------------------------
    //------------------------------------------------------------------------------------

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mx = event.getX();
        my = event.getY();
        switch (mCurrentShape) {
        case 0:
            onTouchEventRectangle(event);
            break;
        case 1:
            onTouchEventSquare(event);
            break;
        case 2:
            onTouchEventCircle(event);
            break;
        case 3:
            onTouchEventLine(event);
            break;
        case 4:
            onTouchEventSmoothLine(event);
            break;
        case 5:
            onTouchEventTriangle(event);
            break;
        default:
            onTouchEventDefault(event);
            break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(canvasBitmap, 0, 0, drawPaint);
        if (isDrawing){
            switch (mCurrentShape) {
            case 0:
                onDrawRectangle(canvas);
                break;
            case 1:
                onDrawSquare(canvas);
                break;
            case 2:
                onDrawCircle(canvas);
                break;
            case 3:
                onDrawLine(canvas);
                break;
            case 4:
                onDrawLine(canvas);
                break;
            case 5:
                onDrawTriangle(canvas);
                break;
            default:
                onDrawDefault(canvas);
                break;
            }
        }
    }

    //------------------------------------------------------------------
    // Default
    //------------------------------------------------------------------
    private boolean onTouchEventDefault(MotionEvent event) {
        switch(event.getAction()){
        case MotionEvent.ACTION_DOWN:
            drawPath.moveTo(mx, my);
            break;
        case MotionEvent.ACTION_MOVE:
            drawPath.lineTo(mx, my);
            break;
        case MotionEvent.ACTION_UP:
            drawCanvas.drawPath(drawPath, drawPaint);
            drawPath.reset();
            break;
        default:
            return false;
        }
        invalidate(); // cause onDraw method to execute, commented for undo/redo
        return true;
    }

    private void onDrawDefault(Canvas canvas) {
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);  //conflicts with previous two lines //3
        canvas.drawPath(drawPath, drawPaint);                                                    //4
    }
    
    //------------------------------------------------------------------
    // Rectangle
    //------------------------------------------------------------------
    private void onTouchEventRectangle(MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            isDrawing = true;
            mStartX = mx;
            mStartY = my;
            invalidate();
            break;
        case MotionEvent.ACTION_MOVE:
            invalidate();
            break;
        case MotionEvent.ACTION_UP:
            isDrawing = false;
            //drawRectangle(drawCanvas,canvasPaint);
            drawRectangle(drawCanvas,drawPaint);
            invalidate();
            break;
        }
    }

    private void onDrawRectangle(Canvas canvas) {
        drawRectangle(canvas,drawPaint);
    }

    private void drawRectangle(Canvas canvas,Paint paint){
        float right = mStartX > mx ? mStartX : mx;
        float left = mStartX > mx ? mx : mStartX;
        float bottom = mStartY > my ? mStartY : my;
        float top = mStartY > my ? my : mStartY;
        canvas.drawRect(left, top , right, bottom, paint);
    }

    //------------------------------------------------------------------
    // Square
    //------------------------------------------------------------------
    private void onDrawSquare(Canvas canvas) {
        onDrawRectangle(canvas);
    }
    private void onTouchEventSquare(MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            isDrawing = true;
            mStartX = mx;
            mStartY = my;
            invalidate();
            break;
        case MotionEvent.ACTION_MOVE:
            adjustSquare(mx, my);
            invalidate();
            break;
        case MotionEvent.ACTION_UP:
            isDrawing = false;
            adjustSquare(mx, my);
            //drawRectangle(drawCanvas,canvasPaint);
            drawRectangle(drawCanvas,drawPaint);
            invalidate();
            break;
        }
    }

    protected void adjustSquare(float x, float y) {
        float deltaX = Math.abs(mStartX - x);
        float deltaY = Math.abs(mStartY - y);
        float max = Math.max(deltaX, deltaY);
        mx = mStartX - x < 0 ? mStartX + max : mStartX - max;
        my = mStartY - y < 0 ? mStartY + max : mStartY - max;
    }

    //------------------------------------------------------------------
    // Circle
    //------------------------------------------------------------------

    private void onDrawCircle(Canvas canvas){
        canvas.drawCircle(mStartX, mStartY, calculateRadius(mStartX, mStartY, mx, my), drawPaint);
    }

    private void onTouchEventCircle(MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            isDrawing = true;
            mStartX = mx;
            mStartY = my;
            invalidate();
            break;
        case MotionEvent.ACTION_MOVE:
            invalidate();
            break;
        case MotionEvent.ACTION_UP:
            isDrawing = false;
            //drawCanvas.drawCircle(mStartX, mStartY, calculateRadius(mStartX,mStartY,mx,my), canvasPaint);
            drawCanvas.drawCircle(mStartX, mStartY, calculateRadius(mStartX,mStartY,mx,my), drawPaint);
            //calculateRadius(mStartX,mStartY,mx,my), canvasPaint);
            invalidate();
            break;
        }
    }

    protected float calculateRadius(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }


    //------------------------------------------------------------------
    // Line
    //------------------------------------------------------------------

    private void onDrawLine(Canvas canvas) {
        float dx = Math.abs(mx - mStartX);
        float dy = Math.abs(my - mStartY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            canvas.drawLine(mStartX, mStartY, mx, my, drawPaint);
        }
    }

    private void onTouchEventLine(MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            isDrawing = true;
            mStartX = mx;
            mStartY = my;
            invalidate();
            break;
        case MotionEvent.ACTION_MOVE:
            invalidate();
            break;
        case MotionEvent.ACTION_UP:
            isDrawing = false;
            //drawCanvas.drawLine(mStartX, mStartY, mx, my, canvasPaint);
            drawCanvas.drawLine(mStartX, mStartY, mx, my, drawPaint);
            invalidate();
            break;
        }
    }


    //------------------------------------------------------------------
    // Smooth Line
    //------------------------------------------------------------------

    private void onTouchEventSmoothLine(MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            isDrawing = true;
            mStartX = mx;
            mStartY = my;
            drawPath.reset();
            drawPath.moveTo(mx, my);
            invalidate();
            break;
        case MotionEvent.ACTION_MOVE:
            float dx = Math.abs(mx - mStartX);
            float dy = Math.abs(my - mStartY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                drawPath.quadTo(mStartX, mStartY, (mx + mStartX) / 2, (my + mStartY) / 2);
                mStartX = mx;
                mStartY = my;
            }
            drawCanvas.drawPath(drawPath, drawPaint);
            invalidate();
            break;
        case MotionEvent.ACTION_UP:
            isDrawing = false;
            drawPath.lineTo(mStartX, mStartY);
            //drawCanvas.drawPath(drawPath, canvasPaint);   // this one produces shadow
            drawCanvas.drawPath(drawPath, drawPaint);
            drawPath.reset();
            invalidate();
            break;
        }
    }


    //------------------------------------------------------------------
    // Triangle
    //------------------------------------------------------------------

    int countTouch =0;
    float basexTriangle =0;
    float baseyTriangle =0;

    private void onDrawTriangle(Canvas canvas){
        if (countTouch<3){
            canvas.drawLine(mStartX,mStartY,mx,my,drawPaint);
        }else if (countTouch==3){
            canvas.drawLine(mx,my,mStartX,mStartY,drawPaint);
            canvas.drawLine(mx,my,basexTriangle,baseyTriangle,drawPaint);
        }
    }

    private void onTouchEventTriangle(MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            countTouch++;
            if (countTouch==1){
                isDrawing = true;
                mStartX = mx;
                mStartY = my;
            } else if (countTouch==3){
                isDrawing = true;
            }
            invalidate();
            break;
        case MotionEvent.ACTION_MOVE:
            invalidate();
            break;
        case MotionEvent.ACTION_UP:
            countTouch++;
            isDrawing = false;
            if (countTouch<3){
                basexTriangle=mx;
                baseyTriangle=my;
                //drawCanvas.drawLine(mStartX,mStartY,mx,my,canvasPaint);
                drawCanvas.drawLine(mStartX,mStartY,mx,my,drawPaint);
            } else if (countTouch>=3){
                //drawCanvas.drawLine(mx,my,mStartX,mStartY,canvasPaint);
                //drawCanvas.drawLine(mx,my,basexTriangle,baseyTriangle,canvasPaint);
                drawCanvas.drawLine(mx,my,mStartX,mStartY,drawPaint);
                drawCanvas.drawLine(mx,my,basexTriangle,baseyTriangle,drawPaint);
                countTouch =0;
            }
            invalidate();
            break;
        }
    }
    
}
