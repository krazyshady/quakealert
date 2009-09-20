package org.jtb.quakealert;

import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class QuakeMapActivity extends MapActivity {
	static QuakeMapActivity mThis;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);

		mThis = this;

		Integer p = savedInstanceState != null ? (Integer) savedInstanceState
				.get("org.jtb.quakealert.quake.position") : null;
		if (p == null) {
			Bundle extras = getIntent().getExtras();
			p = extras != null ? (Integer) extras
					.get("org.jtb.quakealert.quake.position") : null;
		}

		Quake quake = QuakeRefreshService.matchQuakes.get(p);
		MapView mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);

		List<Overlay> mapOverlays = mapView.getOverlays();
		int size = QuakeRefreshService.matchQuakes.size();
		for (int i = size - 1; i != -1; i--) {
			Quake q = QuakeRefreshService.matchQuakes.get(i);

			Drawable drawable = this.getResources().getDrawable(R.drawable.one);
			QuakeOverlay itemizedOverlay = new QuakeOverlay(drawable, q);

			OverlayItem overlayitem = new OverlayItem(q.getGeoPoint(), ""
					+ q.getMagnitude(), "");
			itemizedOverlay.addOverlay(overlayitem);
			mapOverlays.add(itemizedOverlay);
		}

		int zoom;
		if (quake.getMagnitude() < 2) {
			zoom = 11;
		} else if (quake.getMagnitude() < 3) {
			zoom = 10;
		} else if (quake.getMagnitude() < 4) {
			zoom = 9;
		} else if (quake.getMagnitude() < 5) {
			zoom = 8;
		} else if (quake.getMagnitude() < 6) {
			zoom = 7;
		} else {
			zoom = 6;
		}
		mapView.getController().setZoom(zoom);
		mapView.getController().animateTo(quake.getGeoPoint());
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

}
