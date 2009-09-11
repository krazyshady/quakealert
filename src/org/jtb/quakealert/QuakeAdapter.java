package org.jtb.quakealert;

import java.util.List;

import android.app.Activity;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class QuakeAdapter extends ArrayAdapter {
	private Activity context;
	private List<Quake> quakes;
	private Location location;
	private QuakePrefs quakePrefs;
	
	QuakeAdapter(Activity context, List<Quake> quakes, Location location) {
		super(context, R.layout.quake, quakes);

		this.context = context;
		this.quakes = quakes;
		this.location = location;
		
		quakePrefs = new QuakePrefs(context);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		View view = inflater.inflate(R.layout.quake, null);
		Quake q = quakes.get(position);
	
		View severityView = view.findViewById(R.id.severity);
		severityView.setBackgroundColor(q.getColor());
		
		TextView row1Text = (TextView) view.findViewById(R.id.row1_text);
		row1Text.setText("M" + q.getMagnitude() + " - " + q.getRegion());

		Distance d = new Distance(q.getDistance(location));	
		TextView row2Text = (TextView) view.findViewById(R.id.row2_text);
		row2Text.setText(q.getListDateString() + ", " + d.toString(quakePrefs));
		
		return view;
	}	
}
