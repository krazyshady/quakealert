package org.jtb.quakealert;

import java.util.Date;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class QuakeNotifier {
	private static final int ALERT_ID = 0;

	private Context context;
	private QuakePrefs quakePrefs;

	public QuakeNotifier(Context context) {
		this.context = context;
		quakePrefs = new QuakePrefs(context);
	}

	public void alert() {
		int newQuakeCount = 0;
		Date lastAckDate = quakePrefs.getLastAckDate();
		
		for (Quake q: QuakeService.matchQuakes) {
			if (q.getDate().compareTo(lastAckDate) > 0) {
				newQuakeCount++;
			}
		}
		
		if (newQuakeCount == 0) {
			return;
		}
		
		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		int icon = R.drawable.warning;
		CharSequence tickerText = "Quake Alert!";
		long when = System.currentTimeMillis();

		Notification notification = new Notification(icon, tickerText, when);
		if (quakePrefs.isNotificationAlert()) {
			notification.defaults |= Notification.DEFAULT_SOUND;
		}
		if (quakePrefs.isNotificationFlash()) {
			notification.defaults |= Notification.DEFAULT_LIGHTS;
		}
		if (quakePrefs.isNotificationVibrate()) {
			notification.defaults |= Notification.DEFAULT_VIBRATE;
		}
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		CharSequence contentTitle = "Quake Alert!";
		CharSequence contentText = newQuakeCount + " M"
				+ quakePrefs.getMagnitude() + "+ new quakes";
		if (quakePrefs.getRange() > 0) {
			Distance d = new Distance(quakePrefs.getRange());
			contentText = contentText + " within " + d.toString(quakePrefs);
		}

		Intent notificationIntent = new Intent(context,
				NotificationClickReceiver.class);
		PendingIntent contentIntent = PendingIntent.getBroadcast(context, 0,
				notificationIntent, 0);

		notification.setLatestEventInfo(context, contentTitle, contentText,
				contentIntent);
		nm.notify(ALERT_ID, notification);
	}
}
