package edu.fullsail.mgems.cse.pather.christopherwest.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import edu.fullsail.mgems.cse.pather.christopherwest.models.NavCell;


public class DrawSurface extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener {
    private static final int CELL_SIZE = 64;
    private Rect mScreenDim;
    private Context context;
    private int mCellCols = 0;
    private int mCellRows = 0;
    private NavCell[][] mCells;
    private NavCell mCellStart;
    private NavCell mCellEnd;


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
        mScreenDim = new Rect();
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
            mScreenDim.set(0,0, c.getWidth(), c.getHeight());
        }
        holder.unlockCanvasAndPost(c);

        loadNewMap();
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

    private void loadNewMap() {
        // calculate number of cells based on screen
        mCellCols = (int)Math.ceil((float)mScreenDim.width() / (float)CELL_SIZE);
        mCellRows = (int)Math.ceil((float)mScreenDim.height() / (float)CELL_SIZE);

        //create cells
        mCells = new NavCell[mCellRows][mCellCols];
        for(int i = 0; i < mCellRows; i++){
            for (int j = 0; j < mCellCols; j++) {
                mCells[i][j] = new NavCell();
                mCells[i][j].setBounds(i,j,CELL_SIZE);
            }
        }

        // set start cell
        mCellStart = mCells[mCellRows/4][mCellCols/2];
        mCellEnd = null;

        // Set Blockers
        int midRow = mCellRows / 2;
        for (int i = 0; i < mCellRows; i++) {
            for (int j = 0; j < mCellCols; j++) {
                if (i == midRow && i > 0 && i < mCellCols-1)
                mCells[i][j].setImpassable();
            }
        }
    }
}
