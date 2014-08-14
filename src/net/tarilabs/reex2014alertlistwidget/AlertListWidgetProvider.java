package net.tarilabs.reex2014alertlistwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

public class AlertListWidgetProvider extends AppWidgetProvider {

	@Override
	public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
	    super.onUpdate(context, appWidgetManager, appWidgetIds);
	    Log.i("net.tarilabs", "onUpdate()");
	    appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.listViewWidget);
	    for (int appWidgetId: appWidgetIds) {
	        RemoteViews rv = buildRemoteViews(context, appWidgetId);
	        appWidgetManager.updateAppWidget(appWidgetId, rv);
			// works for me with appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.listViewWidget); outside this loop
	        // and the following not more necessary.
	        // appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.listViewWidget);
	    }
	}

	
	public static RemoteViews buildRemoteViews(final Context context, final int appWidgetId) {
		Log.i("net.tarilabs", "buildRemoteViews()" + appWidgetId);
        final RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget);
        Intent intent = new Intent(context, WidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        rv.setRemoteAdapter(R.id.listViewWidget, intent);

        rv.setEmptyView(R.id.listViewWidget, R.id.empty_view);
        
        SharedPreferences prefs = context.getSharedPreferences(Configure.SHARED_PREF_NAME, Context.MODE_PRIVATE);
		String cogitoBaseURL = prefs.getString(Configure.SHARED_PREF_KEY, "asd");
		Log.i("net.tarilabs", "buildRemoteViews() cogito base URL from prefs: " + cogitoBaseURL);
        Intent intent2 = new Intent(Intent.ACTION_VIEW, Uri.parse(cogitoBaseURL + "/explore.xhtml#Alerts"));
		PendingIntent pendingIntent2 = PendingIntent.getActivity(context, 0, intent2, 0);
		rv.setOnClickPendingIntent(R.id.textView1, pendingIntent2);

        // TODO for later each list item can have its own pendingIntentTemplate
//        final Intent toastIntent = new Intent(context, AlertListWidgetProvider.class);
//        toastIntent.setAction(AlertListWidgetProvider.TOAST_ACTION);
//        toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
//        toastIntent.setData(Uri.parse(toastIntent.toUri(Intent.URI_INTENT_SCHEME)));
//        final PendingIntent toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        rv.setPendingIntentTemplate(android.R.id.list, toastPendingIntent);

        return rv;
}

	public static void updateAppWidget(final Context context, final AppWidgetManager appWidgetManager, final int appWidgetId) {
		Log.i("net.tarilabs", "updateAppWidget()" + appWidgetId);
		final RemoteViews views = buildRemoteViews(context, appWidgetId);
		appWidgetManager.updateAppWidget(appWidgetId, views);
	}

}
