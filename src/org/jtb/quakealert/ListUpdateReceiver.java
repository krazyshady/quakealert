package org.jtb.quakealert;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ListUpdateReceiver extends BroadcastReceiver {
	private ListQuakesActivity activity;
	
	public ListUpdateReceiver(ListQuakesActivity activity) {
		this.activity = activity;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		activity.updateList();
	}
}
