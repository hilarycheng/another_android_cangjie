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
    private Context context = null;
    private Rect rect = new Rect();
    
    public CandidateRow(Context context, AttributeSet attrs) {
	super(context, attrs);

	this.context = context;

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
    public boolean onTouchEvent(MotionEvent event) {

	Log.i("Cangjie", "CandidateRow onTouch Event " + event.getAction());

	if (event.getAction() == MotionEvent.ACTION_UP) {
	    int x = (int) event.getX();

	    int pos = x - 25;
	    mPaint.getTextBounds(context.getString(R.string.cangjie), 0, 1, rect);
	    pos = pos / (rect.width() + 14);

	    Log.i("Cangjie", " Pos " + pos + " Word " + mMatch[mOffset + pos]);
	}
	
	return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	int desireWidth = resolveSize(mWidth, widthMeasureSpec);
	int desiredHeight = resolveSize(mHeight, heightMeasureSpec);
		 
	Log.i("Cangjie", "CandidateRow onMeasure " + mWidth + " " + mHeight + " " +
	      desireWidth + " " + desiredHeight);

	setMeasuredDimension(desireWidth, desiredHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
	if (canvas == null) return;
	// Log.i("Cangjie", "CandidateRow onDraw " + getWidth() + " " + getHeight());
	mPaint.setColor(0xff33B5E5);
	canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);
	mPaint.setColor(0xff282828);
	canvas.drawRect(0, 0, getWidth(), getHeight() - 1, mPaint);
	mPaint.setColor(0xff33B5E5);
	if (mMatch != null) {
	    int spacing = 25;
	    mPaint.getTextBounds(context.getString(R.string.cangjie), 0, 1, rect);
	    for (int count = mOffset; count < mOffset + mTotal; count++) {
		canvas.drawText(mMatch, count, 1, spacing, mTopOffset - 7, mPaint);
		spacing += 14 + rect.width();
	    }
	}
    }

}
