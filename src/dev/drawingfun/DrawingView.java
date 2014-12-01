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
//import android.view.View.OnTouchListener;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.TypedValue;
import java.util.ArrayList;

//public class DrawingView extends View implements OnTouchListener {
public class DrawingView extends View {
    private float brushSize, lastBrushSize;
    private boolean erase = false;
    private boolean isClear = false;
    
    private int paintColor = 0xFF660000;
    private Path drawPath;
    private myPath resPath;// = new myPath();
    private Paint drawPaint, canvasPaint;
    private Canvas drawCanvas;
    private Bitmap brightBitmap;
    private Bitmap bridgeBitmap;
    private Bitmap originalBitmap = null;
    private Bitmap canvasBitmap = null;
    private Bitmap new2Bitmap = null;
    private ArrayList<myPath> paths = new ArrayList<myPath>();
    private ArrayList<myPath> undonePaths = new ArrayList<myPath>();
    private boolean isDrawing = false;
    private static float mx;
    private static float my;
    private static float mStartX;
    private static float mStartY;
    private float touchX, touchY;
    private static int mCurrentShape;

    public void setmCurrentShape(int x) {
        if (x >= 0 && x <= 5) {
            mCurrentShape = x;
        }
    }

    public void clear() {
        isClear = true;
        new2Bitmap = originalBitmap;
        //new2Bitmap = bridgeBitmap.copy(Bitmap.Config.ARGB_8888, true);    // original 
        invalidate();
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
        if (isDrawing) {
            canvas.drawLine(mStartX, mStartY, touchX, touchY, drawPaint);
        }
        mStartX = touchX;
        mStartY = touchY;
        if(isClear){
            return new2Bitmap;
        }
        return oriBitmap;
    }
    
    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        drawPath = new Path();
        drawPaint = new Paint();
        brushSize = getResources().getInteger(R.integer.medium_size);
        lastBrushSize = brushSize;
        resPath = new myPath();    // mine
        resPath.mcolor = paintColor; // to solve the imitial not showing problem
        //resPath.myPathWrap(drawPath, drawPaint, paintColor, brushSize, false);  // this one doesn't work
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setDither(true);  // for shapes
        drawPaint.setStrokeWidth(brushSize);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);

        drawCanvas = new Canvas();
        //paths.add(drawPath); // commented to avoid the very first not working undo
        //brightBitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_launcher);
        brightBitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.rose);
        //brightBitmap = Bitmap.createScaledBitmap(brightBitmap, 550, 550, false); // don't like this huge stupid one
        //canvasBitmap = Bitmap.createBitmap(originalBitmap); // it is immutable bitmap, needs a mutable one
        bridgeBitmap = Bitmap.createBitmap(brightBitmap);
        originalBitmap = bridgeBitmap.copy(Bitmap.Config.ARGB_8888, true);    // original 
        canvasBitmap = bridgeBitmap.copy(Bitmap.Config.ARGB_8888, true);  // keep using
    }

    public void setColor(int newColor) {
        invalidate();
        paintColor = newColor;
        drawPaint.setColor(newColor);
        resPath.mcolor = newColor;
    }
    public void setBrushSize(float newSize) {
        float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, newSize, getResources().getDisplayMetrics());
        brushSize = pixelAmount;
        drawPaint.setStrokeWidth(brushSize);
        resPath.msize = brushSize;
    }
    public void setLastBrushSize(float lastSize) {
        lastBrushSize = lastSize;
    }
    public float getLastBrushSize() {
        return lastBrushSize;
    }
    public void setErase(boolean isErase) {
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

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // view given size
        super.onSizeChanged(w, h, oldw, oldh);

        //canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);  //1, only, doesn't work
        //drawCanvas = new Canvas(canvasBitmap);                              //2
    }

    //@Override
    protected void onDraw(Canvas canvas) {
        // draw view
        canvas.drawBitmap(HandWriting(canvasBitmap), 0, 0, canvasPaint);  //conflicts with previous two lines //3 not for undo/redo

        //canvas.drawPath(drawPath, drawPaint);                                                    //4
        // for undo/redo
        for(myPath p: paths){
            drawPaint.setColor(p.mcolor);
            drawPaint.setStrokeWidth(p.msize);
            canvas.drawPath(p.mpath, drawPaint);
        }
        //canvas.drawPath(resPath.mpath, drawPaint);   // leads to crash 1508
        drawPaint.setColor(paintColor);
        drawPaint.setStrokeWidth(brushSize);  
        canvas.drawPath(drawPath, drawPaint); // struggled here
    }
    //@Override
    public boolean onTouchEvent(MotionEvent event) {
         touchX = event.getX();
         touchY = event.getY();
        switch(event.getAction()) {
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
        default:
            return false;
        }
        return true;
    }

    // modified here for undo/redo
    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private void touch_start(float x, float y) {
        undonePaths.clear(); //mark
        drawPath.reset();
        // this is the part produced delay, becasu reset() set all to default;
        drawPaint.setColor(paintColor);
        drawPaint.setStrokeWidth(brushSize);
        drawPath.moveTo(x, y);
        mX = x;
        mY = y;
    }
    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE){
            //drawPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);   // may need some work here to produce smooth line lineTo()
            drawPath.lineTo(x, y);
            mX = x;
            mY = y;
        }
    }
    private void touch_up() {
        drawPath.lineTo(mX, mY);
        
        // commit the path to our offscreen
        drawCanvas.drawPath(drawPath, drawPaint);     // drawCanvas
        //drawCanvas.drawPath(resPath.mpath, drawPaint);
        
        // kill this so we don't double draw
        paths.add(resPath.myPathWrap(drawPath, drawPaint, resPath.mcolor, resPath.msize, false)); // color NOT work
        
        drawPath = new Path();
        resPath.myPathWrap(drawPath, drawPaint, resPath.mcolor, resPath.msize, false);
    }
    public void onClickUndo() {
        if (paths.size() > 0){
            undonePaths.add(paths.remove(paths.size()-1));
            invalidate();
        } else {
        }
    }
    public void onClickRedo() {
        if (undonePaths.size() > 0){
            paths.add(undonePaths.remove(undonePaths.size()-1));
            invalidate();
        } else {
        }
    }

    
    //------------------------------------------------------------------------------------
    //--------- Modified Here for Shapes -------------------------------------------------
    //------------------------------------------------------------------------------------
    /*    @Override   
          protected void onDraw(Canvas canvas) { // want to share this method
          for(myPath p: paths){
          drawPaint.setColor(p.mcolor);
          drawPaint.setStrokeWidth(p.msize);
          canvas.drawPath(p.mpath, drawPaint);
          }
          canvas.drawPath(resPath.mpath, drawPaint);  //canvas.drawPath(drawPath, drawPaint); // struggled here // no difference
          }
          //@Override
          public boolean onTouchEvent(MotionEvent event) {
          float touchX = event.getX();
          float touchY = event.getY();
          switch(event.getAction()) {
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
          default:
          return false;
          }
          return true;
          }
    */
    /*    @Override
          public boolean onTouchEvent(MotionEvent event) {
          mx = event.getX();
          my = event.getY();
          switch (mCurrentShape) {

          case 0:
          onTouchEventRectangle(event);     // special case
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
          //canvas.drawBitmap(canvasBitmap, 0, 0, drawPaint);  // probably don't need this
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
          }  */
    //------------------------------------------------------------------
    // Default
    //------------------------------------------------------------------
    // modify for undo/redo, these are not for undo/redo
    private boolean onTouchEventDefault(MotionEvent event) {
        switch(event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            touch_start(mx, my);
            invalidate();
            break;
        case MotionEvent.ACTION_MOVE:
            touch_move(mx, my);
            invalidate();
            break;
        case MotionEvent.ACTION_UP:
            touch_up();
            invalidate();
            break;
        default:
            return false;
        }
        return true;
    }
    private void onDrawDefault(Canvas canvas) {
        for(myPath p: paths){
            drawPaint.setColor(p.mcolor);
            drawPaint.setStrokeWidth(p.msize);
            canvas.drawPath(p.mpath, drawPaint);
        }
        canvas.drawPath(resPath.mpath, drawPaint);  //canvas.drawPath(drawPath, drawPaint); // struggled here // no difference
        //canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);  //conflicts with previous two lines //3
        //canvas.drawPath(drawPath, drawPaint);                                                    //4
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
            drawRectangle(drawCanvas,drawPaint);
            paths.add(resPath.myPathWrap(drawPath, drawPaint, resPath.mcolor, brushSize, false));
            drawPath = new Path();
            resPath.myPathWrap(drawPath, drawPaint, resPath.mcolor, brushSize, false);
            break;
        }
    }
    private void onDrawRectangle(Canvas canvas) {
        for(myPath p: paths){
            drawPaint.setColor(p.mcolor);
            drawPaint.setStrokeWidth(p.msize);
            //canvas.drawPath(p.mpath, drawPaint);
            drawRectangle(canvas,drawPaint); // modify to skip this one
        }
        //canvas.drawPath(resPath.mpath, drawPaint); 
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
            drawCanvas.drawCircle(mStartX, mStartY, calculateRadius(mStartX,mStartY,mx,my), drawPaint);
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
                drawCanvas.drawLine(mStartX,mStartY,mx,my,drawPaint);
            } else if (countTouch>=3){
                drawCanvas.drawLine(mx,my,mStartX,mStartY,drawPaint);
                drawCanvas.drawLine(mx,my,basexTriangle,baseyTriangle,drawPaint);
                countTouch =0;
            }
            invalidate();
            break;
        }
    }
    
}
