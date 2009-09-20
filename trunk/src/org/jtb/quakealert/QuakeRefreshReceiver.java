package org.jtb.quakealert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;

public class QuakeRefreshReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(getClass().getSimpleName(), "received intent, action: " + intent.getAction());
		
		if (intent.getAction().equals("refresh")) {
			context.sendBroadcast(new Intent("acquire", null, context, WakeLockReceiver.class));
			context.sendBroadcast(new Intent("showRefreshDialog"));
			context.startService(new Intent("refresh", null, context, QuakeRefreshService.class));
		} else if (intent.getAction().equals("schedule")) {
			schedule(context);
		} else if (intent.getAction().equals("cancel")) {
			cancel(context);
		}
	}

	private void schedule(Context context) {
		AlarmManager mgr = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent("refresh", null, context, QuakeRefreshReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
		mgr.cancel(pi);
		QuakePrefs prefs = new QuakePrefs(context);
		mgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				SystemClock.elapsedRealtime()+ prefs.getInterval().getValue(), prefs.getInterval().getValue(), pi);
	}

	private void cancel(Context context) {
		AlarmManager mgr = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent("refresh", null, context, QuakeRefreshReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
		mgr.cancel(pi);
	}	
}
