package com.connectapp.user.members;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;

import com.connectapp.user.R;
import com.connectapp.user.volley.ServerResponseCallback;
import com.connectapp.user.volley.VolleyTaskManager;

public class MembersDirectory extends ActionBarActivity implements ServerResponseCallback {

	private Context mContext;
	private VolleyTaskManager volleyTaskManager;
	private int currentNode = 0;// Current Node meaning current city
	private String TAG = getClass().getSimpleName();
	// Progress Dialog
	private ProgressDialog pDialog;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_members_directory);
		mContext = MembersDirectory.this;

		volleyTaskManager = new VolleyTaskManager(mContext);

		pDialog = new ProgressDialog(this);
		pDialog.setMessage("Preparing Directory for the first time. Please wait...");
		pDialog.setIndeterminate(false);
		pDialog.setMax(100);
		pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pDialog.setCancelable(true);
		pDialog.show();

		fetchMembersDirectory();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		// Inflate menu to add items to action bar if it is present.
		inflater.inflate(R.menu.menu_main, menu);
		// Associate searchable configuration with the SearchView
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

		android.widget.AutoCompleteTextView searchTextView = (android.widget.AutoCompleteTextView) searchView
				.findViewById(android.support.v7.appcompat.R.id.search_src_text);
		try {
			java.lang.reflect.Field mCursorDrawableRes = android.widget.TextView.class.getDeclaredField("mCursorDrawableRes");
			mCursorDrawableRes.setAccessible(true);
			mCursorDrawableRes.set(searchTextView, R.drawable.cursor); //This sets the cursor resource ID to 0 or @null which will make it visible on white background
		} catch (Exception e) {
		}
		return super.onCreateOptionsMenu(menu);
	}

	private void fetchMembersDirectory() {

		volleyTaskManager.doGetMembersDirectory(0);

	}

	@Override
	public void onSuccess(JSONObject resultJsonObject) {

		Log.d(TAG, "onSuccess: " + resultJsonObject);
		if (resultJsonObject.optString("code").trim().equalsIgnoreCase("200")) {

			int memberCount = Integer.parseInt(resultJsonObject.optString("memberCount").trim());
			int cityCount = Integer.parseInt(resultJsonObject.optString("cityCount").trim());

			pDialog.setProgress(20);

			/*// Move to the next node
			currentNode++;
			if (currentNode < totalNode)// Check if the last Node has already been called.
				volleyTaskManager.doGetMembersDirectory(currentNode);
			else if (currentNode == totalNode)
				Toast.makeText(mContext, "All members have been updated.", Toast.LENGTH_SHORT).show();*/
		} else {
			// Retry
		}
	}

	@Override
	public void onError() {
		pDialog.dismiss();

	}
}
