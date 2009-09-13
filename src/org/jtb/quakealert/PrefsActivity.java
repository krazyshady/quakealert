package org.jtb.quakealert;

import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class PrefsActivity extends PreferenceActivity {
	static final int CHANGED_RESULT = 1;
	static final int UNCHANGED_RESULT = 0;

	private PrefsActivity mThis;
	private ListPreference mRangePreference;
	private ListPreference mMagnitudePreference;
	private ListPreference mUnitsPreference;
	private ListPreference mIntervalPreference;
	private CheckBoxPreference mAlertPreference;
	private CheckBoxPreference mVibratePreference;
	private CheckBoxPreference mFlashPreference;
	
	private void setRangeEntries(QuakePrefs qp) {
		if (qp.getUnits().equals("metric")) {
			mRangePreference.setEntries(R.array.range_metric_entries);
		} else {
			mRangePreference.setEntries(R.array.range_us_entries);
		}		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.prefs);

		mThis = this;
		setResult(UNCHANGED_RESULT);

		mRangePreference = (ListPreference) findPreference("range");
		mMagnitudePreference = (ListPreference) findPreference("magnitude");
		mUnitsPreference = (ListPreference) findPreference("units");
		mIntervalPreference = (ListPreference) findPreference("interval");
		mAlertPreference = (CheckBoxPreference) findPreference("notificationAlert");
		mVibratePreference = (CheckBoxPreference) findPreference("notificationVibrate");
		mFlashPreference = (CheckBoxPreference) findPreference("notificationFlash");
		
		QuakePrefs qp = new QuakePrefs(this);

		Preference.OnPreferenceChangeListener changedListener = new Preference.OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				setResult(CHANGED_RESULT);
				return true;
			}
		};

		mRangePreference.setOnPreferenceChangeListener(changedListener);
		setRangeEntries(qp);
		
		mMagnitudePreference.setOnPreferenceChangeListener(changedListener);
		
		Preference.OnPreferenceChangeListener unitsListener = new Preference.OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				String units = (String)newValue;
				QuakePrefs qp = new QuakePrefs(mThis);
				qp.setUnits(units);
				setRangeEntries(qp);
				
				sendBroadcast(new Intent("updateList"));				
				return false;
			}
		};
		
		mUnitsPreference.setOnPreferenceChangeListener(unitsListener);
		
		Preference.OnPreferenceChangeListener notificationsListener = new Preference.OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				boolean checked = ((Boolean) newValue).booleanValue();
				if (checked) {
					mIntervalPreference.setEnabled(true);
					mFlashPreference.setEnabled(true);
					mAlertPreference.setEnabled(true);
					mVibratePreference.setEnabled(true);
					sendBroadcast(new Intent("schedule", null, mThis, QuakeRefreshReceiver.class));
				} else {
					mIntervalPreference.setEnabled(false);
					mFlashPreference.setEnabled(false);
					mAlertPreference.setEnabled(false);
					mVibratePreference.setEnabled(false);
					sendBroadcast(new Intent("cancel", null, mThis, QuakeRefreshReceiver.class));
				}
				return true;
			}
		};

		Preference.OnPreferenceChangeListener intervalListener = new Preference.OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				String is = (String) newValue;
				QuakePrefs qp = new QuakePrefs(mThis);
				qp.setInterval(is);
				sendBroadcast(new Intent("schedule", null, mThis, QuakeRefreshReceiver.class));

				return false;
			}
		};

		ListPreference ip = (ListPreference) findPreference("interval");
		ip.setEnabled(qp.isNotificationsEnabled());
		ip.setOnPreferenceChangeListener(intervalListener);

		CheckBoxPreference cbp = (CheckBoxPreference) findPreference("notificationsEnabled");
		cbp.setOnPreferenceChangeListener(notificationsListener);
	}
}
