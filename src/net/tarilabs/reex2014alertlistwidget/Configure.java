package net.tarilabs.reex2014alertlistwidget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Configure extends Activity {
	
	public static final String SHARED_PREF_NAME = "WidgetPrefs";
	public static final String SHARED_PREF_KEY = "cogitoHostURL";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.configure);

		setResult(RESULT_CANCELED);

		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		int theIdIs = AppWidgetManager.INVALID_APPWIDGET_ID;
		if (extras != null) {
			theIdIs = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		}
		if (theIdIs == AppWidgetManager.INVALID_APPWIDGET_ID) {
			finish();
		}

		final int appWidgetId = theIdIs;
		final EditText cfgEditTextURL = (EditText) findViewById(R.id.cfgEditTextURL);

		Button cfgButtonCreate = (Button) findViewById(R.id.cfgButtonCreate);
		cfgButtonCreate.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				SharedPreferences prefs = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
				Editor editor = prefs.edit();
				editor.putString(SHARED_PREF_KEY, cfgEditTextURL.getText().toString());
				editor.commit();
				
				// First set result OK with appropriate widgetId
				Intent resultValue = new Intent();
				resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
				setResult(RESULT_OK, resultValue);

				// Build/Update widget
				AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());

				// equivalent to xxxWidgetProvider.updateAppWidget()    
				appWidgetManager.updateAppWidget(appWidgetId,
				                                 AlertListWidgetProvider.buildRemoteViews(getApplicationContext(),
				                                                                       appWidgetId));
				// Updates the collection view, not necessary the first time
				appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.listViewWidget);

				finish();
			}
		});

	}
}
