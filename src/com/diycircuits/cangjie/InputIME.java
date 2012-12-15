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
import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.*;
import android.graphics.*;
import android.util.Log;
import android.view.inputmethod.*;
import android.content.pm.PackageManager;
import android.content.pm.ApplicationInfo;
import android.media.AudioManager;

public class InputIME extends InputMethodService implements KeyboardView.OnKeyboardActionListener, CandidateSelect.CandidateListener {

        private final static int CANGJIE    = 0;
        private final static int QUICK      = 1;
    
	private SoftKeyboardView mKeyboard = null;
	private CandidateView mCandidate = null;
	private CandidateSelect mSelect = null;
        private LinearLayout mLinear = null;
        private int numberOfKey = 0;
        private StringBuffer sb = new StringBuffer();
        private char[] single  = new char[26];
        private char[][] cangjie = new char[13167][6];
        private char[][] cangjie_hk = new char[17578][6];
    
        private char[][] quick = new char[21529][4];
        private int[] quick_idx = new int[21529];

        private int[] cangjie_char_idx = new int[26];
        private int[] cangjie_hk_char_idx = new int[26];
        private int[] quick_char_idx = new int[26];
        private char[] user_input = new char[5];
        private int totalMatch = 0;
        private int[] matchCharIdx = new int[21529];
        private char matchChar[] = new char[21529];
        private StringBuffer commit = new StringBuffer();
        private int imeOptions = 0;
        private int mInputMethodState = CANGJIE;
        private Paint mPaint = null;
        private SharedPreferences preferences;
        private MostUsed mUsed = new MostUsed();
        private TableLoader mTable = null;
        private AudioManager am = null; 

