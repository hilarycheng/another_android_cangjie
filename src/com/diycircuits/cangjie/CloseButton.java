package com.diycircuits.cangjie;

import android.graphics.drawable.*;
import android.graphics.*;
import android.content.Context;
import android.util.AttributeSet;
import android.view.*;
import android.widget.*;
import android.graphics.*;
import android.view.LayoutInflater;
import android.util.Log;
import android.os.Handler;
import android.os.Message;

public class CloseButton extends View {

    private Context context = null;
    private Drawable mDraw  = null;
    private Bitmap mBitmap = null;
    private Paint mPaint = null;
    private int mHeight = 0;
    
    public CloseButton(Context context, AttributeSet attrs) {
	super(context, attrs);

	this.context = context;

	mDraw = context.getApplicationContext().getResources().getDrawable(R.drawable.close2);

	mPaint = new Paint();
	mPaint.setColor(Color.BLACK);
	mPaint.setAntiAlias(true);
	mPaint.setTextSize(50);
	mPaint.setStrokeWidth(0);
	//mPaint.setColor(0x00000000);

	if (mDraw instanceof BitmapDrawable) {
	    mBitmap = ((BitmapDrawable) mDraw).getBitmap();
	}
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	int desireWidth = resolveSize(mDraw.getIntrinsicWidth(), widthMeasureSpec);
	int desiredHeight = resolveSize(mDraw.getIntrinsicHeight(), heightMeasureSpec);
	
	mHeight = desiredHeight;
	
	setMeasuredDimension(desireWidth, desiredHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
	if (canvas == null) return;

	mPaint.setColor(0x00000000);
	canvas.drawRect(0, 0, getWidth(), mHeight, mPaint);
	int center = (getWidth() - mDraw.getIntrinsicWidth()) / 2;
	if (mBitmap != null) canvas.drawBitmap(mBitmap, center, 0, mPaint);

    }
    
}
