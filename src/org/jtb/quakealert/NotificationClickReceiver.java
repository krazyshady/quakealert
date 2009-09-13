package org.jtb.quakealert;

import java.util.Collections;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationClickReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		QuakePrefs qp = new QuakePrefs(context);
		qp.clearNewIds();		
		Collections.sort(QuakeRefreshService.matchQuakes, new Quake.ListComparator(null));
	
		Intent listIntent = new Intent(context, ListQuakesActivity.class);
		listIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(listIntent);
	}

}
