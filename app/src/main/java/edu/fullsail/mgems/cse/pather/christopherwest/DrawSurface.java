package edu.fullsail.mgems.cse.pather.christopherwest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public class DrawSurface extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener {
    private Context context;


    public DrawSurface(Context context) {
        super(context);
        initialize(context);
    }

    public DrawSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public DrawSurface(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context) {
        this.context = context;
        setWillNotDraw(false);
        getHolder().addCallback(this);
        setOnTouchListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Canvas c = holder.lockCanvas();
        if (c != null) {
            // mFieldDim.set(0,0, c.getWidth(), c.getHeight());
        }
        holder.unlockCanvasAndPost(c);
        invalidate();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        PointF touchCoords = new PointF(event.getX(),event.getY());
        invalidate();
        return super.onTouchEvent(event);
    }
}
