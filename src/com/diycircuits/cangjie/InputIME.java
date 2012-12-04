package com.diycircuits.cangjie;

import java.io.*;
import java.util.*;
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
import android.widget.*;

public class InputIME extends InputMethodService implements KeyboardView.OnKeyboardActionListener, CandidateSelect.CandidateListener {

        private final static int CANGJIE = 0;
        private final static int QUICK   = 1;
    
	private SoftKeyboardView mKeyboard = null;
	private CandidateView mCandidate = null;
	private CandidateSelect mSelect = null;
        private LinearLayout mLinear = null;
        private int numberOfKey = 0;
        private StringBuffer sb = new StringBuffer();
        private char[][] cangjie = new char[13167][6];
        private char[] single  = new char[26];
        private int[] char_idx = new int[26];
        private char[] user_input = new char[5];
        private int totalMatch = 0;
        private char matchChar[] = new char[13167];
        private StringBuffer commit = new StringBuffer();
        private int imeOptions = 0;
        private int mInputMethodState = CANGJIE;

	@Override
	public View onCreateInputView() {
		LayoutInflater inflater = getLayoutInflater(); 

		mKeyboard = (SoftKeyboardView) inflater.inflate(R.layout.keyboard,
				null);
		
		mKeyboard.setOnKeyboardActionListener(this);
		setCandidatesViewShown(true);

		try {
		    InputStream is = getResources().openRawResource(R.raw.cj);
		    InputStreamReader input = new InputStreamReader(is, "UTF-8");
		    BufferedReader reader = new BufferedReader(input);
		    String str = null;
		    int count = 0, index = 0;
		    char c = 'a';
		    do {
			str = reader.readLine();
			single[count] = str.charAt(2);
			count++;
		    } while (str != null && count < 26);
		    count = 0;
		    do {
			str = reader.readLine();
			index = str.indexOf(' ');
			if (str.charAt(0) == 'z') index = str.indexOf('\t');
			if (index > 0) {
			    str.getChars(0, index, cangjie[count], 0);
			    str.getChars(index + 1, index + 2, cangjie[count], 5);
			    if (cangjie[count][0] == c) {
				char_idx[c - 'a'] = count;
				c = (char) (c + 1);
			    }
			}
			count++;
		    } while (str != null && count < cangjie.length);
		    
		    reader.close();

		    for (count = 0; count < 5; count++)
			user_input[count] = 0;

		} catch (Exception ex) {
		    ex.printStackTrace();
		}
		
		return mKeyboard;
	}

        public void characterSelected(char c) {
	    commit.setLength(0);
	    commit.append(c);
	    getCurrentInputConnection().setComposingText("", 1);
	    getCurrentInputConnection().beginBatchEdit();
	    getCurrentInputConnection().commitText(commit.toString(), 1);
	    getCurrentInputConnection().endBatchEdit();
	    sb.setLength(0);
	    for (int cc = 0; cc < user_input.length; cc++) user_input[cc] = 0;
	    mSelect.updateMatch(null, 0);
	}
    
	@Override
	public View onCreateCandidatesView() {
		LayoutInflater inflater = getLayoutInflater(); 

		mCandidate = (CandidateView) inflater.inflate(R.layout.candidate,
							  null);
		mSelect    = (CandidateSelect) mCandidate.findViewById(R.id.match_view);
		mSelect.setCandidateListener(this);

		return mCandidate;
	}

