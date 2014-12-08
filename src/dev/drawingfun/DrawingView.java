package dev.drawingfun;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.TypedValue;
import java.util.ArrayList;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.graphics.Point;

public class DrawingView extends View {
    private float brushSize, lastBrushSize;
    private Path drawPath;
    private Paint drawPaint, canvasPaint;
    private Canvas drawCanvas;
    private Bitmap brightBitmap;
    private Bitmap bridgeBitmap;
    private Bitmap originalBitmap = null;
    private Bitmap canvasBitmap = null;
    private Bitmap new2Bitmap = null;
    private ArrayList<DrawElement> paths = new ArrayList<DrawElement>();
    private ArrayList<DrawElement> undonePaths = new ArrayList<DrawElement>();
    private myPath resPath = null;
    private myLine resLine = null;
    private myRect resRect = null;
    private mySqar resSqar = null;
    private myCirc resCirc = null;
    private myTria resTria = null;
    private boolean erase = false;
    private boolean isClear = false;
    private boolean isDrawing = false;
    private int paintColor = Color.BLUE;
    private int lastColor = Color.BLUE;
    private static final float TOUCH_TOLERANCE = 4;
    private static int mCurrentShape;
    private static float mx;
    private static float my;
    private static float mStartX;
    private static float mStartY;
    ProgressDialog pd;
    final Point p1 = new Point();

