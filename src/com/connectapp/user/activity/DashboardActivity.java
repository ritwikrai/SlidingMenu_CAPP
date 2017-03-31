/*package com.connectapp.user.activity;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.connectapp.user.R;
import com.connectapp.user.adapter.ListAdapter;
import com.connectapp.user.data.Keyword;
import com.connectapp.user.data.OfflineSubmission;
import com.connectapp.user.data.Thread;
import com.connectapp.user.data.Threads;
import com.connectapp.user.data.UserClass;
import com.connectapp.user.syncadapter.Constant;
import com.connectapp.user.syncadapter.DBConstants;
import com.connectapp.user.syncadapter.OfflineDB;
import com.connectapp.user.util.AlertDialogCallBack;
import com.connectapp.user.util.Util;
import com.connectapp.user.volley.ServerResponseCallback;
import com.connectapp.user.volley.VolleyTaskManager;

public class DashboardActivity extends Activity implements ServerResponseCallback, DBConstants {

	private ListView lv_menu;
	private Context mContext;
	private VolleyTaskManager volleyTaskManager;
	private String TAG = "";
	private ArrayList<Thread> threadList = new ArrayList<Thread>();
	private ArrayList<OfflineSubmission> offlineSubmissions = new ArrayList<OfflineSubmission>();
	private int dataCount = 0;
	private boolean isPostOfflineData = false;
	private boolean isFetchThreads = false;
	private Dialog customDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard);
		initView();
	}

	private void initView() {

		lv_menu = (ListView) findViewById(R.id.lv_menu);

		mContext = DashboardActivity.this;

		TAG = mContext.getClass().getSimpleName();

		volleyTaskManager = new VolleyTaskManager(mContext);

		if (Util.isInternetAvailable(mContext)) {

			HashMap<String, String> requestMap = new HashMap<String, String>();
			isFetchThreads = true;
			volleyTaskManager.doPostFetchThreads(requestMap, true);
		} else {
			if (Util.fetchOfflineKeywordsThreads(mContext) != null) {
				threadList = Util.fetchOfflineKeywordsThreads(mContext).getThreads();
				updateListUI(threadList);
			}
		}

	}

	public void onLogoutClick(View v) {

		startActivity(new Intent(mContext, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
		UserClass userClass = Util.fetchUserClass(mContext);
		userClass.setIsLoggedin(false);
		Util.saveUserClass(mContext, userClass);
		finish();
	}

	@Override
	public void onSuccess(JSONObject resultJsonObject) {

		Log.d(TAG, "" + resultJsonObject);

		if (isFetchThreads) {
			isFetchThreads = false;

			if (resultJsonObject.toString() != null && !resultJsonObject.toString().trim().isEmpty()) {
				threadList.clear();
				JSONArray resultArray = resultJsonObject.optJSONArray("threads");

				for (int i = 0; i < resultArray.length(); i++) {
					Thread item = new Thread();
					item.setThreadID(resultArray.optJSONObject(i).optString("tid"));
					item.setThreadName(resultArray.optJSONObject(i).optString("tname"));
					Log.v("Keywords", "Keywords: " + resultArray.optJSONObject(i).optString("keywords"));
					JSONArray keywordsArray = resultArray.optJSONObject(i).optJSONArray("keywords");
					ArrayList<Keyword> keywordList = new ArrayList<Keyword>();

					if (keywordsArray != null) {
						for (int j = 0; j < keywordsArray.length(); j++) {
							Keyword keyword = new Keyword();
							keyword.setId(keywordsArray.optJSONObject(j).optString("id"));
							keyword.setValue(keywordsArray.optJSONObject(j).optString("value"));
							keywordList.add(keyword);

						}
					}
					item.setKeywords(keywordList);
					threadList.add(item);
				}

				saveThreads(threadList);
				updateListUI(threadList);

			} else {
				Toast.makeText(mContext, " Request failed. Please try again.", Toast.LENGTH_SHORT).show();
			}
		} else if (isPostOfflineData) {
			isPostOfflineData = false;

			Log.d("onSuccess", resultJsonObject.toString());
			if (resultJsonObject.toString() != null && !resultJsonObject.toString().trim().isEmpty()) {
				try {
					if (resultJsonObject.optString("code").trim().equalsIgnoreCase("200")) {

						deleteFromDatabase(offlineSubmissions.get(dataCount).getDate(), offlineSubmissions.get(dataCount).getTime());
						dataCount++;
						postOfflineData();

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	private void saveThreads(ArrayList<Thread> threadList) {

		Threads threads = new Threads();
		threads.setThreads(threadList);
		Util.saveOfflineKeywordsThreads(mContext, threads);

	}

	@Override
	public void onError() {

	}

	private void updateListUI(final ArrayList<Thread> threadList) {
		ListAdapter adapter = new ListAdapter(mContext, threadList);
		lv_menu.setAdapter(adapter);
		lv_menu.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				if (threadList.get(position).getThreadID().trim().equalsIgnoreCase("6")) {

					Intent intent = new Intent(mContext, FormActivity.class);
					intent.putExtra("thread", threadList.get(position));
					startActivity(intent);

				} else {
					openKeyWordsActivity(position);
				}

			}

		});
	}

	private void openKeyWordsActivity(int position) {

		Intent intent = new Intent(mContext, KeyWordActivity.class);
		Thread thread = new Thread();
		thread = threadList.get(position);
		intent.putExtra("threadAndKeywords", thread);
		startActivity(intent);

	}

	public void onDataSettingClick(View v) {

		customDialog = new Dialog(mContext);

		customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

		LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = layoutInflater.inflate(R.layout.dialog_offline_data_status, null);

		ImageButton btn_close = (ImageButton) view.findViewById(R.id.btn_close);

		btn_close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				customDialog.dismiss();

			}
		});

		TextView tv_unsyncedData = (TextView) view.findViewById(R.id.tv_unsyncedData);

		offlineSubmissions.clear();
		try {
			fetchOfflineRows();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			tv_unsyncedData.setText("Unsynced Data: (" + offlineSubmissions.size() + ")");

		}

		RelativeLayout rl_unsyncedDataCountLayout = (RelativeLayout) view.findViewById(R.id.rl_unsyncedDataCount);
		
		rl_unsyncedDataCountLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				customDialog.dismiss();
				startActivity(new Intent(mContext, SubmissionActivity.class));
			}
		});

		Button btn_submit = (Button) view.findViewById(R.id.btn_sync_now);
		btn_submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (Util.isInternetAvailable(mContext)) {
					if (offlineSubmissions.size() > 0) {
						System.out.println("Offline Data size: " + offlineSubmissions.size());
						postOfflineData();
					} else {
						Toast.makeText(mContext, "You have no offline data", Toast.LENGTH_SHORT).show();
					}
				} else {
					Util.showMessageWithOk(mContext, "No Internet Access.");
				}
			}
		});
		customDialog.setCancelable(false);
		customDialog.setContentView(view);
		customDialog.setCanceledOnTouchOutside(false);
		// Start AlertDialog
		customDialog.show();
	}

	private void fetchOfflineRows() {
		OfflineDB mDb = new OfflineDB(mContext);
		SQLiteDatabase database = mDb.getReadableDatabase();

		try {
			Cursor cur = database.query(OFFLINE_TABLE, null, null, null, null, null, null);
			System.out.println("Count: " + cur.getCount());
			if (cur != null) {
				while (cur.moveToNext()) {
					offlineSubmissions.add(Constant.getOfflineSubmissionFromCursor(cur));
				}
				cur.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void postOfflineData() {

		Log.d("postOfflineData", "Submission size " + offlineSubmissions.size());
		Log.d("postOfflineData", "Data Count " + dataCount);

		if (offlineSubmissions.size() > 0 && dataCount < offlineSubmissions.size()) {
			HashMap<String, String> formDataMap = new HashMap<String, String>();

			formDataMap.put("muID", "" + offlineSubmissions.get(dataCount).getMuId());
			formDataMap.put("threadID", "" + offlineSubmissions.get(dataCount).getThreadID());
			formDataMap.put("image", "" + offlineSubmissions.get(dataCount).getBase64Image());
			formDataMap.put("lat", "" + "" + offlineSubmissions.get(dataCount).getLatitude());
			formDataMap.put("long", "" + "" + offlineSubmissions.get(dataCount).getLongitude());
			formDataMap.put("comments", "" + offlineSubmissions.get(dataCount).getComments());
			//formDataMap.put("address", "" + offlineSubmissions.get(dataCount).getAddress());
			formDataMap.put("date", "" + offlineSubmissions.get(dataCount).getDate());
			formDataMap.put("time", "" + offlineSubmissions.get(dataCount).getTime());
			System.out.println("------------------------->>>>>>>>>>>>>--------------------------------");
			System.out.println("TIME: " + offlineSubmissions.get(dataCount).getTime());
			System.out.println("VILLAGE NAME: " + offlineSubmissions.get(dataCount).getVillageName());

			if (offlineSubmissions.get(dataCount).getThreadID().equalsIgnoreCase("6")) {
				//formDataMap.put("otherData", "" + offlineSubmissions.get(dataCount).getOtherData());
				//formDataMap.put("keyWords", "" + offlineSubmissions.get(dataCount).getKeywords());
				formDataMap.put("sCode", offlineSubmissions.get(dataCount).getSchoolCode());
				formDataMap.put("village", offlineSubmissions.get(dataCount).getVillageName());
			} else {
				formDataMap.put("keyWords", "" + offlineSubmissions.get(dataCount).getKeywords());

			}
			postData(formDataMap);
		}

		else {
			dataCount = 0;
			Util.showCallBackMessageWithOkCallback_success(mContext, "Data posted successfully.", new AlertDialogCallBack() {
				@Override
				public void onSubmit() {
					customDialog.dismiss();
				}

				@Override
				public void onCancel() {

				}
			});
		}
	}

	private void postData(HashMap<String, String> formDataMap) {
		System.out.println(formDataMap);
		System.out.println(new JSONObject(formDataMap).toString());
		//generateNoteOnSD(mContext, "OFFLIE NOTE", "" + new JSONObject(formDataMap).toString());
		volleyTaskManager.doPostFormData(formDataMap, true);
		isPostOfflineData = true;

	}

	private void deleteFromDatabase(String date, String time) {
		System.out.println("---------------------------------------------\n");
		System.out.println("delete from database");
		System.out.println("---------------------------------------------\n\n");
		OfflineDB mdb = new OfflineDB(mContext);
		boolean status = mdb.deleteRow(date, time);
		if (status) {
			System.out.println("Deleted!!");
		} else
			System.out.println("NOT DONE DELETE");
	}

}
*/