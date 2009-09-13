package org.jtb.quakealert;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

public class WakeLockReceiver extends BroadcastReceiver {
	private static PowerManager.WakeLock wakeLock = null;

	private static PowerManager.WakeLock getLock(Context context, String tag) {
		PowerManager mgr = (PowerManager) context
				.getSystemService(Context.POWER_SERVICE);

		PowerManager.WakeLock wl = mgr.newWakeLock(
				PowerManager.PARTIAL_WAKE_LOCK, tag);
		wl.setReferenceCounted(true);
		return wl;
	}

	private static void acquire(Context context) {
		wakeLock = getLock(context, "org.jtb.quakealert");
		wakeLock.acquire();
		Log.d(WakeLockReceiver.class.getSimpleName(), "wake lock acquired");
	}

	private static void release() {
		if (wakeLock == null) {
			Log.w(WakeLockReceiver.class.getSimpleName(), "release attempted, but wake lock was null");
		} else {
			wakeLock.release();
			Log.d(WakeLockReceiver.class.getSimpleName(), "wake lock released");
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("acquire")) {
			acquire(context);
		} else if (intent.getAction().equals("release")) {
			release();
		}
	}
}
