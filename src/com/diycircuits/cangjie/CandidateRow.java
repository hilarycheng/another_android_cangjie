package com.diycircuits.cangjie;

import android.content.Context;
import android.util.AttributeSet;
import android.view.*;
import android.widget.*;
import android.graphics.*;
import android.view.LayoutInflater;
import android.util.Log;

public class CandidateRow extends View {

    private int mWidth = 100;
    private int mHeight = 64;
    private Paint mPaint = null;
    private char[] mMatch = null;
    private int mTotal = 0;
    private int mOffset = 0;
    private int mTopOffset = 0;
    private int mFontSize = 0;
    
    public CandidateRow(Context context, AttributeSet attrs) {
	super(context, attrs);

	mPaint = new Paint();
	mPaint.setColor(Color.BLACK);
	mPaint.setAntiAlias(true);
	mPaint.setTextSize(50);
	mPaint.setStrokeWidth(0);
    }

    public void setFontSize(int fs, int off) {
	mFontSize = fs;
	mTopOffset = off;
	mPaint.setTextSize(mFontSize);
    }
    
    public void setMatch(char[] match, int offset, int total) {
	mMatch  = match;
	mOffset = offset;
	mTotal  = total;
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	int desireWidth = resolveSize(mWidth, widthMeasureSpec);
	int desiredHeight = resolveSize(mHeight, heightMeasureSpec);
		 
	setMeasuredDimension(desireWidth, desiredHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
	if (canvas == null) return;
	Log.i("Cangjie", "CandidateRow onDraw " + getWidth() + " " + getHeight());
	mPaint.setColor(0xff33B5E5);
	canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);
	mPaint.setColor(0xff282828);
	canvas.drawRect(1, 1, getWidth() - 10, getHeight() - 1, mPaint);
	mPaint.setColor(0xff33B5E5);
	// for (int count = mOffset; count < mOffset + mTotal; count++) {
	if (mMatch != null) canvas.drawText(mMatch, mOffset, mTotal, 0, mTopOffset, mPaint);
	// }
    }

}
