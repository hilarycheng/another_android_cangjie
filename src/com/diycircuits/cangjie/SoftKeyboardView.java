package com.diycircuits.cangjie;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.Keyboard.Key;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

public class SoftKeyboardView extends KeyboardView {

	private int mKeyboardWidth = 0;
	private int mKeyboardHeight = 0;
        private Keyboard mKeyboard = null;
        private Context mContext = null;
        private CandidateView cv = null;
	
	@SuppressWarnings("deprecation")
	public SoftKeyboardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display d = wm.getDefaultDisplay();
		mKeyboardWidth = d.getWidth();
		mKeyboardHeight = d.getHeight() / 2;

		mContext = context;
		setKeyboard(mKeyboard = new SoftKeyboard(context, R.xml.cangjie));
	}

	@SuppressWarnings("deprecation")
	public SoftKeyboardView(Context context, AttributeSet attrs, int defStyle) {
	        super(context, attrs, defStyle);
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display d = wm.getDefaultDisplay();
		mKeyboardWidth = d.getWidth();
		mKeyboardHeight = d.getHeight() / 2;
		
		mContext = context;
		setKeyboard(mKeyboard = new SoftKeyboard(context, R.xml.cangjie));
	}

        public void setCandidateView(CandidateView cv) {
	    this.cv = cv;
	}
    
        @Override
	public void onSizeChanged(int w, int h, int oldw, int oldh) {
	    super.onSizeChanged(w, h, oldw, oldh);
	    // CandidateView cv = (CandidateView) findViewById(R.id.candidateView);
	    if (cv != null) cv.setDimension(w, h);
	}

        public Keyboard getKeyboard() {
	    return mKeyboard;
	}

        public void updateKeyboard() {
	    setKeyboard(mKeyboard);
        }


        public boolean onLongPress(Key popupKey) {
        	if (popupKey.codes[0] == ' ') {
    			InputMethodManager im = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    			im.showInputMethodPicker();
        		
        		return true;
        	}
            return super.onLongPress(popupKey);
        }

}
