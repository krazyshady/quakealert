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

	private boolean getBoolean(String key, boolean def) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		boolean b = prefs.getBoolean(key, def);
		return b;
	}	
	
	public long getInterval() {
		return getLong("interval", 5 * 60 * 1000);
	}
	
	public void setInterval(String is) {
		setString("interval", is);
	}

	public int getRange() {
		return getInt("range", -1) * 1000; 
	}

	public float getMagnitude() {
		return getFloat("magnitude", (float)2.5); 
	}
	
	public boolean isNotificationsEnabled() {
		return getBoolean("notificationsEnabled", true);
	}
}
