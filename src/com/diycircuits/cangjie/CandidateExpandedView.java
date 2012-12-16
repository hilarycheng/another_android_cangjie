package com.diycircuits.cangjie;

import android.content.Context;
import android.util.AttributeSet;
import android.view.*;
import android.widget.*;
import android.graphics.*;
import android.view.LayoutInflater;
import android.util.Log;

public class CandidateExpandedView extends View {

    private char[] match = null;
    private int totalMatch = 0;
    private int mWidth = 0;
    private int mHeight = 0;
    private Paint paint = null;
    
    public CandidateExpandedView(Context context, AttributeSet attrs) {
	super(context, attrs);

	paint = new Paint();
	paint.setColor(Color.BLACK);
	paint.setAntiAlias(true);
	paint.setTextSize(50);
	paint.setStrokeWidth(0);
    }

    public void setMatch(char[] match, int total) {
	this.match = match;
	this.totalMatch = total;
	invalidate();
    }
    
    public void setDimension(int w, int h) {
	mWidth = w;
	mHeight = h;
	Log.i("Cangjie", "Candidate Expanded View SetDimension " + w + " " + h);
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
	if (canvas == null)
	    return;

	// Log.i("Cangjie", "Candidate Expanded View on Draw");
	if (match == null || totalMatch <= 0) return;

	paint.setColor(0xff282828);
	canvas.drawRect(0, 0, mWidth, mHeight - 0, paint);
	paint.setColor(0xff33B5E5);

	for (int count = 0; count < 66; count++) {
	    int row = (count / 11);
	    int column = (count % 11);
	    canvas.drawText(match, count, 1, column * 64, 72 + row * 64, paint);
	}
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
	Log.i("Cangjie", "Candidate Expanded View on Touch");
	return false;
    }

}
