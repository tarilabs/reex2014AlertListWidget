package net.tarilabs.reex2014alertlistwidget;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

/**
 * If you are familiar with Adapter of ListView,this is the same as adapter with
 * few changes
 *
 */
public class ListProvider implements RemoteViewsFactory {
	private ArrayList<Alert<?>> listItemList = new ArrayList<Alert<?>> ();
	private Context context = null;
	@SuppressWarnings("unused")
	private int appWidgetId;
	
	private static final int S_MS = 1000;
	private static final int M_MS = 60 * S_MS;
	private static final int H_MS = 60 * M_MS;
	private static final int D_MS = 24 * H_MS;


	public static String formatTsAsTimeAgo(long ts) {
	    long now = System.currentTimeMillis();
	    if (ts > now || ts <= 0) {
	        return "boh";
	    }
	    final long diff = now - ts;
	    if (diff < M_MS) {
	        return "Just Now";
	    } else if (diff < 2 * M_MS) {
	        return "A minute ago";
	    } else if (diff < 10 * M_MS) {
	        return "Very recently";
	    } else if (diff < 50 * M_MS) {
	        return diff / M_MS + "m ago";
	    } else if (diff < 90 * M_MS) {
	        return "an hour ago";
	    } else if (diff < 24 * H_MS) {
	        return diff / H_MS + "h ago";
	    } else if (diff < 48 * H_MS) {
	        return "yesterday";
	    } else {
	        return diff / D_MS + "d ago";
	    }
	}

	public ListProvider(Context context, Intent intent) {
		this.context = context;
		appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
				AppWidgetManager.INVALID_APPWIDGET_ID);

		
	}

	private void populateListItem(String cogitoBaseURL) {
		DefaultHttpClient client = new DefaultHttpClient();
		String url = cogitoBaseURL + "/restapi/ruleengine/query/Alert";
		Log.i("net.tarilabs", "http GET to URL : "+url);
		HttpGet httpGet = new HttpGet(url);
		try {
			HttpResponse execute = client.execute(httpGet);
			InputStream content = execute.getEntity().getContent();

			BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
			String s = "";
			StringBuilder rBuilder = new StringBuilder();
			while ((s = buffer.readLine()) != null) {
				rBuilder.append(s);
			}
			// REST api first is array of Drools Query
			JSONArray jsonArray = new JSONArray(rBuilder.toString());
			for (int i = 0; i < jsonArray.length(); i++) {
		        JSONObject jsonObject = jsonArray.getJSONObject(i);
		        JSONObject alertJson = jsonObject.getJSONObject("$alert");
		        Alert<Object> item = new Alert<Object>(alertJson.getLong("ts"), 
		        									   alertJson.getString("condition"),
		        									   getAlertTypeFromString(alertJson.getString("type")),
		        									   null);
		        listItemList.add(item);
			}
			Collections.sort(listItemList, new AlertTsDescComparator());
		} catch (Exception e) {
			Log.e("net.tarilabs", "Something wrong while asyncTask: "+e.getMessage(), e);
			listItemList = new ArrayList<Alert<?>> ();
		}
		
	}

	private AlertType getAlertTypeFromString(String string) {
		if (string.equals("INFO")) {
			return AlertType.INFO;
		} else if (string.equals("WARNING")) {
			return AlertType.WARNING;
		} else if (string.equals("SEVERE")) {
			return AlertType.SEVERE;
		} else {
			return AlertType.INFO;
		}
	}

	@Override
	public int getCount() {
		return listItemList.size();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/*
	 * Similar to getView of Adapter where instead of Viewwe return RemoteViews
	 */
	@Override 
	public RemoteViews getViewAt(int position) {
		final RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.list_row);
		Alert<?> item = listItemList.get(position);
		remoteView.setTextViewText(R.id.heading, formatTsAsTimeAgo(item.getTs()));
		remoteView.setTextViewText(R.id.content, item.getCondition());

		return remoteView;
	}
	
	@Override
	public RemoteViews getLoadingView() {
		return null;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public void onCreate() {
		// moved to OnDataSetChanged
	}

	// Note: accordingly to Android doc. , it's okay to do expensive task (eg: network) here synchronously.
	@Override
	public void onDataSetChanged() {
		SharedPreferences prefs = context.getSharedPreferences(Configure.SHARED_PREF_NAME, Context.MODE_PRIVATE);
		String cogitoBaseURL = prefs.getString(Configure.SHARED_PREF_KEY, "asd");
		Log.i("net.tarilabs", "cogito base URL from prefs: " + cogitoBaseURL);
		
		populateListItem(cogitoBaseURL);
		
		try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
	}

	@Override
	public void onDestroy() {
	}
}