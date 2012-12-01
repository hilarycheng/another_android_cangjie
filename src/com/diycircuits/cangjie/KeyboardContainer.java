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
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		return false;
	}

	@Override
	protected void onMeasure(int width, int height) {
		int mwidth = MeasureSpec.makeMeasureSpec(mKeyboardWidth, MeasureSpec.EXACTLY);
		int mheight = MeasureSpec.makeMeasureSpec(mKeyboardHeight, MeasureSpec.EXACTLY);
		
		super.onMeasure(mwidth, mheight);
	}
}
