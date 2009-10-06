package org.jtb.quakealert;

import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class QuakeMapActivity extends MapActivity {
	static QuakeMapActivity mThis;

	private MapView mMapView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);

		mThis = this;
		mMapView = (MapView)findViewById(R.id.mapview);
		
		Integer p = savedInstanceState != null ? (Integer) savedInstanceState
				.get("org.jtb.quakealert.quake.position") : null;
		if (p == null) {
			Bundle extras = getIntent().getExtras();
			p = extras != null ? (Integer) extras
					.get("org.jtb.quakealert.quake.position") : null;
		}

		Quake quake = QuakeRefreshService.matchQuakes.get(p);
		mMapView.setBuiltInZoomControls(true);

		List<Overlay> mapOverlays = mMapView.getOverlays();
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

		int zoom = getZoom(QuakeRefreshService.matchQuakes, quake);
		mMapView.getController().animateTo(quake.getGeoPoint());
		mMapView.getController().setZoom(zoom);
	}

	private int getZoom(List<Quake> quakeList, Quake center) {
		int latMax = quakeList.get(0).getLatitudeE6();
		int latMin = quakeList.get(0).getLatitudeE6();
		int lonMax = quakeList.get(0).getLongitudeE6();
		int lonMin = quakeList.get(0).getLongitudeE6();
		int latC = center.getLatitudeE6();
		int lonC = center.getLongitudeE6();
		
		for (Quake q: quakeList) {
			if (q.getLatitudeE6() > latMax) {
				latMax = q.getLatitudeE6();
			}
			if (q.getLatitudeE6() < latMin) {
				latMin = q.getLatitudeE6();
			}
			if (q.getLongitudeE6() > lonMax) {
				lonMax = q.getLongitudeE6();
			}
			if (q.getLongitudeE6() < lonMin) {
				lonMin = q.getLongitudeE6();
			}
		}
		
		int latM = (latMax+latMin) / 2;
		int lonM = (lonMax+lonMin) / 2;

		if (latC-latM > 0) {
			latMax += latC-latM;
		} else {
			latMin += latC-latM;
		}
		if (lonC-lonM > 0) {
			lonMax += lonC-lonM;
		} else {
			lonMin += latC-latM;
		}
		
		mMapView.getController().zoomToSpan(latMax-latMin, lonMax - lonMin);	
		int zoom = mMapView.getZoomLevel();
		Log.d(getClass().getSimpleName(), "zoom=" + zoom);
		if (zoom < 16) {
			//mMapView.getController().setZoom(12);
			zoom = 16;
		}
		
		return zoom;
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

}
