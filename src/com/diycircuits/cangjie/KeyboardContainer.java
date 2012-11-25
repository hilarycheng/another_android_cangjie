package com.diycircuits.cangjie;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.RelativeLayout;

public class KeyboardContainer extends RelativeLayout implements
		OnTouchListener {

	private int mKeyboardWidth = 0;
	private int mKeyboardHeight = 0;
	
	@SuppressWarnings("deprecation")
	public KeyboardContainer(Context context, AttributeSet attrs) {
		super(context, attrs);

		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display d = wm.getDefaultDisplay();
		mKeyboardWidth = d.getWidth();
		mKeyboardHeight = d.getHeight() / 2;
		
		Log.i("Cangjie", "Screen WidthxHeight : " + d.getWidth() + " x " + d.getHeight());
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void onMeasure(int width, int height) {
		// int measuredWidth = 0;
		// int measuredHeight = 0;
	
		int mwidth = MeasureSpec.makeMeasureSpec(mKeyboardWidth, MeasureSpec.EXACTLY);
		int mheight = MeasureSpec.makeMeasureSpec(mKeyboardHeight, MeasureSpec.EXACTLY);
		
		super.onMeasure(mwidth, mheight);
		
		Log.i("Cangjie",
				"SoftwareKeyboardView WidthxHeight : " + mKeyboardWidth+ " x " + mKeyboardHeight +
				" Spec1 " + mwidth + " x " + mheight);
	}
}
