package com.diycircuits.cangjie;

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

	@Override
	public View onCreateInputView() {
		Log.i("Cangjie", "onCreateInputView");
		LayoutInflater inflater = getLayoutInflater(); 

		mKeyboard = (SoftKeyboardView) inflater.inflate(R.layout.keyboard,
				null);
		
		mKeyboard.setOnKeyboardActionListener(this);
		setCandidatesViewShown(true);
		
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
	
}
