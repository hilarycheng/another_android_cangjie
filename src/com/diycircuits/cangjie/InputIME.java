package com.diycircuits.cangjie;

import java.io.*;
import java.util.*;
import android.view.KeyEvent;
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
import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.*;
import android.graphics.*;
import android.util.Log;
import java.lang.reflect.Method;
import android.view.inputmethod.*;
import android.content.pm.PackageManager;
import android.content.pm.ApplicationInfo;
import android.media.AudioManager;

public class InputIME extends InputMethodService implements KeyboardView.OnKeyboardActionListener, CandidateSelect.CandidateListener {

        private final static int CANGJIE    = 0;
        private final static int QUICK      = 1;

        private Keyboard.Key mActionKey = null;
	private SoftKeyboardView mKeyboard = null;
	private CandidateView mCandidate = null;
	private CandidateSelect mSelect = null;
        private LinearLayout mLinear = null;
        private int numberOfKey = 0;
        private StringBuffer sb = new StringBuffer();
        private char[] single  = new char[26];
    

        private char[] user_input = new char[5];
        private int totalMatch = 0;
        private char matchChar[] = new char[21529];
        private StringBuffer commit = new StringBuffer();
        private int imeOptions = 0;
        private int mInputMethodState = CANGJIE;
        private Paint mPaint = null;
        private SharedPreferences preferences;
        private TableLoader mTable = null;
        private AudioManager am = null; 

        private static final int NOT_A_CURSOR_POSITION = -1;
        private int mLastSelectionStart = NOT_A_CURSOR_POSITION;
        private int mLastSelectionEnd = NOT_A_CURSOR_POSITION;

        public InputIME() {
	    super();
	    try {
		Method m = InputMethodService.class.getMethod("enableHardwareAcceleration");
		m.invoke(this);
	    } catch (Exception ex) {
	    }
        }
    
