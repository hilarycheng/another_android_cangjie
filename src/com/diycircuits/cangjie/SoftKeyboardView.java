package com.diycircuits.cangjie;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class SoftKeyboardView extends KeyboardView {

	private int mKeyboardWidth = 0;
	private int mKeyboardHeight = 0;
	
	@SuppressWarnings("deprecation")
	public SoftKeyboardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display d = wm.getDefaultDisplay();
		mKeyboardWidth = d.getWidth();
		mKeyboardHeight = d.getHeight() / 2;
		
		Log.i("Cangjie", "Screen WidthxHeight : " + d.getWidth() + " x " + d.getHeight());
		
		setKeyboard(new SoftKeyboard(context, R.xml.cangjie));
	}

	/*
	@Override
	protected void onMeasure(int width, int height) {
		int mwidth = MeasureSpec.makeMeasureSpec(mKeyboardWidth, MeasureSpec.EXACTLY);
		int mheight = MeasureSpec.makeMeasureSpec(mKeyboardHeight, MeasureSpec.EXACTLY);
		
		super.onMeasure(mwidth, mheight);
		
		Log.i("Cangjie",
				"SoftwareKeyboardView WidthxHeight : " + mKeyboardWidth+ " x " + mKeyboardHeight +
				" Spec1 " + mwidth + " x " + mheight);
	}
	*/
}
