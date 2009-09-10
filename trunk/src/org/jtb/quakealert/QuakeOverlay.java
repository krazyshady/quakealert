package org.jtb.quakealert;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

public class QuakeOverlay extends ItemizedOverlay {
	private static final int TEXTSIZE = 12;
	private static final int TEXTLINES = 2;
	private static final int PADDING = 2;

	private List<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private Quake quake;

	public QuakeOverlay(Drawable defaultMarker, Quake quake) {
		super(boundCenterBottom(defaultMarker));
		this.quake = quake;

	}

	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}

	public void addOverlay(OverlayItem overlay) {
		mOverlays.add(overlay);
		populate();
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		Projection projection = mapView.getProjection();
		Point point = new Point();
		projection.toPixels(quake.getGeoPoint(), point);

		Paint text = new Paint();
		text.setAntiAlias(true);
		text.setColor(Color.BLACK);
		text.setTextSize(TEXTSIZE);
		text.setFakeBoldText(true);
		//text.setTypeface(Typeface.MONOSPACE);

		String s1 = "M" + quake.getMagnitude();
		String s2 = quake.getShortDateString();

		float rw = Math.max(text.measureText(s1), text.measureText(s2));

		// background
		Paint bg = new Paint();
		bg.setColor(quake.getColor());
		Paint bgOutline = new Paint();
		bgOutline.setColor(Color.BLACK);
		Paint bgGlow = new Paint();
		bgGlow.setColor(Color.WHITE);
		
		int radius = ((int)quake.getMagnitude())*2;
		
		RectF rect = new RectF();
		float rx1 = point.x + radius + PADDING * 2;
		float ry1 = point.y - (TEXTSIZE * TEXTLINES) / 2 - PADDING;
		float rx2 = rx1 + rw + PADDING * 2;
		float ry2 = ry1 + TEXTSIZE * TEXTLINES + PADDING * 2;
		rect.set(rx1, ry1, rx2, ry2);

		RectF rectOutline = new RectF();
		float rox1 = rx1-1;
		float roy1 = ry1-1;
		float rox2 = rx2+1;
		float roy2 = ry2+1;
		rectOutline.set(rox1, roy1, rox2, roy2);

		RectF rectGlow = new RectF();
		float rgx1 = rx1-2;
		float rgy1 = ry1-2;
		float rgx2 = rx2+2;
		float rgy2 = ry2+2;
		rectGlow.set(rgx1, rgy1, rgx2, rgy2);

		// the circle to mark the spot
		Paint circle = new Paint();
		circle.setColor(quake.getColor());
		circle.setAntiAlias(true);
		Paint circleOutline = new Paint();
		circleOutline.setColor(Color.BLACK);
		circleOutline.setAntiAlias(true);
		Paint circleGlow = new Paint();
		circleGlow.setColor(Color.WHITE);
		circleGlow.setAntiAlias(true);

		canvas.drawCircle(point.x, point.y, radius+2, circleGlow);
		canvas.drawCircle(point.x, point.y, radius+1, circleOutline);
		canvas.drawCircle(point.x, point.y, radius, circle);
		
		if (mapView.getZoomLevel() >= 5) {
			canvas.drawRoundRect(rectGlow, 2, 2, bgGlow);
			canvas.drawRoundRect(rectOutline, 2, 2, bgOutline);
			canvas.drawRoundRect(rect, 2, 2, bg);

			float t1x = rx1 + PADDING;
			float t1y = ry1 + TEXTSIZE;
			canvas.drawText("M" + quake.getMagnitude(), t1x, t1y, text);

			float t2x = rx1 + PADDING;
			float t2y = t1y + TEXTSIZE;

			canvas.drawText(quake.getShortDateString(), t2x, t2y, text);
		}
	}

}
