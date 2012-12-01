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
		
		setKeyboard(new SoftKeyboard(context, R.xml.cangjie));
	}

}
