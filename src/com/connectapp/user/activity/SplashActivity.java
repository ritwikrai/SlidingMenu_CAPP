package com.connectapp.user.activity;

import org.json.JSONObject;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.connectapp.user.R;
import com.connectapp.user.syncadapter.Constant;
import com.connectapp.user.util.AlertDialogCallBack;
import com.connectapp.user.util.Util;
import com.connectapp.user.volley.ServerResponseCallback;
import com.connectapp.user.volley.VolleyTaskManager;

public class SplashActivity extends Activity implements ServerResponseCallback {

	private TextView tv_splash, tv_subText;
	private Typeface custom_font;
	private Account appAccount;
	private VolleyTaskManager volleyTaskManager;
	private String TAG = getClass().getSimpleName().toString();
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_splash);
		mContext = SplashActivity.this;

		//Log.v(null, null);
		tv_splash = (TextView) findViewById(R.id.tv_splash);

		tv_subText = (TextView) findViewById(R.id.tv_subText);

		custom_font = Typeface.createFromAsset(getAssets(), "fonts/arial.ttf");

		tv_splash.setTypeface(custom_font);

		tv_subText.setTypeface(custom_font);

		initializeSyncAdapter();

		volleyTaskManager = new VolleyTaskManager(mContext);

		new SplashTimerTask().execute();

	}

	private class SplashTimerTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			try {
				Thread.sleep(2345);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (Util.isInternetAvailable(SplashActivity.this))

				getApplicationVersion();
			else
				proceedNormally();

		}
	}

	private void openRegistrationActivity() {

		Intent intent = new Intent(SplashActivity.this, RegistrationActivity.class);
		startActivity(intent);
		finish();
	}

	public void openLoginActivity() {

		Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
		startActivity(intent);
		finish();

	}

	private void openMainActivity() {

		Intent intent = new Intent(SplashActivity.this, MainActivity.class);
		startActivity(intent);
		finish();
	}

	/**
	 * Initilaizes the sync adapter with proper Authentication through
	 * authentication service.
	 * **/
	private void initializeSyncAdapter() {

		Log.e("Splash Activity", "------------>> Sync Adapter initialized  <<--------------");
		appAccount = new Account(Constant.ACCOUNT_NAME, Constant.ACCOUNT_TYPE);
		AccountManager accountManager = AccountManager.get(this);
		accountManager.addAccountExplicitly(appAccount, null, null);
		ContentResolver.setIsSyncable(appAccount, Constant.PROVIDER, 1);
		ContentResolver.setMasterSyncAutomatically(true);
		ContentResolver.setSyncAutomatically(appAccount, Constant.PROVIDER, true);
	}

	@Override
	public void onSuccess(JSONObject resultJsonObject) {

		Log.d(TAG, "Result \n" + resultJsonObject);
		if (resultJsonObject.optString("code").trim().equalsIgnoreCase("404")) {

			// Application version is Up to date.
			proceedNormally();

		} else {

			// Older version is installed
			Util.showCallBackMessageWithOkCallback(SplashActivity.this,
					"A new version has arrived. Please tap ok to update!", new AlertDialogCallBack() {

						@Override
						public void onSubmit() {
							openPlaystore();
						}

						@Override
						public void onCancel() {

						}
					});
		}

	}

	@Override
	public void onError() {

		Util.showCallBackMessageWithOkCancel(mContext, "Server not responding. Tap ok to try again.\nor cancel to exit.",
				new AlertDialogCallBack() {

					@Override
					public void onSubmit() {
						getApplicationVersion();

					}

					@Override
					public void onCancel() {
						finish();

					}
				});

	}

	private void getApplicationVersion() {
		try {

			PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			int versionCode = pInfo.versionCode;

			Log.d(TAG, "version code: " + versionCode);
			volleyTaskManager.doGetAppVersionCodeFromPlaystore("" + versionCode);

		} catch (NameNotFoundException e) {

			// If Exception Occurs then the Normal Splash will kick in
			Log.e(TAG, "Package Name not Found!");
			e.printStackTrace();

			proceedNormally();
		}
	}

	private void openPlaystore() {

		final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
		try {
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
		} catch (android.content.ActivityNotFoundException anfe) {
			startActivity(new Intent(Intent.ACTION_VIEW,
					Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
		}
		finish();
	}

	/**
	 * Proceed forward to the respective Activities.
	 * */
	private void proceedNormally() {
		if (Util.fetchUserClass(SplashActivity.this) == null) {

			openRegistrationActivity();

		} else {

			Log.e("User logged in:", Util.fetchUserClass(SplashActivity.this).getIsLoggedin() + "");
			if (Util.fetchUserClass(SplashActivity.this).getIsLoggedin()) {

				openMainActivity();
			} else {
				openLoginActivity();
			}
		}
	}

}
