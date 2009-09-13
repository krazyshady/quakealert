package org.jtb.quakealert;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ListReceiver extends BroadcastReceiver {
	private ListQuakesActivity activity;

	public ListReceiver(ListQuakesActivity activity) {
		this.activity = activity;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("updateList")) {
			activity.updateList();
		} else if (intent.getAction().equals("showRefreshDialog")) {
			activity.showDialog(ListQuakesActivity.REFRESH_DIALOG);
		} else if (intent.getAction().equals("dismissRefreshDialog")) {
			activity.dismissDialog(ListQuakesActivity.REFRESH_DIALOG);
		}
	}
}
