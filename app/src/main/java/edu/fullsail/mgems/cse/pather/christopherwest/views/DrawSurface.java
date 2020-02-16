package edu.fullsail.mgems.cse.pather.christopherwest.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import edu.fullsail.mgems.cse.pather.christopherwest.R;
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
    private Bitmap mBMPStart;
    private Bitmap mBMPEnd;


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
        mBMPStart = BitmapFactory.decodeResource(context.getResources(), R.drawable.start);
        mBMPEnd = BitmapFactory.decodeResource(context.getResources(), R.drawable.end);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        DrawMap(canvas);
        super.onDraw(canvas);
    }

    private void DrawMap(Canvas canvas) {
        Paint fillPaint = new Paint();
        Paint strokePaint = new Paint();
        Paint blockerPaint = new Paint();
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(Color.WHITE);
        fillPaint.setStrokeWidth(3);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setColor(Color.LTGRAY);
        strokePaint.setStrokeWidth(3);
        blockerPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        blockerPaint.setColor(Color.BLACK);
        blockerPaint.setStrokeWidth(3);
        canvas.drawColor(Color.BLACK);
        for (int i = 0; i < mCellRows; i++) {
            for (int j = 0; j < mCellCols; j++) {
                NavCell currentCell = mCells[i][j];
                Rect currentCellRect = currentCell.getBounds();

                if (!currentCell.isPassable()) {
                    canvas.drawRect(currentCellRect, blockerPaint);
                } else {
                    Point center = currentCell.getCentroid();
                    int centerSquareSize = currentCell.getBounds().width() / 16;
                    Rect centerSquareRect = new Rect(center.x - centerSquareSize, center.y + centerSquareSize, center.x + centerSquareSize, center.y - centerSquareSize);

                    canvas.drawRect(currentCellRect, fillPaint);
                    canvas.drawRect(currentCellRect, strokePaint);
                    canvas.drawRect(centerSquareRect, fillPaint);
                    canvas.drawRect(centerSquareRect, strokePaint);
                }
            }
        }

        // draw starting point
        canvas.drawBitmap(
                mBMPStart,
                mCellStart.getCentroid().x - (mCellStart.getBounds().width() / 2),
                mCellStart.getCentroid().y + mCellStart.getBounds().height(),
                null);
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
                if (i == midRow && j > 0 && j < mCellCols-1)
                mCells[i][j].setImpassable();
            }
        }

        // connect cells
        for (int i = 0; i < mCellRows; i++) {
            for (int j = 0; j < mCellCols; j++) {
                if (j > 0 && mCells[i][j-1].isPassable()){mCells[i][j].setNeighbor(0, mCells[i][j-1]);}
                if (i > 0 && mCells[i-1][j].isPassable()){mCells[i][j].setNeighbor(1, mCells[i-1][j]);}
                if (j > mCellCols-1 && mCells[i][j+1].isPassable()){mCells[i][j].setNeighbor(2, mCells[i][j+1]);}
                if (i > mCellRows-1 && mCells[i+1][j].isPassable()){mCells[i][j].setNeighbor(3, mCells[i+1][j]);}
            }
        }
    }
}
