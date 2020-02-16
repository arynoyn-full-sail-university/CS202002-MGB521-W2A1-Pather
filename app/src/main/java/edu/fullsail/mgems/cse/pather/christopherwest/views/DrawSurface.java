package edu.fullsail.mgems.cse.pather.christopherwest.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.preference.Preference;

import java.util.HashSet;
import java.util.PriorityQueue;

import edu.fullsail.mgems.cse.pather.christopherwest.R;
import edu.fullsail.mgems.cse.pather.christopherwest.models.NavCell;

import static android.content.ContentValues.TAG;


public class DrawSurface extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener, SharedPreferences.OnSharedPreferenceChangeListener {
    private static final int CELL_SIZE = 64;
    private Rect mScreenDim;
    private Context mContext;
    private int mCellCols = 0;
    private int mCellRows = 0;
    private NavCell[][] mCells;
    private NavCell mCellStart;
    private NavCell mCellEnd;
    private Bitmap mBMPStart;
    private Bitmap mBMPEnd;
    HashSet<NavCell> mVisitedCells;
    private boolean mDrawVisitedCells;
    private boolean mAddExtraBlockers;
    private SharedPreferences.OnSharedPreferenceChangeListener listner;
    private boolean mNoSolution;
    private float mPercentageBlocked;


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
        setWillNotDraw(false);
        getHolder().addCallback(this);
        setOnTouchListener(this);
        mContext = context;
        mScreenDim = new Rect();
        mBMPStart = BitmapFactory.decodeResource(context.getResources(), R.drawable.start);
        mBMPEnd = BitmapFactory.decodeResource(context.getResources(), R.drawable.end);
        mVisitedCells = new HashSet<>();
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(mContext);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        mDrawVisitedCells = sharedPreferences.getBoolean("display_visited_cells", false);
        mAddExtraBlockers = sharedPreferences.getBoolean("add_extra_blockers", false);
        try {
            mPercentageBlocked = getBlockerPercentChance(Float.parseFloat(sharedPreferences.getString("blocker_percentage", "10")));
        } catch (Exception e){
            Log.e(TAG, "initialize: error converting blocker percentage", e );
            mPercentageBlocked = getBlockerPercentChance(10);
        }

