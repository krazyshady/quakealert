package org.jtb.quakealert;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class QuakePrefs {
	private Context context = null;
	
	public QuakePrefs(Context context) {
		this.context = context;
	}
	
	private String getString(String key, String def) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		String s = prefs.getString(key, def);
		return s;
	}	

	private int getInt(String key, int def) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		int i = Integer.parseInt(prefs.getString(key, Integer.toString(def)));
		return i;
	}	

	private float getFloat(String key, float def) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		float f = Float.parseFloat(prefs.getString(key, Float.toString(def)));
		return f;
	}	

	private long getLong(String key, long def) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		long l = Long.parseLong(prefs.getString(key, Long.toString(def)));
		return l;
	}	

	private void setString(String key, String val) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor e = prefs.edit();
		e.putString(key, val);
		e.commit();
	}	

	private void setBoolean(String key, boolean val) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor e = prefs.edit();
		e.putBoolean(key, val);
		e.commit();
	}	

	private void setInt(String key, int val) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor e = prefs.edit();
		e.putString(key, Integer.toString(val));
		e.commit();
	}	

	private void setLong(String key, long val) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor e = prefs.edit();
		e.putString(key, Long.toString(val));
		e.commit();
	}	

	private boolean getBoolean(String key, boolean def) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		boolean b = prefs.getBoolean(key, def);
		return b;
	}	
	
	public boolean isNotificationFlash() {
		return getBoolean("notificationFlash", true);
	}

	public boolean isNotificationAlert() {
		return getBoolean("notificationAlert", false);
	}

	public boolean isNotificationVibrate() {
		return getBoolean("notificationVibrate", false);
	}
	
	public boolean isWarn(String warnId) {
		return getBoolean(warnId, true);
	}
	
	public void setWarn(String warnId, boolean warn) {
		setBoolean(warnId, warn);
	}
	
	public long getInterval() {
		return getLong("interval", 5 * 60 * 1000);
	}
	
	public void setInterval(String is) {
		setString("interval", is);
	}

	public int getRange() {
		return getInt("range", -1); 
	}

	public void setRange(int meters) {
		setInt("range", meters);
	}
	
	public float getMagnitude() {
		return getFloat("magnitude", (float)2.5); 
	}

	public boolean isNotificationsEnabled() {
		return getBoolean("notificationsEnabled", true);
	}
	
	public String getUnits() {
		return getString("units", "metric");
	}
	
	public void setUnits(String units) {
		setString("units", units);
	}
	
	public boolean isUpgradedTo(int version) {
		int upgradedTo = getInt("upgradedTo", 0);
		if (upgradedTo >= version) {
			return true;
		}
		return false;
	}
	
	public void setUpgradedTo(int version) {
		setInt("upgradedTo", version);
	}
}