	@Override
	public View onCreateInputView() {

	        ApplicationInfo appInfo = getApplicationInfo();
		Log.i("Cangjie", "App Info " + appInfo.dataDir);
	    
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
		loadCangjieTable();
		loadCangjieHKTable();
		// loadQuickTable();

		for (int count = 0; count < 5; count++) {
		    user_input[count] = 0;
		}
		for (int count = 0; count < matchCharIdx.length; count++) {
		    matchCharIdx[count] = 0;
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

        private void loadCangjieTable() {
	    try {
		InputStream is = getResources().openRawResource(R.raw.cj);
		InputStreamReader input = new InputStreamReader(is, "UTF-8");
		BufferedReader reader = new BufferedReader(input);
		String str = null;
		int count = 0, index = 0;
		char c = 'a';

		count = 0;
		do {
		    str = reader.readLine();
		    if (str == null)
			break;
		    index = str.indexOf('\t');
		    if (index > 0) {
			str.getChars(0, index, cangjie[count], 0);
			str.getChars(index + 1, index + 2, cangjie[count], 5);
			if (cangjie[count][0] == c) {
			    cangjie_char_idx[c - 'a'] = count;
			    c = (char) (c + 1);
			}

			if (Character.isLetter(cangjie[count][5])) {
			    count++;
			} else {
			    for (int column = 0; column < 6; column++) cangjie[count][column] = 0;
			}
		    }
		} while (str != null && count < cangjie.length);
		    
		reader.close();

	    } catch (Exception ex) {
		ex.printStackTrace();
	    }
        }
    
        private void loadCangjieHKTable() {
	    try {
		InputStream is = getResources().openRawResource(R.raw.cj_hk);
		InputStreamReader input = new InputStreamReader(is, "UTF-8");
		BufferedReader reader = new BufferedReader(input);
		String str = null;
		int count = 0, index = 0;
		char c = 'a';

		count = 0;
		do {
		    str = reader.readLine();
		    if (str == null)
			break;
		    index = str.indexOf('\t');
		    if (index > 0) {
			str.getChars(0, index, cangjie_hk[count], 0);
			str.getChars(index + 1, index + 2, cangjie_hk[count], 5);
			if (cangjie_hk[count][0] == c) {
			    cangjie_hk_char_idx[c - 'a'] = count;
			    c = (char) (c + 1);
			}

			if (Character.isLetter(cangjie_hk[count][5])) {
			    count++;
			} else {
			    for (int column = 0; column < 6; column++) cangjie_hk[count][column] = 0;
			}
		    }
		} while (str != null && count < cangjie_hk.length);
		    
		reader.close();

	    } catch (Exception ex) {
		ex.printStackTrace();
	    }
        }
    
        private void loadQuickTable() {
	    try {
		InputStream is = getResources().openRawResource(R.raw.quick);
		InputStreamReader input = new InputStreamReader(is, "UTF-8");
		BufferedReader reader = new BufferedReader(input);
		String str = null;
		int count = 0, index = 0;
		char c = 'a';
		count = 0;
		do {
		    str = reader.readLine();
		    if (str == null)
			break;
		    index = str.indexOf('\t');
		    if (index < 0) index = str.indexOf(' ');
		    if (index > 0) {
			str.getChars(0, index, quick[count], 0);
			str.getChars(index + 1, index + 2, quick[count], 2);
			if (quick[count][0] == c) {
			    quick_char_idx[c - 'a'] = count;
			    c = (char) (c + 1);
			}
			if (Character.isLetter(quick[count][2])) {
			    count++;
			    quick[count][3] = 0;
			} else {
			    quick[count][0] = 0;
			    quick[count][1] = 0;
			    quick[count][2] = 0;
			    quick[count][3] = 0;
			}
		    }
		} while (str != null && count < quick.length);
		    
		reader.close();

	    } catch (Exception ex) {
		ex.printStackTrace();
	    }
        }
    
        public void characterSelected(char c, int idx) {
	    if (idx >= 0) mTable.updateFrequencyQuick(c);
	    mTable.reset();
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
	    commit.setLength(0);
	    getCurrentInputConnection().setComposingText("", 1);
	    sb.setLength(0);
	    for (int cc = 0; cc < user_input.length; cc++) {
		user_input[cc] = 0;
	    }
	    mSelect.updateMatch(null, 0);
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
			    im.switchToNextInputMethod(token, false);
			else {
			    try {
				im.setInputMethod(token, id);
			    } catch (java.lang.IllegalArgumentException ex) {
				Toast.makeText(this, R.string.please_select_next_inputmethod_again, Toast.LENGTH_LONG).show();
			    }
			}
		} else if (primaryKey == 5) {
		    if (sb.length() > 1 && sb.length() <= keyLen) {
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
			sb.append(single[primaryKey - 'a']);
			getCurrentInputConnection().setComposingText(sb.toString(), 1);
			match();
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
    
        private boolean match() {
	    if (mInputMethodState == CANGJIE) {
		if (isHongKongCharEnabled()) 
		    return matchCangjieHongKong();
		else
		    return matchCangjie();
	    }
	    
	    if (mInputMethodState == QUICK)
		return matchQuick();

	    return false;
        }
        
        private boolean matchCangjieHongKong() {
	    int i = cangjie_hk_char_idx[user_input[0] - 'a'];
	    int j = 0;
	    
	    if (user_input[0] == 'z') j = cangjie_hk.length;
	    else j = cangjie_hk_char_idx[user_input[0] - 'a' + 1];

	    totalMatch = 0;
	    for (int c = i; c < j; c++) {
		if (sb.length() == 1) {
		    matchChar[totalMatch] = cangjie_hk[c][5];
		    matchCharIdx[totalMatch] = c;
		    totalMatch++;
		} else {
		    int l = 1;
		    for (int k = 1; k < 5; k++) {
			if (user_input[k] == cangjie_hk[c][k] && user_input[k] != 0) {
			    l++;
			}
		    }
		    if (l == 5 || user_input[l] == 0) {
		        matchChar[totalMatch] = cangjie_hk[c][5];
			matchCharIdx[totalMatch] = c;
			totalMatch++;
		    }
		}
	    }

	    mSelect.updateMatch(matchChar, totalMatch);

	    return true;
	}

        private boolean matchCangjie() {
	    int i = cangjie_char_idx[user_input[0] - 'a'];
	    int j = 0;
	    
	    if (user_input[0] == 'z') j = cangjie.length;
	    else j = cangjie_char_idx[user_input[0] - 'a' + 1];

	    totalMatch = 0;
	    for (int c = i; c < j; c++) {
		if (sb.length() == 1) {
		    matchChar[totalMatch] = cangjie[c][5];
		    matchCharIdx[totalMatch] = c;
		    totalMatch++;
		} else {
		    int l = 1;
		    for (int k = 1; k < 5; k++) {
			if (user_input[k] == cangjie[c][k] && user_input[k] != 0) {
			    l++;
			}
		    }
		    if (l == 5 || user_input[l] == 0) {
		        matchChar[totalMatch] = cangjie[c][5];
			matchCharIdx[totalMatch] = c;
			totalMatch++;
		    }
		}
	    }

	    mSelect.updateMatch(matchChar, totalMatch);

	    return true;
	}

        private boolean matchQuick() {
	    mTable.searchQuick(user_input[0], user_input[1]);

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
        public void onStartInputView (EditorInfo info, boolean restarting) {
	    if (getAndClearOftenUsed()) mTable.clearAllFrequency();
	    sb.setLength(0);
	    getCurrentInputConnection().setComposingText("", 1);

	    setCandidatesViewShown(true);

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

        @Override
	public void onFinishInput() {
	    super.onFinishInput();
	    if (mTable != null) mTable.saveMatch();
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

	@Override
	public void onDisplayCompletions(CompletionInfo[] completions) {
           Log.i("Cangjie", " on Display Completions ");
           if (completions == null) return;
           for (int count = 0; count < completions.length; count++) {
             Log.i("Cangjie", " on Display Completions " + completions[count].getText().toString());
           }
   	} 
}
