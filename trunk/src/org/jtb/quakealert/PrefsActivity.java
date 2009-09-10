package org.jtb.quakealert;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceClickListener;

public class PrefsActivity extends PreferenceActivity {
	static final int CHANGED_RESULT = 1;
	static final int UNCHANGED_RESULT = 0;

	static PrefsActivity mThis;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.prefs);

		mThis = this;
		setResult(UNCHANGED_RESULT);

		Preference.OnPreferenceChangeListener changedListener = new Preference.OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				setResult(CHANGED_RESULT);
				return true;
			}
		};

		ListPreference lp = (ListPreference) findPreference("range");
		lp.setOnPreferenceChangeListener(changedListener);
		lp = (ListPreference) findPreference("magnitude");
		lp.setOnPreferenceChangeListener(changedListener);

		Preference.OnPreferenceChangeListener notificationsListener = new Preference.OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				boolean checked = ((Boolean) newValue).booleanValue();
				Intent i = new Intent(mThis, QuakeService.class);
				ListPreference ip = (ListPreference) findPreference("interval");
				if (checked) {
					ip.setEnabled(true);
					startService(i);
				} else {
					ip.setEnabled(false);
					stopService(i);
				}
				return true;
			}
		};

		Preference.OnPreferenceChangeListener intervalListener = new Preference.OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				String is = (String)newValue;
				QuakePrefs qp = new QuakePrefs(mThis);
				qp.setInterval(is);
				QuakeService.mThis.schedule();
				
				return false;
			}
		};
			
		ListPreference ip = (ListPreference) findPreference("interval");
		QuakePrefs qp = new QuakePrefs(this);
		ip.setEnabled(qp.isNotificationsEnabled());
		ip.setOnPreferenceChangeListener(intervalListener);
		
		CheckBoxPreference cbp = (CheckBoxPreference) findPreference("notificationsEnabled");
		cbp.setOnPreferenceChangeListener(notificationsListener);
	}
}
