package org.jtb.quakealert;

import java.util.ArrayList;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

public class QuakeNotifier {
	private static final int ALERT_ID = 0;
	
	private Context context;
	private QuakePrefs quakePrefs;
	
	public QuakeNotifier(Context context) {
		this.context = context;
		quakePrefs = new QuakePrefs(context);
	}
	
	public void alert() {
		if (QuakeService.newQuakes == null || QuakeService.newQuakes.size() == 0) {
			return;
		}
		
		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		int icon = R.drawable.warning;
		CharSequence tickerText = "Quake Alert!";
		long when = System.currentTimeMillis();
		Notification notification = new Notification(icon, tickerText, when);
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		CharSequence contentTitle = "Quake Alert!";
		CharSequence contentText = QuakeService.newQuakes.size() + " M" + quakePrefs.getMagnitude() + "+ quakes";
		if (quakePrefs.getRange() > 0) { 
			contentText = contentText + " within " + (int)(quakePrefs.getRange() / 1000) + "km";
		}
		
		Intent notificationIntent = new Intent(context, NotificationClickReceiver.class);
		PendingIntent contentIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, 0);

		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		nm.notify(ALERT_ID, notification);
	}
}
