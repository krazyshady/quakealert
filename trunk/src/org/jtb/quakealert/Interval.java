package org.jtb.quakealert;

import android.app.AlarmManager;

enum Interval {
	DAY(AlarmManager.INTERVAL_DAY),
	HALF_DAY(AlarmManager.INTERVAL_HALF_DAY),
	HOUR(AlarmManager.INTERVAL_HOUR),
	HALF_HOUR(AlarmManager.INTERVAL_HALF_HOUR),
	FIFTEEN_MINUTES(AlarmManager.INTERVAL_FIFTEEN_MINUTES);
	
	private long value;
	
	private Interval(long value) {
		this.value = value;
	}
	
	public long getValue() {
		return value;
	}
}
