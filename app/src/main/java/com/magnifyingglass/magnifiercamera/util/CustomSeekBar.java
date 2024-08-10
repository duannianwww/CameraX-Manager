package com.magnifyingglass.magnifiercamera.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.magnifyingglass.magnifiercamera.MagnifierApplication;


/**
 * Customize the drag seekBar
 */
public class CustomSeekBar extends View {
    private static final String TAG = "CustomSeekBar";
    private int radius = 40;
    private int thumbSize = 215;
    private int voiceHeight;
    private int voiceWidth;

    private int backgroundLineSize = 40;//Background line width
    private int foregroundLineSize = 40;//Width of progress bar

    private int lineSize;//The length of the entire background line

    private float touchY;
    private Bitmap thumbBitmap;//Drag wheel pictures
    private Bitmap voiceBitmap;//The volume picture in the middle

    private Paint paint;
    private Paint circlePaint;//Paint the progress bar

    private RectF backgroundLineRect = new RectF();//Background rectangle
    private RectF foregroundLineRect = new RectF();//Progress rectangle

    private RectF ovalRectF;//Circumscribed square of a circle

    private Matrix matrix  = new Matrix();//Rotate the bitmap

    private float currentDegrees = 0;//Current progress, in percentage, without the percent sign

    private OnProgressListener onProgressListener;

    public void setOnProgressListener(OnProgressListener onProgressListener) {
        this.onProgressListener = onProgressListener;
    }

    public CustomSeekBar(Context context) {
        this(context,null);
    }

    public CustomSeekBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CustomSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initRect();
        initPaint();
    }
    /**
     * init paint
     */
    private void initPaint() {
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(10);

        circlePaint = new Paint();
        circlePaint.setAntiAlias(true); // Anti-Aliasing
        circlePaint.setDither(true); // Stabilization
        circlePaint.setStrokeWidth(2);//Line Width
        circlePaint.setShader(null); // Clear the last shader
        circlePaint.setStyle(Paint.Style.STROKE); // Set the drawn circle to be hollow
        circlePaint.setShadowLayer(10, 10, 10, Color.RED);
        circlePaint.setColor(Color.WHITE); // Set the color of the arc
        circlePaint.setStrokeCap(Paint.Cap.ROUND); // Change each arc into a rounded one
    }

    private void initRect(){
        ovalRectF  = new RectF(thumbSize/2-radius, thumbSize/2-radius,
                thumbSize/2+radius, thumbSize/2+radius); //
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawLine(canvas);
//        drawThumb(canvas);
//        drawCircleProgress(canvas);
    }
    public float dpToPx( float dp) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                MagnifierApplication.mContext.getResources().getDisplayMetrics()
        );
    }

    /**
     * draw lines
     * @param canvas
     */
    private void drawLine(Canvas canvas){
        //Draw background lines
        backgroundLineRect.set((getWidth()-dpToPx(backgroundLineSize))/2,0,
                (getWidth()+dpToPx(backgroundLineSize))/2, getParentHeight());
        paint.setColor(Color.parseColor("#4D000000"));
        canvas.drawRoundRect(backgroundLineRect, dpToPx(22.5f), dpToPx(22.5f), paint);

        // Set the graphics blending mode
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        // Creating a rounded corner mask
        Path maskPath = new Path();
        maskPath.addRoundRect(backgroundLineRect, dpToPx(22.5f), dpToPx(22.5f), Path.Direction.CW);

        // Save canvas state
        canvas.save();

        // Crop the canvas so that the progress line is only visible in the rounded corner area
        canvas.clipPath(maskPath);

        // Draw a progress line
        paint.setColor(Color.parseColor("#FF866E"));
        foregroundLineRect.set((getWidth()-dpToPx(foregroundLineSize))/2,(getParentHeight())*(100-currentDegrees)/100,
                (getWidth()+dpToPx(foregroundLineSize))/2,getParentHeight());
        canvas.drawRoundRect(foregroundLineRect,0,0,paint);

        // Restore canvas state
        canvas.restore();

        // Clear Blend Mode
        paint.setXfermode(null);
    }

    private int getParentHeight(){
        return getHeight();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled())
            return true;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchY = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                Log.e("test","test:"+currentDegrees);
                currentDegrees += (touchY-event.getRawY())*1f/(getParentHeight())*100.0f;
                if (currentDegrees > 100){
                    currentDegrees = 100;
                }
                if (currentDegrees<0){
                    currentDegrees = 0;
                }

                if (onProgressListener != null){
                    onProgressListener.onProgressNumber(currentDegrees);
                }
                touchY = event.getRawY();
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            case MotionEvent.ACTION_UP:

                break;
        }
        return true;
    }


    /**
     * Send progress
     */
    private void sendProgress(){
        if (onProgressListener != null){
            onProgressListener.onProgress(currentDegrees);
        }
    }

    public void setCurrentDegrees(float currentDegrees){
        this.currentDegrees = currentDegrees;
        invalidate();
    }


    public interface OnProgressListener{
        void onProgress(float progress);
        void onProgressNumber(float progress);
    }

}
