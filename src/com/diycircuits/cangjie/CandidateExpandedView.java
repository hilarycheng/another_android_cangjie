package com.diycircuits.cangjie;

import android.content.Context;
import android.util.AttributeSet;
import android.view.*;
import android.widget.*;
import android.graphics.*;
import android.view.LayoutInflater;
import android.util.Log;
import android.view.ViewTreeObserver.OnTouchModeChangeListener;

public class CandidateExpandedView extends ViewGroup implements ViewTreeObserver.OnTouchModeChangeListener {

    private char[] match = null;
    private int totalMatch = 0;
    private int mWidth = 0;
    private int mHeight = 0;
    private Paint paint = null;
    private CandidateRow row[] = new CandidateRow[5];
    private LayoutParams params = null;
    private int mFontSize = 50;
    private int mTopOffset = 0;
    private int mRowHeight = 0;
    private Rect mRect = new Rect();
    private Rect mRectFull = new Rect();
    private Context mContext = null;
    private int mMotionY = 0;
    
    public CandidateExpandedView(Context context, AttributeSet attrs) {
	super(context, attrs);

	mContext = context;

	paint = new Paint();
	paint.setColor(Color.BLACK);
	paint.setAntiAlias(true);
	paint.setTextSize(50);
	paint.setStrokeWidth(0);

	for (int count = 0; count < row.length; count++) {
	    row[count] = new CandidateRow(context, attrs);
	    addView(row[count], count);
	}
    }

    public void setFontSize(int fs, int top, int h) {
	mFontSize = fs;
	mTopOffset = top;
	mRowHeight = h;
    }
    
    public void setMatch(char[] match, int total) {
	this.match = match;
	this.totalMatch = total;

	paint.setTextSize(mFontSize);
	paint.getTextBounds(mContext.getString(R.string.cangjie_fullname), 0, 2, mRectFull);
	paint.getTextBounds(mContext.getString(R.string.cangjie), 0, 1, mRect);

	int spacing = mRectFull.width() - (2 * mRect.width());

	Log.i("Cangjie", "Set Match " + spacing);
	int _col = mWidth / (mRect.width() + spacing);
	int _row = 0;
	if (_col > 0) {
	    _row = total / _col;
	    if ((total % _col) != 0) _row++;
	    mHeight = _row * mRowHeight;
	}
	for (int count = 0; count < row.length; count++) {
	    if (total > count * _col) {
		int remain = total - (count * _col);
		if (remain > _col) remain = _col;
		row[count].setFontSize(mFontSize, mTopOffset);
		row[count].setMatch(match, count * _col, remain);
	    }
	}

	Log.i("Cangjie", "Set Match " + mHeight + " " + _row + " " + _col + " " + spacing);
	
	invalidate();
    }
    
    public void setDimension(int w, int h) {
	mWidth = w;
	mHeight = h;
	Log.i("Cangjie", "Candidate Expanded View SetDimension " + w + " " + h);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        final ViewTreeObserver treeObserver = getViewTreeObserver();
        if (treeObserver != null) {
            treeObserver.addOnTouchModeChangeListener(this);
        }
    }

    public void onTouchModeChanged(boolean isInTouchMode) {
	Log.i("Cangjie", " On Touch Mode Changed " + isInTouchMode);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        final ViewTreeObserver treeObserver = getViewTreeObserver();
        if (treeObserver != null) {
            treeObserver.removeOnTouchModeChangeListener(this);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
	super.onSizeChanged(w, h, oldw, oldh);
	Log.i("Cangjie", "Candidate Expanded View SizeChanged " + w + " " + h + " " + oldw + " " + oldh);
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	int desireWidth = resolveSize(mWidth, widthMeasureSpec);
	int desiredHeight = resolveSize(mHeight, heightMeasureSpec);
		 
	// Maximum possible width and desired height
	setMeasuredDimension(desireWidth, desiredHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
	super.onDraw(canvas);
	// if (canvas == null)
	//     return;

	// // Log.i("Cangjie", "Candidate Expanded View on Draw");
	// if (match == null || totalMatch <= 0) return;

	// paint.setColor(0xff282828);
	// canvas.drawRect(0, 0, mWidth, mHeight - 0, paint);
	// paint.setColor(0xff33B5E5);

	// for (int count = 0; count < 66; count++) {
	//     int row = (count / 11);
	//     int column = (count % 11);
	//     canvas.drawText(match, count, 1, column * 64, 72 + row * 64, paint);
	// }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
	int y = (int) event.getY();
	int deltaY;
	
	switch (event.getAction()) {
	case MotionEvent.ACTION_UP:
	    Log.i("Cangjie", "Candidate Expanded View on IAction Up");
	    break;
	case MotionEvent.ACTION_DOWN:
	    Log.i("Cangjie", "Candidate Expanded View on IAction Down");
	    mMotionY = y;
	    break;
	case MotionEvent.ACTION_MOVE:
	    deltaY = y - mMotionY;
	    Log.i("Cangjie", "Candidate Expanded View on IAction Move : " + deltaY);
	    
	    break;
	}

	return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
	int y = (int) event.getY();
	int deltaY;
	
	switch (event.getAction()) {
	case MotionEvent.ACTION_UP:
	    Log.i("Cangjie", "Candidate Expanded View on Action Up");
	    break;
	case MotionEvent.ACTION_DOWN:
	    Log.i("Cangjie", "Candidate Expanded View on Action Down");
	    mMotionY = y;
	    break;
	case MotionEvent.ACTION_MOVE:
	    deltaY = y - mMotionY;
	    Log.i("Cangjie", "Candidate Expanded View on Action Move : " + deltaY);
	    
	    break;
	}
	return true;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
	Log.i("Cangjie", "onLayout " + left + " " + top + " " + right + " " + bottom);

	paint.setTextSize(mFontSize);
	paint.getTextBounds(mContext.getString(R.string.cangjie), 0, 1, mRect);

	int mTop = 0;
	for (int count = 0; count < row.length; count++) {
	    View view = row[count];
	    view.layout(0, mTop, mWidth, mTop + mRowHeight);

	    mTop += mRowHeight;
	}

    }

    public static class LayoutParams extends ViewGroup.LayoutParams {

        int viewType;
        boolean recycledHeaderFooter;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int w, int h) {
            super(w, h);
        }

        public LayoutParams(int w, int h, int viewType) {
            super(w, h);
            this.viewType = viewType;
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }

}
