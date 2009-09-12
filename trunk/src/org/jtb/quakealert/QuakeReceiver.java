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
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

public class QuakeReceiver extends BroadcastReceiver {
	private static int MAX_QUAKES = 1000;
	private static final Quakes quakes = new Quakes();

	static ArrayList<Quake> matchQuakes = new ArrayList<Quake>();

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(QuakeReceiver.class.getSimpleName(), "received intent, action: " + intent.getAction());

		if (intent.getAction().equals("refresh")) {
			refresh(context);
		} else if (intent.getAction().equals("schedule")) {
			schedule(context);
		} else if (intent.getAction().equals("cancel")) {
			cancel(context);
		}
	}

	private static void refresh(Context context) {
		Log.d(QuakeReceiver.class.getSimpleName(), "refreshing");
		quakes.update();
		ArrayList<Quake> quakeList = quakes.get();
		if (quakeList == null) {
			return;
		}

		QuakePrefs quakePrefs = new QuakePrefs(context);
		HashSet<String> newIds = quakePrefs.getNewIds();
		HashSet<String> matchIds = quakePrefs.getMatchIds();

		matchQuakes = getQuakeMatches(context, quakeList);

		int mqsSize = matchQuakes.size();
		for (int i = 0; i < mqsSize; i++) {
			Quake q = matchQuakes.get(i);
			if (!matchIds.contains(q.getId())) {
				newIds.add(q.getId());
			}
		}
		Collections.sort(matchQuakes, new Quake.ListComparator(newIds));
		if (mqsSize > MAX_QUAKES) {
			matchQuakes = new ArrayList<Quake>(matchQuakes.subList(0,
					MAX_QUAKES));
		}

		quakePrefs.setMatchIds(matchQuakes);
		newIds.retainAll(quakePrefs.getMatchIds());
		quakePrefs.setNewIds(newIds);

		context.sendBroadcast(new Intent("updateList"));

		if (quakePrefs.isNotificationsEnabled()) {
			QuakeNotifier quakeNotifier = new QuakeNotifier(context);
			quakeNotifier.alert();
		}
	}

	private static Location getLocation(Context context) {
		LocationManager lm = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		String name = lm.getBestProvider(new Criteria(), true);
		if (name == null) {
			// TODO: error dialog an exit (this.finish())?
			Log.e(QuakeReceiver.class.getSimpleName(),
					"no best location provider returned");
		}
		// LocationProvider lp = lm.getProvider(name);
		Location l = lm.getLastKnownLocation(name);

		return l;
	}

	private static ArrayList<Quake> getQuakeMatches(Context context,
			ArrayList<Quake> quakeList) {
		QuakePrefs prefs = new QuakePrefs(context);

		if (quakeList == null || quakeList.size() == 0) {
			return null;
		}

		int range = prefs.getRange();
		float magnitude = prefs.getMagnitude();
		Location location = getLocation(context);

		ArrayList<Quake> matchQuakes = new ArrayList<Quake>();
		int quakeListSize = quakeList.size();
		for (int i = 0; i < quakeListSize; i++) {
			Quake quake = quakeList.get(i);
			if (quake.matches(magnitude, range, location)) {
				matchQuakes.add(quake);
			}
		}

		return matchQuakes;
	}

	private void schedule(Context context) {
		AlarmManager mgr = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent("refresh", null, context, QuakeReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
		mgr.cancel(pi);
		QuakePrefs prefs = new QuakePrefs(context);
		mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				SystemClock.elapsedRealtime(), prefs.getInterval(), pi);
	}

	private void cancel(Context context) {
		AlarmManager mgr = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent("refresh", null, context, QuakeReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
		mgr.cancel(pi);
	}
}
