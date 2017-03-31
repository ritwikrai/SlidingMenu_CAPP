package com.connectapp.user.fragment;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.connectapp.user.R;
import com.connectapp.user.activity.SchoolFormActivity;
import com.connectapp.user.activity.KeyWordActivity;
import com.connectapp.user.activity.RathFormActivity;
import com.connectapp.user.adapter.ThreadListAdapter;
import com.connectapp.user.constant.Consts;
import com.connectapp.user.data.Keyword;
import com.connectapp.user.data.Thread;
import com.connectapp.user.data.Threads;
import com.connectapp.user.members.MembersDirectory;
import com.connectapp.user.members.TempMembersDirectory;
import com.connectapp.user.syncadapter.DBConstants;
import com.connectapp.user.util.Util;
import com.connectapp.user.volley.PostWithJsonWebTask;
import com.connectapp.user.volley.ServerResponseStringCallback;

public class DashboardFragment extends Fragment implements DBConstants {

	private ListView lv_menu;
	private Context mContext;

	private String TAG = "";
	private ArrayList<Thread> threadList = new ArrayList<Thread>();

	private boolean isFetchThreads = false;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
		mContext = this.getActivity();
		initView(view);
		return view;
	}

	private void initView(View view) {

		lv_menu = (ListView) view.findViewById(R.id.lv_menu);

		TAG = mContext.getClass().getSimpleName();

		if (Util.isInternetAvailable(mContext)) {

			HashMap<String, String> requestMap = new HashMap<String, String>();
			isFetchThreads = true;

			//volleyTaskManager.doPostFetchThreads(requestMap, true);

			PostWithJsonWebTask.callPostWithJsonWebtask(getActivity(), Consts.FETCH_THREADS_URL,
					requestMap, new ServerResponseStringCallback() {

						@Override
						public void onSuccess(String resultJsonObject) {
							getThreads(resultJsonObject);
						}

						@Override
						public void ErrorMsg(VolleyError error) {

						}
					}, true, Request.Method.POST);

		} else {
			if (Util.fetchOfflineKeywordsThreads(mContext) != null) {
				threadList = Util.fetchOfflineKeywordsThreads(mContext).getThreads();
				updateListUI(threadList);
			}
		}

	}

	private void getThreads(String resultJsonString) {

		Log.d(TAG, "" + resultJsonString);
		try {
			JSONObject resultJsonObject = new JSONObject(resultJsonString);
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
					Thread item = new Thread();
					item.setThreadID("xyz");
					item.setThreadName("Members Directory");
					threadList.add(item);
					saveThreads(threadList);
					updateListUI(threadList);

				} else {
					Toast.makeText(mContext, " Request failed. Please try again.", Toast.LENGTH_SHORT).show();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void saveThreads(ArrayList<Thread> threadList) {
		Log.d("saveThreads", "Size: " + threadList.size());
		System.out.println("saveThreads Size: " + threadList.size());
		Threads threads = new Threads();
		threads.setThreads(threadList);
		Util.saveOfflineKeywordsThreads(mContext, threads);

	}

	private void updateListUI(final ArrayList<Thread> threadList) {
		Log.d("updateListUI", "Size: " + threadList.size());
		System.out.println("updateListUI Size: " + threadList.size());
		ThreadListAdapter adapter = new ThreadListAdapter(mContext, threadList);
		lv_menu.setAdapter(adapter);
		lv_menu.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				if (threadList.get(position).getThreadID().trim().equalsIgnoreCase("6")) {

					Intent intent = new Intent(mContext, SchoolFormActivity.class);
					intent.putExtra("thread", threadList.get(position));
					startActivity(intent);

				} else if (threadList.get(position).getThreadID().trim().equalsIgnoreCase("7")) {
					Intent intent = new Intent(mContext, RathFormActivity.class);
					intent.putExtra("thread", threadList.get(position));
					startActivity(intent);
					//Toast.makeText(mContext, "Coming Soon....", Toast.LENGTH_SHORT).show();
				}

				else if (threadList.get(position).getThreadName().equalsIgnoreCase("Members Directory")) {
					Intent intent = new Intent(mContext, TempMembersDirectory.class);
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

}
