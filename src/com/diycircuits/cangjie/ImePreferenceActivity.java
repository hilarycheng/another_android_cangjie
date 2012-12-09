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

/**
 * Manages IME preferences. 
 */
public class ImePreferenceActivity extends PreferenceActivity {

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
    String[] entries = new String[info.size() + 1];
    String[] entriesValue = new String[info.size() + 1];
    for (int count = 0; count < info.size(); count++) {
	String label = info.get(count).loadLabel(pm).toString();
	String id = info.get(count).getId();

	entries[count + 1] = label;
	entriesValue[count + 1] = id;
    }
    entries[0] = getString(R.string.no_next_inputmethod);
    entriesValue[0] = "";
    list.setEntries(entries);
    list.setEntryValues(entriesValue);
  }

}
