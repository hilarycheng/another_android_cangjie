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
        private Keyboard.Key mAKeyObj = null;
        private Keyboard.Key mLKeyObj = null;
        private Keyboard mKeyboard = null;
        private Context mContext = null;
        private CandidateView cv = null;
        private int mOldPointerCount = 0;
	
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

        private MotionEvent createMotion(MotionEvent me, int x, int y) {
	    final long now = me.getEventTime();
	    MotionEvent newm = MotionEvent.obtain(now, now,
						  me.getAction(),
						  x, y,
						  me.getPressure(),
						  me.getSize(),
						  me.getMetaState(),
						  me.getXPrecision(),
						  me.getYPrecision(),
						  me.getDeviceId(),
						  me.getEdgeFlags());
	    return newm;
	}
    
        @Override
        public boolean onTouchEvent(MotionEvent me) {
	    boolean res = false;
	    int mAKey = 0;
	    int mLKey = 0;
	    boolean result = false;
	    final int pointerCount = me.getPointerCount();
	    final int action = me.getAction();
	    final long now = me.getEventTime();
	
	    if (mKeyboard != null) {

	     	List<Keyboard.Key> keyList = mKeyboard.getKeys();
		for (int count = 0; count < keyList.size(); count++) {
		    Keyboard.Key lKey = keyList.get(count);
		    boolean inside = lKey.isInside((int) me.getX(), (int) me.getY());
		    if (inside) {
			int newx = lKey.x + (lKey.width  / 2);
			int newy = lKey.y + (lKey.height / 2);
			if (pointerCount != mOldPointerCount) {
			    if (pointerCount == 1) {
				MotionEvent down = MotionEvent.obtain(now, now, MotionEvent.ACTION_DOWN,
								      newx, newy, me.getMetaState());
				result = super.onTouchEvent(down);
				down.recycle();
				if (action == MotionEvent.ACTION_UP) {
				    result = super.onTouchEvent(me);
				}
			    } else {
				MotionEvent up = MotionEvent.obtain(now, now, MotionEvent.ACTION_UP,
								    newx, newy, me.getMetaState());
				result = super.onTouchEvent(up);
				up.recycle();
			    }
			} else {
			    if (pointerCount == 1) {
				result = super.onTouchEvent(me);
			    } else {
				result = true;
			    }
			}
			// MotionEvent newm = createMotion(me, lKey.x + (lKey.width / 2), lKey.y + (lKey.height / 2));
			// return super.onTouchEvent(newm);
			return result;
		    }
		}
	    	int[] keys = mKeyboard.getNearestKeys((int) me.getX(), (int) me.getY());
	    	if (keys != null) {
	    	    boolean inside = false;
	    	    for (int count = 0; count < keys.length; count++) {
	    		if (keys[count] >= keyList.size()) continue;
	    		Keyboard.Key lKey = keyList.get(keys[count]);
	    		inside |= lKey.isInside((int) me.getX(), (int) me.getY());

	    		mAKey = mAKey | ((lKey.codes[0] == 113) ? 0x01 : 0x00) |
	    		    ((lKey.codes[0] == 119) ? 0x02 : 0x00) |
	    		    ((lKey.codes[0] == 101) ? 0x04 : 0x00) |
	    		    ((lKey.codes[0] ==  97) ? 0x08 : 0x00);

	    		mLKey = mLKey | ((lKey.codes[0] == 105) ? 0x01 : 0x00) |
	    		    ((lKey.codes[0] == 111) ? 0x02 : 0x00) |
	    		    ((lKey.codes[0] == 112) ? 0x04 : 0x00) |
	    		    ((lKey.codes[0] == 108) ? 0x08 : 0x00) |
	    		    ((lKey.codes[0] == 107) ? 0x10 : 0x00);

	    		if (mAKeyObj == null && lKey.codes[0] == 97) 
	    		    mAKeyObj = lKey;

	    		if (mLKeyObj == null && lKey.codes[0] == 108) 
	    		    mLKeyObj = lKey;
	    	    }

	    	    if (!inside) {
	    		if (mAKey == 0x0F) {
	    		    if (me.getAction() == MotionEvent.ACTION_DOWN) {
	    			MotionEvent newm = createMotion(me,
	    							mAKeyObj.x + (mAKeyObj.width / 2),
	    							mAKeyObj.y + (mAKeyObj.height / 2));
	    			return super.onTouchEvent(newm);
	    		    }
	    		}
	    		if (mLKey == 0x1F) {
	    		    if (me.getAction() == MotionEvent.ACTION_DOWN) {
	    			MotionEvent newm = createMotion(me,
	    							mLKeyObj.x + (mLKeyObj.width / 2),
	    							mLKeyObj.y + (mLKeyObj.height / 2));
	    			return super.onTouchEvent(newm);
	    		    }
	    		}
	    	    }
	    	}
	    }
	    
	    return false;
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
