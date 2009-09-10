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

		Quake quake = QuakeService.matchQuakes.get(p);
		MapView mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);

		List<Overlay> mapOverlays = mapView.getOverlays();
		for (int i = QuakeService.matchQuakes.size() - 1; i != -1; i--) {
			Quake q = QuakeService.matchQuakes.get(i);

			Drawable drawable = this.getResources().getDrawable(R.drawable.one);
			QuakeOverlay itemizedOverlay = new QuakeOverlay(drawable, q);

			OverlayItem overlayitem = new OverlayItem(q.getGeoPoint(), ""
					+ q.getMagnitude(), "");
			itemizedOverlay.addOverlay(overlayitem);
			mapOverlays.add(itemizedOverlay);
		}

		mapView.getController().setZoom(10);
		mapView.getController().animateTo(quake.getGeoPoint());
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

}
