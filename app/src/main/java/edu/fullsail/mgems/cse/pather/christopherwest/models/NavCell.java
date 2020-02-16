package edu.fullsail.mgems.cse.pather.christopherwest.models;

import android.graphics.Point;
import android.graphics.Rect;

import java.util.Comparator;

public class NavCell {
    // setup once
    private Rect mBounds;
    private Point mCentroid;
    private boolean mPassable;
    private NavCell[] mNeighbors;

    //for pathing
    private float mCost;
    private float mCostFinal;
    private NavCell mPrevious;

    public NavCell() {
        this.mPassable = true;
        mNeighbors = new NavCell[4];
    }

    public Rect getBounds() {
        return mBounds;
    }

    public void setBounds(Rect mBounds) {
        this.mBounds = mBounds;
        this.mCentroid = new Point(mBounds.centerX(), mBounds.centerY());
    }

    public Point getCentroid() {
        return mCentroid;
    }

    public boolean isPassable() {
        return mPassable;
    }

    public void setPassable(boolean mPassable) {
        this.mPassable = mPassable;
    }

    public NavCell[] getNeighbors() {
        return mNeighbors;
    }

    public void setNeighbors(NavCell[] mNeighbors) {
        this.mNeighbors = mNeighbors;
    }

    public float getCost() {
        return mCost;
    }

    public void setCost(float mCost) {
        this.mCost = mCost;
    }

    public float getCostFinal() {
        return mCostFinal;
    }

    public void setCostFinal(float mCostFinal) {
        this.mCostFinal = mCostFinal;
    }

    public NavCell getPrevious() {
        return mPrevious;
    }

    public void setPrevious(NavCell mPrevious) {
        this.mPrevious = mPrevious;
    }

    public void update(float cost, float costFinal, NavCell prev){
        mCost = cost;
        mCostFinal = costFinal;
        mPrevious = prev;
    }

    public void reset(){
        mCost = mCostFinal = Float.MAX_VALUE;
        mPrevious = null;
    }

    public static Comparator<NavCell> NavCellByFinalCostCompartator = new Comparator<NavCell>() {
        @Override
        public int compare(NavCell o1, NavCell o2) {
            if (o1.mCostFinal < o2.mCostFinal){
                return -1;
            }
            if (o1.mCostFinal > o2.mCostFinal){
                return 1;
            }
            return 0;
        }
    };

    private void setBounds(int left, int top, int right, int bottom) {
        mBounds = new Rect(left, top, right, bottom);
        mCentroid = new Point(mBounds.centerX(), mBounds.centerY());
    }
    public void setBounds(int row, int column, int cellSize) {
        setBounds(
                column*cellSize,
                row*cellSize,
                (column*cellSize)+cellSize,
                (row*cellSize)+cellSize);
    }

    public void setPassable(){
        setPassable(true);
    }
    public void setImpassable() {
        setPassable(false);
    }

    public void setNeighbor(int i, NavCell navCell) {
        this.mNeighbors[i] = navCell;
    }
}
