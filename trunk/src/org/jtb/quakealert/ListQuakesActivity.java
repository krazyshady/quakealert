package org.jtb.quakealert;

import java.util.ArrayList;
import java.util.HashSet;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class ListQuakesActivity extends Activity {
	private static final int REFRESH_MENU = 0;
	private static final int PREFS_MENU = 1;

	private static final int PREFS_REQUEST = 0;

	private AlertDialog mServiceWarnDialog;
	private ListView mListView;
	private ListUpdateReceiver listUpdateReceiver;
	private ListQuakesActivity mThis;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);

		mThis = this;
		listUpdateReceiver = new ListUpdateReceiver(this);
		
		upgrade();

		mListView = (ListView) findViewById(R.id.list);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				Intent i = new Intent(mThis, QuakeMapActivity.class);
				i.putExtra("org.jtb.quakealert.quake.position", new Integer(
						position));
				mThis.startActivity(i);
			}
		});

		QuakePrefs qp = new QuakePrefs(this);
		if (qp.isNotificationsEnabled()) {
			sendBroadcast(new Intent("schedule", null, this, QuakeReceiver.class));
		}

		WarnDialog.Builder builder = new WarnDialog.Builder(this,
				"serviceWarn", R.string.service_warn);
		if (builder.isWarn()) {
			mServiceWarnDialog = builder.create();
			mServiceWarnDialog.show();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		unregisterReceiver(listUpdateReceiver);		
	}

	@Override
	public void onResume() {
		super.onResume();
		registerReceiver(listUpdateReceiver, new IntentFilter("updateList"));
	}
	
	private Location getLocation() {
		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		String name = lm.getBestProvider(new Criteria(), true);
		if (name == null) {
			// TODO: error dialog an exit (this.finish())?
			Log.e(getClass().getSimpleName(),
					"no best location provider returned");
		}
		// LocationProvider lp = lm.getProvider(name);
		Location l = lm.getLastKnownLocation(name);

		return l;
	}

	public void updateList() {
		if (QuakeReceiver.matchQuakes != null) {
			QuakeAdapter qa = new QuakeAdapter(this, QuakeReceiver.matchQuakes,
					getLocation());
			mListView.setAdapter(qa);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		menu.add(0, REFRESH_MENU, 0, R.string.refresh_menu).setIcon(
				R.drawable.refresh);
		menu.add(0, PREFS_MENU, 1, R.string.prefs_menu).setIcon(
				R.drawable.prefs);
		return result;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case REFRESH_MENU:
			sendBroadcast(new Intent("refresh", null, this, QuakeReceiver.class));
			return true;
		case PREFS_MENU:
			Intent prefsActivity = new Intent(getBaseContext(),
					PrefsActivity.class);
			startActivityForResult(prefsActivity, PREFS_REQUEST);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case PREFS_REQUEST:
			if (resultCode == PrefsActivity.CHANGED_RESULT) {
				sendBroadcast(new Intent("refresh", null, this, QuakeReceiver.class));
			}
			break;
		}
	}

	protected Dialog onCreateDialog(int id) {
		switch (id) {
		}
		return null;
	}

	private void upgrade() {
		PackageManager manager = getPackageManager();
		PackageInfo info;
		try {
			info = manager.getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			Log.e(getClass().getSimpleName(), "could not get version", e);
			return;
		}
		
		QuakePrefs qp = new QuakePrefs(this);
		if (!qp.isUpgradedTo(4)) {
			// upgrade range from km to m
			int km = qp.getRange();
			if (km > 0) {
				qp.setRange(km * 1000);
			}

		}
		
		qp.setUpgradedTo(info.versionCode);		
	}
}