	@Override
	public View onCreateInputView() {

	        ApplicationInfo appInfo = getApplicationInfo();
	    
		preferences = PreferenceManager.getDefaultSharedPreferences(this);

		mInputMethodState = getPreferredInputMethod();

		LayoutInflater inflater = getLayoutInflater(); 

		View view = inflater.inflate(R.layout.keyboard, null);

		mKeyboard = (SoftKeyboardView) view.findViewById(R.id.mainKeyboard);

		mTable = new TableLoader();
		// char[] array = new char[5];
		// mTable.getChar();
		// mTable.passCharArray(array);
		// array = null;
		// Log.i("Cangjie", "You : " + (int) getString(R.string.you).charAt(0));
		
		mKeyboard.setOnKeyboardActionListener(this);
		setCandidatesViewShown(true);

		mPaint = new Paint();
		loadCangjieKey();
		// loadCangjieTable();
		// loadCangjieHKTable();

		for (int count = 0; count < 5; count++) {
		    user_input[count] = 0;
		}
		try {
		    mTable.setPath(appInfo.dataDir.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException ex) {
		}

		mTable.initialize();
		if (getAndClearOftenUsed()) mTable.clearAllFrequency();

		am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

	 	mInputMethodState = getPreferredInputMethod();

		mCandidate = (CandidateView) view.findViewById(R.id.candidateView);
		mKeyboard.setCandidateView(mCandidate);
		mCandidate.setParent(view.findViewById(R.id.mainView));

		mSelect    = (CandidateSelect) view.findViewById(R.id.match_view);

		mSelect.setCandidateListener(this);

		updateInputMethod(mInputMethodState);

		setCandidatesViewShown(false);

		return view;
	}

        private void loadCangjieKey() {
	    try {
		InputStream is = getResources().openRawResource(R.raw.cj_key);
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
		    
		reader.close();

	    } catch (Exception ex) {
		ex.printStackTrace();
	    }
        }

        public void characterSelected(char c, int idx) {
	    if (idx >= 0) mTable.updateFrequencyQuick(c);
	    commit.setLength(0);
	    commit.append(c);
	    getCurrentInputConnection().setComposingText("", 1);
	    getCurrentInputConnection().beginBatchEdit();
	    getCurrentInputConnection().commitText(commit.toString(), 1);
	    getCurrentInputConnection().endBatchEdit();
            clearAllInput();
	}

        private void clearAllInput() {
	    commit.setLength(0);
	    totalMatch = 0;
	    if (mTable != null) mTable.reset();
	    commit.setLength(0);

	    sb.setLength(0);
	    for (int cc = 0; cc < user_input.length; cc++) {
		user_input[cc] = 0;
	    }
	    if (mSelect != null) {
		mSelect.updateMatch(null, 0);
		mSelect.closePopup();
	    }
        }

        private void simulateKeyEventDownUp(int keyCode) {
	    InputConnection ic = getCurrentInputConnection();
	    if (null == ic) return;

	    ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, keyCode));
	    ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, keyCode));
	}
    
	@Override
	public void onKey(int _primaryKey, int[] keyCode) {
	        int primaryKey = (int) Math.abs(_primaryKey);
	        int keyLen = 5;

		if (mInputMethodState == CANGJIE)
		    keyLen = 5;
		if (mInputMethodState == QUICK)
		    keyLen = 2;
		if (primaryKey == 200) {
			IBinder token = getWindow().getWindow().getAttributes().token;
			InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			String id = getSwitchedInputMethod();
			if (id == null || id.length() == 0)
			    im.switchToLastInputMethod(token);
			else {
			    try {
				im.setInputMethod(token, id);
			    } catch (java.lang.IllegalArgumentException ex) {
				Toast.makeText(this, R.string.please_select_next_inputmethod_again, Toast.LENGTH_LONG).show();
			    }
			}
		} else if (primaryKey == 5) {
		    if (mSelect != null) mSelect.closePopup();
		    if (sb.length() > 1 && sb.length() <= keyLen) {
			user_input[sb.length() - 1] = 0;
			sb.setLength(sb.length() - 1);
			getCurrentInputConnection().setComposingText(sb.toString(), 1);
			match();
		    } else if (sb.length() == 1) {
			user_input[0] = 0;
			sb.setLength(0);
			clearAllInput();
			getCurrentInputConnection().setComposingText("", 1);
			mSelect.updateMatch(null, 0);
		    } else if (sb.length() == 0) {
			if (mLastSelectionStart != mLastSelectionEnd) {
			    final int lengthToDelete = mLastSelectionEnd - mLastSelectionStart;
			    getCurrentInputConnection().setSelection(mLastSelectionEnd, mLastSelectionEnd);
			    getCurrentInputConnection().deleteSurroundingText(lengthToDelete, 0);
			} else {
			    // Needs to do compatible for old application
			    // getCurrentInputConnection().deleteSurroundingText(1, 0);
			    simulateKeyEventDownUp(KeyEvent.KEYCODE_DEL);
			    mSelect.updateMatch(null, 0);
			}
		    }
		} else {
		    if (primaryKey == 300) {
			toggleInputMethod();
		    } else if (primaryKey == ' ' || primaryKey == 10 ||
			       primaryKey == 65311 || primaryKey == 65292 ||
			       primaryKey == 12290 || primaryKey == 65281 ||
			       (primaryKey >= '0' && primaryKey <= '9')) {

			if (isAutoSendEnabled()) {
			    if (primaryKey == ' ' || primaryKey == 10 || primaryKey == 65311 ||
				primaryKey == 65292 || primaryKey == 12290 || primaryKey == 65281) {
				if (totalMatch > 0 || mTable.totalMatch() > 0) {
				    characterSelected((char) matchChar[0], 0);
				    clearAllInput();
				    if (mSelect != null) mSelect.closePopup();
				    if (primaryKey == ' ') return;
				}
			    }
			}

			if (primaryKey == 10 && ((imeOptions & EditorInfo.IME_FLAG_NO_ENTER_ACTION) == 0)) {
			    if (((imeOptions & EditorInfo.IME_MASK_ACTION) == EditorInfo.IME_ACTION_DONE)     ||
				((imeOptions & EditorInfo.IME_MASK_ACTION) == EditorInfo.IME_ACTION_GO)       ||
				((imeOptions & EditorInfo.IME_MASK_ACTION) == EditorInfo.IME_ACTION_NEXT)     ||
				((imeOptions & EditorInfo.IME_MASK_ACTION) == EditorInfo.IME_ACTION_PREVIOUS) ||
				((imeOptions & EditorInfo.IME_MASK_ACTION) == EditorInfo.IME_ACTION_SEARCH)   ||
				((imeOptions & EditorInfo.IME_MASK_ACTION) == EditorInfo.IME_ACTION_SEND)) {
				getCurrentInputConnection().performEditorAction(imeOptions);
			    } else {
				characterSelected((char) primaryKey, -1);
			    }
			} else {
			    characterSelected((char) primaryKey, -1);
			}
		    } else if (sb.length() < keyLen && primaryKey >= (int) 'a' && primaryKey <= (int) 'z') {
			user_input[sb.length()] = (char) primaryKey;
			if (tryMatch()) {
			    sb.append(single[primaryKey - 'a']);
			    getCurrentInputConnection().setComposingText(sb.toString(), 1);
			    match();
			} else {
			    user_input[sb.length()] = 0;
			}
		    }
		}
	}

        private boolean isHongKongCharEnabled() {
	    String hkKey = getString(R.string.prefs_hongkongchar_key);
	    boolean enabled = preferences.getBoolean(hkKey, false);

	    return enabled;
	}
    
        private boolean isAutoSendEnabled() {
	    String autosend = getString(R.string.prefs_autosend_key);
	    boolean enabled = preferences.getBoolean(autosend, true);

	    return enabled;
	}
    
        private boolean getAndClearOftenUsed() {
	    String often = getString(R.string.prefs_clear_often_used_key);
	    boolean enabled = preferences.getBoolean(often, false);
	    if (enabled) {
		Editor editor = preferences.edit();
		editor.putBoolean(often, false);
		editor.commit();
	    }

	    return enabled;
	}

        private int getPreferredInputMethod() {
	    String autosend = getString(R.string.prefs_inputmethod_key);
	    String imstring = preferences.getString(autosend, "0");
	    int im = 0;
	    
	    try {
		im = Integer.parseInt(imstring);
	    } catch(NumberFormatException nfe) {
		im = 0;
	    }

	    return im;
	}
    
        private String getSwitchedInputMethod() {
	    String key = getString(R.string.prefs_next_inputmethod_key);
	    String imstring = preferences.getString(key, "");

	    return imstring;
	}
    
        private boolean tryMatch() {
	    if (mInputMethodState == CANGJIE) {
		mTable.setInputMethod(TableLoader.CANGJIE);
		mTable.enableHongKongChar(isHongKongCharEnabled());
		return mTable.tryMatchCangjie(user_input[0], user_input[1], user_input[2], user_input[3], user_input[4]);
	    }
	    
	    if (mInputMethodState == QUICK) {
		mTable.setInputMethod(TableLoader.QUICK);
		return mTable.tryMatchCangjie(user_input[0], user_input[1], user_input[2], user_input[3], user_input[4]);
	    }

	    return false;
        }
        
        private boolean match() {
	    if (mInputMethodState == CANGJIE) {
		// if (isHongKongCharEnabled()) 
		//     return matchCangjieHongKong();
		// else
		//     return matchCangjie();
		mTable.setInputMethod(TableLoader.CANGJIE);
		mTable.enableHongKongChar(isHongKongCharEnabled());
		return matchCangjie();
	    }
	    
	    if (mInputMethodState == QUICK) {
		mTable.setInputMethod(TableLoader.QUICK);
		return matchQuick();
	    }

	    return false;
        }
        
        private boolean matchCangjieHongKong() {
	    mTable.searchCangjie(user_input[0], user_input[1], user_input[2], user_input[3], user_input[4]);

	    for (int count = 0; count < mTable.totalMatch(); count++) {
		matchChar[count] = mTable.getMatchChar(count);
	    }
	    mSelect.updateMatch(matchChar, mTable.totalMatch());

	    return true;
	}

        private boolean matchCangjie() {
	    mTable.searchCangjie(user_input[0], user_input[1], user_input[2], user_input[3], user_input[4]);

	    for (int count = 0; count < mTable.totalMatch(); count++) {
		matchChar[count] = mTable.getMatchChar(count);
	    }
	    mSelect.updateMatch(matchChar, mTable.totalMatch());

	    return true;
	}

        private boolean matchQuick() {
	    mTable.searchCangjie(user_input[0], user_input[1], (char) 0, (char) 0, (char) 0);

	    for (int count = 0; count < mTable.totalMatch(); count++) {
		matchChar[count] = mTable.getMatchChar(count);
	    }
	    mSelect.updateMatch(matchChar, mTable.totalMatch());

	    return true;
	}

	@Override
	public void onPress(int arg0) {
	    if (preferences.getBoolean("vibrate_on", false)) {
		VibratorUtils.getInstance(this).vibrate(preferences.getInt("pref_vibration_duration_settings", 5));
	    }
	    if (preferences.getBoolean("sound_on", false)) {
		float f = (float) preferences.getInt("pref_keypress_sound_volume", 50) / (float) 100.0;
		am.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, f);
	    }
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
        public void onUpdateSelection(final int oldSelStart, final int oldSelEnd,
				      final int newSelStart, final int newSelEnd,
				      final int composingSpanStart, final int composingSpanEnd) {
	    super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd,
				    composingSpanStart, composingSpanEnd);
	    
	    mLastSelectionStart = newSelStart;
	    mLastSelectionEnd = newSelEnd;
	}

	@Override
        public void onStartInputView(EditorInfo editorInfo, boolean restarting) {
	    if (getAndClearOftenUsed()) mTable.clearAllFrequency();
	    sb.setLength(0);

	    mLastSelectionStart = editorInfo.initialSelStart;
	    mLastSelectionEnd = editorInfo.initialSelEnd;

	    imeOptions = editorInfo.imeOptions;

	    if (mActionKey == null) mActionKey = searchKey(10);
	    
	    if (mActionKey != null) {
		if ((editorInfo.imeOptions & EditorInfo.IME_MASK_ACTION) == EditorInfo.IME_ACTION_SEARCH && ((imeOptions & EditorInfo.IME_FLAG_NO_ENTER_ACTION) == 0)) {
		    mActionKey.icon = getResources().getDrawable(R.drawable.sym_keyboard_search);
		} else {
		    mActionKey.icon = getResources().getDrawable(R.drawable.enter_icon);
		}
		mKeyboard.updateKeyboard();
	    }
	}

        @Override
	public void onFinishInput() {
	    super.onFinishInput();
	    if (mTable != null) mTable.saveMatch();
	    clearAllInput();
	}

        @Override
	public void onFinishInputView(boolean input) {
	    super.onFinishInputView(input);
	    if (mTable != null) mTable.saveMatch();
	}

        @Override
	public void onDestroy() {
	    super.onDestroy();
	    if (mTable != null) mTable.saveMatch();
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

        private void updateInputMethod(int inputMethod) {
	    Keyboard.Key methodKey = searchKey(-300);

	    if (methodKey == null)
		return;

	    if (inputMethod == CANGJIE) {
		methodKey.label = getString(R.string.cangjie);
		clearAllInput();
	    } else {
		methodKey.label = getString(R.string.quick);
		clearAllInput();
	    }
	}
    
        public void toggleInputMethod() {

	    if (mInputMethodState == CANGJIE) {
		mInputMethodState = QUICK;
		updateInputMethod(mInputMethodState);
	    } else {
		mInputMethodState = CANGJIE;
		updateInputMethod(mInputMethodState);
	    }

	    mKeyboard.updateKeyboard();
        }

}
