package com.diycircuits.cangjie;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.Keyboard.Key;
import android.util.AttributeSet;
import android.util.Log;
import java.util.List;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

public class SoftKeyboardView extends KeyboardView {

	private int mKeyboardWidth = 0;
	private int mKeyboardHeight = 0;
        private Keyboard mKeyboard = null;
        private Context mContext = null;
        private CandidateView cv = null;
        private int mOldPointerCount = 0;
	
        private Keyboard.Key mAKey = null;
        private Keyboard.Key mLKey = null;

	@SuppressWarnings("deprecation")
	public SoftKeyboardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display d = wm.getDefaultDisplay();
		mKeyboardWidth = d.getWidth();
		mKeyboardHeight = d.getHeight() / 2;

		setPadding(0, 0, 0, 0);
		
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
		
		setPadding(0, 0, 0, 0);

		mContext = context;
		setKeyboard(mKeyboard = new SoftKeyboard(context, R.xml.cangjie));
	}

        private void scanKeys() {
	    List<Keyboard.Key> keyList = mKeyboard.getKeys();
	    for (int count = 0; count < keyList.size(); count++) {
		Keyboard.Key lKey = keyList.get(count);
		if (lKey.codes[0] ==     97) { // A Key
		    mAKey = lKey;
		    continue;
		}
		if (lKey.codes[0] ==    108) { // L Key
		    mLKey = lKey;
		    continue;
		}
	    }
	}

        public void setCandidateView(CandidateView cv) {
	    this.cv = cv;
	}
    
        @Override
	public void onSizeChanged(int w, int h, int oldw, int oldh) {
	    super.onSizeChanged(w, h, oldw, oldh);
	    if (cv != null) cv.setDimension(w, h);
	}

        private MotionEvent createMotion(MotionEvent me, int x, int y) {
	    final long now = me.getEventTime();

	    MotionEvent newm = MotionEvent.obtain(me);
	    newm.setLocation(x, y);

	    return newm;
	}

        @Override
        public boolean onTouchEvent(MotionEvent me) {
	    boolean res = false;

	    if (me == null)
		return super.onTouchEvent(me);

	    if (me.getPointerCount() > 1)
		return super.onTouchEvent(me);
	    
	    if (mKeyboard != null && mAKey == null && mLKey == null) {
		scanKeys();
	    }
	    
	    if (mKeyboard != null && mAKey != null && mLKey != null) {

		if (mAKey.x >= (int) me.getX() &&
		    mAKey.y <= (int) me.getY() &&
		    (mAKey.y + mAKey.height) >= (int) me.getY()) {
		    if (me.getAction() == MotionEvent.ACTION_DOWN) {
			MotionEvent newm = createMotion(me,
							mAKey.x + (mAKey.width / 2),
							mAKey.y + (mAKey.height / 2));
			res = super.onTouchEvent(newm);
			newm.recycle();
			return res;
		    }
		}
		
		if ((mLKey.x + mLKey.width) <= (int) me.getX() &&
		    mLKey.y <= (int) me.getY() &&
		    (mLKey.y + mLKey.height) >= (int) me.getY()) {
		    if (me.getAction() == MotionEvent.ACTION_DOWN) {
			MotionEvent newm = createMotion(me,
							mLKey.x + (mLKey.width / 2),
							mLKey.y + (mLKey.height / 2));
			res = super.onTouchEvent(newm);
			newm.recycle();
			return res;
		    }
		}

	    }
	    
	    return super.onTouchEvent(me);
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
