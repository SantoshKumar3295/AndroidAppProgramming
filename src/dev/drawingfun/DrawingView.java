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
    /*
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

        drawCanvas = new Canvas();
        paths.add(drawPath);
        canvasBitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_launcher);
    }
    */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // view given size
        super.onSizeChanged(w, h, oldw, oldh);

        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);  //1, only, doesn't work
        drawCanvas = new Canvas(canvasBitmap);                              //2
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // draw view
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);  //conflicts with previous two lines //3
        canvas.drawPath(drawPath, drawPaint);                                                    //4
        /*
        // for undo/redo
        for(Path p: paths){
            canvas.drawPath(p, drawPaint);
        }
        canvas.drawPath(drawPath, drawPaint);  // in order to draw the in-progress Path
        */
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // retrieve the x and y positions of the user touch
        float touchX = event.getX();
        float touchY = event.getY();

        // detect user touch
        switch(event.getAction()){
            /*
        case MotionEvent.ACTION_DOWN:
            touch_start(touchX, touchY);
            invalidate();
            break;
        case MotionEvent.ACTION_MOVE:
            touch_move(touchX, touchY);
            invalidate();
            break;
        case MotionEvent.ACTION_UP:
            touch_up();
            invalidate();
            break;
            */
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
    /*
    public void setColor(String newColor) {
        // invalidate the view first
        invalidate();

        // parse and set color for drawing
        paintColor = Color.parseColor(newColor);
        drawPaint.setColor(paintColor);
    }
    */
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
        if (erase)
            drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            //drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        else
            drawPaint.setXfermode(null);
    }

    public void startNew() {
        // clear the canvas and updates the display
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }
}
