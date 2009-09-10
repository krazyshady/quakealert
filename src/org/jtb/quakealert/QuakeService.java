package org.jtb.quakealert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class QuakeService extends Service {
	private static int MAX_QUAKES = 1000;

	static QuakeService mThis = null;
	static Quakes quakes = new Quakes();
	static List<Quake> matchQuakes = new ArrayList<Quake>();
	static List<Quake> newQuakes = new ArrayList<Quake>();

	private Timer mTimer;

	public QuakeService() {
		// nothing
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	public static void refresh(Context context) {
		Log.d(QuakeService.class.getSimpleName(), "refreshing");
		quakes.update();

		List<Quake> quakeList = quakes.get();
		List<Quake> mqs = getQuakeMatches(context, quakeList);
		Collections.sort(mqs, Quake.DATE_COMPARATOR);
		mqs = mqs.subList(0, Math.min(mqs.size(), MAX_QUAKES));

		List<Quake> nqs = new ArrayList<Quake>(newQuakes);
		for (Quake quakeMatch : mqs) {
			if (!matchQuakes.contains(quakeMatch)) {
				nqs.add(quakeMatch);
			}
		}

		if (!matchQuakes.equals(mqs)) {
			matchQuakes = mqs;
			ListQuakesActivity.mHandler.sendMessage(Message.obtain(
					ListQuakesActivity.mHandler,
					ListQuakesActivity.UPDATE_LIST_WHAT));
		}

		if (!newQuakes.equals(nqs)) {
			newQuakes = nqs;
			QuakePrefs qp = new QuakePrefs(context);
			if (qp.isNotificationsEnabled()) {
				QuakeNotifier quakeNotifier = new QuakeNotifier(context);
				quakeNotifier.alert();
			}
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();

		mThis = this;

		Log.d(getClass().getSimpleName(), "service started");
		schedule();
	}

	public void schedule() {
		if (mTimer != null) {
			mTimer.cancel();
		}
		QuakePrefs qp = new QuakePrefs(this);
		mTimer = new Timer();
		mTimer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				Log.d(QuakeService.class.getSimpleName(), "service running");
				refresh(mThis);
			}
		}, 0, qp.getInterval());		
	}
	
	private static Location getLocation(Context context) {
		LocationManager lm = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		String name = lm.getBestProvider(new Criteria(), true);
		if (name == null) {
			// TODO: error dialog an exit (this.finish())?
			Log.e(QuakeService.class.getSimpleName(),
					"no best location provider returned");
		}
		// LocationProvider lp = lm.getProvider(name);
		Location l = lm.getLastKnownLocation(name);

		return l;
	}

	private static List<Quake> getQuakeMatches(Context context,
			List<Quake> quakeList) {
		QuakePrefs prefs = new QuakePrefs(context);

		if (quakeList == null || quakeList.size() == 0) {
			return null;
		}

		int range = prefs.getRange();
		float magnitude = prefs.getMagnitude();
		Location location = getLocation(context);

		List<Quake> matchQuakes = new ArrayList<Quake>();
		for (Quake quake : quakeList) {
			if (quake.matches(magnitude, range, location)) {
				matchQuakes.add(quake);
			}
		}

		return matchQuakes;
	}

	@Override
	public void onDestroy() {
		Log.d(getClass().getSimpleName(), "service stopped");
		mTimer.cancel();
	}

}