    public void setmCurrentShape(int x) {
        if (x >= 0 && x <= 6) {  // 6 for FloodFill
            mCurrentShape = x;
        } else {
            mCurrentShape = 4;
        }
    }
    public void setErase(boolean isErase) {
        erase = isErase;
        if (erase) {
            //drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR)); // change to setColor method
            lastColor = paintColor;
            paintColor = Color.WHITE;
        } else {
            //drawPaint.setXfermode(null);
            paintColor = lastColor;
        }
    }

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        drawPath = new Path();
        drawPaint = new Paint();
        resPath = new myPath();   
        resLine = new myLine();
        resRect = new myRect();
        resSqar = new mySqar();
        resCirc = new myCirc();
        resTria = new myTria();
        pd = new ProgressDialog(context);
        brushSize = getResources().getInteger(R.integer.medium_size);
        /*
        resPath.mColor = paintColor;
        resLine.mColor = paintColor;
        resRect.mColor = paintColor;
        resSqar.mColor = paintColor;
        resCirc.mColor = paintColor;
        resTria.mColor = paintColor;
        */
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setDither(true);  
        drawPaint.setStrokeWidth(brushSize);
        /*
        resPath.mSize = brushSize;
        resLine.mSize = brushSize;
        resRect.mSize = brushSize;
        resSqar.mSize = brushSize;
        resCirc.mSize = brushSize;
        resTria.mSize = brushSize;
        */
        lastBrushSize = brushSize;
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);
        drawCanvas = new Canvas();
        
        //brightBitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.rose);   // chage to sth that can floodfill
        brightBitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.ssaved);   // sun flower house

        //brightBitmap = Bitmap.createScaledBitmap(brightBitmap, 550, 550, false); // don't like this huge stupid one
        //canvasBitmap = Bitmap.createBitmap(originalBitmap); // it is immutable bitmap, needs a mutable one
        bridgeBitmap = Bitmap.createBitmap(brightBitmap);
        originalBitmap = bridgeBitmap.copy(Bitmap.Config.ARGB_8888, true);  
        canvasBitmap = bridgeBitmap.copy(Bitmap.Config.ARGB_8888, true);  // keep using
    }

    // this function has problems when size change, like device vertical to horizontal
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh); 
        canvasBitmap = HandWriting(canvasBitmap);
        drawCanvas = new Canvas(canvasBitmap);                              
        
        //canvasBitmap = canvasBitmap.copy(Bitmap.Config.ARGB_8888, true);  
        //drawCanvas = new Canvas(canvasBitmap);                              //2
    }

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
        case 6:
            onTouchEventFloodFill(event);
            break;
        default:
            onTouchEventSmoothLine(event);
        }
        return true;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);        //added here today
        canvas.drawBitmap(HandWriting(canvasBitmap), 0, 0, drawPaint);  // Actually I didn't do anything on Bitmap yet, except Yello Rose

        for(DrawElement p: paths){
            if (p.getId() == 0) {
                drawPaint.setColor(((myRect)p).mColor);
                drawPaint.setStrokeWidth(((myRect)p).mSize);
                canvas.drawRect(((myRect)p).mStartX, ((myRect)p).mStartY, ((myRect)p).mEndX, ((myRect)p).mEndY, drawPaint);
            } else if (p.getId() == 1) {
                drawPaint.setColor(((mySqar)p).mColor);
                drawPaint.setStrokeWidth(((mySqar)p).mSize);
                canvas.drawRect(((mySqar)p).mStartX, ((mySqar)p).mStartY, ((mySqar)p).mEndX, ((mySqar)p).mEndY, drawPaint);
            } else if (p.getId() == 2) {
                drawPaint.setColor(((myCirc)p).mColor);
                drawPaint.setStrokeWidth(((myCirc)p).mSize);
                canvas.drawCircle(((myCirc)p).mStartX, ((myCirc)p).mStartY, ((myCirc)p).mEnd, drawPaint);
            } else if (p.getId() == 3) {
                drawPaint.setColor(((myLine)p).mColor);
                drawPaint.setStrokeWidth(((myLine)p).mSize);
                canvas.drawLine(((myLine)p).mStartX, ((myLine)p).mStartY, ((myLine)p).mEndX, ((myLine)p).mEndY, drawPaint);
            } else if (p.getId() == 4) {
                drawPaint.setColor(((myPath)p).mColor);
                drawPaint.setStrokeWidth(((myPath)p).mSize);
                canvas.drawPath(((myPath)p).mPath, drawPaint);
            } else if (p.getId() == 5) {
                drawPaint.setColor(((myTria)p).mColor);
                drawPaint.setStrokeWidth(((myTria)p).mSize);
                canvas.drawLine(((myTria)p).mStartX, ((myTria)p).mStartY, ((myTria)p).mEndX, ((myTria)p).mEndY, drawPaint);
                canvas.drawLine(((myTria)p).mMidX, ((myTria)p).mMidY, ((myTria)p).mEndX, ((myTria)p).mEndY, drawPaint);
            } 
        }
        drawPaint.setColor(paintColor);
        drawPaint.setStrokeWidth(brushSize);
        
        //if (isDrawing) { // i removed this one
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
                onDrawSmoothLine(canvas);
                break;
            case 5:
                onDrawTriangle(canvas);
                break; 
            default: // for FloodFill
                //onDrawSmoothLine(canvas);
                break;
            }
            //}
    }

    //------------------------------------------------------------------
    // FloodFill
    //------------------------------------------------------------------
    private void onTouchEventFloodFill(MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            p1.x = (int) mx;
            p1.y = (int) my;
            final int sourceColor = HandWriting(canvasBitmap).getPixel((int)mx, (int)my);
            final int targetColor = paintColor;
            QueueLinearFloodFiller ff = new QueueLinearFloodFiller(HandWriting(canvasBitmap), sourceColor, targetColor);
            ff.setTolerance(1);
            ff.floodFill(p1.x, p1.y);
            //new TheTask(HandWriting(canvasBitmap), p1, sourceColor, targetColor).execute();
            invalidate();
        }
    }

    //------------------------------------------------------------------
    // Triangle
    //------------------------------------------------------------------
    int countTouch =0;
    float basexTriangle =0;
    float baseyTriangle =0;

    private void onDrawTriangle(Canvas canvas){
        if (isDrawing) {
            if (countTouch<3){
                canvas.drawLine(mStartX,mStartY,mx,my,drawPaint);
            } else if (countTouch == 3){
                canvas.drawLine(mx, my, mStartX, mStartY, drawPaint);
                canvas.drawLine(mx, my, basexTriangle, baseyTriangle, drawPaint);
            }
        }
    }
    private void onTouchEventTriangle(MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            countTouch++;
            if (countTouch == 1){
                isDrawing = true;
                drawPaint.setColor(paintColor);
                drawPaint.setStrokeWidth(brushSize);
                drawPath.moveTo(mx, my);
                mStartX = mx;
                mStartY = my;
            } else if (countTouch == 3){
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
            if (countTouch < 3){
                basexTriangle = mx;
                baseyTriangle = my;
                paths.add(resLine.myLineWrap(mStartX, mStartY, mx, my, drawPaint, paintColor, brushSize));
                resLine.setPoints(0, 0, 0, 0);
            } else if (countTouch >= 3){
                paths.add(resTria.myTriaWrap(mStartX, mStartY, basexTriangle, baseyTriangle, mx, my, drawPaint, paintColor, brushSize));
                resTria.setPoints(0, 0, 0, 0, 0, 0);
                countTouch = 0;
            }
            invalidate();
            break;
        }
    }
    public void onClickUndo() {
        if (paths.size() > 0){
            DrawElement p = paths.get(paths.size() - 1);
            if (p.getId() == 5) {
                undonePaths.add(paths.remove(paths.size()-1));
                undonePaths.add(paths.remove(paths.size()-1));
            } else {
                undonePaths.add(paths.remove(paths.size()-1));
            }
            invalidate();
        } else {
        }
    }
    public void onClickRedo() {
        if (undonePaths.size() > 0) {
            paths.add(undonePaths.remove(undonePaths.size()-1));
            if (undonePaths.size() > 0) {
                DrawElement p = undonePaths.get(undonePaths.size() - 1);
                if (p.getId() == 5) {
                    paths.add(undonePaths.remove(undonePaths.size()-1));
                } else {
                }
            }
            invalidate();
        } else {
        }
    }

    //------------------------------------------------------------------
    // Line
    //------------------------------------------------------------------
    private void onDrawLine(Canvas canvas) {
        if (isDrawing) {
            drawLineSpecial(canvas);
        }
    }
    private void drawLineSpecial(Canvas canvas) {
        float dx = Math.abs(mx - mStartX);
        float dy = Math.abs(my - mStartY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            canvas.drawLine(mStartX, mStartY, mx, my, drawPaint);
        }
    }
    private void onTouchEventLine(MotionEvent event) {
        mx = event.getX();
        my = event.getY();
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            undonePaths.clear();
            isDrawing = true;
            drawPaint.setColor(paintColor);
            drawPaint.setStrokeWidth(brushSize);
            drawPath.moveTo(mx, my);
            mStartX = mx;
            mStartY = my;
            resLine.setStart(mStartX, mStartY);
            invalidate();
            break;
        case MotionEvent.ACTION_MOVE:
            invalidate();
            break;
        case MotionEvent.ACTION_UP:
            isDrawing = false;
            paths.add(resLine.myLineWrap(resLine.mStartX, resLine.mStartY, mx, my, drawPaint, paintColor, brushSize));
            resLine.setPoints(0, 0, 0, 0);
            invalidate();
            break;
        }
    }

    //------------------------------------------------------------------
    // Circle
    //------------------------------------------------------------------
    private void onDrawCircle(Canvas canvas){
        if (isDrawing) {
            canvas.drawCircle(mStartX, mStartY, calculateRadius(mStartX, mStartY, mx, my), drawPaint);
        }
    }
    private void onTouchEventCircle(MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            isDrawing = true;
            drawPaint.setColor(paintColor);
            drawPaint.setStrokeWidth(brushSize);
            drawPath.moveTo(mx, my);
            mStartX = mx;
            mStartY = my;
            resCirc.setStart(mStartX, mStartY);
            invalidate();
            break;
        case MotionEvent.ACTION_MOVE:
            invalidate();
            break;
        case MotionEvent.ACTION_UP:
            isDrawing = false;
            paths.add(resCirc.myCircWrap(resCirc.mStartX, resCirc.mStartY, calculateRadius(mStartX,mStartY,mx,my), drawPaint, paintColor, brushSize));
            resCirc.setPoints(0, 0, 0);
            invalidate();
            break;
        }
    }
    protected float calculateRadius(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    //------------------------------------------------------------------
    // Square
    //------------------------------------------------------------------
    private void onDrawSquare(Canvas canvas) {
        if (isDrawing) {
            drawRectangle(canvas,drawPaint);
        }
    }
    private void onTouchEventSquare(MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            isDrawing = true;
            drawPaint.setColor(paintColor);
            drawPaint.setStrokeWidth(brushSize);
            drawPath.moveTo(mx, my);
            mStartX = mx;
            mStartY = my;
            resSqar.setStart(mStartX, mStartY);
            invalidate();
            break;
        case MotionEvent.ACTION_MOVE:
            adjustSquare(mx, my);
            invalidate();
            break;
        case MotionEvent.ACTION_UP:
            isDrawing = false;
            adjustSquare(mx, my);
            paths.add(resSqar.mySqarWrap(resSqar.mStartX, resSqar.mStartY, mx, my, drawPaint, paintColor, brushSize));
            resSqar.setPoints(0, 0, 0, 0);
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
    // Rectangle
    //------------------------------------------------------------------
    private void onTouchEventRectangle(MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            isDrawing = true;
            drawPaint.setColor(paintColor);
            drawPaint.setStrokeWidth(brushSize);
            drawPath.moveTo(mx, my);
            mStartX = mx;
            mStartY = my;
            resRect.setStart(mStartX, mStartY);
            invalidate();
            break;
        case MotionEvent.ACTION_MOVE:
            invalidate();
            break;
        case MotionEvent.ACTION_UP:
            isDrawing = false;
            paths.add(resRect.myRectWrap(resRect.mStartX, resRect.mStartY, mx, my, drawPaint, paintColor, brushSize));
            resRect.setPoints(0, 0, 0, 0);
            
            invalidate();
            break;
        }
    }
    private void onDrawRectangle(Canvas canvas) {
        if (isDrawing) {
            drawRectangle(canvas,drawPaint);
        }
    }
    private void drawRectangle(Canvas canvas, Paint paint){
        float right = mStartX > mx ? mStartX : mx;
        float left = mStartX > mx ? mx : mStartX;
        float bottom = mStartY > my ? mStartY : my;
        float top = mStartY > my ? my : mStartY;
        canvas.drawRect(left, top , right, bottom, paint);
    }

    public Bitmap HandWriting(Bitmap oriBitmap) {    
        Canvas canvas = null;
        if (isClear) {
            canvas = new Canvas(new2Bitmap);
        } else {        
            canvas = new Canvas(oriBitmap);
        }
        drawPaint = new Paint();
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setAntiAlias(true);
        drawPaint.setColor(paintColor);
        drawPaint.setStrokeWidth(brushSize);
        /*
        // didn't do anything on bitmap yet
        if (isDrawing) {
            if (mCurrentShape == 3) {
                float dx = Math.abs(mx - resLine.mStartX);
                float dy = Math.abs(my - resLine.mStartY);
                if (dx >= 1000*TOUCH_TOLERANCE || dy >= 1000*TOUCH_TOLERANCE) {
                    canvas.drawLine(resLine.mStartX, resLine.mStartY, mx, my, drawPaint);  // didn't really function
                }
            } else {
                canvas.drawLine(mStartX, mStartY, mx, my, drawPaint);
            }
            canvas.drawLine(mStartX, mStartY, mx, my, drawPaint);
        }
        */
        /*
        if (mCurrentShape != 3){
            mStartX = mx;
            mStartY = my;
        }
        */
        if(isClear){
            return new2Bitmap;
        }
        return oriBitmap;
    }

    //------------------------------------------------------------------
    // Smooth Line
    //------------------------------------------------------------------
    private void onTouchEventSmoothLine(MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            isDrawing = true;
            touch_start(mx, my);
            invalidate();
            break;
        case MotionEvent.ACTION_MOVE:
            touch_move(mx, my);
            invalidate();
            break;
        case MotionEvent.ACTION_UP:
            isDrawing = false;
            touch_up();
            invalidate();
            break;
        }
    }
    private void onDrawSmoothLine(Canvas canvas) {
        canvas.drawPath(drawPath, drawPaint); 
    }
    private void touch_start(float x, float y) {
        undonePaths.clear();
        drawPath.reset();
        drawPaint.setColor(paintColor);
        drawPaint.setStrokeWidth(brushSize);
        drawPath.moveTo(x, y);
        mStartX = x;
        mStartY = y;
    }
    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mStartX);
        float dy = Math.abs(y - mStartY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE){
            drawPath.quadTo(mStartX, mStartY, (x + mStartX)/2, (y + mStartY)/2); 
            mStartX = x;
            mStartY = y;
        }
    }
    private void touch_up() {
        drawPath.lineTo(mStartX, mStartY);
        drawCanvas.drawPath(drawPath, drawPaint);
        paths.add(resPath.myPathWrap(drawPath, drawPaint, paintColor, brushSize)); 
        drawPath = new Path();
    }

    
    public void setColor(int newColor) {
        invalidate();
        paintColor = newColor;
        drawPaint.setColor(newColor);
    }
    public void setBrushSize(float newSize) {
        float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, newSize, getResources().getDisplayMetrics());
        brushSize = pixelAmount;
    }

    public void clear() {
        isClear = true;
        new2Bitmap = bridgeBitmap.copy(Bitmap.Config.ARGB_8888, true); 
        paths = new ArrayList<DrawElement>();
        undonePaths = new ArrayList<DrawElement>();
        invalidate();
    }

    public void startNew() {
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }

    public void setLastBrushSize(float lastSize) {
        lastBrushSize = lastSize;
    }
    public float getLastBrushSize() {
        return lastBrushSize;
    }
    
    /*
    class TheTask extends AsyncTask<Void, Integer, Void> {
        Bitmap bmp;
        Point pt;
        int replacementColor, targetColor;
        public TheTask(Bitmap bm, Point p, int sc, int tc) {
            this.bmp = bm;
            this.pt = p;
            this.replacementColor = tc;
            this.targetColor = sc;
            pd.setMessage("Filling....");
            pd.show();
        }
        @Override
            protected void onPreExecute() {
            pd.show();
        }
        @Override
            protected void onProgressUpdate(Integer... values) {
        }
        @Override
            protected Void doInBackground(Void... params) {
            FloodFill f = new FloodFill();
            f.floodFill(bmp, pt, targetColor, replacementColor);
            return null;
        }
        @Override
            protected void onPostExecute(Void result) {
            pd.dismiss();
            invalidate();
        }
    }
*/    
}
