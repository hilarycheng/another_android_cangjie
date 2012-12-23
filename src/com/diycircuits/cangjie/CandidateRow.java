package com.diycircuits.cangjie;

import android.content.Context;
import android.util.AttributeSet;
import android.view.*;
import android.widget.*;
import android.graphics.*;
import android.view.LayoutInflater;
import android.util.Log;
import android.os.Handler;
import android.os.Message;

public class CandidateRow extends View {

    private int mWidth = 100;
    private int mHeight = 64;
    private Paint mPaint = null;
    private char[] mMatch = null;
    private int mTotal = 0;
    private int mOffset = 0;
    private int mTopOffset = 0;
    private int mLeftOffset = 0;
    private int mFontSize = 0;
    private Context context = null;
    private Rect rect = new Rect();
    private Handler mHandler = null;
    private int mAllTotal = 0;
    private int cspacing = 17;
    
    public CandidateRow(Context context, AttributeSet attrs) {
	super(context, attrs);

	this.context = context;

	mPaint = new Paint();
	mPaint.setColor(Color.BLACK);
	mPaint.setAntiAlias(true);
	mPaint.setTextSize(50);
	mPaint.setStrokeWidth(0);
    }

    public void setHandler(Handler handler) {
	mHandler = handler;
    }
    
    public void setFontSize(int fs, int off, int lo) {
	mFontSize = fs;
	mTopOffset = off;
	mLeftOffset = lo;
	mPaint.setTextSize(mFontSize);
    }
    
    public void setMatch(char[] match, int offset, int total, int alltotal) {
	mMatch  = match;
	mOffset = offset;
	mTotal  = total;
	mAllTotal = alltotal;
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {

	if (event.getAction() == MotionEvent.ACTION_UP) {
	    int x = (int) event.getX();

	    int pos = x - mLeftOffset;
	    mPaint.getTextBounds(context.getString(R.string.cangjie), 0, 1, rect);
	    pos = pos / (rect.width() + cspacing);

	    if (x < mLeftOffset + rect.width() + cspacing) {
		pos = 0;
	    }
	    
	    if ((mOffset + pos) < mAllTotal) {
		Message msg = mHandler.obtainMessage(CandidateSelect.CHARACTER, mMatch[mOffset + pos], mOffset + pos);
		mHandler.sendMessage(msg);
	    }
	}
	
	return super.onTouchEvent(event);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	int desireWidth = resolveSize(mWidth, widthMeasureSpec);
	int desiredHeight = resolveSize(mHeight, heightMeasureSpec);

	mHeight = desiredHeight;
	
	setMeasuredDimension(desireWidth, desiredHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
	if (canvas == null) return;
	
	mPaint.setColor(0xff444444);
	canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);
	mPaint.setColor(0xff000000);
	canvas.drawRect(0, 0, getWidth(), getHeight() - 1, mPaint);
	mPaint.setColor(0xff33B5E5);
	if (mMatch != null) {
	    int spacing = mLeftOffset + (cspacing / 2);
	    mPaint.getTextBounds(context.getString(R.string.cangjie), 0, 1, rect);
	    int topOffset = (rect.height() - rect.bottom);
	    topOffset = topOffset + ((mHeight - rect.height()) / 2);
	    for (int count = mOffset; count < mOffset + mTotal; count++) {
		canvas.drawText(mMatch, count, 1, spacing, topOffset, mPaint);
		spacing += cspacing + rect.width();
	    }
	}
    }

}
