package org.jtb.quakealert;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class WarnDialog extends AlertDialog {

	public static class Builder extends AlertDialog.Builder {
		private Builder mThis;
		private Context context;
		private String warnId;
	

		public Builder(Context context, String warnId, int warnMessageId) {
			super(context);
			mThis = this;
			this.context = context;
			this.warnId = warnId;

			setTitle("Warning");
			
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View layout = inflater.inflate(R.layout.warn_dialog, null);
			layout.setMinimumHeight(180);
			layout.setMinimumWidth(240);
			setView(layout);
			setNeutralButton(R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});

			CheckBox cb = (CheckBox)layout.findViewById(R.id.warn_check);
			cb.setChecked(false);
			cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					if (isChecked) {
						QuakePrefs qp = new QuakePrefs(mThis.context);
						qp.setWarn(mThis.warnId, false);
					}
				}
			});

			TextView tv = (TextView)layout.findViewById(R.id.warn_text);
			tv.setText(context.getResources().getString(warnMessageId));
		}

		public boolean isWarn() {
			QuakePrefs qp = new QuakePrefs(context);
			return qp.isWarn(warnId);
		}
	}

	public WarnDialog(Context context) {
		super(context);
	}
}
