package com.diycircuits.cangjie;

import java.io.InputStream;
import android.view.inputmethod.EditorInfo;
import android.content.Context;
import android.inputmethodservice.InputMethodService;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.Keyboard;
import android.os.IBinder;

public class InputIME extends InputMethodService implements KeyboardView.OnKeyboardActionListener {

	private SoftKeyboardView mKeyboard = null;
	private CandidateView mCandidate = null;
        private int numberOfKey = 0;
        private StringBuffer sb = new StringBuffer();

	@Override
	public View onCreateInputView() {
		Log.i("Cangjie", "onCreateInputView");
		LayoutInflater inflater = getLayoutInflater(); 

		mKeyboard = (SoftKeyboardView) inflater.inflate(R.layout.keyboard,
				null);
		
		mKeyboard.setOnKeyboardActionListener(this);
		setCandidatesViewShown(true);

		try {
		    InputStream is = getResources().openRawResource(R.raw.cj);
		} catch (Exception ex) {
		    ex.printStackTrace();
		}
		
		return mKeyboard;
	}

	@Override
	public View onCreateCandidatesView() {
		Log.i("Cangjie", "onCreateCandidatesView");
		LayoutInflater inflater = getLayoutInflater(); 

		mCandidate = (CandidateView) inflater.inflate(R.layout.candidate,
				null);
		
		return mCandidate;
	}

	@Override
	public void onKey(int primaryKey, int[] keyCode) {
		if (primaryKey == -200) {
			IBinder token = getWindow().getWindow().getAttributes().token;
			InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			im.switchToNextInputMethod(token, false);
		} else if (primaryKey == -5) {
		    if (sb.length() > 0) {
			sb.setLength(sb.length() - 1);
			getCurrentInputConnection().setComposingText(sb.toString(), 1);
		    } else {
			getCurrentInputConnection().deleteSurroundingText(1, 0);
		    }
		} else {
		    Log.i("Cangjie", "onKey " + primaryKey);
		    sb.append(String.valueOf((char) primaryKey));
		    getCurrentInputConnection().setComposingText(sb.toString(), 1);
		}
	}

	@Override
	public void onPress(int arg0) {
	}

	@Override
	public void onRelease(int arg0) {
	}

	@Override
	public void onText(CharSequence arg0) {
		Log.i("Cangjie", "onText " + arg0);
	}

	@Override
	public void swipeDown() {
	}

	@Override
	public void swipeLeft() {
	}

	@Override
	public void swipeRight() {
	}

	@Override
	public void swipeUp() {
	}

	@Override
        public void onStartInputView (EditorInfo info, boolean restarting) {
	    sb.setLength(0);
	    getCurrentInputConnection().setComposingText("", 1);
	}

}
