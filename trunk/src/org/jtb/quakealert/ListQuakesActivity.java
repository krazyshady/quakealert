package org.jtb.quakealert;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ListQuakesActivity extends Activity {
	private static final int REFRESH_MENU = 0;
	private static final int PREFS_MENU = 1;

	private static final int PREFS_REQUEST = 0;

	static final int REFRESH_DIALOG = 0;

	private AlertDialog mServiceWarnDialog;
	private ProgressDialog mRefreshDialog;

	private ListView mList;
	private LinearLayout mNoQuakesLayout;
	private ListReceiver listUpdateReceiver;
	private ListQuakesActivity mThis;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);

		mThis = this;
		listUpdateReceiver = new ListReceiver(this);

		upgrade();

		mNoQuakesLayout = (LinearLayout) findViewById(R.id.no_quakes_layout);
		mList = (ListView) findViewById(R.id.list);
		mList.setOnItemClickListener(new OnItemClickListener() {
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
			sendBroadcast(new Intent("schedule", null, this,
					QuakeRefreshReceiver.class));
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

		updateList();
		
		registerReceiver(listUpdateReceiver, new IntentFilter("updateList"));
		registerReceiver(listUpdateReceiver, new IntentFilter("showRefreshDialog"));
		registerReceiver(listUpdateReceiver, new IntentFilter("dismissRefreshDialog"));	
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
		Log.d(getClass().getSimpleName(), "updating list");
		
		if (QuakeRefreshService.matchQuakes != null
				&& QuakeRefreshService.matchQuakes.size() > 0) {
			QuakeAdapter qa = new QuakeAdapter(this, QuakeRefreshService.matchQuakes,
					getLocation());
			mNoQuakesLayout.setVisibility(View.GONE);
			mList.setVisibility(View.VISIBLE);
			mList.setAdapter(qa);
		} else {
			mNoQuakesLayout.setVisibility(View.VISIBLE);
			mList.setVisibility(View.GONE);
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
			sendBroadcast(new Intent("refresh", null, this, QuakeRefreshReceiver.class));
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
				sendBroadcast(new Intent("receiverRefresh", null, this,
						QuakeRefreshReceiver.class));
			}
			break;
		}
	}

	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case REFRESH_DIALOG:
			mRefreshDialog = new ProgressDialog(this);
			mRefreshDialog.setMessage("Refreshing, please wait.");
			mRefreshDialog.setIndeterminate(true);
			mRefreshDialog.setCancelable(false);
			return mRefreshDialog;
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