	@Override
	public void onKey(int primaryKey, int[] keyCode) {
		if (primaryKey == -200) {
			IBinder token = getWindow().getWindow().getAttributes().token;
			InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			im.switchToNextInputMethod(token, false);
		} else if (primaryKey == -5) {
		    if (sb.length() > 1 && sb.length() <= 5) {
			user_input[sb.length() - 1] = 0;
			sb.setLength(sb.length() - 1);
			getCurrentInputConnection().setComposingText(sb.toString(), 1);
			match();
		    } else if (sb.length() == 1) {
			user_input[0] = 0;
			sb.setLength(0);
			getCurrentInputConnection().setComposingText("", 1);
			mSelect.updateMatch(null, 0);
		    } else if (sb.length() == 0) {
			getCurrentInputConnection().deleteSurroundingText(1, 0);
			mSelect.updateMatch(null, 0);
		    }
		} else {
		    if (primaryKey == -300) {
			toggleInputMethod();
		    } else if (primaryKey == ' ' || primaryKey == 10 || primaryKey == 65292 || primaryKey == 12290 || (primaryKey >= '0' && primaryKey <= '9')) {
			if (primaryKey == 10 && ((imeOptions & EditorInfo.IME_FLAG_NO_ENTER_ACTION) == 0)) {
			    if (((imeOptions & EditorInfo.IME_MASK_ACTION) == EditorInfo.IME_ACTION_DONE)     ||
				((imeOptions & EditorInfo.IME_MASK_ACTION) == EditorInfo.IME_ACTION_GO)       ||
				((imeOptions & EditorInfo.IME_MASK_ACTION) == EditorInfo.IME_ACTION_NEXT)     ||
				((imeOptions & EditorInfo.IME_MASK_ACTION) == EditorInfo.IME_ACTION_PREVIOUS) ||
				((imeOptions & EditorInfo.IME_MASK_ACTION) == EditorInfo.IME_ACTION_SEARCH)   ||
				((imeOptions & EditorInfo.IME_MASK_ACTION) == EditorInfo.IME_ACTION_SEND)) {
				getCurrentInputConnection().performEditorAction(imeOptions);
			    } else {
				characterSelected((char) primaryKey);
			    }
			} else {
			    characterSelected((char) primaryKey);
			}
		    } else if (sb.length() < 5 && primaryKey >= (int) 'a' && primaryKey <= (int) 'z') {
			user_input[sb.length()] = (char) primaryKey;
			sb.append(single[primaryKey - 'a']);
			getCurrentInputConnection().setComposingText(sb.toString(), 1);
			match();
		    }
		}
	}

        private boolean match() {
	    int i = char_idx[user_input[0] - 'a'];
	    int j = 0;
	    
	    if (user_input[0] == 'z') j = char_idx.length;
	    else j = char_idx[user_input[0] - 'a' + 1];

	    totalMatch = 0;
	    // Log.i("Cangjie", "Range " + i + " " + j);
	    for (int c = i; c < j; c++) {
		if (sb.length() == 1) {
		    // Log.i("Cangjie", " Match " + cangjie[c][5] + " " + cangjie[c][0] + cangjie[c][1] + cangjie[c][2] + cangjie[c][3] + cangjie[c][4]);
		    matchChar[totalMatch] = cangjie[c][5];
		    totalMatch++;
		} else {
		    int l = 1;
		    for (int k = 1; k < 5; k++) {
			if (user_input[k] == cangjie[c][k] && user_input[k] != 0) {
			    l++;
			}
		    }
		    if (l == 5 || user_input[l] == 0) {
			// Log.i("Cangjie", " Match " + cangjie[c][5] + " " + cangjie[c][0] + cangjie[c][1] + cangjie[c][2] + cangjie[c][3] + cangjie[c][4]);
		        matchChar[totalMatch] = cangjie[c][5];
			totalMatch++;
		    }
		}
	    }

	    mSelect.updateMatch(matchChar, totalMatch);

	    return true;
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

	@Override
        public void onStartInputView (EditorInfo info, boolean restarting) {
	    sb.setLength(0);
	    getCurrentInputConnection().setComposingText("", 1);


	    imeOptions = info.imeOptions;
	    
	    Keyboard.Key actionKey = searchKey(10);
	    
	    if (actionKey != null) {
		if ((info.imeOptions & EditorInfo.IME_MASK_ACTION) == EditorInfo.IME_ACTION_SEARCH && ((imeOptions & EditorInfo.IME_FLAG_NO_ENTER_ACTION) == 0)) {
		    actionKey.icon = getResources().getDrawable(R.drawable.sym_keyboard_search);
		} else {
		    actionKey.icon = getResources().getDrawable(R.drawable.enter_icon);
		}
		mKeyboard.updateKeyboard();
	    }
	}

        private Keyboard.Key searchKey(int code) {
	    if (mKeyboard == null || mKeyboard.getKeyboard() == null)
		return null;
	    
	    Keyboard key = mKeyboard.getKeyboard();
	    List<Keyboard.Key> keys = key.getKeys();
	    Keyboard.Key actionKey = null;

	    for (int count = 0; count < keys.size(); count++) {
		int[] codes = keys.get(count).codes;
		if (codes == null || codes.length <= 0) continue;
		if (codes[0] != code) continue;
		actionKey = keys.get(count);
		break;
	    }

	    return actionKey;
        }
    
        public void toggleInputMethod() {
	    Keyboard.Key methodKey = searchKey(-300);

	    if (methodKey == null)
		return;

	    Log.i("Cangjie", "Input State " + mInputMethodState);
	    
	    if (mInputMethodState == CANGJIE) {
		mInputMethodState = QUICK;
		methodKey.label = getString(R.string.quick);
	    } else {
		mInputMethodState = CANGJIE;
		methodKey.label = getString(R.string.cangjie);
	    }

	    mKeyboard.updateKeyboard();
        }
    
}