        mNoSolution = false;
    }

    private float getBlockerPercentChance(float stored_percent) {
        return Math.abs((stored_percent / 100) - 1.0f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        DrawMap(canvas);
        super.onDraw(canvas);
    }

    private void DrawMap(Canvas canvas) {
        canvas.drawColor(Color.BLACK);

        // Draw initial grid
        for (int i = 0; i < mCellRows; i++) {
            for (int j = 0; j < mCellCols; j++) {
                NavCell currentCell = mCells[i][j];

                if (!currentCell.isPassable()) {
                    DrawBlockingCell(canvas, currentCell);
                } else {
                    DrawNonBlockingCell(canvas, currentCell, Color.WHITE, Color.LTGRAY);
                }
            }
        }

        // draw all visited cells
        if (mDrawVisitedCells) {
            if (!mVisitedCells.isEmpty()) {
                for (NavCell cell : mVisitedCells) {
                    DrawNonBlockingCell(canvas, cell, Color.RED, Color.GRAY);
                }
            }
        }

        // draw optimal path
        if (!mNoSolution) {
            if (mCellEnd != null) {
                NavCell current = mCellEnd;
                do {
                    DrawNonBlockingCell(canvas, current, Color.GREEN, Color.LTGRAY);
                    current = current.getPrevious();
                } while (current.getPrevious() != null);
                DrawNonBlockingCell(canvas, current, Color.GREEN, Color.LTGRAY);
            }
        } else {
            //throw error
            AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
            dialog.setTitle("No Solution");
            dialog.setMessage("There is no path between the start cell and the end cell.");
            dialog.setPositiveButton(" OK ", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }

        // draw starting point
        DrawCellMarker(canvas, mCellStart, mBMPStart);

        // draw end point
        DrawCellMarker(canvas, mCellEnd, mBMPEnd);
    }

    private void DrawCellMarker(Canvas canvas, NavCell cell, Bitmap marker ){
        if(cell != null && cell.getBounds() != null) {
            canvas.drawBitmap(
                    marker,
                    cell.getCentroid().x - (marker.getWidth() / 2),
                    cell.getCentroid().y - marker.getHeight(),
                    null);
        }
    }

    private void DrawBlockingCell(Canvas canvas, NavCell cell ){
        Rect currentCellRect = cell.getBounds();

        Paint blockerPaint = new Paint();
        blockerPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        blockerPaint.setColor(Color.BLACK);
        blockerPaint.setStrokeWidth(3);

        canvas.drawRect(currentCellRect, blockerPaint);
    }
    private void DrawNonBlockingCell(Canvas canvas, NavCell cell, int fillColor, int strokeColor ){
        Rect currentCellRect = cell.getBounds();

        Paint fillPaint = new Paint();
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(fillColor);
        fillPaint.setStrokeWidth(3);

        Paint strokePaint = new Paint();
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setColor(strokeColor);
        strokePaint.setStrokeWidth(3);


        Point center = cell.getCentroid();
        int centerSquareSize = cell.getBounds().width() / 16;
        Rect centerSquareRect = new Rect(center.x - centerSquareSize, center.y + centerSquareSize, center.x + centerSquareSize, center.y - centerSquareSize);

        canvas.drawRect(currentCellRect, fillPaint);
        canvas.drawRect(currentCellRect, strokePaint);
        canvas.drawRect(centerSquareRect, fillPaint);
        canvas.drawRect(centerSquareRect, strokePaint);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Canvas c = holder.lockCanvas();
        if (c != null) {
            mScreenDim.set(0,0, c.getWidth(), c.getHeight());
        }
        holder.unlockCanvasAndPost(c);

        loadNewMap();
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
                NavCell clickedCell = mCells[(int) touchCoords.y / CELL_SIZE][(int) touchCoords.x / CELL_SIZE]; //[rows][columns] = [verticalOffset (y-axis)][horizontalOffset (x-axis)]
        if (!clickedCell.isPassable()) {
            //throw error
            AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
            dialog.setTitle("Invalid Selection");
            dialog.setMessage("You cannot choose a blocker cell for the start or end cell");
            dialog.setPositiveButton(" OK ", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        } else {

            if (mCellEnd == null) {
                mCellEnd = clickedCell;
                calculateAStar(mCellStart, mCellEnd);
            } else {
                resetGrid();
                mCellStart = clickedCell;
                mCellEnd = null;
            }

            invalidate();
        }

        return super.onTouchEvent(event);
    }

    private void resetGrid() {
        for (int i = 0; i < mCellRows; i++) {
            for (int j = 0; j < mCellCols; j++) {
                mCells[i][j].reset();
            }
        }
        mVisitedCells = new HashSet<>();
        mNoSolution = false;
    }

    private void calculateAStar(NavCell mCellStart, NavCell mCellEnd) {
        PriorityQueue<NavCell> openSet = new PriorityQueue<>(NavCell.NavCellByFinalCostCompartator);
        mVisitedCells = new HashSet<>();
        mCellStart.update(0, aStarHeuristic(mCellStart, mCellEnd), null);
        openSet.add(mCellStart);
        while(!openSet.isEmpty()){
            NavCell currentCell = openSet.peek();
            if (currentCell == mCellEnd){
                return; // reconstruct path
            } else {
                currentCell = openSet.poll();
                mVisitedCells.add(currentCell);
                for (NavCell neighbor: currentCell.getNeighbors()) {
                    if (neighbor == null) {continue;}
                    if (mVisitedCells.contains(neighbor)){continue;}
                    float tmpCost = currentCell.getCost() + 1.0f;
                    if (openSet.contains(neighbor)){
                        if (tmpCost < neighbor.getCost()){
                            neighbor.update(tmpCost, aStarHeuristic(neighbor, mCellEnd), currentCell);
                            openSet.remove(neighbor);
                            openSet.add(neighbor);
                        }
                    } else {
                        neighbor.update(tmpCost, aStarHeuristic(neighbor, mCellEnd), currentCell);
                        openSet.add(neighbor);
                        mVisitedCells.add(neighbor);
                    }
                }
            }
        }
        if (mCellEnd.getPrevious() == null){
            mNoSolution = true;
        }
    }

    private float aStarHeuristic(NavCell from, NavCell to) {
        // the distance formula
        Point p1 = from.getCentroid();
        Point p2 = to.getCentroid();
        float dx = (p2.x-p1.x)*(p2.x-p1.x);
        float dy = (p2.y-p1.y)*(p2.y-p1.y);
        return (float)Math.sqrt(dx+dy);
    }

    public void loadNewMap() {
        resetGrid();
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

        // Set Blockers
        int midRow = mCellRows / 2;
        for (int i = 0; i < mCellRows; i++) {
            for (int j = 0; j < mCellCols; j++) {
                if (i == midRow && j > 0 && j < mCellCols-1) {
                    mCells[i][j].setImpassable();
                } else if (i != midRow && mAddExtraBlockers) {
                    mCells[i][j].setPassable(Math.random() < mPercentageBlocked);
                }
            }
        }

        // connect cells
        for (int i = 0; i < mCellRows; i++) {
            for (int j = 0; j < mCellCols; j++) {
                if (j > 0 && mCells[i][j-1].isPassable()){mCells[i][j].setNeighbor(0, mCells[i][j-1]);}
                if (i > 0 && mCells[i-1][j].isPassable()){mCells[i][j].setNeighbor(1, mCells[i-1][j]);}
                if (j < mCellCols-1 && mCells[i][j+1].isPassable()){mCells[i][j].setNeighbor(2, mCells[i][j+1]);}
                if (i < mCellRows-1 && mCells[i+1][j].isPassable()){mCells[i][j].setNeighbor(3, mCells[i+1][j]);}
            }
        }

        // set start cell
        mCellStart = mCells[mCellRows/4][mCellCols/2];
        if (!mCellStart.isPassable()){
            while(!mCellStart.isPassable()){
                mCellStart = mCells[(int)(Math.random() * mCellRows)][(int)(Math.random() * mCellCols)];
            }
        }
        mCellEnd = null;

        invalidate();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("display_visited_cells")){
            try {
                mDrawVisitedCells = sharedPreferences.getBoolean(key, false);
            } catch (Exception e) {
                Log.e(TAG, "onSharedPreferenceChanged: exception", e);
            }
        }
        if (key.equals("add_extra_blockers")){
            try {
                mAddExtraBlockers = sharedPreferences.getBoolean(key, false);
            } catch (Exception e) {
                Log.e(TAG, "onSharedPreferenceChanged: exception", e);
            }
        }
        if (key.equals("blocker_percentage")){
            try {
                mPercentageBlocked = getBlockerPercentChance(Float.parseFloat(sharedPreferences.getString(key, "10")));
            } catch (Exception e){
                Log.e(TAG, "initialize: error converting blocker percentage", e );
                mPercentageBlocked = getBlockerPercentChance(10);
            }
        }
    }
}
