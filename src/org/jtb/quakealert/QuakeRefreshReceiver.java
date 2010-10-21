package org.jtb.quakealert;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

public class QuakeRefreshReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("quakealert", "received intent, action: " + intent.getAction());

		if (intent.getAction().equals("refresh")) {
			Lock.acquire(context);
			context.sendBroadcast(new Intent("showRefreshDialog"));
			context.startService(new Intent("refresh", null, context,
					QuakeRefreshService.class));
		} else if (intent.getAction().equals("schedule")) {
			schedule(context);
		} else if (intent.getAction().equals("cancel")) {
			cancel(context);
		}
	}

	private void schedule(Context context) {
		AlarmManager mgr = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent("refresh", null, context,
				QuakeRefreshReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, i,
				PendingIntent.FLAG_CANCEL_CURRENT);
		mgr.cancel(pi);
		QuakePrefs prefs = new QuakePrefs(context);
		mgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				SystemClock.elapsedRealtime() + prefs.getInterval().getValue(),
				prefs.getInterval().getValue(), pi);
		// mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
		// SystemClock.elapsedRealtime(), 30000, pi);
	}

	private void cancel(Context context) {
		AlarmManager mgr = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent("refresh", null, context,
				QuakeRefreshReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, i,
				PendingIntent.FLAG_CANCEL_CURRENT);
		mgr.cancel(pi);
	}
}