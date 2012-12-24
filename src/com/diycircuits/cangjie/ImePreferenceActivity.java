package com.diycircuits.cangjie;

import android.app.AlertDialog;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.ListPreference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.view.inputmethod.InputMethodManager;
import android.view.View;
import android.content.Context;
import java.util.List;
import android.view.inputmethod.*;
import android.content.pm.PackageManager;
import android.util.Log;
import android.media.AudioManager;
import android.content.res.Resources;
import android.content.SharedPreferences;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

/**
 * Manages IME preferences. 
 */
public class ImePreferenceActivity extends PreferenceActivity {

    private TextView mKeypressVibrationDurationSettingsTextView;
    private TextView mKeypressSoundVolumeSettingsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	addPreferencesFromResource(R.xml.pref);

	Preference inputmethod = findPreference(getString(R.string.prefs_next_inputmethod_key));
	if (inputmethod == null) return;

	if (!(inputmethod instanceof ListPreference)) return;

	ListPreference list = (ListPreference) inputmethod;

	InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	PackageManager pm = getPackageManager();
	List<InputMethodInfo> info = im.getEnabledInputMethodList();
	String[] entries = new String[info.size()];
	String[] entriesValue = new String[info.size()];
	int index = 1;
	for (int count = 0; count < info.size(); count++) {
	    String label = info.get(count).loadLabel(pm).toString();
	    String id = info.get(count).getId();

	    if (id.indexOf("com.diycircuits.cangjie") == -1) {
		entries[index] = label;
		entriesValue[index] = id;
		index++;
	    }
	}
	entries[0] = getString(R.string.no_next_inputmethod);
	entriesValue[0] = "";
	list.setEntries(entries);
	list.setEntryValues(entriesValue);

	Preference vibrator_on = findPreference("vibrate_on");
	Preference vibrator_settings = findPreference("pref_vibration_duration_settings");
	Preference sound_on = findPreference("sound_on");
	Preference sound_settings = findPreference("pref_keypress_sound_volume");
        final SharedPreferences sp = getPreferenceManager().getSharedPreferences();

	if (getSystemService(Context.VIBRATOR_SERVICE) == null) {
	    vibrator_on.setEnabled(false);
	    vibrator_settings.setEnabled(false);
	} else {
	    vibrator_settings.setEnabled(vibrator_on.getSharedPreferences().getBoolean("vibrate_on", false));
	    vibrator_settings.setSummary(Integer.toString(sp.getInt("pref_vibration_duration_settings", 5)) + "ms");
	}
	sound_settings.setEnabled(sound_on.getSharedPreferences().getBoolean("sound_on", false));
	sound_settings.setSummary(Integer.toString(sp.getInt("pref_keypress_sound_volume", 50)));
	
	vibrator_on.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
		    if (newValue != null && newValue instanceof Boolean) {
			Preference vibrator_on = findPreference("vibrate_on");
			Preference vibrator_settings = findPreference("pref_vibration_duration_settings");
			vibrator_settings.setEnabled(((Boolean) newValue).booleanValue());
			vibrator_settings.setSummary(Integer.toString(sp.getInt("pref_vibration_duration_settings", 5)) + "ms");
		    }
		    return true;
		}
	    });
    
	vibrator_settings.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
		@Override
		public boolean onPreferenceClick(Preference arg0) {
		    showKeypressVibrationDurationSettingsDialog();
		    return true;
		}
	    });
    
	sound_on.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
		    if (newValue != null && newValue instanceof Boolean) {
			Preference sound_on = findPreference("sound_on");
			Preference sound_settings = findPreference("pref_keypress_sound_volume");
			sound_settings.setEnabled(((Boolean) newValue).booleanValue());
			sound_settings.setSummary(Integer.toString(sp.getInt("pref_keypress_sound_volume", 50)));
		    }
		    return true;
		}
	    });
    
	sound_settings.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
		@Override
		public boolean onPreferenceClick(Preference arg0) {
		    showKeypressSoundVolumeSettingDialog();
		    return true;
		}
	    });
    
    }

    private void showKeypressSoundVolumeSettingDialog() {
        final Context context = this;
        final AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        final SharedPreferences sp = getPreferenceManager().getSharedPreferences();
        final Resources res = context.getResources();
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.prefs_keypress_sound_volume_settings);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int whichButton) {
		    final int volume = Integer.valueOf(mKeypressSoundVolumeSettingsTextView.getText().toString());
		    sp.edit().putInt("pref_keypress_sound_volume", volume).apply();
		    sp.edit().commit();
		    Preference sound_settings = findPreference("pref_keypress_sound_volume");
		    sound_settings.setSummary(Integer.toString(volume));
		}        
	    });       
        builder.setNegativeButton(android.R.string.cancel,  new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int whichButton) {
		    dialog.dismiss();
		}        
	    });
	final View v = LayoutInflater.from(context).inflate(R.layout.sound_effect_volume_dialog, null);

	final int currentVolumeInt = sp.getInt("pref_keypress_sound_volume", 50);
        mKeypressSoundVolumeSettingsTextView =
                (TextView)v.findViewById(R.id.sound_effect_volume_value);
        final SeekBar sb = (SeekBar)v.findViewById(R.id.sound_effect_volume_bar);
        sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
                final int tempVolume = arg1;
                mKeypressSoundVolumeSettingsTextView.setText(String.valueOf(tempVolume));
            }
 
            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
            }
 
            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
                final float tempVolume = ((float)arg0.getProgress()) / 100;
                am.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, tempVolume);
            }
        });
        sb.setProgress(currentVolumeInt);
        mKeypressSoundVolumeSettingsTextView.setText(String.valueOf(currentVolumeInt));
	
	builder.setView(v);    
        builder.create().show();
    }

    private void showKeypressVibrationDurationSettingsDialog() {
	final SharedPreferences sp = getPreferenceManager().getSharedPreferences();
	final Context context = this;
        final Resources res = context.getResources();
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.prefs_keypress_vibration_duration_settings);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int whichButton) {
		    final int ms = Integer.valueOf(mKeypressVibrationDurationSettingsTextView.getText().toString());
		    sp.edit().putInt("pref_vibration_duration_settings", ms).apply();
		    sp.edit().commit();
		    Preference vibrator_settings = findPreference("pref_vibration_duration_settings");
		    vibrator_settings.setSummary(Integer.toString(ms) + "ms");
		}    
	    });      
        builder.setNegativeButton(android.R.string.cancel,  new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int whichButton) {
		    dialog.dismiss();
		}    
	    });      
        final View v = LayoutInflater.from(context).inflate(R.layout.vibration_settings_dialog, null);
	final int currentMs = sp.getInt("pref_vibration_duration_settings", 5);

	mKeypressVibrationDurationSettingsTextView = (TextView)v.findViewById(R.id.vibration_value);
	final SeekBar sb = (SeekBar)v.findViewById(R.id.vibration_settings);                                                                                               
        sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
		@Override
		public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
		    final int tempMs = arg1;
		    mKeypressVibrationDurationSettingsTextView.setText(String.valueOf(tempMs));
		}
         
		@Override
		public void onStartTrackingTouch(SeekBar arg0) {
		}
         
		@Override
		public void onStopTrackingTouch(SeekBar arg0) {
		    final int tempMs = arg0.getProgress();
		    VibratorUtils.getInstance(context).vibrate(tempMs);
		}
	    });
        sb.setProgress(currentMs);
        mKeypressVibrationDurationSettingsTextView.setText(String.valueOf(currentMs));
        builder.setView(v);
        builder.create().show();
    }  
}
